package com.example.myapplication

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding

@Composable
fun OtherScreen(navController: NavController, showNotification: () -> Unit) {

    fun saveImageToInternalStorage(uri: Uri, context: Context): String? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            inputStream?.use { stream ->
                val timeStamp = System.currentTimeMillis()
                val fileName = "myImage_$timeStamp.jpg"
                val outputFile = File(context.filesDir, fileName)
                FileOutputStream(outputFile).use { outputStream ->
                    stream.copyTo(outputStream)
                }

                val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
                sharedPreferences.edit().putString("lastImagePath", outputFile.absolutePath).apply()
                outputFile.absolutePath
            }
        } catch (e: Exception) {
            Log.e("saveImage", "Error saving image: ${e.message}")
            null
        }
    }

    fun saveTextToSharedPreferences(text: String, context: Context) {
        val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("savedText", text).apply()
    }

    var title by remember { mutableStateOf("") }
    var selectedImagePath by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
                selectedImagePath = saveImageToInternalStorage(it, context)
            }
        }
    )

    LaunchedEffect(Unit) {
        val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val lastImagePath = sharedPreferences.getString("lastImagePath", null)
        selectedImagePath = lastImagePath
        val savedText = sharedPreferences.getString("savedText", "")
        title = savedText ?: ""
    }

    // Composable for your other view
    // Button to navigate back to the conversation screen
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Button(onClick = {
            // Navigate to "conversation" and pop the back stack up to that destination
            navController.navigate("conversation") {
                popUpTo("conversation") {
                    inclusive = true
                }
            }
        }) {
            Text("Go back to Conversation Screen")
        }

        Spacer(modifier = Modifier.height(16.dp)) // Add spacing between Button and AsyncImage

        if (selectedImagePath?.isNotEmpty() == true) {
            selectedImagePath?.let { path ->
                AsyncImage(
                    model = File(path),
                    contentDescription = "Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(100.dp).clickable {
                            singlePhotoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                )
            }
        }
        else{
            // Placeholder content if lastImagePath is null
            Image(
                painter = painterResource(id = R.drawable.profile_picture),
                contentDescription = "Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clip(CircleShape)
                    .size(100.dp).clickable {
                        singlePhotoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
            )
        }

        TextField(
            value = title,
            onValueChange = {
                title = it
                saveTextToSharedPreferences(title, context)
            },
            label = { Text("Name") }
        )

        Button(
            onClick = showNotification,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Show Notification")
        }
    }
}