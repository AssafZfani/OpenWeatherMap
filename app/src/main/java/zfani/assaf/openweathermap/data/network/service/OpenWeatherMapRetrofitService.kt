package zfani.assaf.openweathermap.data.network.service

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import zfani.assaf.openweathermap.Consts
import zfani.assaf.openweathermap.data.network.entity.responses.ForecastResponse

interface OpenWeatherMapRetrofitService {

    @GET(Consts.DATA_SOURCE_URL)
    suspend fun getForecast(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("cnt") cnt: String,
        @Query("units") units: String,
        @Query("apiKey") apiKey: String
    ): Response<ForecastResponse>
}