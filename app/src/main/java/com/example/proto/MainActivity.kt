// MainActivity.kt
package com.example.proto

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import androidx.compose.ui.Alignment

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = getExternalFilesDir(null)

        val modelFile = File(root, "gemma-3n-E4B-it-int4.task")

        if (!modelFile.exists()) {
            val intent = Intent(this, SecondaryActivity::class.java)
            startActivity(intent)
        }

        Log.i("Model Exists", "Model exists at ${modelFile.absolutePath} an its ${modelFile.canRead()} and its size is ${modelFile.length()}")
        val taskOptions = LlmInference.LlmInferenceOptions.builder()
            .setModelPath(modelFile.absolutePath)
            .setMaxTopK(64)
            .build()

        val llmInference = LlmInference.createFromOptions(this, taskOptions)
        setContent {
            ChatScreen(llmInference)
        }
    }
}

@Composable
fun ChatScreen(llmInference: LlmInference) {
    var messages by remember { mutableStateOf(listOf("Welcome to Local Chat!")) }
    var input by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.padding(16.dp)) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = 8.dp)
        ) {
            messages.forEach {
                Text(it, modifier = Modifier.padding(4.dp))
            }

            // ⬇️ Show typing indicator if loading
            if (isLoading) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(16.dp)
                            .padding(end = 8.dp),
                        strokeWidth = 2.dp
                    )
                    Text("Bot is typing...")
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            TextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier.weight(1f),
                enabled = !isLoading // disable while waiting
            )
            Button(
                onClick = {
                    val userMessage = "You: $input"
                    messages = messages + userMessage
                    isLoading = true

                    coroutineScope.launch {
                        val response = try {
                            withContext(Dispatchers.IO) {
                                llmInference.generateResponse(input)
                            }
                        } catch (e: Exception) {
                            Log.e("Inference", "Error generating response", e)
                            "Oops! Something went wrong."
                        }

                        val botMessage = "Bot: $response"
                        messages = messages + botMessage
                        isLoading = false
                    }

                    input = ""
                },
                enabled = input.isNotBlank() && !isLoading // disable if loading or input is empty
            ) {
                Text("Send")
            }
        }
    }
}



