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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.Homework
import com.example.model.HomeworkSubmission
import com.example.model.SubmissionStatus
import com.example.ui.theme.DeepIndigoBlue
import com.example.ui.theme.ElectricSapphire
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GlowBlue
import com.example.ui.theme.LuxuryGold
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
fun HomeworkScreen(
  homeworks: List<Homework>,
  submissions: List<HomeworkSubmission>,
  onSubmitHomework: (homeworkId: String, text: String, fileNames: List<String>) -> Unit,
  onDownloadAttachment: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedHomeworkToSubmit by remember { mutableStateOf<Homework?>(null) }

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

        // Header Card
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, SurfaceBorder, RoundedCornerShape(18.dp)),
          colors = CardDefaults.cardColors(containerColor = SurfaceDark),
          shape = RoundedCornerShape(18.dp)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .background(
                brush = Brush.horizontalGradient(
                  colors = listOf(
                    DeepIndigoBlue.copy(alpha = 0.35f),
                    SurfaceDark
                  )
                )
              )
              .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(ElectricSapphire.copy(alpha = 0.2f))
                .border(1.dp, NeonSkyBlue, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                Icons.Filled.Assignment,
                contentDescription = null,
                tint = NeonSkyBlue,
                modifier = Modifier.size(24.dp)
              )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
              Text(
                text = "قسم رفع الواجبات والأنشطة",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
              )
              Text(
                text = "قم بحل التدريبات ورفع صور الحل ليتم تصحيحها وإعطائك درجات التقييم وملاحظات المعلم",
                fontSize = 11.sp,
                color = TextSecondary,
                lineHeight = 15.sp,
                modifier = Modifier.padding(top = 2.dp)
              )
            }
          }
        }
      }

      // Homeworks list
      items(homeworks) { hw ->
        val submission = submissions.find { it.homeworkId == hw.id }
        val isSubmitted = submission != null
        val isReviewed = submission?.status == SubmissionStatus.REVIEWED

        Card(
          modifier = Modifier
            .fillMaxWidth()
            .border(
              1.dp,
              if (isReviewed) LuxuryGold.copy(alpha = 0.5f) else if (isSubmitted) EmeraldSuccess.copy(alpha = 0.5f) else SurfaceBorder,
              RoundedCornerShape(16.dp)
            )
            .testTag("homework_card_${hw.id}"),
          colors = CardDefaults.cardColors(containerColor = SurfaceDarkElevated),
          shape = RoundedCornerShape(16.dp)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            // Subject & Due Date Row
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(DeepIndigoBlue.copy(alpha = 0.4f))
                  .padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Text(
                  text = hw.subject,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = NeonSkyBlue
                )
              }

              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  Icons.Filled.CalendarToday,
                  contentDescription = null,
                  tint = TextMuted,
                  modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = hw.dueDate,
                  fontSize = 11.sp,
                  color = TextSecondary
                )
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Homework Title
            Text(
              text = hw.title,
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = TextPrimary
            )

            Text(
              text = "مرتبط بـ: ${hw.lessonTitle}",
              fontSize = 12.sp,
              color = NeonSkyBlue,
              modifier = Modifier.padding(top = 2.dp)
            )

            // Instructions
            Text(
              text = hw.instructions,
              fontSize = 12.sp,
              color = TextSecondary,
              lineHeight = 17.sp,
              modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Attachment question sheet
            hw.attachmentName?.let { attachName ->
              Row(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(SurfaceDark)
                  .clickable { onDownloadAttachment(attachName) }
                  .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  Icons.Filled.Description,
                  contentDescription = null,
                  tint = GlowBlue,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "تحميل ورقة الأسئلة ($attachName)",
                  fontSize = 11.sp,
                  color = TextPrimary
                )
              }
              Spacer(modifier = Modifier.height(12.dp))
            }

            // Submission Status or Button
            if (submission != null) {
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(12.dp))
                  .background(SurfaceDark)
                  .padding(12.dp)
              ) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      imageVector = if (isReviewed) Icons.Filled.Stars else Icons.Filled.CheckCircle,
                      contentDescription = null,
                      tint = if (isReviewed) LuxuryGold else EmeraldSuccess,
                      modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = if (isReviewed) "تم التقييم بواسطة المعلم" else "تم تسليم الواجب بنجاح",
                      fontSize = 12.sp,
                      fontWeight = FontWeight.Bold,
                      color = if (isReviewed) LuxuryGold else EmeraldSuccess
                    )
                  }

                  if (isReviewed && submission.grade != null) {
                    Box(
                      modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(LuxuryGold.copy(alpha = 0.2f))
                        .border(1.dp, LuxuryGold, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                      Text(
                        text = "${submission.grade} / ${hw.maxGrade} ⭐",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = LuxuryGold
                      )
                    }
                  } else {
                    Text(
                      text = "قيد المراجعة ⏳",
                      fontSize = 11.sp,
                      color = TextSecondary
                    )
                  }
                }

                if (!submission.teacherFeedback.isNullOrBlank()) {
                  Spacer(modifier = Modifier.height(8.dp))
                  Text(
                    text = "ملاحظات المعلم: \"${submission.teacherFeedback}\"",
                    fontSize = 11.sp,
                    color = NeonSkyBlue,
                    fontWeight = FontWeight.Medium
                  )
                }

                if (submission.attachedFileNames.isNotEmpty()) {
                  Spacer(modifier = Modifier.height(6.dp))
                  Text(
                    text = "الملفات المرفقة: ${submission.attachedFileNames.joinToString(", ")}",
                    fontSize = 10.sp,
                    color = TextMuted
                  )
                }
              }
            } else {
              // Not submitted yet button
              Button(
                onClick = { selectedHomeworkToSubmit = hw },
                modifier = Modifier
                  .fillMaxWidth()
                  .height(44.dp)
                  .testTag("submit_hw_button_${hw.id}"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                  containerColor = ElectricSapphire
                )
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    Icons.Filled.Send,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "رفع وتسليم الحل الآن",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                  )
                }
              }
            }
          }
        }
      }

      item {
        Spacer(modifier = Modifier.height(24.dp))
      }
    }

    // Submit Homework Modal Dialog
    if (selectedHomeworkToSubmit != null) {
      SubmitHomeworkDialog(
        homework = selectedHomeworkToSubmit!!,
        onDismiss = { selectedHomeworkToSubmit = null },
        onSubmit = { text, files ->
          onSubmitHomework(selectedHomeworkToSubmit!!.id, text, files)
          selectedHomeworkToSubmit = null
        }
      )
    }
  }
}

@Composable
fun SubmitHomeworkDialog(
  homework: Homework,
  onDismiss: () -> Unit,
  onSubmit: (text: String, files: List<String>) -> Unit
) {
  var solutionText by remember { mutableStateOf("") }
  val attachedFiles = remember { mutableStateListOf<String>() }

  Dialog(
    onDismissRequest = onDismiss,
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
          .padding(20.dp)
      ) {
        // Dialog Top Bar
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(
            onClick = onDismiss,
            modifier = Modifier
              .size(38.dp)
              .clip(CircleShape)
              .background(SurfaceDarkElevated)
          ) {
            Icon(Icons.Filled.Close, contentDescription = "إلغاء", tint = TextPrimary)
          }

          Text(
            text = "تسليم الواجب الأكاديمي",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = NeonSkyBlue
          )

          Spacer(modifier = Modifier.size(38.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Homework info card
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, SurfaceBorder, RoundedCornerShape(14.dp)),
          colors = CardDefaults.cardColors(containerColor = SurfaceDark),
          shape = RoundedCornerShape(14.dp)
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Text(
              text = homework.title,
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.Bold,
              color = TextPrimary
            )
            Text(
              text = homework.instructions,
              fontSize = 11.sp,
              color = TextSecondary,
              modifier = Modifier.padding(top = 4.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
          text = "اكتب خطوات الحل أو التوضيح:",
          style = MaterialTheme.typography.titleSmall,
          fontWeight = FontWeight.Bold,
          color = TextPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = solutionText,
          onValueChange = { solutionText = it },
          placeholder = { Text("اكتب هنا نواتج المسائل أو تفاصيل الحل...") },
          modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .testTag("homework_solution_text_field"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = NeonSkyBlue,
            unfocusedBorderColor = SurfaceBorder,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            focusedContainerColor = SurfaceDark,
            unfocusedContainerColor = SurfaceDark
          ),
          shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
          text = "إرفاق صور الكشكول أو ملفات PDF:",
          style = MaterialTheme.typography.titleSmall,
          fontWeight = FontWeight.Bold,
          color = TextPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Attach buttons row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedButton(
            onClick = {
              val idx = attachedFiles.size + 1
              attachedFiles.add("صورة_كشكول_الواجب_ص$idx.jpg")
            },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(10.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorderLight)
          ) {
            Icon(Icons.Filled.AddPhotoAlternate, contentDescription = null, tint = GlowBlue, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("التقاط صورة للحل", fontSize = 11.sp, color = TextPrimary)
          }

          OutlinedButton(
            onClick = {
              val idx = attachedFiles.size + 1
              attachedFiles.add("ملف_إجابة_الواجب_$idx.pdf")
            },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(10.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorderLight)
          ) {
            Icon(Icons.Filled.AttachFile, contentDescription = null, tint = NeonSkyBlue, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("إرفاق ملف PDF", fontSize = 11.sp, color = TextPrimary)
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Attached files chips
        if (attachedFiles.isNotEmpty()) {
          Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            attachedFiles.forEachIndexed { index, fileName ->
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(8.dp))
                  .background(SurfaceDarkElevated)
                  .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    Icons.Filled.Description,
                    contentDescription = null,
                    tint = NeonSkyBlue,
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(fileName, fontSize = 11.sp, color = TextPrimary)
                }
                IconButton(
                  onClick = { attachedFiles.removeAt(index) },
                  modifier = Modifier.size(24.dp)
                ) {
                  Icon(Icons.Filled.Close, contentDescription = "حذف", tint = Color.Red, modifier = Modifier.size(14.dp))
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Submit Button
        Button(
          onClick = {
            val text = solutionText.ifBlank { "تم إرفاق صور الحل في المرفقات الموضحة أعلاه." }
            onSubmit(text, attachedFiles.toList())
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("confirm_submit_homework_button"),
          shape = RoundedCornerShape(14.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = ElectricSapphire
          )
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Check, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "إرسال الواجب للأستاذ الآن",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          }
        }
      }
    }
  }
}
