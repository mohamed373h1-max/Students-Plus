package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.StudentLevelStats
import com.example.model.UserSession
import com.example.model.VideoLesson
import com.example.model.WatchProgress
import com.example.ui.theme.DeepIndigoBlue
import com.example.ui.theme.ElectricSapphire
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GlowBlue
import com.example.ui.theme.LuxuryGold
import com.example.ui.theme.MidnightBlack
import com.example.ui.theme.NeonSkyBlue
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceDarkElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun StudentLevelScreen(
  session: UserSession,
  stats: StudentLevelStats,
  lessons: List<VideoLesson>,
  watchProgress: Map<String, WatchProgress>,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxSize()
      .background(ObsidianBlack)
  ) {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      item {
        Spacer(modifier = Modifier.height(12.dp))

        // Student VIP Profile Card
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, LuxuryGold.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
            .testTag("student_profile_card"),
          colors = CardDefaults.cardColors(containerColor = SurfaceDarkElevated),
          shape = RoundedCornerShape(20.dp)
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .background(
                brush = Brush.verticalGradient(
                  colors = listOf(
                    DeepIndigoBlue.copy(alpha = 0.5f),
                    SurfaceDarkElevated
                  )
                )
              )
              .padding(18.dp)
          ) {
            Column {
              Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Box(
                    modifier = Modifier
                      .size(54.dp)
                      .clip(CircleShape)
                      .background(
                        brush = Brush.radialGradient(
                          colors = listOf(LuxuryGold, DeepIndigoBlue)
                        )
                      )
                      .border(2.dp, LuxuryGold, CircleShape),
                    contentAlignment = Alignment.Center
                  ) {
                    Icon(
                      Icons.Filled.School,
                      contentDescription = null,
                      tint = Color.White,
                      modifier = Modifier.size(28.dp)
                    )
                  }

                  Spacer(modifier = Modifier.width(12.dp))

                  Column {
                    Text(
                      text = stats.studentName,
                      style = MaterialTheme.typography.titleMedium,
                      fontWeight = FontWeight.Bold,
                      color = TextPrimary
                    )
                    Text(
                      text = stats.levelRank,
                      fontSize = 12.sp,
                      fontWeight = FontWeight.SemiBold,
                      color = LuxuryGold,
                      modifier = Modifier.padding(top = 2.dp)
                    )
                  }
                }

                // Points Badge
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(LuxuryGold.copy(alpha = 0.15f))
                    .border(1.dp, LuxuryGold.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      Icons.Filled.Stars,
                      contentDescription = null,
                      tint = LuxuryGold,
                      modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                      text = "${stats.points} نقطة",
                      fontSize = 12.sp,
                      fontWeight = FontWeight.Bold,
                      color = LuxuryGold
                    )
                  }
                }
              }

              Spacer(modifier = Modifier.height(14.dp))

              // Subscription active info
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(10.dp))
                  .background(SurfaceDark)
                  .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "كود الاشتراك الحالي: ${session.activationCode.ifBlank { "غير مفعل" }}",
                  fontSize = 11.sp,
                  color = TextSecondary
                )
                Text(
                  text = "متبقي ${session.remainingDays} يوم",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = NeonSkyBlue
                )
              }
            }
          }
        }
      }

      // 4 Stats KPI Cards Grid
      item {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            KpiCard(
              title = "فيديوهات كاملة 100%",
              value = "${stats.completedVideos} من ${stats.totalVideos}",
              subtitle = "تم سماعها بالكامل",
              icon = Icons.Filled.PlayCircleFilled,
              accentColor = EmeraldSuccess,
              modifier = Modifier.weight(1f)
            )

            KpiCard(
              title = "نسبة الإنجاز",
              value = "${(stats.completionRate * 100).toInt()}%",
              subtitle = "من المنهج الدراسي",
              icon = Icons.Filled.Timelapse,
              accentColor = NeonSkyBlue,
              modifier = Modifier.weight(1f)
            )
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            KpiCard(
              title = "دقائق المشاهدة",
              value = "${stats.totalWatchMinutes} دقيقة",
              subtitle = "وقت تعلم فعال",
              icon = Icons.Filled.WorkspacePremium,
              accentColor = PurpleAccent,
              modifier = Modifier.weight(1f)
            )

            KpiCard(
              title = "الواجبات المنجزة",
              value = "${stats.submittedHomeworks} من ${stats.totalHomeworks}",
              subtitle = if (stats.averageGrade > 0) "التقييم: ${String.format("%.1f", stats.averageGrade)}/10" else "قيد المراجعة",
              icon = Icons.Filled.AssignmentTurnedIn,
              accentColor = LuxuryGold,
              modifier = Modifier.weight(1f)
            )
          }
        }
      }

      // Title Section: Detailed Videos Watched
      item {
        Text(
          text = "تفاصيل سجل مشاهدة الفيديوهات بالكامل (100%):",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = TextPrimary,
          modifier = Modifier.padding(top = 8.dp)
        )
      }

      // Detailed Lesson Watch Status Items
      items(lessons) { lesson ->
        val progress = watchProgress[lesson.id]
        val isCompleted = progress?.isCompleted == true

        Card(
          modifier = Modifier
            .fillMaxWidth()
            .border(
              1.dp,
              if (isCompleted) EmeraldSuccess.copy(alpha = 0.6f) else SurfaceBorder,
              RoundedCornerShape(14.dp)
            ),
          colors = CardDefaults.cardColors(containerColor = SurfaceDark),
          shape = RoundedCornerShape(14.dp)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(
                  if (isCompleted) EmeraldSuccess.copy(alpha = 0.2f) else DeepIndigoBlue.copy(alpha = 0.3f)
                ),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = if (isCompleted) Icons.Filled.CheckCircle else Icons.Filled.PlayCircleFilled,
                contentDescription = null,
                tint = if (isCompleted) EmeraldSuccess else TextMuted,
                modifier = Modifier.size(22.dp)
              )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = lesson.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                maxLines = 1
              )
              Text(
                text = "${lesson.subject} • ${lesson.durationMinutes} دقيقة",
                fontSize = 11.sp,
                color = TextSecondary,
                modifier = Modifier.padding(top = 2.dp)
              )

              if (!isCompleted && progress != null && progress.watchedSeconds > 0) {
                Spacer(modifier = Modifier.height(4.dp))
                val percentage = (progress.watchedSeconds.toFloat() / (lesson.durationMinutes * 60)).coerceIn(0f, 1f)
                LinearProgressIndicator(
                  progress = { percentage },
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                  color = NeonSkyBlue,
                  trackColor = SurfaceBorder
                )
              }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Completion Pill
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(
                  if (isCompleted) EmeraldSuccess.copy(alpha = 0.15f) else SurfaceDarkElevated
                )
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text(
                text = if (isCompleted) "سمعه كامل ✓" else if (progress != null && progress.watchedSeconds > 0) "قيد المشاهدة" else "لم يبدأ",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (isCompleted) EmeraldSuccess else TextMuted
              )
            }
          }
        }
      }

      // Achievement Badges Section
      item {
        Text(
          text = "أوسمة الإنجاز الأكاديمية:",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = TextPrimary,
          modifier = Modifier.padding(top = 10.dp)
        )
      }

      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, SurfaceBorder, RoundedCornerShape(16.dp)),
          colors = CardDefaults.cardColors(containerColor = SurfaceDark),
          shape = RoundedCornerShape(16.dp)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround
          ) {
            BadgeItem(
              title = "طالب النخبة",
              description = "التسجيل بالكود",
              isUnlocked = true,
              color = NeonSkyBlue
            )
            BadgeItem(
              title = "مكتمل الدروس",
              description = "سمع فيديو كامل",
              isUnlocked = stats.completedVideos > 0,
              color = EmeraldSuccess
            )
            BadgeItem(
              title = "درع التفوق",
              description = "إنجاز 50% من المنهج",
              isUnlocked = stats.completionRate >= 0.5f,
              color = LuxuryGold
            )
          }
        }
      }

      item {
        Spacer(modifier = Modifier.height(24.dp))
      }
    }
  }
}

@Composable
private fun KpiCard(
  title: String,
  value: String,
  subtitle: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  accentColor: Color,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier.border(1.dp, SurfaceBorder, RoundedCornerShape(16.dp)),
    colors = CardDefaults.cardColors(containerColor = SurfaceDarkElevated),
    shape = RoundedCornerShape(16.dp)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = title,
          fontSize = 11.sp,
          color = TextSecondary,
          fontWeight = FontWeight.Medium
        )
        Box(
          modifier = Modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(accentColor.copy(alpha = 0.15f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = accentColor,
            modifier = Modifier.size(16.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = value,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = TextPrimary
      )

      Text(
        text = subtitle,
        fontSize = 10.sp,
        color = accentColor,
        modifier = Modifier.padding(top = 2.dp)
      )
    }
  }
}

@Composable
private fun BadgeItem(
  title: String,
  description: String,
  isUnlocked: Boolean,
  color: Color
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier.width(90.dp)
  ) {
    Box(
      modifier = Modifier
        .size(46.dp)
        .clip(CircleShape)
        .background(if (isUnlocked) color.copy(alpha = 0.2f) else SurfaceDarkElevated)
        .border(1.dp, if (isUnlocked) color else SurfaceBorder, CircleShape),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = Icons.Filled.MilitaryTech,
        contentDescription = null,
        tint = if (isUnlocked) color else TextMuted,
        modifier = Modifier.size(24.dp)
      )
    }

    Spacer(modifier = Modifier.height(6.dp))

    Text(
      text = title,
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold,
      color = if (isUnlocked) TextPrimary else TextMuted
    )

    Text(
      text = description,
      fontSize = 9.sp,
      color = TextMuted,
      lineHeight = 12.sp,
      modifier = Modifier.padding(top = 2.dp)
    )
  }
}
