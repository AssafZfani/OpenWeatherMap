package zfani.assaf.openweathermap.data.network.interceptor

import zfani.assaf.openweathermap.Consts
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class RetryInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        var response = chain.proceed(request)
        var tryCount = 0
        while (!response.isSuccessful && tryCount < Consts.RETRY_COUNT) {
            tryCount++
            response.close()
            response = chain.proceed(request)
        }
        // otherwise just pass the original response on
        return response
    }
}