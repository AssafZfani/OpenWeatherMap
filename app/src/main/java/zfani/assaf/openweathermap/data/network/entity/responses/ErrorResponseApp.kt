package zfani.assaf.openweathermap.data.network.entity.responses

import android.os.Parcelable
import androidx.annotation.StringRes
import zfani.assaf.openweathermap.R
import kotlinx.parcelize.Parcelize

@Parcelize
data class ErrorResponseApp(
    val internetError: Boolean = false,
    val sessionExpired: Boolean = false,
    @StringRes val title: Int = R.string.error_default_title,
    @StringRes val message: Int = R.string.error_default_message,
    @StringRes val button: Int = R.string.error_default_button
) : Parcelable