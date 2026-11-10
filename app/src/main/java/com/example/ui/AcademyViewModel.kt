package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AcademyRepository
import com.example.model.ActivationCode
import com.example.model.Homework
import com.example.model.HomeworkSubmission
import com.example.model.StudentLevelStats
import com.example.model.TelegramChannel
import com.example.model.TelegramPost
import com.example.model.UserSession
import com.example.model.VideoLesson
import com.example.model.WatchProgress
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AcademyTab(val titleAr: String, val testTag: String) {
  VIDEOS("الشروحات", "tab_videos"),
  LEVEL("مستوى الطالب", "tab_level"),
  HOMEWORK("الواجبات", "tab_homework"),
  CHANNEL("قناة البث", "tab_channel"),
  OWNER("لوحة المالك", "tab_owner")
}

class AcademyViewModel(application: Application) : AndroidViewModel(application) {

  private val repository = AcademyRepository(application.applicationContext)

  val session: StateFlow<UserSession> = repository.session
  val activationCodes: StateFlow<List<ActivationCode>> = repository.activationCodes
  val lessons: StateFlow<List<VideoLesson>> = repository.lessons
  val watchProgress: StateFlow<Map<String, WatchProgress>> = repository.watchProgress
  val homeworks: StateFlow<List<Homework>> = repository.homeworks
  val submissions: StateFlow<List<HomeworkSubmission>> = repository.submissions
  val channels: StateFlow<List<TelegramChannel>> = repository.channels
  val posts: StateFlow<List<TelegramPost>> = repository.posts

  // UI Navigation State
  private val _selectedTab = MutableStateFlow(AcademyTab.VIDEOS)
  val selectedTab: StateFlow<AcademyTab> = _selectedTab.asStateFlow()

  // Video Player state
  private val _activeLesson = MutableStateFlow<VideoLesson?>(null)
  val activeLesson: StateFlow<VideoLesson?> = _activeLesson.asStateFlow()

  private val _isPlaying = MutableStateFlow(false)
  val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

  private val _playerSeconds = MutableStateFlow(0)
  val playerSeconds: StateFlow<Int> = _playerSeconds.asStateFlow()

  private val _playbackSpeed = MutableStateFlow(1.0f)
  val playbackSpeed: StateFlow<Float> = _playbackSpeed.asStateFlow()

  private var playbackTickerJob: Job? = null

  // Active Channel in Telegram view
  private val _selectedChannelId = MutableStateFlow("chan_official")
  val selectedChannelId: StateFlow<String> = _selectedChannelId.asStateFlow()

  // Search queries
  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

  private val _selectedSubjectFilter = MutableStateFlow("الكل")
  val selectedSubjectFilter: StateFlow<String> = _selectedSubjectFilter.asStateFlow()

  // Alert & SnackBar Message
  private val _snackBarMessage = MutableStateFlow<String?>(null)
  val snackBarMessage: StateFlow<String?> = _snackBarMessage.asStateFlow()

  // Student Stats flow
  val studentStats: StateFlow<StudentLevelStats> = combine(
    lessons,
    watchProgress,
    homeworks,
    submissions,
    session
  ) { _, _, _, _, _ ->
    repository.getStudentStats()
  }.stateIn(
    viewModelScope,
    SharingStarted.WhileSubscribed(5000),
    repository.getStudentStats()
  )

  fun selectTab(tab: AcademyTab) {
    _selectedTab.value = tab
  }

  fun setSearchQuery(query: String) {
    _searchQuery.value = query
  }

  fun setSubjectFilter(subject: String) {
    _selectedSubjectFilter.value = subject
  }

  fun clearSnackBarMessage() {
    _snackBarMessage.value = null
  }

  fun showMessage(msg: String) {
    _snackBarMessage.value = msg
  }

  // ---------------------------------------------
  // Activation & Login Actions
  // ---------------------------------------------
  fun activateCode(code: String, studentName: String, phone: String, onSuccess: () -> Unit) {
    val result = repository.activateWithCode(code, studentName, phone)
    if (result.isSuccess) {
      showMessage("تم تفعيل اشتراكك بنجاح! أهلاً بك في منصة النخبة 🚀")
      onSuccess()
    } else {
      showMessage(result.exceptionOrNull()?.message ?: "خطأ في تفعيل الكود")
    }
  }

  fun loginOwner(pin: String, onSuccess: () -> Unit) {
    val success = repository.loginAsOwner(pin)
    if (success) {
      showMessage("مرحباً بك يا أستاذنا الفاضل! تم تسجيل الدخول كمالك للمنصة 👑")
      _selectedTab.value = AcademyTab.OWNER
      onSuccess()
    } else {
      showMessage("رمز المالك السري غير صحيح! يرجى التأكد من الرمز والمحاولة مرة أخرى.")
    }
  }

  fun updateOwnerPin(newPin: String): Boolean {
    val success = repository.updateOwnerPin(newPin)
    if (success) {
      showMessage("تم تغيير رمز المالك السري بنجاح وحفظه بأمان 🔒")
    } else {
      showMessage("يجب أن يتكون الرمز السري من 4 أرقام على الأقل!")
    }
    return success
  }

  fun logout() {
    repository.logout()
    _selectedTab.value = AcademyTab.VIDEOS
    stopPlayback()
    showMessage("تم تسجيل الخروج بنجاح")
  }

  // ---------------------------------------------
  // Owner Code Generation
  // ---------------------------------------------
  fun generateNewCode(days: Int, studentNote: String) {
    val created = repository.generateActivationCode(days, studentNote)
    showMessage("تم توليد كود جديد: ${created.code} لمدة $days يوم ✨")
  }

  fun toggleCodeStatus(code: String) {
    repository.toggleCodeStatus(code)
    showMessage("تم تحديث حالة الكود")
  }

  // ---------------------------------------------
  // Video Player Logic
  // ---------------------------------------------
  fun openLessonPlayer(lesson: VideoLesson) {
    _activeLesson.value = lesson
    val progress = repository.watchProgress.value[lesson.id]
    _playerSeconds.value = progress?.watchedSeconds ?: 0
    _isPlaying.value = true
    startPlaybackTicker()
  }

  fun closeLessonPlayer() {
    _activeLesson.value?.let { lesson ->
      val totalSec = lesson.durationMinutes * 60
      repository.updateWatchSeconds(lesson.id, _playerSeconds.value, totalSec)
    }
    stopPlayback()
    _activeLesson.value = null
  }

  fun togglePlayPause() {
    if (_isPlaying.value) {
      stopPlayback()
    } else {
      _isPlaying.value = true
      startPlaybackTicker()
    }
  }

  fun seekTo(seconds: Int) {
    _activeLesson.value?.let { lesson ->
      val maxSec = lesson.durationMinutes * 60
      _playerSeconds.value = seconds.coerceIn(0, maxSec)
      repository.updateWatchSeconds(lesson.id, _playerSeconds.value, maxSec)
    }
  }

  fun setSpeed(speed: Float) {
    _playbackSpeed.value = speed
  }

  fun markLessonComplete(lessonId: String, completed: Boolean) {
    repository.markVideoWatched(lessonId, completed)
    if (completed) {
      showMessage("تهانينا! تم احتساب مشاهدة الفيديو بنجاح وحصلت على +50 نقطة 🎉")
    }
  }

  private fun startPlaybackTicker() {
    playbackTickerJob?.cancel()
    playbackTickerJob = viewModelScope.launch {
      while (_isPlaying.value) {
        val delayMillis = (1000L / _playbackSpeed.value).toLong()
        delay(delayMillis)
        _activeLesson.value?.let { lesson ->
          val totalSec = lesson.durationMinutes * 60
          if (_playerSeconds.value < totalSec) {
            _playerSeconds.value += 1
            if (_playerSeconds.value >= totalSec) {
              // Reached 100%
              repository.markVideoWatched(lesson.id, true)
              showMessage("أحسنت! أكملت مشاهدة المحاضرة بالكامل 100% 🎓")
              _isPlaying.value = false
            } else if (_playerSeconds.value % 10 == 0) {
              repository.updateWatchSeconds(lesson.id, _playerSeconds.value, totalSec)
            }
          } else {
            _isPlaying.value = false
          }
        }
      }
    }
  }

  private fun stopPlayback() {
    _isPlaying.value = false
    playbackTickerJob?.cancel()
    playbackTickerJob = null
  }

  // ---------------------------------------------
  // Homework submission & grading
  // ---------------------------------------------
  fun submitHomework(homeworkId: String, text: String, fileNames: List<String>) {
    val currentSession = session.value
    val studentName = currentSession.studentName.ifBlank { "طالب النخبة" }
    val studentPhone = currentSession.phoneNumber.ifBlank { "غير مسجل" }

    repository.submitHomework(homeworkId, studentName, studentPhone, text, fileNames)
    showMessage("تم تسليم الواجب بنجاح! سيقوم الأستاذ بمراجعته وتقييمه قريباً 📝")
  }

  fun gradeSubmission(submissionId: String, grade: Int, feedback: String) {
    repository.gradeSubmission(submissionId, grade, feedback)
    showMessage("تم تقييم الواجب بنجاح وإرسال الملاحظات للطالب ⭐")
  }

  // ---------------------------------------------
  // Telegram Channel Actions
  // ---------------------------------------------
  fun selectChannel(channelId: String) {
    _selectedChannelId.value = channelId
  }

  fun toggleChannelSubscription(channelId: String) {
    repository.toggleChannelSubscription(channelId)
    val channel = repository.channels.value.find { it.id == channelId }
    if (channel?.isSubscribed == true) {
      showMessage("تم إلغاء الاشتراك من القناة")
    } else {
      showMessage("تم الاشتراك في القناة بنجاح! ستصلك التنبيهات 🔔")
    }
  }

  fun toggleChannelMute(channelId: String) {
    repository.toggleChannelMute(channelId)
  }

  fun toggleReaction(postId: String, emoji: String) {
    repository.toggleReaction(postId, emoji)
  }

  fun postBroadcast(channelId: String, text: String, fileName: String?, imageUrl: String?) {
    val author = if (session.value.isOwner) "الأستاذ محمد | إدارة المنصة" else "مشرف المنصة"
    repository.postToChannel(
      channelId = channelId,
      content = text,
      author = author,
      fileName = fileName,
      fileType = if (fileName != null) "PDF" else null,
      imageUrl = imageUrl
    )
    showMessage("تم نشر الرسالة في القناة بنجاح 📢")
  }

  // ---------------------------------------------
  // Owner Add Lesson
  // ---------------------------------------------
  fun addNewLesson(
    title: String,
    subject: String,
    chapter: String,
    description: String,
    durationMinutes: Int,
    videoUrl: String,
    pdfTitle: String
  ) {
    repository.addLesson(title, subject, chapter, description, durationMinutes, videoUrl, pdfTitle)
    showMessage("تم إضافة محاضرة الفيديو الجديدة بنجاح! 🎬")
  }
}
