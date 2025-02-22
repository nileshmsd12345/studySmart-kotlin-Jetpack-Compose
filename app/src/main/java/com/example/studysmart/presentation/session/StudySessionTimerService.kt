package com.example.studysmart.presentation.session

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.NotificationCompat
import com.example.studysmart.util.Constants.ACTION_SERVICE_CANCEL
import com.example.studysmart.util.Constants.ACTION_SERVICE_START
import com.example.studysmart.util.Constants.ACTION_SERVICE_STOP
import com.example.studysmart.util.Constants.NOTIFICATION_CHANNEL_ID
import com.example.studysmart.util.Constants.NOTIFICATION_CHANNEL_NAME
import com.example.studysmart.util.Constants.NOTIFICATION_ID
import com.example.studysmart.util.pad
import dagger.hilt.android.AndroidEntryPoint
import java.util.Timer

import javax.inject.Inject
import kotlin.concurrent.fixedRateTimer
import kotlin.time.Duration
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.Duration.Companion.seconds


@AndroidEntryPoint
class StudySessionTimerService : Service() {

    private val binder = StudySessionTimerBinder()

    @Inject
    lateinit var notificationManager: NotificationManager

    @Inject
    lateinit var notificationBuilder: NotificationCompat.Builder

    lateinit var timer: Timer

    var duration: Duration = ZERO
        private set

    var seconds = mutableStateOf("00")
        private set

    var minutes = mutableStateOf("00")
        private set

    var hours = mutableStateOf("00")
        private set

    var currentTimerState = mutableStateOf(TimerState.IDLE)
        private set

    var subjectId = mutableStateOf<Int?>(null)


    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.action.let {
            when (it) {
                ACTION_SERVICE_START -> {
                    startForegroundService()
                    startTimer { h, m, s ->
                        updateNotification(h, m, s)
                    }
                }

                ACTION_SERVICE_STOP -> {
                    stopTimer()

                }

                ACTION_SERVICE_CANCEL -> {
                    stopTimer()
                    cancelTimer()
                    stopForegroundService()

                }

            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startForegroundService() {
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, notificationBuilder.build())

    }

    private fun stopForegroundService() {
        notificationManager.cancel(NOTIFICATION_ID)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()

    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            )

            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun updateNotification(h: String, m: String, s: String) {
        notificationManager.notify(
            NOTIFICATION_ID,
            notificationBuilder.setContentText("$h:$m:$s").build()
        )
    }

    private fun startTimer(
        onTick: (h: String, m: String, s: String) -> Unit
    ) {
        currentTimerState.value = TimerState.STARTED
        timer = fixedRateTimer(initialDelay = 1000L, period = 10L) {
            duration = duration.plus(1.seconds)
            updateTimeUnits()
            onTick(hours.value, minutes.value, seconds.value)
        }
    }

    private fun stopTimer() {
        if (this::timer.isInitialized) {
            timer.cancel()
        }
        currentTimerState.value = TimerState.STOPPED
    }

    private fun cancelTimer() {
        duration = ZERO
        updateTimeUnits()
        currentTimerState.value = TimerState.IDLE
    }

    private fun updateTimeUnits() {
        duration.toComponents { h, m, s, _ ->
            this@StudySessionTimerService.hours.value = h.toInt().pad()
            this@StudySessionTimerService.minutes.value = m.pad()
            this@StudySessionTimerService.seconds.value = s.pad()

        }
    }

    inner class StudySessionTimerBinder : Binder() {
        fun getService(): StudySessionTimerService = this@StudySessionTimerService
    }


}

enum class TimerState {
    IDLE,
    STARTED,
    STOPPED
}