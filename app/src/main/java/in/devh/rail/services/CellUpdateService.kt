package `in`.devh.rail.services

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.IBinder
import androidx.core.app.NotificationCompat
import `in`.devh.rail.CellTower
import com.slaviboy.iconscompose.R
import fetchCoordinates
import `in`.devh.rail.database.CellDataDB
import `in`.devh.rail.functions.Logs.logD
import `in`.devh.rail.wrappers.CellInfoWrapper
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.*

class CellUpdateService : Service() {
    private val serviceScope = CoroutineScope(Dispatchers.Default + Job())
    private var updateJob: Job? = null
    private val context: Context
        get() = applicationContext


    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "cell_update_channel"
        private const val NOTIFICATION_ID = 1
        const val ACTION_START_SERVICE = "ACTION_START_SERVICE"
        const val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_SERVICE -> startForegroundService()
            ACTION_STOP_SERVICE -> stopSelf()
        }
        return START_STICKY
    }
    @SuppressLint("NewApi")
    private fun startForegroundService() {
        val notification = createNotification("Starting cell updates...")
        startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        val db = CellDataDB()


        // Cancel existing job if any
        updateJob?.cancel()

        // Start new job in serviceScope
        updateJob = serviceScope.launch {
            while (isActive) {
                try {
                    val timeStr = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                    val cellInfoWrapper = CellInfoWrapper(context)
                    val cellData = cellInfoWrapper.getCellInfo()
                    val cellDataStr = "${cellData.mcc}_${cellData.mnc}_${cellData.lac}_${cellData.cid}"
                    db.store(cellData.mcc, cellData.mnc, cellData.lac, cellData.cid)

                    // Update notification
                    val updatedNotification = createNotification(
                        "Last updated at: $timeStr\nCell Data: $cellDataStr"
                    )
                    startForeground(
                        NOTIFICATION_ID,
                        updatedNotification,
                        ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
                    )

                    // Launch fetchCoordinates in IO dispatcher but with a timeout
                    withTimeoutOrNull(4000) { // 4 seconds timeout
                        withContext(Dispatchers.IO) {
                            fetchCoordinates()
                        }
                    }

                    delay(60000)
                } catch (e: Exception) {
                    logD("ForegroundService", "Error in service loop: ${e.message}")
                    delay(5000) // Still delay on error to avoid rapid retries
                }
            }
        }
    }


    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            "Cell Updates",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Shows ongoing cell information updates"
        }

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun createNotification(
        content: String,
        showProgress: Boolean = false,
        progress: Int = 0,
        maxProgress: Int = 0
    ): Notification {
        val intent = Intent(this, CellTower::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Rail service")
            .setContentText("Tower tracking service active.") // Short text for collapsed view
            .setSmallIcon(R.drawable.fi_rr_train)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setStyle(NotificationCompat.BigTextStyle()
                .setBigContentTitle("Rail service")
                .bigText(content)  // Full content for expanded view
                .setSummaryText("Cell tower tracking active"))
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .setAutoCancel(false)

        if (showProgress && maxProgress > 0) {
            builder.setProgress(maxProgress, progress, false)
        }

        return builder.build()
    }


    override fun onDestroy() {
        super.onDestroy()
        updateJob?.cancel()
        serviceScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}

