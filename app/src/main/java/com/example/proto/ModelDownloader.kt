package com.example.proto

import android.util.Log
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

//object ModelDownloader {
//    fun downloadModel(url: String, destFile: File) {
//        val client = OkHttpClient()
//        val token = ""
//        val request = Request.Builder()
//            .url("https://huggingface.co/google/gemma-3n-E4B-it-litert-preview/resolve/main/gemma-3n-E4B-it-int4.task")
//            .addHeader("Authorization", "Bearer $token")
//            .build()
//
//        try {
//            client.newCall(request).execute().use { response ->
//                if (!response.isSuccessful) throw IOException("Unexpected code $response")
//                Log.i("ModelDownloader", "Downloading model to" + destFile.absolutePath)
//                response.body?.byteStream()?.use { input ->
//                    destFile.outputStream().use { output ->
//                        input.copyTo(output)
//                    }
//                }
//                Log.i("ModelDownloader", "Downloading model...")
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//        finally {
//            Log.i("ModelDownloader", "Model downloaded successfully")
//        }
//    }
//}


object ModelDownloader {
    fun downloadModel(
        url: String,
        destFile: File,
        token: String,
        onProgress: (downloaded: Long, total: Long) -> Unit = { _, _ -> }
    ) {
        val client = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.MINUTES)
            .writeTimeout(60, TimeUnit.MINUTES)
            .build()

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected response code $response")

                val body = response.body ?: throw IOException("Empty response body")
                val totalBytes = body.contentLength()

                Log.i("ModelDownloader", "Downloading model to ${destFile.absolutePath}")

                body.byteStream().use { input ->
                    destFile.outputStream().use { output ->
                        val buffer = ByteArray(8 * 1024)
                        var downloadedBytes = 0L
                        var read: Int

                        while (input.read(buffer).also { read = it } != -1) {
                            output.write(buffer, 0, read)
                            downloadedBytes += read
                            onProgress(downloadedBytes, totalBytes)
                        }
                    }
                }

                Log.i("ModelDownloader", "Download complete.")
            }
        } catch (e: Exception) {
            Log.e("ModelDownloader", "Download failed", e)
            throw e
        }
    }
}
