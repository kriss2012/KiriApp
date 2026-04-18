package com.apex.asg.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apex.asg.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobBoardScreen() {
    val jobs = listOf(
        JobItem("Frontend Developer Intern", "Apex Startups", "Remote", "Internship", "$500 - $800"),
        JobItem("Product Manager", "TechLink Community", "Mumbai", "Full-time", "$2k - $3k"),
        JobItem("Marketing Specialist", "Founder Connect", "Pune", "Part-time", "Equity based")
    )

    Scaffold(
        containerColor = BgCream,
        topBar = {
            TopAppBar(
                title = { Text("Opportunity Board", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BgCream)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* Post Job */ }, containerColor = OrangePrimary, contentColor = Color.White) {
                Icon(Icons.Default.Add, contentDescription = "Post Opportunity")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    "Find Your Next Startup Role",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }
            items(jobs) { job ->
                JobCard(job)
            }
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

data class JobItem(val title: String, val company: String, val location: String, val type: String, val compensation: String)

@Composable
fun JobCard(job: JobItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.BusinessCenter, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(job.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            Text(job.company, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(14.dp))
                    Text(job.location, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                }
                Surface(color = OrangeLight, shape = RoundedCornerShape(6.dp)) {
                    Text(job.type, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, color = OrangeDark, fontWeight = FontWeight.Bold)
                }
            }
            
            Divider(modifier = Modifier.padding(vertical = 12.dp), color = BorderColor)
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(job.compensation, style = MaterialTheme.typography.titleSmall, color = TextPrimary, fontWeight = FontWeight.Bold)
                Button(
                    onClick = { /* Apply */ },
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("Apply", fontSize = 12.sp)
                }
            }
        }
    }
}
