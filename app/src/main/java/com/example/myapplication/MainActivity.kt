package com.example.myapplication

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import com.example.myapplication.ui.theme.MyApplicationTheme
import androidx.compose.material3.Text
import android.Manifest
import android.hardware.SensorEventListener
import androidx.compose.material3.Button
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier




class MainActivity : ComponentActivity(), SensorEventListener{

    private val CHANNEL_ID = "notification_channel"
    private val NOTIFICATION_ID = 101
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        if (accelerometer == null) {
            Log.e("Accelerometer", "This device does not have an accelerometer.")
        }

        createNotificationChannel()

        setContent {
            MyApplicationTheme {
                AppNavigation(showNotification = { checkAndShowNotification() })
            }
        }

    }


    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (it.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                // Gravity components on each axis
                val gravityX = 0f
                val gravityY = 0f
                val gravityZ = 9.81f

                // Acceleration minus gravity
                val x = it.values[0] - gravityX
                val y = it.values[1] - gravityY
                val z = it.values[2] - gravityZ

                // Calculate the magnitude of the acceleration vector
                val magnitude = Math.sqrt((x * x + y * y + z * z).toDouble())

                // Define a threshold for "a lot of movement"
                val movementThreshold = 15.0 // This is an example value, adjust based on your testing

                if (magnitude > movementThreshold) {
                    Log.d("Movement", "Significant movement detected. Magnitude: $magnitude")
                    showNotification(magnitude.toFloat())
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Implement if needed
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    @Composable
    fun NotificationScreen() {
        Column(modifier = Modifier.padding(16.dp)) {
            Greeting(name = "Android")
            Button(
                onClick = {
                    checkAndShowNotification()
                },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Show Notification")
            }
        }
    }

    private fun checkAndShowNotification() {
        // For Android 12 (API level 31) and above, check for POST_NOTIFICATIONS permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)) {
                PackageManager.PERMISSION_GRANTED -> {
                    // Permission is granted, you can show the notification
                    showNotification(10.123f)
                }
                else -> {
                    // Permission is not granted, request the permission
                    requestNotificationPermission()
                }
            }
        } else {
            // For versions below Android 12, POST_NOTIFICATIONS permission isn't needed
            showNotification(10.123f)
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            // Permission granted, show the notification
            showNotification(10.123f)
        } else {
            // Permission denied, handle the denial
        }
    }

    private fun requestNotificationPermission() {
        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
    @SuppressLint("MissingPermission")
    private fun showNotification(magnitude: Float) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Movement Alert")
            .setContentText("Significant movement detected: $magnitude")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            notify(NOTIFICATION_ID, builder.build())
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    @Composable
    fun Greeting(name: String) {
        Text(text = "Hello $name!")
    }
}


data class Message(val author: String, val body: String)