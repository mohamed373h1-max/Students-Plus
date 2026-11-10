package com.example.model

enum class SubmissionStatus {
  SUBMITTED,
  REVIEWED
}

data class ActivationCode(
  val code: String,
  val durationDays: Int,
  val studentName: String = "",
  val createdAt: Long = System.currentTimeMillis(),
  val activatedAt: Long? = null,
  val isUsed: Boolean = false,
  val isActive: Boolean = true
) {
  val remainingDays: Int
    get() {
      if (activatedAt == null) return durationDays
      val elapsedMillis = System.currentTimeMillis() - activatedAt
      val elapsedDays = (elapsedMillis / (1000 * 60 * 60 * 24)).toInt()
      val left = durationDays - elapsedDays
      return if (left > 0) left else 0
    }

  val isExpired: Boolean
    get() = activatedAt != null && remainingDays <= 0
}

data class UserSession(
  val isActivated: Boolean = false,
  val studentName: String = "",
  val phoneNumber: String = "",
  val activationCode: String = "",
  val activatedTimestamp: Long = 0L,
  val durationDays: Int = 30,
  val isOwner: Boolean = false
) {
  val remainingDays: Int
    get() {
      if (isOwner) return 999
      if (activatedTimestamp == 0L) return 0
      val elapsedMillis = System.currentTimeMillis() - activatedTimestamp
      val elapsedDays = (elapsedMillis / (1000 * 60 * 60 * 24)).toInt()
      val left = durationDays - elapsedDays
      return if (left > 0) left else 0
    }

  val isSubscriptionActive: Boolean
    get() = isOwner || (isActivated && remainingDays > 0)
}

data class VideoLesson(
  val id: String,
  val title: String,
  val subject: String,
  val chapter: String,
  val description: String,
  val durationMinutes: Int,
  val videoUrl: String,
  val pdfTitle: String = "ملخص الدرس والتمارين PDF",
  val pdfUrl: String = "https://example.com/notes.pdf",
  val dateAdded: String = "منذ يومين",
  val orderIndex: Int = 0
)

data class WatchProgress(
  val lessonId: String,
  val watchedSeconds: Int = 0,
  val totalSeconds: Int = 0,
  val isCompleted: Boolean = false,
  val completedAt: Long? = null,
  val timesWatched: Int = 0
)

data class Homework(
  val id: String,
  val title: String,
  val lessonTitle: String,
  val subject: String,
  val instructions: String,
  val dueDate: String,
  val maxGrade: Int = 10,
  val questionsCount: Int = 5,
  val attachmentName: String? = "نموذج_الواجب_والأسئلة.pdf"
)

data class HomeworkSubmission(
  val id: String,
  val homeworkId: String,
  val studentName: String,
  val studentPhone: String,
  val submittedAt: Long = System.currentTimeMillis(),
  val submissionText: String,
  val attachedFileNames: List<String> = emptyList(),
  val status: SubmissionStatus = SubmissionStatus.SUBMITTED,
  val grade: Int? = null,
  val teacherFeedback: String? = null
)

data class TelegramPost(
  val id: String,
  val channelId: String,
  val authorName: String = "الأستاذ محمد | إدارة المنصة",
  val isVerified: Boolean = true,
  val content: String,
  val timestamp: Long = System.currentTimeMillis(),
  val imageUrl: String? = null,
  val fileName: String? = null,
  val fileSize: String? = null,
  val viewsCount: Int = 142,
  val reactions: Map<String, Int> = mapOf("❤️" to 24, "🔥" to 45, "👏" to 19, "💡" to 12),
  val userReaction: String? = null
)

data class TelegramChannel(
  val id: String,
  val name: String,
  val handle: String,
  val description: String,
  val subscribersCount: Int = 1250,
  val isSubscribed: Boolean = true,
  val isMuted: Boolean = false,
  val iconEmoji: String = "📢"
)

data class StudentLevelStats(
  val studentName: String,
  val totalVideos: Int,
  val completedVideos: Int,
  val completionRate: Float,
  val totalWatchMinutes: Int,
  val totalHomeworks: Int,
  val submittedHomeworks: Int,
  val averageGrade: Float,
  val points: Int,
  val levelRank: String,
  val levelBadge: String
)
