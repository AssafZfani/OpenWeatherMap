package zfani.assaf.openweathermap.data.network.entity.responses

data class ForecastResponse(val city: City, val list: List<Forecast>)

data class City(val name: String)

data class Forecast(
    val temp: Temp,
    val weather: List<Weather>,
    val day: String = "",
    val avgTemp: String = ""
)

data class Temp(val min: Double, val max: Double)

data class Weather(val main: String, val icon: String)