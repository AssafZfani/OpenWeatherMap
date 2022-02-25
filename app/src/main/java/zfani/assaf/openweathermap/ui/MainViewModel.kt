package zfani.assaf.openweathermap.ui

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import zfani.assaf.openweathermap.data.repository.forecast.ForecastRepository
import zfani.assaf.openweathermap.data.events.forecast.ForecastEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

class MainViewModel(private val forecastRepository: ForecastRepository) : ViewModel() {

    lateinit var location: Location
    val forecastChannel = Channel<ForecastEvent>()

    fun getForecast() = viewModelScope.launch(Dispatchers.IO) {
        forecastChannel.send(ForecastEvent.Loading)
        forecastChannel.send(forecastRepository.getForecast(location.latitude, location.longitude))
    }
}