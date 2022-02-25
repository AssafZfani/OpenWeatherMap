package zfani.assaf.openweathermap

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import zfani.assaf.openweathermap.di.openWeatherMapAppModules

class OpenWeatherMapApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(applicationContext)
            modules(openWeatherMapAppModules)
        }
    }
}