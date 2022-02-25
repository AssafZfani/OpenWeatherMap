package zfani.assaf.openweathermap.data.events.forecast

import com.google.gson.JsonObject
import zfani.assaf.openweathermap.R
import zfani.assaf.openweathermap.data.network.entity.responses.ErrorResponseApp
import zfani.assaf.openweathermap.data.network.entity.responses.ForecastResponse

sealed class ForecastEvent {

    object Loading : ForecastEvent()

    data class SuccessForecast(val forecastResponse: ForecastResponse) : ForecastEvent()

    object UncheckedError : ForecastEvent() {
        fun getMessage() = R.string.error_unchecked
    }

    data class Error(val error: ErrorResponseApp) : ForecastEvent()
}