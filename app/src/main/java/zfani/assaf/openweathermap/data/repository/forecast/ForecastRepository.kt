package zfani.assaf.openweathermap.data.repository.forecast

import zfani.assaf.openweathermap.data.events.forecast.ForecastEvent

interface ForecastRepository {
    suspend fun getForecast(latitude:Double, longitude:Double): ForecastEvent
}
