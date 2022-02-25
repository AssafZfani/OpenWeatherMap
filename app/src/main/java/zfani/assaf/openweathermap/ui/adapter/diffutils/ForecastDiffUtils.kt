package zfani.assaf.openweathermap.ui.adapter.diffutils

import androidx.recyclerview.widget.DiffUtil
import zfani.assaf.openweathermap.data.network.entity.responses.Forecast

class ForecastDiffUtils(
    private val oldForecastList: List<Forecast>,
    private val newForecastList: List<Forecast>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldForecastList.size

    override fun getNewListSize(): Int = newForecastList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldForecastList[oldItemPosition].temp.min == newForecastList[newItemPosition].temp.min

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldForecastList[oldItemPosition].weather.first().main == newForecastList[newItemPosition].weather.first().main
}