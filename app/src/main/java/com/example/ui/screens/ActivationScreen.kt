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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DeepIndigoBlue
import com.example.ui.theme.ElectricSapphire
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
fun ActivationScreen(
  onActivate: (code: String, name: String, phone: String) -> Unit,
  onOwnerLogin: (pin: String) -> Unit,
  modifier: Modifier = Modifier
) {
  var name by remember { mutableStateOf("") }
  var phone by remember { mutableStateOf("") }
  var code by remember { mutableStateOf("") }
  var isOwnerMode by remember { mutableStateOf(false) }
  var ownerPin by remember { mutableStateOf("") }
  var isPinVisible by remember { mutableStateOf(false) }

  val scrollState = rememberScrollState()

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(
        brush = Brush.verticalGradient(
          colors = listOf(
            ObsidianBlack,
            MidnightBlack,
            DeepIndigoBlue.copy(alpha = 0.35f)
          )
        )
      )
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(scrollState)
        .padding(horizontal = 24.dp, vertical = 32.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Spacer(modifier = Modifier.height(16.dp))

      // Emblem Logo
      Box(
        modifier = Modifier
          .size(86.dp)
          .clip(CircleShape)
          .background(
            brush = Brush.radialGradient(
              colors = listOf(
                ElectricSapphire,
                DeepIndigoBlue,
                MidnightBlack
              )
            )
          )
          .border(2.dp, NeonSkyBlue.copy(alpha = 0.8f), CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Filled.School,
          contentDescription = "شعار الأكاديمية",
          tint = Color.White,
          modifier = Modifier.size(46.dp)
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      Text(
        text = "أكاديمية النخبة التعليمية",
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold,
        color = TextPrimary,
        textAlign = TextAlign.Center
      )

      Text(
        text = if (isOwnerMode) "بوابة المعلم والمالك الرئيسية" else "نظام التسجيل والتفعيل المباشر بالأكواد",
        style = MaterialTheme.typography.bodyMedium,
        color = NeonSkyBlue,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(top = 4.dp)
      )

      Spacer(modifier = Modifier.height(28.dp))

      Card(
        modifier = Modifier
          .fillMaxWidth()
          .border(1.dp, SurfaceBorderLight, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark.copy(alpha = 0.95f)),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
      ) {
        Column(
          modifier = Modifier.padding(20.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          if (!isOwnerMode) {
            // Student Activation Form
            Text(
              text = "تفعيل اشتراك الطالب",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = TextPrimary,
              modifier = Modifier.fillMaxWidth(),
              textAlign = TextAlign.Start
            )

            Text(
              text = "أدخل كود الاشتراك الصادر لك من المعلم للوصول لكافة الشروحات وقنوات البث",
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondary,
              modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 16.dp),
              textAlign = TextAlign.Start
            )

            // Name Field
            OutlinedTextField(
              value = name,
              onValueChange = { name = it },
              label = { Text("اسم الطالب الثلاثي") },
              leadingIcon = {
                Icon(Icons.Filled.Person, contentDescription = null, tint = GlowBlue)
              },
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
                .testTag("student_name_input")
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Phone Field
            OutlinedTextField(
              value = phone,
              onValueChange = { phone = it },
              label = { Text("رقم الهاتف (واتساب)") },
              leadingIcon = {
                Icon(Icons.Filled.Phone, contentDescription = null, tint = GlowBlue)
              },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
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
                .testTag("student_phone_input")
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Code Field
            OutlinedTextField(
              value = code,
              onValueChange = { code = it.uppercase() },
              label = { Text("كود التفعيل (Activation Code)") },
              placeholder = { Text("مثال: ELITE-30D") },
              leadingIcon = {
                Icon(Icons.Filled.Key, contentDescription = null, tint = LuxuryGold)
              },
              singleLine = true,
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = LuxuryGold,
                unfocusedBorderColor = SurfaceBorder,
                focusedTextColor = LuxuryGold,
                unfocusedTextColor = TextPrimary,
                focusedLabelColor = LuxuryGold,
                unfocusedLabelColor = TextSecondary,
                cursorColor = LuxuryGold
              ),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("activation_code_input")
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Quick Demo Codes Helper Box
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceDarkElevated)
                .border(1.dp, SurfaceBorder, RoundedCornerShape(12.dp))
                .padding(12.dp)
            ) {
              Text(
                text = "💡 أكواد تجريبية جاهزة للتجربة الفورية:",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = NeonSkyBlue
              )
              Spacer(modifier = Modifier.height(8.dp))
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                listOf(
                  "ELITE-30D" to "30 يوم",
                  "VIP-60D" to "60 يوم",
                  "TEST-7D" to "7 أيام"
                ).forEach { (demoCode, label) ->
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(8.dp))
                      .background(ElectricSapphire.copy(alpha = 0.2f))
                      .border(1.dp, ElectricSapphire.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                      .clickable { code = demoCode }
                      .padding(horizontal = 8.dp, vertical = 6.dp)
                  ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                      Text(demoCode, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                      Text(label, fontSize = 9.sp, color = NeonSkyBlue)
                    }
                  }
                }
              }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
              onClick = {
                if (code.isNotBlank()) {
                  val finalName = name.ifBlank { "طالب النخبة" }
                  val finalPhone = phone.ifBlank { "01000000000" }
                  onActivate(code, finalName, finalPhone)
                }
              },
              modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("activate_submit_button"),
              shape = RoundedCornerShape(14.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = ElectricSapphire
              ),
              enabled = code.isNotBlank()
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.VerifiedUser, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "تفعيل الكود والدخول للمنصة",
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold,
                  color = Color.White
                )
              }
            }

          } else {
            // Owner / Teacher PIN Login Mode
            Text(
              text = "دخول مالك المنصة والمعلم",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = LuxuryGold,
              modifier = Modifier.fillMaxWidth(),
              textAlign = TextAlign.Start
            )

            Text(
              text = "سجل الدخول بواسطة رمز PIN للتحكم بالمنصة وتوليد الأكواد ونشر الفيديوهات والواجبات",
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondary,
              modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 16.dp),
              textAlign = TextAlign.Start
            )

            OutlinedTextField(
              value = ownerPin,
              onValueChange = { ownerPin = it },
              label = { Text("رمز المعلم السري (PIN)") },
              placeholder = { Text("••••••••") },
              leadingIcon = {
                Icon(Icons.Filled.AdminPanelSettings, contentDescription = null, tint = LuxuryGold)
              },
              trailingIcon = {
                IconButton(onClick = { isPinVisible = !isPinVisible }) {
                  Icon(
                    imageVector = if (isPinVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                    contentDescription = if (isPinVisible) "إخفاء الرمز" else "إظهار الرمز",
                    tint = TextSecondary
                  )
                }
              },
              visualTransformation = if (isPinVisible) VisualTransformation.None else PasswordVisualTransformation(),
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
              singleLine = true,
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = LuxuryGold,
                unfocusedBorderColor = SurfaceBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedLabelColor = LuxuryGold,
                unfocusedLabelColor = TextSecondary,
                cursorColor = LuxuryGold
              ),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("owner_pin_input")
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
              onClick = {
                if (ownerPin.isNotBlank()) {
                  onOwnerLogin(ownerPin)
                }
              },
              modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("owner_login_submit_button"),
              shape = RoundedCornerShape(14.dp),
              enabled = ownerPin.isNotBlank(),
              colors = ButtonDefaults.buttonColors(
                containerColor = LuxuryGold
              )
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.AdminPanelSettings, contentDescription = null, tint = ObsidianBlack)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "دخول لوحة التحكم والمالك",
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold,
                  color = ObsidianBlack
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Switch between Student and Owner
          TextButton(
            onClick = { isOwnerMode = !isOwnerMode },
            modifier = Modifier.testTag("toggle_owner_mode_button")
          ) {
            Text(
              text = if (isOwnerMode) "← العودة إلى تفعيل اشتراك الطلاب" else "أنت صاحب المنصة والمعلم؟ سجل دخولك من هنا 👑",
              style = MaterialTheme.typography.bodySmall,
              color = if (isOwnerMode) NeonSkyBlue else LuxuryGold,
              fontWeight = FontWeight.SemiBold
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Features banner at bottom
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
      ) {
        FeatureMiniItem(icon = Icons.Filled.School, title = "شروحات فيديو بدقة عالية")
        FeatureMiniItem(icon = Icons.Filled.CheckCircle, title = "متابعة نسبة إنجاز الطالب")
        FeatureMiniItem(icon = Icons.Filled.Key, title = "صلاحيات محددة بالأيام")
      }
    }
  }
}

@Composable
private fun FeatureMiniItem(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  title: String
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier.width(96.dp)
  ) {
    Box(
      modifier = Modifier
        .size(36.dp)
        .clip(CircleShape)
        .background(SurfaceDarkElevated)
        .border(1.dp, SurfaceBorder, CircleShape),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = GlowBlue,
        modifier = Modifier.size(18.dp)
      )
    }
    Spacer(modifier = Modifier.height(6.dp))
    Text(
      text = title,
      fontSize = 10.sp,
      color = TextSecondary,
      textAlign = TextAlign.Center,
      lineHeight = 13.sp
    )
  }
}
