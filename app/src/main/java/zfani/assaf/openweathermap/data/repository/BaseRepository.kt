package zfani.assaf.openweathermap.data.repository

import zfani.assaf.openweathermap.data.repository.ApiResult.Success
import zfani.assaf.openweathermap.data.repository.ApiResult.SuccessWithoutResponse
import zfani.assaf.openweathermap.utils.ErrorUtils
import retrofit2.Response
import java.net.UnknownHostException

abstract class BaseRepository {

    protected suspend fun <T> getApiResult(call: suspend () -> Response<T>): ApiResult<T> {
        return try {
            with(call.invoke()) {
                when {
                    isSuccessful -> body()?.let { Success(it) } ?: SuccessWithoutResponse
                    code() == 401 -> ErrorUtils.getUnAuthorisedError()
                    else -> ErrorUtils.getDefaultError()
                }
            }
        } catch (e: UnknownHostException) {
            ErrorUtils.getInternetError()
        } catch (e: Exception) {
            ErrorUtils.getDefaultError()
        }
    }
}