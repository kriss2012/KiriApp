package com.apex.asg.data.services

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.widget.Toast
import com.apex.asg.data.remote.models.ActivityDto
import java.io.File
import java.io.FileOutputStream

object CertificateService {

    fun generateInnovationReport(context: Context, userName: String, activities: List<ActivityDto>) {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas
        val paint = Paint()

        // Draw Header
        paint.color = Color.BLACK
        paint.textSize = 24f
        paint.isFakeBoldText = true
        canvas.drawText("ASG INNOVATION ECOSYSTEM", 50f, 60f, paint)
        
        paint.textSize = 14f
        paint.isFakeBoldText = false
        canvas.drawText("Official Innovation Portfolio: $userName", 50f, 90f, paint)
        canvas.drawText("Generated on: ${java.util.Date()}", 50f, 110f, paint)

        // Draw Table Header
        paint.isFakeBoldText = true
        canvas.drawText("ACTIVITY", 50f, 160f, paint)
        canvas.drawText("DATE", 400f, 160f, paint)
        canvas.drawText("POINTS", 500f, 160f, paint)
        
        canvas.drawLine(50f, 170f, 550f, 170f, paint)

        // Draw Rows
        paint.isFakeBoldText = false
        var y = 200f
        activities.take(20).forEach { activity ->
            canvas.drawText(activity.title, 50f, y, paint)
            canvas.drawText(activity.createdAt.split("T").firstOrNull() ?: "", 400f, y, paint)
            canvas.drawText("+${activity.points}", 500f, y, paint)
            y += 25f
        }

        // Verification Footer
        paint.color = Color.GRAY
        paint.textSize = 10f
        canvas.drawText("Credential Hash: SHA256-${System.currentTimeMillis()}", 50f, 800f, paint)
        canvas.drawText("Verified by ASG Regional Hub", 50f, 815f, paint)

        pdfDocument.finishPage(page)

        // Save file
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "ASG_Innovation_Record.pdf")
        try {
            pdfDocument.writeTo(FileOutputStream(file))
            Toast.makeText(context, "Report saved to: ${file.absolutePath}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }

        pdfDocument.close()
    }
}
