package zfani.assaf.openweathermap.data.repository.forecast

import zfani.assaf.openweathermap.data.events.forecast.ForecastEvent
import zfani.assaf.openweathermap.data.network.service.OpenWeatherMapRetrofitService
import zfani.assaf.openweathermap.data.repository.ApiResult
import zfani.assaf.openweathermap.data.repository.BaseRepository

class ForecastRepositoryImpl(
    private var openWeatherMapRetrofitService: OpenWeatherMapRetrofitService,
) : BaseRepository(), ForecastRepository {

    override suspend fun getForecast(latitude: Double, longitude: Double) = with(getApiResult {
        openWeatherMapRetrofitService.getForecast(
            latitude.toString(),
            longitude.toString(),
            "16",
            "metric",
            "d0e768cea24fb97446df2eb979cd7234"
        )
    }) {
        when (this) {
            is ApiResult.Success -> ForecastEvent.SuccessForecast(data)
            is ApiResult.SuccessWithoutResponse -> ForecastEvent.UncheckedError
            is ApiResult.Error -> ForecastEvent.Error(error)
        }
    }
}
