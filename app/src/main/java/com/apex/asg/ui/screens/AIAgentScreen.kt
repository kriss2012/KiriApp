package com.apex.asg.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apex.asg.ui.theme.*

@Composable
fun AIAgentScreen() {
    val messages = remember { mutableStateListOf(
        ChatMessage("Namaste! I'm your ASG AI Agent. I can help you find co-founders, mentors, funding & events. How can I help you?", "assistant", listOf("Find co-founder", "Hackathons", "Get funding")),
        ChatMessage("I need a tech co-founder for my agri-tech startup. I'm from Kolhapur.", "user"),
        ChatMessage("Found 6 students in Kolhapur with tech skills interested in agri-tech. Here are top matches:", "assistant", null, true)
    ) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgSurface)
            .padding(bottom = 80.dp)
    ) {
        // Chat Header
        ChatHeader()

        // Chat List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(9.dp),
            reverseLayout = false // Not reverse for this simple demo
        ) {
            items(messages) { msg ->
                ChatBubble(msg)
            }
        }

        // Voice/Input Bar
        ChatInputBar()
    }
}

data class ChatMessage(val text: String, val role: String, val chips: List<String>? = null, val isMatch: Boolean = false)

@Composable
fun ChatHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(10.dp, 18.dp)
            .border(BorderStroke(0.5.dp, BorderColor)), 
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(9.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Brush.linearGradient(listOf(OrangePrimary, OrangeDark))),
            contentAlignment = Alignment.Center
        ) {
            Text("🤖", fontSize = 16.sp)
        }
        Column {
            Text("ASG AI Agent", style = MaterialTheme.typography.titleMedium, color = TextPrimary, fontWeight = FontWeight.ExtraBold)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(6.dp).background(GreenSuccess, RoundedCornerShape(50)))
                Spacer(Modifier.width(4.dp))
                Text("Active · Voice enabled", style = MaterialTheme.typography.labelSmall, color = GreenSuccess, fontWeight = FontWeight.SemiBold, fontSize = 9.sp)
            }
        }
        Spacer(Modifier.weight(1f))
        Text("⋯", fontSize = 20.sp, color = BorderColor)
    }
}

@Composable
fun ChatBubble(msg: ChatMessage) {
    val isBot = msg.role == "assistant"
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isBot) Arrangement.Start else Arrangement.End
    ) {
            Card(
                modifier = Modifier.width(if (isBot) 220.dp else 210.dp),
                shape = if (isBot) RoundedCornerShape(4.dp, 14.dp, 14.dp, 14.dp) else RoundedCornerShape(14.dp, 4.dp, 14.dp, 14.dp),
                colors = CardDefaults.cardColors(containerColor = if (isBot) Color.White else OrangePrimary),
                border = if (isBot) BorderStroke(1.dp, BorderColor) else null,
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Text(
                    text = msg.text,
                    modifier = Modifier.padding(9.dp, 11.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isBot) TextPrimary else Color.White,
                    lineHeight = 16.sp
                )
            }
            
            if (msg.chips != null) {
                LazyRow(modifier = Modifier.padding(top = 4.dp), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    items(msg.chips) { chip ->
                        Surface(
                            color = OrangeLight,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, OrangePrimary),
                            modifier = Modifier.clickable { }
                        ) {
                            Text(chip, modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, color = OrangeDark, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            if (msg.isMatch) {
                MatchResultCard()
            }

            Text("9:32 AM", style = MaterialTheme.typography.labelSmall, fontSize = 8.sp, color = TextSecondary, modifier = Modifier.padding(top = 2.dp))
        }
    }
}

@Composable
fun MatchResultCard() {
    Card(
        modifier = Modifier.padding(top = 2.dp).fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = OrangeLight),
        border = BorderStroke(1.dp, OrangePrimary)
    ) {
        Column(modifier = Modifier.padding(9.dp)) {
            Text("TOP MATCHES", style = MaterialTheme.typography.labelSmall, color = OrangeDark, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Spacer(Modifier.height(5.dp))
            MatchItem("VS", "Vikram Shinde", "Full Stack · KIT College", 94)
            MatchItem("AM", "Aditya Mane", "AI/ML · DY Patil", 89)
        }
    }
}

@Composable
fun MatchItem(initials: String, name: String, info: String, score: Int) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Brush.linearGradient(listOf(OrangePrimary, OrangeDark))),
            contentAlignment = Alignment.Center
        ) {
            Text(initials, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(name, style = MaterialTheme.typography.labelSmall, color = TextPrimary, fontWeight = FontWeight.Bold)
            Text(info, style = MaterialTheme.typography.labelSmall, color = TextSecondary, fontSize = 8.sp)
        }
        Text("$score%", style = MaterialTheme.typography.titleMedium, color = OrangePrimary, fontWeight = FontWeight.Black, fontSize = 12.sp)
    }
}

@Composable
fun ChatInputBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(10.dp, 12.dp, 10.dp, 20.dp)
            .border(BorderStroke(0.5.dp, BorderColor)), 
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(OrangePrimary),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Mic, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .height(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(BgCream)
                .border(1.dp, BorderColor, RoundedCornerShape(10.dp))
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text("Speak or type your message...", style = MaterialTheme.typography.bodySmall, color = Color(0xFFBBBBBB))
        }
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(OrangeLight),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(16.dp))
        }
    }
}

