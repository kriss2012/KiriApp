package com.kiriplatform.app.data.services

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.kiriplatform.app.data.local.KiriDatabase
import com.kiriplatform.app.data.remote.ApiClient
import com.kiriplatform.app.data.remote.models.ProjectReviewRequest
import com.kiriplatform.app.data.remote.models.ReferralRequest

class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val database = KiriDatabase.getDatabase(applicationContext)
        val dao = database.offlineActionDao()
        val pending = dao.getPendingActions()

        if (pending.isEmpty()) {
            return Result.success()
        }

        val gson = Gson()
        var hasFailure = false

        for (action in pending) {
            try {
                when (action.actionType) {
                    "PROJECT_REVIEW" -> {
                        val payloadData = gson.fromJson(action.payload, ProjectReviewPayload::class.java)
                        val response = ApiClient.service.submitProjectReview(payloadData.projectId, payloadData.request)
                        if (response.success) {
                            dao.deleteAction(action.id)
                        } else {
                            hasFailure = true
                        }
                    }
                    "REFERRAL" -> {
                        val request = gson.fromJson(action.payload, ReferralRequest::class.java)
                        val response = ApiClient.service.referUser(request)
                        if (response.success) {
                            dao.deleteAction(action.id)
                        } else {
                            hasFailure = true
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                hasFailure = true
            }
        }

        return if (hasFailure) {
            Result.retry()
        } else {
            Result.success()
        }
    }

    data class ProjectReviewPayload(
        val projectId: String,
        val request: ProjectReviewRequest
    )
}
