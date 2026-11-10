package com.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.AcademyTab
import com.example.ui.AcademyViewModel
import com.example.ui.components.LuxuryBottomNavigationBar
import com.example.ui.components.LuxuryTopAppBar
import com.example.ui.screens.ActivationScreen
import com.example.ui.screens.HomeworkScreen
import com.example.ui.screens.OwnerPortalScreen
import com.example.ui.screens.StudentLevelScreen
import com.example.ui.screens.TelegramChannelScreen
import com.example.ui.screens.VideosScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.ObsidianBlack
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

  private val viewModel: AcademyViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    setContent {
      MyApplicationTheme(darkTheme = true, dynamicColor = false) {
        // Enforce Right-to-Left (RTL) for Arabic experience
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
          AcademyApp(viewModel = viewModel)
        }
      }
    }
  }
}

@Composable
fun AcademyApp(viewModel: AcademyViewModel) {
  val context = LocalContext.current
  val session by viewModel.session.collectAsStateWithLifecycle()
  val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
  val activationCodes by viewModel.activationCodes.collectAsStateWithLifecycle()
  val lessons by viewModel.lessons.collectAsStateWithLifecycle()
  val watchProgress by viewModel.watchProgress.collectAsStateWithLifecycle()
  val homeworks by viewModel.homeworks.collectAsStateWithLifecycle()
  val submissions by viewModel.submissions.collectAsStateWithLifecycle()
  val channels by viewModel.channels.collectAsStateWithLifecycle()
  val posts by viewModel.posts.collectAsStateWithLifecycle()
  val studentStats by viewModel.studentStats.collectAsStateWithLifecycle()

  val activeLesson by viewModel.activeLesson.collectAsStateWithLifecycle()
  val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()
  val playerSeconds by viewModel.playerSeconds.collectAsStateWithLifecycle()
  val playbackSpeed by viewModel.playbackSpeed.collectAsStateWithLifecycle()
  val selectedChannelId by viewModel.selectedChannelId.collectAsStateWithLifecycle()
  val snackBarMessage by viewModel.snackBarMessage.collectAsStateWithLifecycle()

  val snackbarHostState = remember { SnackbarHostState() }
  val scope = rememberCoroutineScope()

  // Handle Toast / SnackBar messages
  LaunchedEffect(snackBarMessage) {
    snackBarMessage?.let { msg ->
      scope.launch {
        snackbarHostState.showSnackbar(msg)
      }
      viewModel.clearSnackBarMessage()
    }
  }

  // Handle hardware Back button
  BackHandler(enabled = activeLesson != null || selectedTab != AcademyTab.VIDEOS) {
    if (activeLesson != null) {
      viewModel.closeLessonPlayer()
    } else if (selectedTab != AcademyTab.VIDEOS) {
      viewModel.selectTab(AcademyTab.VIDEOS)
    }
  }

  val onDownloadFile: (String) -> Unit = { fileName ->
    Toast.makeText(context, "جاري تنزيل الملف: $fileName 📥", Toast.LENGTH_SHORT).show()
  }

  if (!session.isSubscriptionActive) {
    // Show Code Activation Screen when user does not have an active code or subscription
    ActivationScreen(
      onActivate = { code, name, phone ->
        viewModel.activateCode(code, name, phone) {
          // Success callback
        }
      },
      onOwnerLogin = { pin ->
        viewModel.loginOwner(pin) {
          // Success callback
        }
      },
      modifier = Modifier
        .fillMaxSize()
        .windowInsetsPadding(WindowInsets.statusBars)
        .windowInsetsPadding(WindowInsets.navigationBars)
    )
  } else {
    // Full Educational Platform Dashboard
    Scaffold(
      modifier = Modifier
        .fillMaxSize()
        .background(ObsidianBlack),
      topBar = {
        LuxuryTopAppBar(
          session = session,
          onOwnerClick = {
            if (session.isOwner) {
              viewModel.selectTab(AcademyTab.OWNER)
            } else {
              viewModel.showMessage("للوصول للوحة المالك يرجى تسجيل الدخول كمعلم من شاشة الدخول")
            }
          },
          onLogoutClick = { viewModel.logout() },
          modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)
        )
      },
      bottomBar = {
        LuxuryBottomNavigationBar(
          currentTab = selectedTab,
          onTabSelected = { viewModel.selectTab(it) },
          isOwner = session.isOwner,
          modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
        )
      },
      snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
      containerColor = ObsidianBlack
    ) { innerPadding ->
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding)
      ) {
        when (selectedTab) {
          AcademyTab.VIDEOS -> {
            VideosScreen(
              lessons = lessons,
              watchProgress = watchProgress,
              activeLesson = activeLesson,
              isPlaying = isPlaying,
              playerSeconds = playerSeconds,
              playbackSpeed = playbackSpeed,
              onOpenPlayer = { viewModel.openLessonPlayer(it) },
              onClosePlayer = { viewModel.closeLessonPlayer() },
              onTogglePlay = { viewModel.togglePlayPause() },
              onSeek = { viewModel.seekTo(it) },
              onSetSpeed = { viewModel.setSpeed(it) },
              onMarkComplete = { id, comp -> viewModel.markLessonComplete(id, comp) },
              onDownloadPdf = onDownloadFile
            )
          }

          AcademyTab.LEVEL -> {
            StudentLevelScreen(
              session = session,
              stats = studentStats,
              lessons = lessons,
              watchProgress = watchProgress
            )
          }

          AcademyTab.HOMEWORK -> {
            HomeworkScreen(
              homeworks = homeworks,
              submissions = submissions,
              onSubmitHomework = { hwId, text, files ->
                viewModel.submitHomework(hwId, text, files)
              },
              onDownloadAttachment = onDownloadFile
            )
          }

          AcademyTab.CHANNEL -> {
            TelegramChannelScreen(
              channels = channels,
              posts = posts,
              selectedChannelId = selectedChannelId,
              isOwner = session.isOwner,
              onSelectChannel = { viewModel.selectChannel(it) },
              onToggleSubscribe = { viewModel.toggleChannelSubscription(it) },
              onToggleMute = { viewModel.toggleChannelMute(it) },
              onToggleReaction = { postId, emoji -> viewModel.toggleReaction(postId, emoji) },
              onBroadcastPost = { chId, text, file, img ->
                viewModel.postBroadcast(chId, text, file, img)
              },
              onDownloadFile = onDownloadFile
            )
          }

          AcademyTab.OWNER -> {
            OwnerPortalScreen(
              activationCodes = activationCodes,
              submissions = submissions,
              onGenerateCode = { days, note -> viewModel.generateNewCode(days, note) },
              onToggleCodeStatus = { viewModel.toggleCodeStatus(it) },
              onAddNewLesson = { t, s, c, d, dur, u, pdf ->
                viewModel.addNewLesson(t, s, c, d, dur, u, pdf)
              },
              onGradeSubmission = { id, gr, fb -> viewModel.gradeSubmission(id, gr, fb) },
              onChangePin = { viewModel.updateOwnerPin(it) },
              onLogout = { viewModel.logout() },
              onShowMessage = { viewModel.showMessage(it) }
            )
          }
        }
      }
    }
  }
}
