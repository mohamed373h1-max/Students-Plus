package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.example.model.ActivationCode
import com.example.model.HomeworkSubmission
import com.example.model.SubmissionStatus
import com.example.ui.theme.DeepIndigoBlue
import com.example.ui.theme.ElectricSapphire
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GlowBlue
import com.example.ui.theme.LuxuryGold
import com.example.ui.theme.MidnightBlack
import com.example.ui.theme.NeonSkyBlue
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.RoseAlert
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceBorderLight
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceDarkElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

enum class OwnerSection(val title: String) {
  CODES("توليد الأكواد"),
  ADD_LESSON("إضافة شرح"),
  GRADE_HOMEWORK("تصحيح الواجب"),
  SECURITY("رمز الأمان 🔒")
}

@Composable
fun OwnerPortalScreen(
  activationCodes: List<ActivationCode>,
  submissions: List<HomeworkSubmission>,
  onGenerateCode: (days: Int, studentNote: String) -> Unit,
  onToggleCodeStatus: (String) -> Unit,
  onAddNewLesson: (title: String, subject: String, chapter: String, description: String, duration: Int, url: String, pdf: String) -> Unit,
  onGradeSubmission: (subId: String, grade: Int, feedback: String) -> Unit,
  onChangePin: (newPin: String) -> Unit,
  onLogout: () -> Unit,
  onShowMessage: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var currentSection by remember { mutableStateOf(OwnerSection.CODES) }

  // Code generator state
  var studentNote by remember { mutableStateOf("") }
  var selectedDurationDays by remember { mutableIntStateOf(30) }
  var customDaysInput by remember { mutableStateOf("") }

  // Add Lesson State
  var lessonTitle by remember { mutableStateOf("") }
  var lessonSubject by remember { mutableStateOf("الرياضيات التطبيقية") }
  var lessonChapter by remember { mutableStateOf("الوحدة الأولى") }
  var lessonDescription by remember { mutableStateOf("") }
  var lessonDurationStr by remember { mutableStateOf("45") }
  var lessonPdfTitle by remember { mutableStateOf("ملزمة_شرح_الدرس.pdf") }

  // Grade dialog
  var submissionToGrade by remember { mutableStateOf<HomeworkSubmission?>(null) }

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

        // Owner Welcome & Stats Hero
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, LuxuryGold.copy(alpha = 0.6f), RoundedCornerShape(18.dp))
            .testTag("owner_portal_hero"),
          colors = CardDefaults.cardColors(containerColor = SurfaceDarkElevated),
          shape = RoundedCornerShape(18.dp)
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
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Box(
                    modifier = Modifier
                      .size(46.dp)
                      .clip(CircleShape)
                      .background(LuxuryGold.copy(alpha = 0.2f))
                      .border(1.5.dp, LuxuryGold, CircleShape),
                    contentAlignment = Alignment.Center
                  ) {
                    Icon(
                      Icons.Filled.AdminPanelSettings,
                      contentDescription = null,
                      tint = LuxuryGold,
                      modifier = Modifier.size(26.dp)
                    )
                  }

                  Spacer(modifier = Modifier.width(12.dp))

                  Column {
                    Text(
                      text = "لوحة تحكم مالك المنصة والمعلم",
                      style = MaterialTheme.typography.titleMedium,
                      fontWeight = FontWeight.Bold,
                      color = TextPrimary
                    )
                    Text(
                      text = "إدارة التراخيص والأكواد ورفع الدروس والواجبات",
                      fontSize = 11.sp,
                      color = NeonSkyBlue
                    )
                  }
                }

                // Logout button
                IconButton(
                  onClick = onLogout,
                  modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(SurfaceDark)
                ) {
                  Icon(Icons.Filled.Logout, contentDescription = "خروج", tint = RoseAlert, modifier = Modifier.size(18.dp))
                }
              }

              Spacer(modifier = Modifier.height(14.dp))

              // 3 Quick Metrics
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
              ) {
                MetricColumn(title = "الأكواد المولدة", value = "${activationCodes.size}")
                MetricColumn(title = "الأكواد النشطة", value = "${activationCodes.count { it.isActive }}")
                MetricColumn(
                  title = "واجبات الطلاب",
                  value = "${submissions.size}",
                  highlightColor = LuxuryGold
                )
              }
            }
          }
        }
      }

      // Section Navigation Chips
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OwnerSection.values().forEach { section ->
            val isSelected = currentSection == section
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(if (isSelected) ElectricSapphire else SurfaceDark)
                .border(1.dp, if (isSelected) NeonSkyBlue else SurfaceBorder, RoundedCornerShape(12.dp))
                .clickable { currentSection = section }
                .padding(vertical = 10.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = section.title,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) Color.White else TextSecondary
              )
            }
          }
        }
      }

      // ---------------------------------------------
      // SECTION 1: CODES GENERATOR & LIST
      // ---------------------------------------------
      if (currentSection == OwnerSection.CODES) {
        item {
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .border(1.dp, SurfaceBorderLight, RoundedCornerShape(16.dp))
              .testTag("code_generator_card"),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            shape = RoundedCornerShape(16.dp)
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Text(
                text = "🔑 توليد كود تفعيل جديد للطالب:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = LuxuryGold
              )

              Text(
                text = "حدد اسم الطالب وعدد الأيام المسموح له باستخدام المنصة فيها بعد تفعيل الكود",
                fontSize = 11.sp,
                color = TextSecondary,
                modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
              )

              // Student name / note
              OutlinedTextField(
                value = studentNote,
                onValueChange = { studentNote = it },
                label = { Text("اسم الطالب أو ملاحظة (اختياري)") },
                placeholder = { Text("مثال: الطالب محمود علي - مجموعة الأحد") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = NeonSkyBlue,
                  unfocusedBorderColor = SurfaceBorder,
                  focusedTextColor = TextPrimary,
                  unfocusedTextColor = TextPrimary,
                  focusedLabelColor = NeonSkyBlue,
                  unfocusedLabelColor = TextSecondary,
                  cursorColor = NeonSkyBlue
                ),
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("code_generator_note_input")
              )

              Spacer(modifier = Modifier.height(14.dp))

              Text(
                text = "اختر مدة صلاحية الكود (بالأيام):",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
              )

              Spacer(modifier = Modifier.height(8.dp))

              // Days preset chips
              LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
              ) {
                val presets = listOf(7, 15, 30, 60, 90, 180, 365)
                items(presets) { days ->
                  val isSelected = selectedDurationDays == days && customDaysInput.isBlank()
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(8.dp))
                      .background(if (isSelected) ElectricSapphire else SurfaceDarkElevated)
                      .border(1.dp, if (isSelected) NeonSkyBlue else SurfaceBorder, RoundedCornerShape(8.dp))
                      .clickable {
                        selectedDurationDays = days
                        customDaysInput = ""
                      }
                      .padding(horizontal = 12.dp, vertical = 6.dp)
                  ) {
                    Text(
                      text = "$days يوم",
                      fontSize = 11.sp,
                      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                      color = if (isSelected) Color.White else TextSecondary
                    )
                  }
                }
              }

              Spacer(modifier = Modifier.height(10.dp))

              // Custom days input
              OutlinedTextField(
                value = customDaysInput,
                onValueChange = {
                  customDaysInput = it
                  val parsed = it.toIntOrNull()
                  if (parsed != null && parsed > 0) {
                    selectedDurationDays = parsed
                  }
                },
                label = { Text("أو أدخل عدد أيام مخصص") },
                placeholder = { Text("مثال: 45") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = LuxuryGold,
                  unfocusedBorderColor = SurfaceBorder,
                  focusedTextColor = TextPrimary,
                  unfocusedTextColor = TextPrimary,
                  focusedLabelColor = LuxuryGold,
                  unfocusedLabelColor = TextSecondary
                ),
                modifier = Modifier.fillMaxWidth()
              )

              Spacer(modifier = Modifier.height(16.dp))

              // Generate Button
              Button(
                onClick = {
                  val days = customDaysInput.toIntOrNull() ?: selectedDurationDays
                  onGenerateCode(days, studentNote)
                  studentNote = ""
                  customDaysInput = ""
                },
                modifier = Modifier
                  .fillMaxWidth()
                  .height(48.dp)
                  .testTag("generate_code_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LuxuryGold)
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Filled.Key, contentDescription = null, tint = ObsidianBlack)
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = "توليد كود التفعيل ($selectedDurationDays يوم) 🔑",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = ObsidianBlack
                  )
                }
              }
            }
          }
        }

        item {
          Text(
            text = "قائمة الأكواد المولدة (${activationCodes.size} كود):",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.padding(top = 8.dp)
          )
        }

        // List of all generated codes
        items(activationCodes) { codeItem ->
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .border(
                1.dp,
                if (codeItem.isActive) ElectricSapphire.copy(alpha = 0.5f) else SurfaceBorder,
                RoundedCornerShape(14.dp)
              )
              .testTag("activation_code_card_${codeItem.code}"),
            colors = CardDefaults.cardColors(containerColor = SurfaceDarkElevated),
            shape = RoundedCornerShape(14.dp)
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = codeItem.code,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = LuxuryGold
                  )

                  Spacer(modifier = Modifier.width(8.dp))

                  // Copy button
                  IconButton(
                    onClick = {
                      val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                      val clip = ClipData.newPlainText("Activation Code", codeItem.code)
                      clipboard.setPrimaryClip(clip)
                      onShowMessage("تم نسخ الكود: ${codeItem.code} 📋")
                    },
                    modifier = Modifier.size(28.dp)
                  ) {
                    Icon(
                      Icons.Filled.ContentCopy,
                      contentDescription = "نسخ الكود",
                      tint = NeonSkyBlue,
                      modifier = Modifier.size(16.dp)
                    )
                  }

                  // Share button
                  IconButton(
                    onClick = {
                      val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(
                          Intent.EXTRA_TEXT,
                          "كود تفعيل اشتراكك في منصة أكاديمية النخبة هو: ${codeItem.code}\nصالح لمدة: ${codeItem.durationDays} يوم."
                        )
                      }
                      context.startActivity(Intent.createChooser(shareIntent, "مشاركة الكود مع الطالب"))
                    },
                    modifier = Modifier.size(28.dp)
                  ) {
                    Icon(
                      Icons.Filled.Share,
                      contentDescription = "مشاركة",
                      tint = GlowBlue,
                      modifier = Modifier.size(16.dp)
                    )
                  }
                }

                // Status Pill
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                      if (!codeItem.isActive) RoseAlert.copy(alpha = 0.2f)
                      else if (codeItem.isUsed) EmeraldSuccess.copy(alpha = 0.2f)
                      else DeepIndigoBlue.copy(alpha = 0.4f)
                    )
                    .clickable { onToggleCodeStatus(codeItem.code) }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                  Text(
                    text = if (!codeItem.isActive) "معطل" else if (codeItem.isUsed) "مستخدم (${codeItem.remainingDays} يوم)" else "جاهز للاستخدام",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (!codeItem.isActive) RoseAlert else if (codeItem.isUsed) EmeraldSuccess else NeonSkyBlue
                  )
                }
              }

              Spacer(modifier = Modifier.height(6.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(
                  text = if (codeItem.studentName.isNotBlank()) "الملاحظة: ${codeItem.studentName}" else "بدون ملاحظة",
                  fontSize = 11.sp,
                  color = TextSecondary
                )
                Text(
                  text = "المدة: ${codeItem.durationDays} يوم",
                  fontSize = 11.sp,
                  color = NeonSkyBlue,
                  fontWeight = FontWeight.SemiBold
                )
              }
            }
          }
        }
      }

      // ---------------------------------------------
      // SECTION 2: ADD NEW LESSON
      // ---------------------------------------------
      if (currentSection == OwnerSection.ADD_LESSON) {
        item {
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .border(1.dp, SurfaceBorderLight, RoundedCornerShape(16.dp))
              .testTag("add_lesson_form"),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            shape = RoundedCornerShape(16.dp)
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Text(
                text = "🎬 رفع وإضافة فيديو شرح جديد:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = NeonSkyBlue
              )

              Spacer(modifier = Modifier.height(12.dp))

              OutlinedTextField(
                value = lessonTitle,
                onValueChange = { lessonTitle = it },
                label = { Text("عنوان محاضرة الشرح") },
                placeholder = { Text("مثال: المحاضرة 7: حل تمارين التكامل بالتعويض") },
                singleLine = true,
                colors = defaultTextFieldColors(),
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("new_lesson_title_input")
              )

              Spacer(modifier = Modifier.height(10.dp))

              OutlinedTextField(
                value = lessonSubject,
                onValueChange = { lessonSubject = it },
                label = { Text("المادة الدراسية") },
                singleLine = true,
                colors = defaultTextFieldColors(),
                modifier = Modifier.fillMaxWidth()
              )

              Spacer(modifier = Modifier.height(10.dp))

              OutlinedTextField(
                value = lessonChapter,
                onValueChange = { lessonChapter = it },
                label = { Text("الوحدة / الفصل") },
                singleLine = true,
                colors = defaultTextFieldColors(),
                modifier = Modifier.fillMaxWidth()
              )

              Spacer(modifier = Modifier.height(10.dp))

              OutlinedTextField(
                value = lessonDurationStr,
                onValueChange = { lessonDurationStr = it },
                label = { Text("مدة الفيديو (بالدقائق)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                colors = defaultTextFieldColors(),
                modifier = Modifier.fillMaxWidth()
              )

              Spacer(modifier = Modifier.height(10.dp))

              OutlinedTextField(
                value = lessonDescription,
                onValueChange = { lessonDescription = it },
                label = { Text("وصف ونقاط الشرح") },
                placeholder = { Text("ماذا سيتعلم الطالب في هذه المحاضرة...") },
                colors = defaultTextFieldColors(),
                modifier = Modifier
                  .fillMaxWidth()
                  .height(100.dp)
              )

              Spacer(modifier = Modifier.height(10.dp))

              OutlinedTextField(
                value = lessonPdfTitle,
                onValueChange = { lessonPdfTitle = it },
                label = { Text("اسم ملف ملزمة PDF المرفقة") },
                singleLine = true,
                colors = defaultTextFieldColors(),
                modifier = Modifier.fillMaxWidth()
              )

              Spacer(modifier = Modifier.height(16.dp))

              Button(
                onClick = {
                  if (lessonTitle.isNotBlank()) {
                    val duration = lessonDurationStr.toIntOrNull() ?: 45
                    onAddNewLesson(
                      lessonTitle,
                      lessonSubject,
                      lessonChapter,
                      lessonDescription,
                      duration,
                      "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                      lessonPdfTitle
                    )
                    lessonTitle = ""
                    lessonDescription = ""
                  }
                },
                modifier = Modifier
                  .fillMaxWidth()
                  .height(48.dp)
                  .testTag("submit_new_lesson_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ElectricSapphire),
                enabled = lessonTitle.isNotBlank()
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Filled.PlayCircle, contentDescription = null, tint = Color.White)
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = "نشر محاضرة الفيديو للطلاب فوراً 🎬",
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

      // ---------------------------------------------
      // SECTION 3: GRADE HOMEWORKS
      // ---------------------------------------------
      if (currentSection == OwnerSection.GRADE_HOMEWORK) {
        item {
          Text(
            text = "إجابات واجبات الطلاب المستلمة (${submissions.size}):",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
          )
        }

        if (submissions.isEmpty()) {
          item {
            Card(
              modifier = Modifier.fillMaxWidth(),
              colors = CardDefaults.cardColors(containerColor = SurfaceDark)
            ) {
              Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                Text(
                  text = "لا توجد واجبات مسلّمة حالياً بانتظار التصحيح.",
                  fontSize = 12.sp,
                  color = TextMuted
                )
              }
            }
          }
        }

        items(submissions) { sub ->
          val isReviewed = sub.status == SubmissionStatus.REVIEWED
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .border(
                1.dp,
                if (isReviewed) EmeraldSuccess.copy(alpha = 0.5f) else LuxuryGold.copy(alpha = 0.6f),
                RoundedCornerShape(14.dp)
              )
              .testTag("grade_submission_card_${sub.id}"),
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
                    text = sub.studentName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                  )
                  Text(
                    text = "رقم الهاتف: ${sub.studentPhone}",
                    fontSize = 11.sp,
                    color = NeonSkyBlue
                  )
                }

                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                      if (isReviewed) EmeraldSuccess.copy(alpha = 0.2f) else LuxuryGold.copy(alpha = 0.2f)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                  Text(
                    text = if (isReviewed) "تم التقييم: ${sub.grade}/10 ✓" else "بانتظار تصحيحك ⏳",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isReviewed) EmeraldSuccess else LuxuryGold
                  )
                }
              }

              Spacer(modifier = Modifier.height(10.dp))

              Text(
                text = "إجابة الطالب:",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary
              )
              Text(
                text = sub.submissionText,
                fontSize = 12.sp,
                color = TextPrimary,
                lineHeight = 16.sp,
                modifier = Modifier.padding(top = 2.dp)
              )

              if (sub.attachedFileNames.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                  text = "المرفقات: ${sub.attachedFileNames.joinToString(", ")}",
                  fontSize = 10.sp,
                  color = GlowBlue
                )
              }

              if (!sub.teacherFeedback.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                  text = "ملاحظتك للطالب: \"${sub.teacherFeedback}\"",
                  fontSize = 11.sp,
                  color = EmeraldSuccess
                )
              }

              Spacer(modifier = Modifier.height(12.dp))

              Button(
                onClick = { submissionToGrade = sub },
                modifier = Modifier
                  .fillMaxWidth()
                  .height(40.dp)
                  .testTag("open_grade_dialog_${sub.id}"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                  containerColor = if (isReviewed) DeepIndigoBlue else LuxuryGold
                )
              ) {
                Text(
                  text = if (isReviewed) "تعديل تقييم الطالب ⭐" else "تصحيح الواجب ورصد الدرجة ⭐",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isReviewed) Color.White else ObsidianBlack
                )
              }
            }
          }
        }
      }

      // ---------------------------------------------
      // SECTION 4: SECURITY & PIN MANAGEMENT
      // ---------------------------------------------
      if (currentSection == OwnerSection.SECURITY) {
        item {
          var newPin by remember { mutableStateOf("") }
          var confirmPin by remember { mutableStateOf("") }
          var newPinVisible by remember { mutableStateOf(false) }

          Card(
            modifier = Modifier
              .fillMaxWidth()
              .border(1.dp, LuxuryGold.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
              .testTag("security_pin_card"),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            shape = RoundedCornerShape(16.dp)
          ) {
            Column(modifier = Modifier.padding(18.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(LuxuryGold.copy(alpha = 0.2f)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(Icons.Filled.Lock, contentDescription = null, tint = LuxuryGold, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text(
                    text = "رمز المالك السري والخصوصية",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = LuxuryGold
                  )
                  Text(
                    text = "قم بتعيين رمز PIN سري خاص بك لا يعرفه سواك",
                    fontSize = 11.sp,
                    color = TextSecondary
                  )
                }
              }

              Spacer(modifier = Modifier.height(16.dp))

              OutlinedTextField(
                value = newPin,
                onValueChange = { newPin = it },
                label = { Text("الرمز السري الجديد") },
                placeholder = { Text("••••••••") },
                leadingIcon = {
                  Icon(Icons.Filled.Lock, contentDescription = null, tint = LuxuryGold)
                },
                trailingIcon = {
                  IconButton(onClick = { newPinVisible = !newPinVisible }) {
                    Icon(
                      imageVector = if (newPinVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                      contentDescription = null,
                      tint = TextSecondary
                    )
                  }
                },
                visualTransformation = if (newPinVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                singleLine = true,
                colors = defaultTextFieldColors(),
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("new_pin_input")
              )

              Spacer(modifier = Modifier.height(12.dp))

              OutlinedTextField(
                value = confirmPin,
                onValueChange = { confirmPin = it },
                label = { Text("تأكيد الرمز السري الجديد") },
                placeholder = { Text("••••••••") },
                leadingIcon = {
                  Icon(Icons.Filled.Lock, contentDescription = null, tint = LuxuryGold)
                },
                visualTransformation = if (newPinVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                singleLine = true,
                colors = defaultTextFieldColors(),
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("confirm_pin_input")
              )

              Spacer(modifier = Modifier.height(18.dp))

              Button(
                onClick = {
                  if (newPin.trim().length < 4) {
                    onShowMessage("يجب أن يتكون الرمز السري من 4 أرقام على الأقل!")
                  } else if (newPin.trim() != confirmPin.trim()) {
                    onShowMessage("الرمزان غير متطابقين! يرجى إعادة التأكيد.")
                  } else {
                    onChangePin(newPin.trim())
                    newPin = ""
                    confirmPin = ""
                  }
                },
                modifier = Modifier
                  .fillMaxWidth()
                  .height(48.dp)
                  .testTag("save_new_pin_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LuxuryGold),
                enabled = newPin.isNotBlank() && confirmPin.isNotBlank()
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Filled.Check, contentDescription = null, tint = ObsidianBlack)
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = "حفظ وتأمين الرمز السري الجديد 🔒",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = ObsidianBlack
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

    // Modal Grade Submission Dialog
    if (submissionToGrade != null) {
      GradeSubmissionDialog(
        submission = submissionToGrade!!,
        onDismiss = { submissionToGrade = null },
        onConfirmGrade = { grade, feedback ->
          onGradeSubmission(submissionToGrade!!.id, grade, feedback)
          submissionToGrade = null
        }
      )
    }
  }
}

@Composable
fun GradeSubmissionDialog(
  submission: HomeworkSubmission,
  onDismiss: () -> Unit,
  onConfirmGrade: (grade: Int, feedback: String) -> Unit
) {
  var gradeStr by remember { mutableStateOf((submission.grade ?: 10).toString()) }
  var feedback by remember {
    mutableStateOf(submission.teacherFeedback ?: "أحسنت يا بطل! خطوات ممتازة وإجابة نموذجية.")
  }

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
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(onClick = onDismiss) {
            Icon(Icons.Filled.Close, contentDescription = null, tint = TextPrimary)
          }
          Text(
            text = "رصد درجة الطالب وملاحظات المعلم",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = LuxuryGold
          )
          Spacer(modifier = Modifier.size(36.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
          text = "الطالب: ${submission.studentName}",
          style = MaterialTheme.typography.titleSmall,
          fontWeight = FontWeight.Bold,
          color = TextPrimary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "الدرجة المستحقة (من 10):", fontSize = 12.sp, color = TextSecondary)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
          value = gradeStr,
          onValueChange = { gradeStr = it },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          singleLine = true,
          colors = defaultTextFieldColors(),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("grade_score_input")
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "ملاحظات وتوجيهات المعلم للطالب:", fontSize = 12.sp, color = TextSecondary)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
          value = feedback,
          onValueChange = { feedback = it },
          colors = defaultTextFieldColors(),
          modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .testTag("grade_feedback_input")
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
          onClick = {
            val grade = gradeStr.toIntOrNull() ?: 10
            onConfirmGrade(grade, feedback)
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("confirm_grade_submit_button"),
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = LuxuryGold)
        ) {
          Text(
            text = "حفظ وإرسال التقييم للطالب ⭐",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = ObsidianBlack
          )
        }
      }
    }
  }
}

@Composable
private fun MetricColumn(
  title: String,
  value: String,
  highlightColor: Color = NeonSkyBlue
) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Text(
      text = value,
      fontSize = 20.sp,
      fontWeight = FontWeight.Bold,
      color = highlightColor
    )
    Text(
      text = title,
      fontSize = 10.sp,
      color = TextSecondary,
      modifier = Modifier.padding(top = 2.dp)
    )
  }
}

@Composable
private fun defaultTextFieldColors() = OutlinedTextFieldDefaults.colors(
  focusedBorderColor = NeonSkyBlue,
  unfocusedBorderColor = SurfaceBorder,
  focusedTextColor = TextPrimary,
  unfocusedTextColor = TextPrimary,
  focusedLabelColor = NeonSkyBlue,
  unfocusedLabelColor = TextSecondary,
  cursorColor = NeonSkyBlue
)
