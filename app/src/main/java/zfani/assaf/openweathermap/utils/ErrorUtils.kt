package zfani.assaf.openweathermap.utils

import zfani.assaf.openweathermap.data.repository.ApiResult
import zfani.assaf.openweathermap.R
import zfani.assaf.openweathermap.data.network.entity.responses.ErrorResponseApp

object ErrorUtils {

    fun getInternetError() = ErrorResponseApp(
        internetError = true,
        title = R.string.error_no_internet_title,
        message = R.string.error_no_internet_message,
        button = R.string.error_no_internet_button
    ).let { ApiResult.Error(it) }

    fun getUnAuthorisedError() =
        ErrorResponseApp(
            sessionExpired = true,
            title = R.string.error_auth_title,
            message = R.string.error_auth_message,
            button = R.string.error_auth_button
        ).let { ApiResult.Error(it) }

    fun getDefaultError() = ErrorResponseApp().let { ApiResult.Error(it) }
}