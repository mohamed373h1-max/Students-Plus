package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.VideoLesson
import com.example.model.WatchProgress
import com.example.ui.theme.DeepIndigoBlue
import com.example.ui.theme.DeepNavyBlack
import com.example.ui.theme.ElectricSapphire
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GlowBlue
import com.example.ui.theme.LuxuryGold
import com.example.ui.theme.MidnightBlack
import com.example.ui.theme.NeonSkyBlue
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceBorderLight
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceDarkElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun VideosScreen(
  lessons: List<VideoLesson>,
  watchProgress: Map<String, WatchProgress>,
  activeLesson: VideoLesson?,
  isPlaying: Boolean,
  playerSeconds: Int,
  playbackSpeed: Float,
  onOpenPlayer: (VideoLesson) -> Unit,
  onClosePlayer: () -> Unit,
  onTogglePlay: () -> Unit,
  onSeek: (Int) -> Unit,
  onSetSpeed: (Float) -> Unit,
  onMarkComplete: (String, Boolean) -> Unit,
  onDownloadPdf: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  var searchQuery by remember { mutableStateOf("") }
  var selectedSubject by remember { mutableStateOf("الكل") }

  val subjects = listOf("الكل", "الرياضيات التطبيقية", "الفيزياء الحديثة", "الكيمياء العامة", "المراجعات النهائية")

  val filteredLessons = lessons.filter { lesson ->
    val matchesSearch = lesson.title.contains(searchQuery, ignoreCase = true) ||
      lesson.description.contains(searchQuery, ignoreCase = true) ||
      lesson.subject.contains(searchQuery, ignoreCase = true)
    val matchesSubject = selectedSubject == "الكل" || lesson.subject == selectedSubject
    matchesSearch && matchesSubject
  }

  val completedCount = watchProgress.values.count { it.isCompleted }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(ObsidianBlack)
  ) {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      item {
        Spacer(modifier = Modifier.height(12.dp))

        // Progress summary banner
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, ElectricSapphire.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
          colors = CardDefaults.cardColors(containerColor = SurfaceDark),
          shape = RoundedCornerShape(16.dp)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .background(
                brush = Brush.horizontalGradient(
                  colors = listOf(
                    DeepIndigoBlue.copy(alpha = 0.4f),
                    SurfaceDark
                  )
                )
              )
              .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(ElectricSapphire.copy(alpha = 0.2f))
                .border(1.dp, NeonSkyBlue, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                Icons.Filled.Stars,
                contentDescription = null,
                tint = LuxuryGold,
                modifier = Modifier.size(26.dp)
              )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "مستوى إنجاز الفيديوهات والشروحات",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
              )
              Text(
                text = "أكملت مشاهدة $completedCount من ${lessons.size} فيديو بالكامل (100%)",
                fontSize = 12.sp,
                color = NeonSkyBlue,
                modifier = Modifier.padding(top = 2.dp)
              )
              Spacer(modifier = Modifier.height(6.dp))
              val progress = if (lessons.isNotEmpty()) completedCount.toFloat() / lessons.size else 0f
              LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                  .fillMaxWidth()
                  .height(6.dp)
                  .clip(RoundedCornerShape(3.dp)),
                color = ElectricSapphire,
                trackColor = SurfaceBorder
              )
            }
          }
        }
      }

      // Search field
      item {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = { Text("ابحث في الشروحات والمحاضرات...") },
          leadingIcon = {
            Icon(Icons.Filled.Search, contentDescription = null, tint = GlowBlue)
          },
          singleLine = true,
          shape = RoundedCornerShape(14.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = NeonSkyBlue,
            unfocusedBorderColor = SurfaceBorder,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            focusedContainerColor = SurfaceDark,
            unfocusedContainerColor = SurfaceDark
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("videos_search_field")
        )
      }

      // Subject Filter Chips
      item {
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          items(subjects) { subject ->
            val isSelected = selectedSubject == subject
            FilterChip(
              selected = isSelected,
              onClick = { selectedSubject = subject },
              label = {
                Text(
                  text = subject,
                  fontSize = 12.sp,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
              },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = ElectricSapphire,
                selectedLabelColor = Color.White,
                containerColor = SurfaceDark,
                labelColor = TextSecondary
              ),
              border = FilterChipDefaults.filterChipBorder(
                borderColor = if (isSelected) NeonSkyBlue else SurfaceBorder,
                selectedBorderColor = NeonSkyBlue,
                enabled = true,
                selected = isSelected
              )
            )
          }
        }
      }

      // Lessons List
      items(filteredLessons) { lesson ->
        val progress = watchProgress[lesson.id]
        val isCompleted = progress?.isCompleted == true

        Card(
          modifier = Modifier
            .fillMaxWidth()
            .border(
              1.dp,
              if (isCompleted) EmeraldSuccess.copy(alpha = 0.5f) else SurfaceBorder,
              RoundedCornerShape(16.dp)
            )
            .testTag("lesson_card_${lesson.id}"),
          colors = CardDefaults.cardColors(containerColor = SurfaceDarkElevated),
          shape = RoundedCornerShape(16.dp)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            // Subject & Duration Badges
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(DeepIndigoBlue.copy(alpha = 0.35f))
                  .padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Text(
                  text = lesson.subject,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = NeonSkyBlue
                )
              }

              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  Icons.Filled.AccessTime,
                  contentDescription = null,
                  tint = TextMuted,
                  modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "${lesson.durationMinutes} دقيقة",
                  fontSize = 11.sp,
                  color = TextSecondary
                )
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Lesson Title
            Text(
              text = lesson.title,
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = TextPrimary
            )

            // Chapter
            Text(
              text = lesson.chapter,
              fontSize = 12.sp,
              color = TextMuted,
              modifier = Modifier.padding(top = 2.dp)
            )

            // Description
            Text(
              text = lesson.description,
              fontSize = 12.sp,
              color = TextSecondary,
              maxLines = 2,
              overflow = TextOverflow.Ellipsis,
              modifier = Modifier.padding(top = 6.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Status Bar & PDF preview
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              if (isCompleted) {
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(EmeraldSuccess.copy(alpha = 0.15f))
                    .border(1.dp, EmeraldSuccess.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      Icons.Filled.CheckCircle,
                      contentDescription = null,
                      tint = EmeraldSuccess,
                      modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                      text = "تمت المشاهدة بالكامل 100%",
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold,
                      color = EmeraldSuccess
                    )
                  }
                }
              } else if (progress != null && progress.watchedSeconds > 0) {
                val watchedMin = progress.watchedSeconds / 60
                Text(
                  text = "شاهدت $watchedMin من ${lesson.durationMinutes} دقيقة",
                  fontSize = 11.sp,
                  color = NeonSkyBlue
                )
              } else {
                Text(
                  text = "لم يبدأ بعد",
                  fontSize = 11.sp,
                  color = TextMuted
                )
              }

              // PDF Attachment Chip
              Row(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(SurfaceDark)
                  .clickable { onDownloadPdf(lesson.pdfTitle) }
                  .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  Icons.Filled.Description,
                  contentDescription = null,
                  tint = GlowBlue,
                  modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "الملزمة PDF",
                  fontSize = 10.sp,
                  color = TextSecondary
                )
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Watch Button
            Button(
              onClick = { onOpenPlayer(lesson) },
              modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .testTag("play_lesson_${lesson.id}"),
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = if (isCompleted) DeepIndigoBlue else ElectricSapphire
              )
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  Icons.Filled.PlayArrow,
                  contentDescription = null,
                  tint = Color.White,
                  modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = if (isCompleted) "إعادة مشاهدة المحاضرة" else "مشاهدة الشرح الآن",
                  style = MaterialTheme.typography.titleSmall,
                  fontWeight = FontWeight.Bold,
                  color = Color.White
                )
              }
            }
          }
        }
      }

      item {
        Spacer(modifier = Modifier.height(24.dp))
      }
    }

    // Modal Video Player Dialog
    if (activeLesson != null) {
      VideoPlayerDialog(
        lesson = activeLesson,
        isPlaying = isPlaying,
        currentSeconds = playerSeconds,
        playbackSpeed = playbackSpeed,
        isCompleted = watchProgress[activeLesson.id]?.isCompleted == true,
        onClose = onClosePlayer,
        onTogglePlay = onTogglePlay,
        onSeek = onSeek,
        onSetSpeed = onSetSpeed,
        onMarkComplete = { onMarkComplete(activeLesson.id, it) },
        onDownloadPdf = onDownloadPdf
      )
    }
  }
}

@Composable
fun VideoPlayerDialog(
  lesson: VideoLesson,
  isPlaying: Boolean,
  currentSeconds: Int,
  playbackSpeed: Float,
  isCompleted: Boolean,
  onClose: () -> Unit,
  onTogglePlay: () -> Unit,
  onSeek: (Int) -> Unit,
  onSetSpeed: (Float) -> Unit,
  onMarkComplete: (Boolean) -> Unit,
  onDownloadPdf: (String) -> Unit
) {
  val totalSec = lesson.durationMinutes * 60

  Dialog(
    onDismissRequest = onClose,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxSize()
        .background(ObsidianBlack),
      color = ObsidianBlack
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp)
      ) {
        // Player Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(
            onClick = onClose,
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
              .background(SurfaceDarkElevated)
              .testTag("close_player_button")
          ) {
            Icon(Icons.Filled.Close, contentDescription = "إغلاق", tint = TextPrimary)
          }

          Text(
            text = "مشغل المحاضرات الذكي",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = NeonSkyBlue
          )

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(ElectricSapphire.copy(alpha = 0.2f))
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Text("${playbackSpeed}x", fontSize = 12.sp, color = NeonSkyBlue, fontWeight = FontWeight.Bold)
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Video Viewport Canvas
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(230.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(
              brush = Brush.radialGradient(
                colors = listOf(
                  DeepIndigoBlue.copy(alpha = 0.8f),
                  MidnightBlack,
                  ObsidianBlack
                )
              )
            )
            .border(1.5.dp, ElectricSapphire.copy(alpha = 0.7f), RoundedCornerShape(18.dp)),
          contentAlignment = Alignment.Center
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
          ) {
            Box(
              modifier = Modifier
                .size(68.dp)
                .clip(CircleShape)
                .background(ElectricSapphire.copy(alpha = 0.3f))
                .border(2.dp, NeonSkyBlue, CircleShape)
                .clickable { onTogglePlay() },
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                contentDescription = if (isPlaying) "إيقاف" else "تشغيل",
                tint = Color.White,
                modifier = Modifier.size(38.dp)
              )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
              text = if (isPlaying) "جاري تشغيل الفيديو بدقة 1080p FHD" else "فيديو الشرح متوقف مؤقتاً",
              style = MaterialTheme.typography.bodySmall,
              color = NeonSkyBlue
            )

            Text(
              text = lesson.title,
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.Bold,
              color = TextPrimary,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
              modifier = Modifier.padding(top = 4.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Scrubber Slider
        Slider(
          value = currentSeconds.toFloat(),
          onValueChange = { onSeek(it.toInt()) },
          valueRange = 0f..totalSec.toFloat().coerceAtLeast(1f),
          colors = SliderDefaults.colors(
            thumbColor = NeonSkyBlue,
            activeTrackColor = ElectricSapphire,
            inactiveTrackColor = SurfaceBorder
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("video_player_slider")
        )

        // Time Counter
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          val curM = currentSeconds / 60
          val curS = currentSeconds % 60
          val totM = totalSec / 60
          val totS = totalSec % 60
          Text(
            text = String.format("%02d:%02d", curM, curS),
            fontSize = 12.sp,
            color = NeonSkyBlue,
            fontWeight = FontWeight.SemiBold
          )
          Text(
            text = String.format("%02d:%02d", totM, totS),
            fontSize = 12.sp,
            color = TextSecondary
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Playback Controls Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceEvenly,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Rewind 10s
          IconButton(
            onClick = { onSeek((currentSeconds - 10).coerceAtLeast(0)) },
            modifier = Modifier.size(46.dp)
          ) {
            Icon(Icons.Filled.FastRewind, contentDescription = "-10 ثواني", tint = GlowBlue)
          }

          // Big Play/Pause
          IconButton(
            onClick = onTogglePlay,
            modifier = Modifier
              .size(56.dp)
              .clip(CircleShape)
              .background(ElectricSapphire)
          ) {
            Icon(
              imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
              contentDescription = null,
              tint = Color.White,
              modifier = Modifier.size(32.dp)
            )
          }

          // Forward 10s
          IconButton(
            onClick = { onSeek((currentSeconds + 10).coerceAtMost(totalSec)) },
            modifier = Modifier.size(46.dp)
          ) {
            Icon(Icons.Filled.FastForward, contentDescription = "+10 ثواني", tint = GlowBlue)
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Speed options
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(text = "سرعة التشغيل:", fontSize = 12.sp, color = TextSecondary)
          Spacer(modifier = Modifier.width(8.dp))
          listOf(1.0f, 1.25f, 1.5f, 2.0f).forEach { speed ->
            val isCurrent = playbackSpeed == speed
            Box(
              modifier = Modifier
                .padding(horizontal = 4.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(if (isCurrent) ElectricSapphire else SurfaceDark)
                .clickable { onSetSpeed(speed) }
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text(
                text = "${speed}x",
                fontSize = 11.sp,
                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                color = if (isCurrent) Color.White else TextSecondary
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 100% Complete Action Card
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .border(
              1.dp,
              if (isCompleted) EmeraldSuccess else SurfaceBorder,
              RoundedCornerShape(14.dp)
            ),
          colors = CardDefaults.cardColors(containerColor = SurfaceDarkElevated),
          shape = RoundedCornerShape(14.dp)
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = "تأكيد سماع المحاضرة بالكامل (100%)",
                  style = MaterialTheme.typography.titleSmall,
                  fontWeight = FontWeight.Bold,
                  color = TextPrimary
                )
                Text(
                  text = if (isCompleted) "✓ مسجل في تقريرك الأكاديمي كمكتمل" else "اضغط لاحتساب المحاضرة في مستواك (+50 نقطة)",
                  fontSize = 11.sp,
                  color = if (isCompleted) EmeraldSuccess else TextSecondary
                )
              }

              Button(
                onClick = { onMarkComplete(!isCompleted) },
                colors = ButtonDefaults.buttonColors(
                  containerColor = if (isCompleted) EmeraldSuccess else ElectricSapphire
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("toggle_complete_button")
              ) {
                Text(
                  text = if (isCompleted) "مكتمل ✓" else "تأكيد الإكمال",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // PDF Download button inside player
            OutlinedButton(
              onClick = { onDownloadPdf(lesson.pdfTitle) },
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(10.dp),
              border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder)
            ) {
              Icon(Icons.Filled.Description, contentDescription = null, tint = GlowBlue, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "تحميل ${lesson.pdfTitle}",
                fontSize = 12.sp,
                color = TextPrimary
              )
            }
          }
        }
      }
    }
  }
}
