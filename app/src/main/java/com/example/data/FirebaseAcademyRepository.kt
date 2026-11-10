package com.example.data

import android.content.Context
import android.util.Log
import com.example.R
import com.example.model.ActivationCode
import com.example.model.UserSession
import com.example.model.WatchProgress
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FirebaseAcademyRepository(
  private val context: Context,
  private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(
    context.getString(R.string.firestore_database_id)
  )
) {
  private val auth = Firebase.auth
  private val TAG = "FirebaseAcademyRepo"

  // -------------------------------------------------------------
  // 1. User Profiles in Cloud
  // -------------------------------------------------------------
  suspend fun saveUserProfile(session: UserSession): Result<Unit> {
    return try {
      val uid = auth.currentUser?.uid ?: session.studentName.ifBlank { "anonymous_user" }
      val userDoc = firestore.collection("users").document(uid)

      val data = hashMapOf(
        "userId" to uid,
        "studentName" to session.studentName,
        "phoneNumber" to session.phoneNumber,
        "activationCode" to session.activationCode,
        "activatedTimestamp" to session.activatedTimestamp,
        "durationDays" to session.durationDays,
        "isOwner" to session.isOwner,
        "updatedAt" to FieldValue.serverTimestamp()
      )

      userDoc.set(data, SetOptions.merge()).await()
      Log.d(TAG, "Successfully synced user profile to cloud for $uid")
      Result.success(Unit)
    } catch (e: Exception) {
      Log.e(TAG, "Failed to save user profile to cloud", e)
      Result.failure(e)
    }
  }

  suspend fun getUserProfile(userId: String): UserSession? {
    return try {
      val doc = firestore.collection("users").document(userId).get().await()
      if (doc.exists()) {
        UserSession(
          isActivated = true,
          studentName = doc.getString("studentName") ?: "",
          phoneNumber = doc.getString("phoneNumber") ?: "",
          activationCode = doc.getString("activationCode") ?: "",
          activatedTimestamp = doc.getLong("activatedTimestamp") ?: 0L,
          durationDays = (doc.getLong("durationDays") ?: 30L).toInt(),
          isOwner = doc.getBoolean("isOwner") ?: false
        )
      } else {
        null
      }
    } catch (e: Exception) {
      Log.e(TAG, "Error fetching user profile from cloud", e)
      null
    }
  }

  // -------------------------------------------------------------
  // 2. Activation Codes in Cloud
  // -------------------------------------------------------------
  suspend fun saveActivationCode(code: ActivationCode): Result<Unit> {
    return try {
      val doc = firestore.collection("activation_codes").document(code.code)
      val data = hashMapOf(
        "code" to code.code,
        "durationDays" to code.durationDays,
        "studentName" to code.studentName,
        "createdAt" to code.createdAt,
        "activatedAt" to code.activatedAt,
        "isUsed" to code.isUsed,
        "isActive" to code.isActive
      )
      doc.set(data, SetOptions.merge()).await()
      Log.d(TAG, "Activation code ${code.code} synced to cloud")
      Result.success(Unit)
    } catch (e: Exception) {
      Log.e(TAG, "Failed to save activation code to cloud", e)
      Result.failure(e)
    }
  }

  fun observeActivationCodes(): Flow<List<ActivationCode>> {
    return firestore.collection("activation_codes")
      .snapshots()
      .map { snapshot ->
        snapshot.documents.mapNotNull { doc ->
          val code = doc.getString("code") ?: doc.id
          val durationDays = (doc.getLong("durationDays") ?: 30L).toInt()
          val studentName = doc.getString("studentName") ?: ""
          val createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis()
          val activatedAt = doc.getLong("activatedAt")
          val isUsed = doc.getBoolean("isUsed") ?: false
          val isActive = doc.getBoolean("isActive") ?: true

          ActivationCode(
            code = code,
            durationDays = durationDays,
            studentName = studentName,
            createdAt = createdAt,
            activatedAt = activatedAt,
            isUsed = isUsed,
            isActive = isActive
          )
        }
      }
      .catch { e ->
        Log.e(TAG, "Error observing cloud activation codes", e)
        emit(emptyList())
      }
  }

  suspend fun validateAndUseCodeInCloud(codeStr: String, studentName: String): Result<ActivationCode> {
    return try {
      val cleanCode = codeStr.trim().uppercase()
      val docRef = firestore.collection("activation_codes").document(cleanCode)
      val snapshot = docRef.get().await()

      if (!snapshot.exists()) {
        return Result.failure(Exception("الكود غير موجود في السحابة"))
      }

      val isActive = snapshot.getBoolean("isActive") ?: true
      val isUsed = snapshot.getBoolean("isUsed") ?: false
      val durationDays = (snapshot.getLong("durationDays") ?: 30L).toInt()
      val activatedAt = snapshot.getLong("activatedAt")

      if (!isActive) {
        return Result.failure(Exception("هذا الكود تم إلغاؤه أو تعطيله"))
      }

      val now = System.currentTimeMillis()
      val updatedCode = ActivationCode(
        code = cleanCode,
        durationDays = durationDays,
        studentName = studentName,
        createdAt = snapshot.getLong("createdAt") ?: now,
        activatedAt = activatedAt ?: now,
        isUsed = true,
        isActive = true
      )

      // Update in cloud
      docRef.update(
        mapOf(
          "isUsed" to true,
          "activatedAt" to (activatedAt ?: now),
          "studentName" to studentName
        )
      ).await()

      Result.success(updatedCode)
    } catch (e: Exception) {
      Log.e(TAG, "Error validating code in cloud", e)
      Result.failure(e)
    }
  }

  // -------------------------------------------------------------
  // 3. Video Progress Tracking in Cloud
  // -------------------------------------------------------------
  suspend fun saveVideoProgress(
    userId: String,
    lessonId: String,
    watchedSeconds: Int,
    totalSeconds: Int,
    isCompleted: Boolean
  ): Result<Unit> {
    return try {
      val docRef = firestore.collection("users")
        .document(userId)
        .collection("video_progress")
        .document(lessonId)

      val data = hashMapOf(
        "userId" to userId,
        "lessonId" to lessonId,
        "watchedSeconds" to watchedSeconds,
        "totalSeconds" to totalSeconds,
        "isCompleted" to isCompleted,
        "completedAt" to (if (isCompleted) System.currentTimeMillis() else null),
        "updatedAt" to FieldValue.serverTimestamp()
      )

      docRef.set(data, SetOptions.merge()).await()
      Log.d(TAG, "Progress for lesson $lessonId saved to cloud for user $userId")
      Result.success(Unit)
    } catch (e: Exception) {
      Log.e(TAG, "Failed to save video progress to cloud", e)
      Result.failure(e)
    }
  }

  fun observeUserVideoProgress(userId: String): Flow<Map<String, WatchProgress>> {
    return firestore.collection("users")
      .document(userId)
      .collection("video_progress")
      .snapshots()
      .map { snapshot ->
        val map = mutableMapOf<String, WatchProgress>()
        for (doc in snapshot.documents) {
          val lessonId = doc.getString("lessonId") ?: doc.id
          val watchedSeconds = (doc.getLong("watchedSeconds") ?: 0L).toInt()
          val totalSeconds = (doc.getLong("totalSeconds") ?: 0L).toInt()
          val isCompleted = doc.getBoolean("isCompleted") ?: false
          val completedAt = doc.getLong("completedAt")
          val timesWatched = (doc.getLong("timesWatched") ?: (if (isCompleted) 1L else 0L)).toInt()

          map[lessonId] = WatchProgress(
            lessonId = lessonId,
            watchedSeconds = watchedSeconds,
            totalSeconds = totalSeconds,
            isCompleted = isCompleted,
            completedAt = completedAt,
            timesWatched = timesWatched
          )
        }
        map.toMap()
      }
      .catch { e ->
        Log.e(TAG, "Error observing video progress from cloud", e)
        emit(emptyMap())
      }
  }
}
