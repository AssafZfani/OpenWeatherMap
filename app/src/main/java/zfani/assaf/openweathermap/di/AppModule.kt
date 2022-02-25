package zfani.assaf.openweathermap.di

import zfani.assaf.openweathermap.ui.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { MainViewModel(get()) }
}

val openWeatherMapAppModules = listOf(
    appModule, networkModule, repositoryModule
)
