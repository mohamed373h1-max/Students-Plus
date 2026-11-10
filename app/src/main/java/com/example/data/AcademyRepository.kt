package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.ActivationCode
import com.example.model.Homework
import com.example.model.HomeworkSubmission
import com.example.model.StudentLevelStats
import com.example.model.SubmissionStatus
import com.example.model.TelegramChannel
import com.example.model.TelegramPost
import com.example.model.UserSession
import com.example.model.VideoLesson
import com.example.model.WatchProgress
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlin.random.Random

class AcademyRepository(private val context: Context) {

  val firebaseRepo: FirebaseAcademyRepository by lazy {
    FirebaseAcademyRepository(context)
  }

  private val prefs: SharedPreferences =
    context.getSharedPreferences("academy_prefs", Context.MODE_PRIVATE)

  // Current User Session
  private val _session = MutableStateFlow(loadSession())
  val session: StateFlow<UserSession> = _session.asStateFlow()

  // Activation Codes (Created by Owner)
  private val _activationCodes = MutableStateFlow(loadInitialCodes())
  val activationCodes: StateFlow<List<ActivationCode>> = _activationCodes.asStateFlow()

  // Lessons
  private val _lessons = MutableStateFlow(loadInitialLessons())
  val lessons: StateFlow<List<VideoLesson>> = _lessons.asStateFlow()

  // Watch Progress Map: lessonId -> WatchProgress
  private val _watchProgress = MutableStateFlow(loadInitialProgress())
  val watchProgress: StateFlow<Map<String, WatchProgress>> = _watchProgress.asStateFlow()

  // Homeworks
  private val _homeworks = MutableStateFlow(loadInitialHomeworks())
  val homeworks: StateFlow<List<Homework>> = _homeworks.asStateFlow()

  // Homework Submissions
  private val _submissions = MutableStateFlow(loadInitialSubmissions())
  val submissions: StateFlow<List<HomeworkSubmission>> = _submissions.asStateFlow()

  // Telegram Channels
  private val _channels = MutableStateFlow(loadInitialChannels())
  val channels: StateFlow<List<TelegramChannel>> = _channels.asStateFlow()

  // Telegram Posts
  private val _posts = MutableStateFlow(loadInitialPosts())
  val posts: StateFlow<List<TelegramPost>> = _posts.asStateFlow()


  // ---------------------------------------------
  // Session & Activation Methods
  // ---------------------------------------------
  private fun loadSession(): UserSession {
    val isActivated = prefs.getBoolean("session_is_activated", false)
    val studentName = prefs.getString("session_student_name", "") ?: ""
    val phoneNumber = prefs.getString("session_phone", "") ?: ""
    val code = prefs.getString("session_code", "") ?: ""
    val timestamp = prefs.getLong("session_timestamp", 0L)
    val durationDays = prefs.getInt("session_duration_days", 30)
    val isOwner = prefs.getBoolean("session_is_owner", false)

    return UserSession(
      isActivated = isActivated,
      studentName = studentName,
      phoneNumber = phoneNumber,
      activationCode = code,
      activatedTimestamp = timestamp,
      durationDays = durationDays,
      isOwner = isOwner
    )
  }

  private fun saveSession(session: UserSession) {
    prefs.edit().apply {
      putBoolean("session_is_activated", session.isActivated)
      putString("session_student_name", session.studentName)
      putString("session_phone", session.phoneNumber)
      putString("session_code", session.activationCode)
      putLong("session_timestamp", session.activatedTimestamp)
      putInt("session_duration_days", session.durationDays)
      putBoolean("session_is_owner", session.isOwner)
      apply()
    }
    _session.value = session

    CoroutineScope(Dispatchers.IO).launch {
      try {
        firebaseRepo.saveUserProfile(session)
      } catch (_: Exception) {}
    }
  }

  fun activateWithCode(
    codeStr: String,
    studentName: String,
    phoneNumber: String
  ): Result<UserSession> {
    val cleanCode = codeStr.trim().uppercase()

    // Check if matching owner shortcut or activation code
    val foundCode = _activationCodes.value.find { it.code.uppercase() == cleanCode && it.isActive }
      ?: return Result.failure(Exception("كود التفعيل غير صحيح أو تم إلغاؤه! تواصل مع المعلم للحصول على كود جديد."))

    if (foundCode.isUsed && foundCode.activatedAt != null && foundCode.remainingDays <= 0) {
      return Result.failure(Exception("هذا الكود منتهي الصلاحية! يرجى تجديد الاشتراك."))
    }

    val now = System.currentTimeMillis()
    // Mark code as used
    val updatedCodes = _activationCodes.value.map {
      if (it.code.uppercase() == cleanCode) {
        it.copy(
          isUsed = true,
          activatedAt = it.activatedAt ?: now,
          studentName = if (it.studentName.isBlank()) studentName else it.studentName
        )
      } else it
    }
    _activationCodes.value = updatedCodes

    val newSession = UserSession(
      isActivated = true,
      studentName = studentName.ifBlank { "طالب النخبة" },
      phoneNumber = phoneNumber,
      activationCode = cleanCode,
      activatedTimestamp = now,
      durationDays = foundCode.durationDays,
      isOwner = false
    )
    saveSession(newSession)
    return Result.success(newSession)
  }

  fun getOwnerMasterPin(): String {
    return prefs.getString("owner_custom_pin", "1234") ?: "1234"
  }

  fun updateOwnerPin(newPin: String): Boolean {
    val clean = newPin.trim()
    if (clean.length >= 4) {
      prefs.edit().putString("owner_custom_pin", clean).apply()
      return true
    }
    return false
  }

  fun loginAsOwner(enteredPin: String): Boolean {
    if (enteredPin.trim() == getOwnerMasterPin()) {
      val ownerSession = UserSession(
        isActivated = true,
        studentName = "الأستاذ محمد (المالك)",
        phoneNumber = "01000000000",
        activationCode = "MASTER-OWNER",
        activatedTimestamp = System.currentTimeMillis(),
        durationDays = 9999,
        isOwner = true
      )
      saveSession(ownerSession)
      return true
    }
    return false
  }

  fun logout() {
    saveSession(UserSession())
  }

  // ---------------------------------------------
  // Owner Code Generator
  // ---------------------------------------------
  fun generateActivationCode(durationDays: Int, studentNote: String): ActivationCode {
    val randomId = Random.nextInt(1000, 9999)
    val codeStr = "ELITE-$randomId-${durationDays}D"
    val newCode = ActivationCode(
      code = codeStr,
      durationDays = durationDays,
      studentName = studentNote.trim(),
      createdAt = System.currentTimeMillis()
    )
    _activationCodes.value = listOf(newCode) + _activationCodes.value

    CoroutineScope(Dispatchers.IO).launch {
      try {
        firebaseRepo.saveActivationCode(newCode)
      } catch (_: Exception) {}
    }

    return newCode
  }

  fun toggleCodeStatus(codeStr: String) {
    _activationCodes.value = _activationCodes.value.map {
      if (it.code == codeStr) it.copy(isActive = !it.isActive) else it
    }
  }

  // ---------------------------------------------
  // Lessons & Video Progress (Listen 100% Tracking)
  // ---------------------------------------------
  fun markVideoWatched(lessonId: String, completed: Boolean) {
    val current = _watchProgress.value[lessonId]
    val lesson = _lessons.value.find { it.id == lessonId }
    val totalSec = (lesson?.durationMinutes ?: 30) * 60

    val updatedProgress = current?.copy(
      isCompleted = completed,
      watchedSeconds = if (completed) totalSec else (current.watchedSeconds),
      completedAt = if (completed) System.currentTimeMillis() else current.completedAt,
      timesWatched = if (completed) current.timesWatched + 1 else current.timesWatched
    ) ?: WatchProgress(
      lessonId = lessonId,
      watchedSeconds = if (completed) totalSec else 0,
      totalSeconds = totalSec,
      isCompleted = completed,
      completedAt = if (completed) System.currentTimeMillis() else null,
      timesWatched = if (completed) 1 else 0
    )

    _watchProgress.value = _watchProgress.value + (lessonId to updatedProgress)

    CoroutineScope(Dispatchers.IO).launch {
      try {
        val uid = _session.value.studentName.ifBlank { "anonymous_student" }
        firebaseRepo.saveVideoProgress(uid, lessonId, updatedProgress.watchedSeconds, totalSec, completed)
      } catch (_: Exception) {}
    }
  }

  fun updateWatchSeconds(lessonId: String, seconds: Int, totalSeconds: Int) {
    val current = _watchProgress.value[lessonId]
    val isCompleted = current?.isCompleted == true || (totalSeconds > 0 && seconds >= totalSeconds * 0.95f)

    val updated = (current ?: WatchProgress(lessonId = lessonId, totalSeconds = totalSeconds)).copy(
      watchedSeconds = seconds,
      totalSeconds = totalSeconds,
      isCompleted = isCompleted,
      completedAt = if (isCompleted && current?.completedAt == null) System.currentTimeMillis() else current?.completedAt
    )
    _watchProgress.value = _watchProgress.value + (lessonId to updated)
  }

  fun addLesson(
    title: String,
    subject: String,
    chapter: String,
    description: String,
    durationMinutes: Int,
    videoUrl: String,
    pdfTitle: String
  ) {
    val newLesson = VideoLesson(
      id = "lesson_${System.currentTimeMillis()}",
      title = title,
      subject = subject,
      chapter = chapter,
      description = description,
      durationMinutes = durationMinutes,
      videoUrl = videoUrl.ifBlank { "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4" },
      pdfTitle = pdfTitle.ifBlank { "ملخص الدرس والتمارين PDF" },
      dateAdded = "اليوم",
      orderIndex = _lessons.value.size + 1
    )
    _lessons.value = _lessons.value + newLesson
  }

  // ---------------------------------------------
  // Homework & Grading
  // ---------------------------------------------
  fun submitHomework(
    homeworkId: String,
    studentName: String,
    studentPhone: String,
    text: String,
    fileNames: List<String>
  ) {
    val submission = HomeworkSubmission(
      id = "sub_${UUID.randomUUID().toString().take(8)}",
      homeworkId = homeworkId,
      studentName = studentName,
      studentPhone = studentPhone,
      submittedAt = System.currentTimeMillis(),
      submissionText = text,
      attachedFileNames = fileNames,
      status = SubmissionStatus.SUBMITTED
    )
    _submissions.value = listOf(submission) + _submissions.value
  }

  fun gradeSubmission(submissionId: String, grade: Int, feedback: String) {
    _submissions.value = _submissions.value.map {
      if (it.id == submissionId) {
        it.copy(
          status = SubmissionStatus.REVIEWED,
          grade = grade,
          teacherFeedback = feedback
        )
      } else it
    }
  }

  // ---------------------------------------------
  // Telegram Broadcast Channels
  // ---------------------------------------------
  fun toggleChannelSubscription(channelId: String) {
    _channels.value = _channels.value.map {
      if (it.id == channelId) {
        val newSub = !it.isSubscribed
        it.copy(
          isSubscribed = newSub,
          subscribersCount = if (newSub) it.subscribersCount + 1 else it.subscribersCount - 1
        )
      } else it
    }
  }

  fun toggleChannelMute(channelId: String) {
    _channels.value = _channels.value.map {
      if (it.id == channelId) it.copy(isMuted = !it.isMuted) else it
    }
  }

  fun toggleReaction(postId: String, emoji: String) {
    _posts.value = _posts.value.map { post ->
      if (post.id == postId) {
        val currentReactions = post.reactions.toMutableMap()
        val userCurrent = post.userReaction

        if (userCurrent == emoji) {
          // Remove reaction
          val count = (currentReactions[emoji] ?: 1) - 1
          if (count <= 0) currentReactions.remove(emoji) else currentReactions[emoji] = count
          post.copy(reactions = currentReactions, userReaction = null)
        } else {
          // Switch or add reaction
          if (userCurrent != null) {
            val oldCount = (currentReactions[userCurrent] ?: 1) - 1
            if (oldCount <= 0) currentReactions.remove(userCurrent) else currentReactions[userCurrent] = oldCount
          }
          currentReactions[emoji] = (currentReactions[emoji] ?: 0) + 1
          post.copy(reactions = currentReactions, userReaction = emoji)
        }
      } else post
    }
  }

  fun postToChannel(
    channelId: String,
    content: String,
    author: String,
    fileName: String?,
    fileType: String?,
    imageUrl: String?
  ) {
    val newPost = TelegramPost(
      id = "post_${System.currentTimeMillis()}",
      channelId = channelId,
      authorName = author.ifBlank { "الأستاذ محمد | إدارة المنصة" },
      isVerified = true,
      content = content,
      timestamp = System.currentTimeMillis(),
      imageUrl = imageUrl,
      fileName = fileName,
      fileSize = if (fileName != null) "4.2 MB" else null,
      viewsCount = 1
    )
    _posts.value = listOf(newPost) + _posts.value
  }

  // ---------------------------------------------
  // Student Level & Progress Computation
  // ---------------------------------------------
  fun getStudentStats(): StudentLevelStats {
    val totalVideos = _lessons.value.size
    val completedCount = _watchProgress.value.values.count { it.isCompleted }
    val completionRate = if (totalVideos > 0) completedCount.toFloat() / totalVideos else 0f

    val totalMinutes = _lessons.value.filter {
      _watchProgress.value[it.id]?.isCompleted == true
    }.sumOf { it.durationMinutes }

    val totalHw = _homeworks.value.size
    val subList = _submissions.value
    val gradedSubmissions = subList.filter { it.status == SubmissionStatus.REVIEWED }
    val avgGrade = if (gradedSubmissions.isNotEmpty()) {
      gradedSubmissions.mapNotNull { it.grade }.average().toFloat()
    } else 0f

    // Points system: 50 pts per completed video + 100 pts per reviewed homework
    val points = (completedCount * 50) + (gradedSubmissions.size * 100)

    val (rank, badge) = when {
      completionRate >= 0.8f -> "نخبة الأكاديمية 👑" to "الرتبة الماسية"
      completionRate >= 0.5f -> "طالب متفوق 🌟" to "الرتبة الذهبية"
      completionRate >= 0.25f -> "طالب مجتهد ⚡" to "الرتبة الفضية"
      else -> "طالب مبتدئ 🚀" to "رتبة البداية"
    }

    return StudentLevelStats(
      studentName = _session.value.studentName.ifBlank { "طالب الأكاديمية" },
      totalVideos = totalVideos,
      completedVideos = completedCount,
      completionRate = completionRate,
      totalWatchMinutes = totalMinutes,
      totalHomeworks = totalHw,
      submittedHomeworks = subList.size,
      averageGrade = avgGrade,
      points = points,
      levelRank = rank,
      levelBadge = badge
    )
  }

  // ---------------------------------------------
  // Initial Mock / Seed Data
  // ---------------------------------------------
  private fun loadInitialCodes(): List<ActivationCode> {
    return listOf(
      ActivationCode(
        code = "ELITE-30D",
        durationDays = 30,
        studentName = "كود تجريبي مفتوح 30 يوم",
        createdAt = System.currentTimeMillis() - (86400000L * 2)
      ),
      ActivationCode(
        code = "VIP-60D",
        durationDays = 60,
        studentName = "كود النخبة 60 يوم",
        createdAt = System.currentTimeMillis() - (86400000L * 1)
      ),
      ActivationCode(
        code = "TEST-7D",
        durationDays = 7,
        studentName = "كود تجربة أسبوع 7 أيام",
        createdAt = System.currentTimeMillis()
      ),
      ActivationCode(
        code = "SUPER-90D",
        durationDays = 90,
        studentName = "كود الفصل الدراسي الشامل 90 يوم",
        createdAt = System.currentTimeMillis()
      )
    )
  }

  private fun loadInitialLessons(): List<VideoLesson> {
    return listOf(
      VideoLesson(
        id = "les_1",
        title = "المحاضرة 1: قواعد الاشتقاق المتقدم والتفاضل الضمني",
        subject = "الرياضيات التطبيقية",
        chapter = "الوحدة الأولى: التفاضل والتكامل",
        description = "شرح كامل وتفصيلي مع حل أكثر من 15 مسألة وزارية معقدة وتطبيق القواعد الذهبية للاشتقاق.",
        durationMinutes = 45,
        videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
        pdfTitle = "ملزمة_قواعد_الاشتقاق_الذهبية.pdf",
        dateAdded = "منذ 3 أيام",
        orderIndex = 1
      ),
      VideoLesson(
        id = "les_2",
        title = "المحاضرة 2: تطبيقات القيم العظمى والصغرى والمساحات",
        subject = "الرياضيات التطبيقية",
        chapter = "الوحدة الأولى: التفاضل والتكامل",
        description = "كيف تحل مسائل المعدلات الزمنية المرتبطة ورسم المنحنيات خطوة بخطوة بطريقة هندسية مبسطة.",
        durationMinutes = 52,
        videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
        pdfTitle = "ملخص_تطبيقات_التفاضل_والتكامل.pdf",
        dateAdded = "منذ يومين",
        orderIndex = 2
      ),
      VideoLesson(
        id = "les_3",
        title = "المحاضرة 3: قوانين كيرشوف والدوائر الكهربائية المعقدة",
        subject = "الفيزياء الحديثة",
        chapter = "الوحدة الثانية: الكهرومغناطيسية",
        description = "فهم فيزيائي عميق لفرق الجهد وحساب التيارات في العقد المغلقة وتجنب الأخطاء الشائعة.",
        durationMinutes = 40,
        videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
        pdfTitle = "مخطط_قوانين_كيرشوف_والدوائر.pdf",
        dateAdded = "أمس",
        orderIndex = 3
      ),
      VideoLesson(
        id = "les_4",
        title = "المحاضرة 4: الحث الكهرومغناطيسي وتجربة فاراداي",
        subject = "الفيزياء الحديثة",
        chapter = "الوحدة الثانية: الكهرومغناطيسية",
        description = "شرح ظاهرة الحث المتبادل والحث الذاتي واستنتاج قانون القوة الدافعة الكهربية المستحثة.",
        durationMinutes = 48,
        videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
        pdfTitle = "ملف_تجارب_فاراداي_والحث.pdf",
        dateAdded = "اليوم",
        orderIndex = 4
      ),
      VideoLesson(
        id = "les_5",
        title = "المحاضرة 5: الكيمياء العضوية - الهيدروكربونات وتسمية الألكانات",
        subject = "الكيمياء العامة",
        chapter = "الوحدة الثالثة: الكيمياء العضوية",
        description = "قواعد تسمية الأيوباك (IUPAC) والمتشكلات الجزيئية وحل نماذج أسئلة الربط الصعبة.",
        durationMinutes = 38,
        videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4",
        pdfTitle = "ملزمة_الكيمياء_العضوية_الجزء_الأول.pdf",
        dateAdded = "منذ 4 أيام",
        orderIndex = 5
      ),
      VideoLesson(
        id = "les_6",
        title = "المحاضرة 6: بنك الامتحانات وحل النموذج الوزاري الشامل",
        subject = "المراجعات النهائية",
        chapter = "الوحدة الشاملة: المراجعة النهائية",
        description = "مراجعة مكثفة لمدة 65 دقيقة تشمل أهم 50 سؤال متوقع في الامتحانات النهائية للعام الدراسي.",
        durationMinutes = 65,
        videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyBlazes.mp4",
        pdfTitle = "النموذج_الامتحاني_الشامل_مع_الإجابات.pdf",
        dateAdded = "اليوم",
        orderIndex = 6
      )
    )
  }

  private fun loadInitialProgress(): Map<String, WatchProgress> {
    // Pre-seed some realistic watched state: e.g. Lesson 1 completed 100%, Lesson 2 in progress
    return mapOf(
      "les_1" to WatchProgress(
        lessonId = "les_1",
        watchedSeconds = 45 * 60,
        totalSeconds = 45 * 60,
        isCompleted = true,
        completedAt = System.currentTimeMillis() - 86400000L,
        timesWatched = 1
      ),
      "les_2" to WatchProgress(
        lessonId = "les_2",
        watchedSeconds = 25 * 60,
        totalSeconds = 52 * 60,
        isCompleted = false,
        completedAt = null,
        timesWatched = 0
      )
    )
  }

  private fun loadInitialHomeworks(): List<Homework> {
    return listOf(
      Homework(
        id = "hw_1",
        title = "واجب الرياضيات: حل مسائل الاشتقاق الضمني والمعدلات",
        lessonTitle = "المحاضرة 1: قواعد الاشتقاق المتقدم",
        subject = "الرياضيات",
        instructions = "قم بحل المسائل الخمسة المرفقة في ورقة خارجية بخط واضح وصور الحل أو اكتب الخطوات النهائية هنا.",
        dueDate = "الخميس القادم - الساعة 11 مساءً",
        maxGrade = 10,
        questionsCount = 5,
        attachmentName = "أسئلة_واجب_الاشتقاق_الضمني.pdf"
      ),
      Homework(
        id = "hw_2",
        title = "واجب الفيزياء: تدريبات قانون كيرشوف والدوائر",
        lessonTitle = "المحاضرة 3: قوانين كيرشوف والدوائر",
        subject = "الفيزياء",
        instructions = "احسب شدة التيار المار في كل فرع من فروع الدائرة الكهربية الموضحة بالشكل.",
        dueDate = "الأحد القادم",
        maxGrade = 10,
        questionsCount = 3,
        attachmentName = "مخطط_مسائل_كيرشوف.pdf"
      ),
      Homework(
        id = "hw_3",
        title = "واجب الكيمياء: رسم الصيغ البنائية وتسمية الألكانات",
        lessonTitle = "المحاضرة 5: الكيمياء العضوية",
        subject = "الكيمياء",
        instructions = "اكتب الصيغة البنائية لكل مركب من المركبات التالية ووضح عدد ذرات الكربون والهيدروجين.",
        dueDate = "الثلاثاء القادم",
        maxGrade = 10,
        questionsCount = 4,
        attachmentName = "تدريبات_تسمية_الألكانات.pdf"
      )
    )
  }

  private fun loadInitialSubmissions(): List<HomeworkSubmission> {
    return listOf(
      HomeworkSubmission(
        id = "sub_demo_1",
        homeworkId = "hw_1",
        studentName = "أحمد خالد الشريف",
        studentPhone = "01012345678",
        submittedAt = System.currentTimeMillis() - 86400000L,
        submissionText = "تم حل جميع المسائل بالتفصيل. في المسألة الثالثة تم التعويض بقيمة dy/dx = 4 وحصلنا على الناتج المطلوب 12.",
        attachedFileNames = listOf("حل_واجب_الرياضيات_ص1.jpg", "حل_واجب_الرياضيات_ص2.jpg"),
        status = SubmissionStatus.REVIEWED,
        grade = 10,
        teacherFeedback = "أحسنت يا أحمد، خطوات منظمة وإجابة نموذجية 10/10 بارك الله فيك!"
      )
    )
  }

  private fun loadInitialChannels(): List<TelegramChannel> {
    return listOf(
      TelegramChannel(
        id = "chan_official",
        name = "قناة أكاديمية النخبة الرسمية",
        handle = "@Elite_Academy_Official",
        description = "القناة الرسمية لنشر مواعيد الحصص، الإعلانات الهامة، ومذكرات الشرح بصيغة PDF لجميع المواد.",
        subscribersCount = 2840,
        isSubscribed = true,
        iconEmoji = "📢"
      ),
      TelegramChannel(
        id = "chan_bank",
        name = "بنك الأسئلة والامتحانات السريعة",
        handle = "@Elite_Exam_Bank",
        description = "أسئلة يومية تفاعلية، كويزات سريعة، وأهم المسائل المتوقعة في الامتحانات مع حلولها النموذجية.",
        subscribersCount = 1950,
        isSubscribed = true,
        iconEmoji = "⚡"
      )
    )
  }

  private fun loadInitialPosts(): List<TelegramPost> {
    return listOf(
      TelegramPost(
        id = "post_1",
        channelId = "chan_official",
        authorName = "الأستاذ محمد | إدارة المنصة",
        content = "مرحباً بكم جميعاً طلابنا الأبطال في منصة أكاديمية النخبة! 🌟\nتم بحمد الله رفع المحاضرات الجديدة للأسبوع الحالي وقسم رفع الواجبات متاح الآن. يرجى مشاهدة المحاضرات بتركيز وتسليم الواجب قبل الموعد النهائي.",
        timestamp = System.currentTimeMillis() - 3600000L * 4,
        imageUrl = "https://images.unsplash.com/photo-1516321318423-f06f85e504b3?w=800&q=80",
        fileName = "جدول_المحاضرات_الأسبوعية_2025.pdf",
        fileSize = "2.4 MB",
        viewsCount = 318,
        reactions = mapOf("❤️" to 45, "🔥" to 82, "👏" to 38, "💡" to 19),
        userReaction = "🔥"
      ),
      TelegramPost(
        id = "post_2",
        channelId = "chan_official",
        authorName = "الأستاذ محمد | إدارة المنصة",
        content = "ملزمة المراجعة الذهبية لقواعد الاشتقاق جاهزة للتحميل الآن! 📑\nتتضمن تلخيصاً شاملاً لجميع القوانين مع خرائط ذهنية ملونة لحفظ العلاقات الهامة.",
        timestamp = System.currentTimeMillis() - 3600000L * 8,
        fileName = "ملزمة_المراجعة_الذهبية_تفاضل.pdf",
        fileSize = "5.1 MB",
        viewsCount = 420,
        reactions = mapOf("❤️" to 60, "🔥" to 95, "💡" to 42)
      ),
      TelegramPost(
        id = "post_3",
        channelId = "chan_bank",
        authorName = "فريق الإشراف الأكاديمي",
        content = "تحدي اليوم السريع في الفيزياء! ⚡\nسؤال وزاري ورد في امتحان 2023: احسب المقاومة المكافئة عندما تكون الدائرة في حالة رنين كهربي؟\nشاركوا إجاباتكم في التعليقات وسنقوم برفع الحل النموذجي مساء اليوم.",
        timestamp = System.currentTimeMillis() - 3600000L * 14,
        imageUrl = "https://images.unsplash.com/photo-1635070041078-e363dbe005cb?w=800&q=80",
        viewsCount = 280,
        reactions = mapOf("🔥" to 35, "👏" to 22, "💡" to 58)
      )
    )
  }
}
