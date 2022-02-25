package zfani.assaf.openweathermap.ui.adapter.diffutils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import zfani.assaf.openweathermap.R
import zfani.assaf.openweathermap.data.network.entity.responses.Forecast
import zfani.assaf.openweathermap.databinding.ItemListForecastBinding
import kotlin.math.roundToInt

class ForecastAdapter : RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder>() {

    private var data = listOf<Forecast>()
    private var onItemSelectedListener: ((forecast: Forecast) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        LayoutInflater.from(parent.context)
            .let { ItemListForecastBinding.inflate(it, parent, false) }
            .let { ForecastViewHolder(it) }

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount() = data.size

    fun setData(forecastList: List<Forecast>) {
        val forecastDiffUtilCallback = ForecastDiffUtils(data, forecastList)
        val forecastDiffResult = DiffUtil.calculateDiff(forecastDiffUtilCallback)
        data = forecastList
        forecastDiffResult.dispatchUpdatesTo(this)
    }

    fun setOnItemSelectedListener(onItemSelectedListener: ((forecast: Forecast) -> Unit)?) {
        this.onItemSelectedListener = onItemSelectedListener
    }

    inner class ForecastViewHolder(private val binding: ItemListForecastBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Forecast) = with(binding) {
            dayTv.text = item.day
            forecastAvgTempTv.text = item.avgTemp
            forecastWeatherTv.text = item.weather.first().main
            Glide.with(root.context)
                .load("https://openweathermap.org/img/wn/${item.weather.first().icon}.png")
                .into(forecastImageIv)
            root.setOnClickListener {
                onItemSelectedListener?.invoke(item)
            }
        }
    }
}