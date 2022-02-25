package zfani.assaf.openweathermap.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import zfani.assaf.openweathermap.Consts
import zfani.assaf.openweathermap.data.network.interceptor.LoggingInterceptor
import zfani.assaf.openweathermap.data.network.interceptor.RetryInterceptor
import zfani.assaf.openweathermap.data.network.service.OpenWeatherMapRetrofitService
import okhttp3.OkHttpClient
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val networkModule = module {
    single { provideGson() }
    single { provideHttpLoggingInterceptor() }
    single { provideRetryInterceptor() }
    single { provideOkHttpClient(get(), get()) }
    single { provideOpenWeatherMapService(get(), get()) }
}

fun provideGson(): Gson =
    GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").setPrettyPrinting().setLenient().create()

fun provideHttpLoggingInterceptor() = LoggingInterceptor()

fun provideRetryInterceptor() = RetryInterceptor()

fun provideOkHttpClient(
    loggingInterceptor: LoggingInterceptor,
    retryInterceptor: RetryInterceptor
) = OkHttpClient.Builder().addInterceptor(loggingInterceptor).addInterceptor(retryInterceptor)
    .build()

private fun provideOpenWeatherMapService(
    gson: Gson,
    okHttpClient: OkHttpClient
): OpenWeatherMapRetrofitService =
    Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create(gson))
        .baseUrl(Consts.BASE_URL)
        .client(okHttpClient)
        .build()
        .create(OpenWeatherMapRetrofitService::class.java)