package zfani.assaf.openweathermap.data.network.interceptor

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import okio.Buffer
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit

class LoggingInterceptor : Interceptor {

    companion object {
        private const val TAG = "API"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        // Request
        val request = chain.request()
        val requestBody = request.body
        Log.d(TAG, "--> ${request.method} ${request.url}")
        if (requestBody == null) {
            Log.d(TAG, "--> END ${request.method}")
        } else {
            val buffer = Buffer()
            requestBody.writeTo(buffer)
            val contentType = requestBody.contentType()
            val charset = contentType?.charset(StandardCharsets.UTF_8) ?: StandardCharsets.UTF_8
            Log.d(TAG, buffer.readString(charset))
        }

        // Response
        val startNs = System.nanoTime()
        val response: Response
        try {
            response = chain.proceed(request)
        } catch (e: Exception) {
            Log.d(TAG, "<-- HTTP FAILED: $e")
            throw e
        }
        val tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)
        val responseBody = response.body
        val contentLength = responseBody?.contentLength()
        Log.d(
            TAG,
            "<-- ${response.code}${if (response.message.isEmpty()) "" else ' ' + response.message} ${response.request.url} (${tookMs}ms)"
        )
        responseBody?.source()?.apply {
            request(Long.MAX_VALUE) // Buffer the entire body.
            if (contentLength != 0L)
                Log.d(
                    TAG,
                    buffer.clone().readString(
                        responseBody.contentType()?.charset(StandardCharsets.UTF_8)
                            ?: StandardCharsets.UTF_8
                    )
                )
        }
        return response
    }
}