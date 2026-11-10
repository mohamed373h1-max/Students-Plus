package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.PlayCircleOutline
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.UserSession
import com.example.ui.AcademyTab
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
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceDarkElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun LuxuryTopAppBar(
  session: UserSession,
  onOwnerClick: () -> Unit,
  onLogoutClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier.fillMaxWidth(),
    color = MidnightBlack,
    tonalElevation = 8.dp,
    shadowElevation = 4.dp
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(
          brush = Brush.verticalGradient(
            colors = listOf(
              DeepIndigoBlue.copy(alpha = 0.25f),
              MidnightBlack
            )
          )
        )
        .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        // App Identity & Icon
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.testTag("app_header_branding")
        ) {
          Box(
            modifier = Modifier
              .size(42.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(
                brush = Brush.linearGradient(
                  colors = listOf(ElectricSapphire, NeonSkyBlue)
                )
              )
              .border(1.dp, NeonSkyBlue.copy(alpha = 0.6f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Filled.School,
              contentDescription = "أكاديمية النخبة",
              tint = Color.White,
              modifier = Modifier.size(24.dp)
            )
          }

          Spacer(modifier = Modifier.width(10.dp))

          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "أكاديمية النخبة",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
              )
              Spacer(modifier = Modifier.width(6.dp))
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(ElectricSapphire.copy(alpha = 0.25f))
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Text(
                  text = "PRO",
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Black,
                  color = NeonSkyBlue
                )
              }
            }
            Text(
              text = if (session.isOwner) "حساب المعلم والمالك" else if (session.isActivated) session.studentName else "تفعيل الحساب بالكود",
              style = MaterialTheme.typography.labelSmall,
              color = TextSecondary,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
        }

        // Status & Action Pills
        Row(verticalAlignment = Alignment.CenterVertically) {
          if (session.isSubscriptionActive) {
            // Subscription Days Left Badge
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(SurfaceDarkElevated)
                .border(
                  width = 1.dp,
                  color = if (session.isOwner) LuxuryGold else ElectricSapphire.copy(alpha = 0.5f),
                  shape = RoundedCornerShape(20.dp)
                )
                .padding(horizontal = 10.dp, vertical = 6.dp)
                .testTag("subscription_status_badge")
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (session.isOwner) LuxuryGold else EmeraldSuccess)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = if (session.isOwner) "المالك 👑" else "${session.remainingDays} يوم متبقي",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (session.isOwner) LuxuryGold else NeonSkyBlue
                )
              }
            }
            Spacer(modifier = Modifier.width(8.dp))
          }

          // Owner switch icon button
          IconButton(
            onClick = onOwnerClick,
            modifier = Modifier
              .size(38.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(if (session.isOwner) ElectricSapphire else SurfaceDark)
              .border(1.dp, SurfaceBorder, RoundedCornerShape(10.dp))
              .testTag("admin_portal_button")
          ) {
            Icon(
              imageVector = if (session.isOwner) Icons.Filled.AdminPanelSettings else Icons.Filled.Lock,
              contentDescription = "لوحة المالك",
              tint = if (session.isOwner) Color.White else GlowBlue,
              modifier = Modifier.size(20.dp)
            )
          }
        }
      }
    }
  }
}

@Composable
fun LuxuryBottomNavigationBar(
  currentTab: AcademyTab,
  onTabSelected: (AcademyTab) -> Unit,
  isOwner: Boolean,
  modifier: Modifier = Modifier
) {
  NavigationBar(
    modifier = modifier
      .fillMaxWidth()
      .border(1.dp, SurfaceBorder.copy(alpha = 0.6f), RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
      .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
    containerColor = MidnightBlack,
    tonalElevation = 8.dp
  ) {
    val tabs = if (isOwner) AcademyTab.values().toList() else AcademyTab.values().filter { it != AcademyTab.OWNER }

    tabs.forEach { tab ->
      val selected = currentTab == tab
      val icon = when (tab) {
        AcademyTab.VIDEOS -> if (selected) Icons.Filled.PlayCircle else Icons.Outlined.PlayCircleOutline
        AcademyTab.LEVEL -> if (selected) Icons.Filled.Stars else Icons.Filled.Stars
        AcademyTab.HOMEWORK -> if (selected) Icons.Filled.Assignment else Icons.Outlined.Assignment
        AcademyTab.CHANNEL -> if (selected) Icons.Filled.Send else Icons.Outlined.Send
        AcademyTab.OWNER -> if (selected) Icons.Filled.AdminPanelSettings else Icons.Outlined.AdminPanelSettings
      }

      NavigationBarItem(
        selected = selected,
        onClick = { onTabSelected(tab) },
        icon = {
          Icon(
            imageVector = icon,
            contentDescription = tab.titleAr,
            modifier = Modifier.size(24.dp)
          )
        },
        label = {
          Text(
            text = tab.titleAr,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            maxLines = 1
          )
        },
        colors = NavigationBarItemDefaults.colors(
          selectedIconColor = Color.White,
          selectedTextColor = NeonSkyBlue,
          indicatorColor = ElectricSapphire,
          unselectedIconColor = TextSecondary,
          unselectedTextColor = TextMuted
        ),
        modifier = Modifier.testTag(tab.testTag)
      )
    }
  }
}
