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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.model.TelegramChannel
import com.example.model.TelegramPost
import com.example.ui.theme.DeepIndigoBlue
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
import com.example.ui.theme.TelegramBlue
import com.example.ui.theme.TelegramDarkBubble
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TelegramChannelScreen(
  channels: List<TelegramChannel>,
  posts: List<TelegramPost>,
  selectedChannelId: String,
  isOwner: Boolean,
  onSelectChannel: (String) -> Unit,
  onToggleSubscribe: (String) -> Unit,
  onToggleMute: (String) -> Unit,
  onToggleReaction: (postId: String, emoji: String) -> Unit,
  onBroadcastPost: (channelId: String, text: String, fileName: String?, imageUrl: String?) -> Unit,
  onDownloadFile: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val activeChannel = channels.find { it.id == selectedChannelId } ?: channels.firstOrNull()
  val channelPosts = posts.filter { it.channelId == selectedChannelId }
  var showBroadcastDialog by remember { mutableStateOf(false) }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(ObsidianBlack)
  ) {
    Column(modifier = Modifier.fillMaxSize()) {
      // Channels selector row
      if (channels.size > 1) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(MidnightBlack)
            .padding(horizontal = 16.dp, vertical = 8.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          channels.forEach { channel ->
            val isSelected = channel.id == selectedChannelId
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(if (isSelected) ElectricSapphire else SurfaceDark)
                .clickable { onSelectChannel(channel.id) }
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .testTag("channel_tab_${channel.id}")
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(channel.iconEmoji, fontSize = 14.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = channel.name,
                  fontSize = 11.sp,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                  color = if (isSelected) Color.White else TextSecondary
                )
              }
            }
          }
        }
      }

      // Telegram Channel Header
      if (activeChannel != null) {
        Surface(
          modifier = Modifier.fillMaxWidth(),
          color = SurfaceDark,
          tonalElevation = 4.dp
        ) {
          Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
              ) {
                // Channel Avatar
                Box(
                  modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(
                      brush = Brush.linearGradient(
                        colors = listOf(TelegramBlue, ElectricSapphire)
                      )
                    ),
                  contentAlignment = Alignment.Center
                ) {
                  Text(activeChannel.iconEmoji, fontSize = 22.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                      text = activeChannel.name,
                      style = MaterialTheme.typography.titleSmall,
                      fontWeight = FontWeight.Bold,
                      color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                      Icons.Filled.Verified,
                      contentDescription = "قناة موثقة",
                      tint = TelegramBlue,
                      modifier = Modifier.size(16.dp)
                    )
                  }

                  Text(
                    text = "${activeChannel.subscribersCount} مشتركاً • ${activeChannel.handle}",
                    fontSize = 11.sp,
                    color = NeonSkyBlue
                  )
                }
              }

              // Actions: Mute & Subscribe
              Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                  onClick = { onToggleMute(activeChannel.id) },
                  modifier = Modifier.size(36.dp)
                ) {
                  Icon(
                    imageVector = if (activeChannel.isMuted) Icons.Filled.NotificationsOff else Icons.Filled.Notifications,
                    contentDescription = "الإشعارات",
                    tint = if (activeChannel.isMuted) TextMuted else GlowBlue,
                    modifier = Modifier.size(20.dp)
                  )
                }

                Spacer(modifier = Modifier.width(6.dp))

                Button(
                  onClick = { onToggleSubscribe(activeChannel.id) },
                  modifier = Modifier
                    .height(34.dp)
                    .testTag("toggle_subscribe_channel"),
                  shape = RoundedCornerShape(18.dp),
                  colors = ButtonDefaults.buttonColors(
                    containerColor = if (activeChannel.isSubscribed) SurfaceDarkElevated else TelegramBlue
                  )
                ) {
                  Text(
                    text = if (activeChannel.isSubscribed) "مشترك ✓" else "انضمام",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (activeChannel.isSubscribed) EmeraldSuccess else Color.White
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
              text = activeChannel.description,
              fontSize = 11.sp,
              color = TextSecondary,
              lineHeight = 15.sp
            )
          }
        }
      }

      // Telegram Feed Posts
      LazyColumn(
        modifier = Modifier
          .weight(1f)
          .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        item {
          Spacer(modifier = Modifier.height(10.dp))
          // Date pill separator
          Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
          ) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceDark.copy(alpha = 0.8f))
                .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
              Text(
                text = "اليوم - منشورات القناة الرسمية",
                fontSize = 10.sp,
                color = TextSecondary
              )
            }
          }
        }

        items(channelPosts) { post ->
          TelegramPostBubble(
            post = post,
            onReactionClick = { emoji -> onToggleReaction(post.id, emoji) },
            onDownloadFile = onDownloadFile
          )
        }

        item {
          Spacer(modifier = Modifier.height(70.dp))
        }
      }
    }

    // Owner Broadcast FAB
    if (isOwner && activeChannel != null) {
      FloatingActionButton(
        onClick = { showBroadcastDialog = true },
        modifier = Modifier
          .align(Alignment.BottomStart)
          .padding(20.dp)
          .testTag("owner_broadcast_fab"),
        containerColor = TelegramBlue,
        contentColor = Color.White,
        shape = CircleShape
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 16.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(Icons.Filled.Send, contentDescription = null, modifier = Modifier.size(20.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(text = "نشر رسالة جديدة", fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
      }
    }

    // Broadcast Dialog
    if (showBroadcastDialog && activeChannel != null) {
      BroadcastPostDialog(
        channelName = activeChannel.name,
        onDismiss = { showBroadcastDialog = false },
        onPost = { text, file, img ->
          onBroadcastPost(activeChannel.id, text, file, img)
          showBroadcastDialog = false
        }
      )
    }
  }
}

@Composable
fun TelegramPostBubble(
  post: TelegramPost,
  onReactionClick: (String) -> Unit,
  onDownloadFile: (String) -> Unit
) {
  val timeFormat = remember { SimpleDateFormat("hh:mm a", Locale("ar")) }
  val formattedTime = timeFormat.format(Date(post.timestamp))

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .border(1.dp, SurfaceBorderLight.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
      .testTag("telegram_post_${post.id}"),
    colors = CardDefaults.cardColors(containerColor = TelegramDarkBubble),
    shape = RoundedCornerShape(16.dp)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      // Author header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = post.authorName,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = TelegramBlue
          )
          if (post.isVerified) {
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
              Icons.Filled.Verified,
              contentDescription = null,
              tint = TelegramBlue,
              modifier = Modifier.size(14.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Post text content
      Text(
        text = post.content,
        style = MaterialTheme.typography.bodyMedium,
        color = TextPrimary,
        lineHeight = 22.sp
      )

      // Attached Image
      if (!post.imageUrl.isNullOrBlank()) {
        Spacer(modifier = Modifier.height(10.dp))
        AsyncImage(
          model = post.imageUrl,
          contentDescription = "صورة مرفقة",
          modifier = Modifier
            .fillMaxWidth()
            .height(190.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, SurfaceBorder, RoundedCornerShape(12.dp)),
          contentScale = ContentScale.Crop
        )
      }

      // Attached Document / PDF
      if (!post.fileName.isNullOrBlank()) {
        Spacer(modifier = Modifier.height(10.dp))
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceDarkElevated)
            .border(1.dp, SurfaceBorder, RoundedCornerShape(12.dp))
            .clickable { onDownloadFile(post.fileName) }
            .padding(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(42.dp)
              .clip(CircleShape)
              .background(TelegramBlue.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              Icons.Filled.Download,
              contentDescription = "تحميل الملف",
              tint = TelegramBlue,
              modifier = Modifier.size(22.dp)
            )
          }

          Spacer(modifier = Modifier.width(12.dp))

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = post.fileName,
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.Bold,
              color = TextPrimary,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
            Text(
              text = "${post.fileSize ?: "3.5 MB"} • انقر للتحميل",
              fontSize = 11.sp,
              color = NeonSkyBlue
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Footer: Timestamp, Seen, Views
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Reactions bar
        Row(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          listOf("❤️", "🔥", "👏", "💡").forEach { emoji ->
            val count = post.reactions[emoji] ?: 0
            val isUserSelected = post.userReaction == emoji

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(if (isUserSelected) TelegramBlue.copy(alpha = 0.3f) else SurfaceDark)
                .border(
                  1.dp,
                  if (isUserSelected) TelegramBlue else SurfaceBorder.copy(alpha = 0.5f),
                  RoundedCornerShape(14.dp)
                )
                .clickable { onReactionClick(emoji) }
                .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(emoji, fontSize = 12.sp)
                if (count > 0) {
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = "$count",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isUserSelected) TelegramBlue else TextSecondary
                  )
                }
              }
            }
          }
        }

        // Views & Time
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            Icons.Filled.RemoveRedEye,
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(13.dp)
          )
          Spacer(modifier = Modifier.width(3.dp))
          Text(text = "${post.viewsCount}", fontSize = 10.sp, color = TextMuted)
          Spacer(modifier = Modifier.width(8.dp))
          Text(text = formattedTime, fontSize = 10.sp, color = TextMuted)
          Spacer(modifier = Modifier.width(3.dp))
          Text(text = "✓✓", fontSize = 10.sp, color = TelegramBlue, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

@Composable
fun BroadcastPostDialog(
  channelName: String,
  onDismiss: () -> Unit,
  onPost: (text: String, fileName: String?, imageUrl: String?) -> Unit
) {
  var text by remember { mutableStateOf("") }
  var fileName by remember { mutableStateOf<String?>(null) }
  var imageUrl by remember { mutableStateOf<String?>(null) }

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
        // Header
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
            text = "نشر في $channelName",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TelegramBlue
          )

          Spacer(modifier = Modifier.size(38.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
          text = "اكتب نص الرسالة أو الإعلان:",
          style = MaterialTheme.typography.titleSmall,
          fontWeight = FontWeight.Bold,
          color = TextPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = text,
          onValueChange = { text = it },
          placeholder = { Text("اكتب رسالتك لجميع الطلاب المشتركين في القناة...") },
          modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .testTag("broadcast_message_input"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = TelegramBlue,
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
          text = "إرفاق وسائط أو ملفات للمنشور:",
          style = MaterialTheme.typography.titleSmall,
          fontWeight = FontWeight.Bold,
          color = TextPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedButton(
            onClick = {
              imageUrl = "https://images.unsplash.com/photo-1434030216411-0b793f4b4173?w=800&q=80"
            },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(10.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorderLight)
          ) {
            Icon(Icons.Filled.AddPhotoAlternate, contentDescription = null, tint = TelegramBlue, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("إرفاق صورة", fontSize = 11.sp, color = TextPrimary)
          }

          OutlinedButton(
            onClick = {
              fileName = "ملزمة_المراجعة_الشاملة_${System.currentTimeMillis().toString().takeLast(4)}.pdf"
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

        Spacer(modifier = Modifier.height(12.dp))

        // Previews of attached media
        if (imageUrl != null) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .background(SurfaceDarkElevated)
              .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("✓ تم إرفاق صورة توضيحية للمنشور", fontSize = 11.sp, color = NeonSkyBlue)
            IconButton(onClick = { imageUrl = null }, modifier = Modifier.size(24.dp)) {
              Icon(Icons.Filled.Close, contentDescription = null, tint = Color.Red, modifier = Modifier.size(14.dp))
            }
          }
          Spacer(modifier = Modifier.height(6.dp))
        }

        if (fileName != null) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .background(SurfaceDarkElevated)
              .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("✓ تم إرفاق: $fileName", fontSize = 11.sp, color = EmeraldSuccess)
            IconButton(onClick = { fileName = null }, modifier = Modifier.size(24.dp)) {
              Icon(Icons.Filled.Close, contentDescription = null, tint = Color.Red, modifier = Modifier.size(14.dp))
            }
          }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
          onClick = {
            if (text.isNotBlank()) {
              onPost(text, fileName, imageUrl)
            }
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("submit_broadcast_button"),
          shape = RoundedCornerShape(14.dp),
          colors = ButtonDefaults.buttonColors(containerColor = TelegramBlue),
          enabled = text.isNotBlank()
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Send, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "إرسال البث في القناة فوراً 📢",
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
