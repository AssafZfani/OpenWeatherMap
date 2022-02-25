package zfani.assaf.openweathermap.di

import zfani.assaf.openweathermap.data.repository.forecast.ForecastRepository
import zfani.assaf.openweathermap.data.repository.forecast.ForecastRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {
    single<ForecastRepository> { ForecastRepositoryImpl(get()) }
}