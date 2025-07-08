package com.example.proto

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.proto.ui.theme.ProtoTheme
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import java.io.IOException

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val exists = isModelFileAvailable(this)
        var name = ""
        if (exists){
            name = "Yay"
        }
        else{
            name = "Nay"
        }
        enableEdgeToEdge()
        setContent {
            ProtoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = name,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ProtoTheme {
        Greeting("Android")
    }
}
fun isModelFileAvailable(context: Context): Boolean {
    // Set the configuration options for the LLM Inference task
        val taskOptions = LlmInference.LlmInferenceOptions.builder()
            .setModelPath("assets/model.task")
            .setMaxTopK(64)
            .build()

    // Create an instance of the LLM Inference task
    var llmInference = LlmInference.createFromOptions(context, taskOptions)
    var result = llmInference.generateResponse("Hello")
    return result == null
}
