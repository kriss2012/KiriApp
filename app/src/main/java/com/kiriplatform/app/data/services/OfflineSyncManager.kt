package com.kiriplatform.app.data.services

import android.content.Context
import androidx.work.*
import com.google.gson.Gson
import com.kiriplatform.app.data.local.KiriDatabase
import com.kiriplatform.app.data.local.OfflineActionEntity
import java.util.concurrent.TimeUnit

object OfflineSyncManager {

    fun scheduleSync(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "OfflineSyncWork",
            ExistingWorkPolicy.REPLACE,
            syncRequest
        )
    }

    suspend fun queueAction(context: Context, actionType: String, payload: Any) {
        val json = Gson().toJson(payload)
        val database = KiriDatabase.getDatabase(context)
        database.offlineActionDao().insertAction(
            OfflineActionEntity(
                actionType = actionType,
                payload = json
            )
        )
        scheduleSync(context)
    }
}
