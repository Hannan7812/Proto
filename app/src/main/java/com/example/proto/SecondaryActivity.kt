package com.example.proto

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember // Often used with mutableStateOf
import androidx.compose.runtime.getValue // If you use the `by` delegate
import androidx.compose.runtime.setValue // If you use the `by` delegateimport java.io.File
import java.io.File

class SecondaryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = getExternalFilesDir(null)
        val modelFile = File(root, "gemma-3n-E4B-it-int4.task")

        // State holders for Compose UI
        val progress = mutableStateOf<Float?>(null)
        val message = mutableStateOf("Downloading model...")

        // Set up the UI
        setContent {
            DownloadingModelScreen(progress = progress.value, message = message.value)
        }

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                ModelDownloader.downloadModel(
                    url = "https://huggingface.co/google/gemma-3n-E4B-it-litert-preview/resolve/main/gemma-3n-E4B-it-int4.task",
                    destFile = modelFile,
                    token = "",
                    onProgress = { downloaded, total ->
                        progress.value = downloaded.toFloat() / total
                    }
                )

                withContext(Dispatchers.Main) {
                    finish()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    message.value = "Download failed: ${e.localizedMessage}"
                }
            }
        }

    }
}


@Composable
fun downloadingModelLoadingScreen(){
    Text(
        text = "Downloading model...",
        modifier = Modifier
            .padding(16.dp) // Add padding around the text
            .fillMaxWidth(), // Make the text take the full width available
        color = Color.Blue, // Change text color
        fontSize = 20.sp, // Change font size
        fontWeight = FontWeight.Bold, // Make text bold
        textAlign = TextAlign.Center // Center the text horizontally
    )
    Log.i("ModelDownloader", "Downloading model...")
    // Add a loading Icon or animation here

}


@Composable
fun DownloadingModelScreen(progress: Float?, message: String) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (progress != null) {
            LinearProgressIndicator(progress = progress)
            Spacer(modifier = Modifier.height(16.dp))
            Text("${(progress * 100).toInt()}% downloaded")
        } else {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text(message)
        }
    }
}
