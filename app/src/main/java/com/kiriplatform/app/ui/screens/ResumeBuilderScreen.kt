package com.kiriplatform.app.ui.screens

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import com.kiriplatform.app.data.remote.ApiClient
import com.kiriplatform.app.data.remote.models.*
import com.kiriplatform.app.ui.theme.*
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResumeBuilderScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var linkedInUrl by remember { mutableStateOf("") }
    var githubUrl by remember { mutableStateOf("") }
    var targetRole by remember { mutableStateOf("") }

    // Dynamic Lists
    val educationList = remember { mutableStateListOf<ResumeEducation>() }
    val experienceList = remember { mutableStateListOf<ResumeExperience>() }
    val projectsList = remember { mutableStateListOf<ResumeProject>() }
    var skillsInput by remember { mutableStateOf("") }

    // Temporary input fields for adding
    var schoolInput by remember { mutableStateOf("") }
    var degreeInput by remember { mutableStateOf("") }
    var yearInput by remember { mutableStateOf("") }
    var gpaInput by remember { mutableStateOf("") }

    var companyInput by remember { mutableStateOf("") }
    var roleInput by remember { mutableStateOf("") }
    var durationInput by remember { mutableStateOf("") }
    var experienceDescInput by remember { mutableStateOf("") }

    var projectNameInput by remember { mutableStateOf("") }
    var projectTechInput by remember { mutableStateOf("") }
    var projectDescInput by remember { mutableStateOf("") }

    var isOptimizing by remember { mutableStateOf(false) }
    var optimizedResult by remember { mutableStateOf<OptimizedResume?>(null) }

    fun handleOptimizeResume() {
        if (fullName.isBlank() || email.isBlank() || targetRole.isBlank()) {
            Toast.makeText(context, "Full Name, Email and Target Role are required", Toast.LENGTH_SHORT).show()
            return
        }
        scope.launch {
            isOptimizing = true
            try {
                val parsedSkills = skillsInput.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                val request = ResumeGenerateRequest(
                    fullName = fullName,
                    email = email,
                    linkedInUrl = linkedInUrl,
                    githubUrl = githubUrl,
                    education = educationList.toList(),
                    experience = experienceList.toList(),
                    projects = projectsList.toList(),
                    skills = parsedSkills,
                    targetRole = targetRole
                )
                val response = ApiClient.service.generateResume(request)
                if (response.success) {
                    optimizedResult = response.resume
                    Toast.makeText(context, "Resume Optimized Successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Failed to optimize resume", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                isOptimizing = false
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("AI Resume Builder", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text("ATS-Optimized Resume Generator", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // General Info Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Contact Information", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                        OutlinedTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            label = { Text("Full Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email Address") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = targetRole,
                            onValueChange = { targetRole = it },
                            label = { Text("Target Role (e.g. Android Engineer)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = linkedInUrl,
                            onValueChange = { linkedInUrl = it },
                            label = { Text("LinkedIn Profile URL") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = githubUrl,
                            onValueChange = { githubUrl = it },
                            label = { Text("GitHub URL") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Education Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Education Details", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                        educationList.forEach { edu ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("${edu.degree} - ${edu.school}", fontWeight = FontWeight.Bold)
                                    Text("Graduation Year: ${edu.year} | GPA: ${edu.gpa}", style = MaterialTheme.typography.bodySmall)
                                }
                                IconButton(onClick = { educationList.remove(edu) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                        OutlinedTextField(value = schoolInput, onValueChange = { schoolInput = it }, label = { Text("School/University") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = degreeInput, onValueChange = { degreeInput = it }, label = { Text("Degree & Field of Study") }, modifier = Modifier.fillMaxWidth())
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = yearInput, onValueChange = { yearInput = it }, label = { Text("Year") }, modifier = Modifier.weight(1f))
                            OutlinedTextField(value = gpaInput, onValueChange = { gpaInput = it }, label = { Text("GPA/Percentage") }, modifier = Modifier.weight(1f))
                        }
                        Button(
                            onClick = {
                                if (schoolInput.isNotBlank() && degreeInput.isNotBlank() && yearInput.isNotBlank()) {
                                    educationList.add(ResumeEducation(schoolInput, degreeInput, yearInput, gpaInput))
                                    schoolInput = ""
                                    degreeInput = ""
                                    yearInput = ""
                                    gpaInput = ""
                                }
                            },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(Modifier.width(4.dp))
                            Text("Add Education")
                        }
                    }
                }
            }

            // Experience Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Work Experience", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                        experienceList.forEach { exp ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("${exp.role} at ${exp.company}", fontWeight = FontWeight.Bold)
                                    Text(exp.duration, style = MaterialTheme.typography.bodySmall)
                                }
                                IconButton(onClick = { experienceList.remove(exp) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                        OutlinedTextField(value = companyInput, onValueChange = { companyInput = it }, label = { Text("Company Name") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = roleInput, onValueChange = { roleInput = it }, label = { Text("Role/Title") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = durationInput, onValueChange = { durationInput = it }, label = { Text("Duration (e.g. May 2025 - Present)") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(
                            value = experienceDescInput,
                            onValueChange = { experienceDescInput = it },
                            label = { Text("Raw Experience description (what you did)") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3
                        )
                        Button(
                            onClick = {
                                if (companyInput.isNotBlank() && roleInput.isNotBlank()) {
                                    experienceList.add(ResumeExperience(companyInput, roleInput, durationInput, experienceDescInput))
                                    companyInput = ""
                                    roleInput = ""
                                    durationInput = ""
                                    experienceDescInput = ""
                                }
                            },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(Modifier.width(4.dp))
                            Text("Add Experience")
                        }
                    }
                }
            }

            // Projects Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Projects", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                        projectsList.forEach { proj ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(proj.name, fontWeight = FontWeight.Bold)
                                    Text("Tech Stack: ${proj.techStack}", style = MaterialTheme.typography.bodySmall)
                                }
                                IconButton(onClick = { projectsList.remove(proj) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                        OutlinedTextField(value = projectNameInput, onValueChange = { projectNameInput = it }, label = { Text("Project Name") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = projectTechInput, onValueChange = { projectTechInput = it }, label = { Text("Tech Stack (comma separated)") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(
                            value = projectDescInput,
                            onValueChange = { projectDescInput = it },
                            label = { Text("Raw Project Description") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3
                        )
                        Button(
                            onClick = {
                                if (projectNameInput.isNotBlank()) {
                                    projectsList.add(ResumeProject(projectNameInput, projectTechInput, projectDescInput))
                                    projectNameInput = ""
                                    projectTechInput = ""
                                    projectDescInput = ""
                                }
                            },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(Modifier.width(4.dp))
                            Text("Add Project")
                        }
                    }
                }
            }

            // Skills Input
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Skills", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                        OutlinedTextField(
                            value = skillsInput,
                            onValueChange = { skillsInput = it },
                            label = { Text("Skills (comma-separated, e.g. Kotlin, Java, Node.js)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Optimization Button
            item {
                Button(
                    onClick = { handleOptimizeResume() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isOptimizing
                ) {
                    if (isOptimizing) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(20.dp))
                    } else {
                        Text("OPTIMIZE & GENERATE RESUME (ATS formula)")
                    }
                }
            }

            // Optimized Result Output Section
            item {
                AnimatedVisibility(visible = optimizedResult != null) {
                    val result = optimizedResult
                    if (result != null) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text("ATS Assessment", fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleLarge)
                                            Text("Quantified Google formula bullets used", style = MaterialTheme.typography.bodySmall)
                                        }
                                        Surface(
                                            color = MaterialTheme.colorScheme.primary,
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.padding(8.dp)
                                        ) {
                                            Text(
                                                text = "${result.atsScore} / 100",
                                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                                color = MaterialTheme.colorScheme.onPrimary,
                                                fontWeight = FontWeight.Black,
                                                fontSize = 18.sp
                                            )
                                        }
                                    }

                                    Spacer(Modifier.height(16.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Button(
                                            onClick = { exportResumeToPdf(context, result) },
                                            modifier = Modifier.weight(1f),
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                        ) {
                                            Icon(Icons.Default.Download, contentDescription = null)
                                            Spacer(Modifier.width(4.dp))
                                            Text("Save PDF")
                                        }
                                        Button(
                                            onClick = { shareResumeAsText(context, result) },
                                            modifier = Modifier.weight(1f),
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                                        ) {
                                            Icon(Icons.Default.Share, contentDescription = null)
                                            Spacer(Modifier.width(4.dp))
                                            Text("Share Text")
                                        }
                                    }
                                }
                            }

                            // Professional preview
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                    Text("Preview: ATS Optimized Resume", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    
                                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                                        Text(result.fullName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                                        Text("${result.email} | LinkedIn | GitHub", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }

                                    Divider()

                                    Text("PROFESSIONAL SUMMARY", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    Text(result.optimizedSummary, style = MaterialTheme.typography.bodyMedium)

                                    Divider()

                                    Text("EXPERIENCE", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    result.experience.forEach { exp ->
                                        Column {
                                            Text("${exp.role} - ${exp.company} (${exp.duration})", fontWeight = FontWeight.Bold)
                                            exp.bullets.forEach { bullet ->
                                                Text("• $bullet", modifier = Modifier.padding(start = 8.dp, top = 4.dp), style = MaterialTheme.typography.bodyMedium)
                                            }
                                        }
                                    }

                                    Divider()

                                    Text("PROJECTS", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    result.projects.forEach { proj ->
                                        Column {
                                            Text("${proj.name} (Tech Stack: ${proj.techStack})", fontWeight = FontWeight.Bold)
                                            proj.bullets.forEach { bullet ->
                                                Text("• $bullet", modifier = Modifier.padding(start = 8.dp, top = 4.dp), style = MaterialTheme.typography.bodyMedium)
                                            }
                                        }
                                    }

                                    Divider()

                                    Text("SKILLS", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    Text(result.skills.joinToString(", "), style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun shareResumeAsText(context: Context, resume: OptimizedResume) {
    val text = buildString {
        appendLine(resume.fullName)
        appendLine(resume.email)
        appendLine("LinkedIn: ${resume.linkedInUrl}")
        appendLine("GitHub: ${resume.githubUrl}")
        appendLine()
        appendLine("SUMMARY")
        appendLine(resume.optimizedSummary)
        appendLine()
        appendLine("EXPERIENCE")
        resume.experience.forEach { exp ->
            appendLine("${exp.role} at ${exp.company} (${exp.duration})")
            exp.bullets.forEach { appendLine("• $it") }
            appendLine()
        }
        appendLine("PROJECTS")
        resume.projects.forEach { proj ->
            appendLine("${proj.name} - ${proj.techStack}")
            proj.bullets.forEach { appendLine("• $it") }
            appendLine()
        }
        appendLine("SKILLS")
        appendLine(resume.skills.joinToString(", "))
    }

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "${resume.fullName} Resume")
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(intent, "Share Resume"))
}

fun exportResumeToPdf(context: Context, resume: OptimizedResume) {
    val pdfDocument = PdfDocument()
    val paint = Paint()
    
    val titlePaint = Paint().apply {
        textSize = 16f
        isFakeBoldText = true
        color = android.graphics.Color.BLACK
    }
    
    val sectionPaint = Paint().apply {
        textSize = 12f
        isFakeBoldText = true
        color = android.graphics.Color.BLACK
    }

    val subTitlePaint = Paint().apply {
        textSize = 10f
        isFakeBoldText = true
        color = android.graphics.Color.DKGRAY
    }

    val bodyPaint = Paint().apply {
        textSize = 9f
        color = android.graphics.Color.BLACK
    }

    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
    val page = pdfDocument.startPage(pageInfo)
    val canvas: Canvas = page.canvas

    var y = 50f
    
    // Header
    canvas.drawText(resume.fullName.uppercase(), 50f, y, titlePaint)
    y += 18f
    canvas.drawText("${resume.email} | LinkedIn: ${resume.linkedInUrl} | GitHub: ${resume.githubUrl}", 50f, y, bodyPaint)
    y += 10f
    canvas.drawLine(50f, y, 545f, y, paint)
    y += 20f

    // Summary Section
    canvas.drawText("PROFESSIONAL SUMMARY", 50f, y, sectionPaint)
    y += 15f
    val summaryLines = wrapText(resume.optimizedSummary, 495, bodyPaint)
    for (line in summaryLines) {
        canvas.drawText(line, 50f, y, bodyPaint)
        y += 12f
    }
    y += 15f

    // Experience Section
    canvas.drawText("PROFESSIONAL EXPERIENCE", 50f, y, sectionPaint)
    y += 15f
    for (exp in resume.experience) {
        canvas.drawText("${exp.role.uppercase()} - ${exp.company} (${exp.duration})", 50f, y, subTitlePaint)
        y += 14f
        for (bullet in exp.bullets) {
            val bulletLines = wrapText("• $bullet", 480, bodyPaint)
            for (line in bulletLines) {
                canvas.drawText(line, 60f, y, bodyPaint)
                y += 12f
            }
        }
        y += 8f
    }
    y += 10f

    // Projects Section
    canvas.drawText("KEY PROJECTS", 50f, y, sectionPaint)
    y += 15f
    for (proj in resume.projects) {
        canvas.drawText("${proj.name.uppercase()} (Tech Stack: ${proj.techStack})", 50f, y, subTitlePaint)
        y += 14f
        for (bullet in proj.bullets) {
            val bulletLines = wrapText("• $bullet", 480, bodyPaint)
            for (line in bulletLines) {
                canvas.drawText(line, 60f, y, bodyPaint)
                y += 12f
            }
        }
        y += 8f
    }
    y += 10f

    // Skills Section
    canvas.drawText("TECHNICAL SKILLS", 50f, y, sectionPaint)
    y += 15f
    canvas.drawText(resume.skills.joinToString(", "), 50f, y, bodyPaint)

    pdfDocument.finishPage(page)

    val file = File(context.cacheDir, "${resume.fullName.replace(" ", "_")}_Resume.pdf")
    try {
        pdfDocument.writeTo(FileOutputStream(file))
        val authority = "${context.packageName}.provider"
        val uri = FileProvider.getUriForFile(context, authority, file)
        
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Share Resume PDF"))
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Error generating PDF: ${e.message}", Toast.LENGTH_SHORT).show()
    } finally {
        pdfDocument.close()
    }
}

private fun wrapText(text: String, width: Int, paint: Paint): List<String> {
    val words = text.split(" ")
    val lines = mutableListOf<String>()
    var currentLine = ""
    for (word in words) {
        val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
        val measureWidth = paint.measureText(testLine)
        if (measureWidth > width) {
            lines.add(currentLine)
            currentLine = word
        } else {
            currentLine = testLine
        }
    }
    if (currentLine.isNotEmpty()) {
        lines.add(currentLine)
    }
    return lines
}
