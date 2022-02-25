package zfani.assaf.openweathermap.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import zfani.assaf.openweathermap.R
import zfani.assaf.openweathermap.data.events.forecast.ForecastEvent
import zfani.assaf.openweathermap.data.network.entity.responses.ErrorResponseApp
import zfani.assaf.openweathermap.data.network.entity.responses.Forecast
import zfani.assaf.openweathermap.data.network.entity.responses.ForecastResponse
import zfani.assaf.openweathermap.databinding.ActivityMainBinding
import zfani.assaf.openweathermap.ui.adapter.diffutils.ForecastAdapter
import zfani.assaf.openweathermap.utils.viewBinding
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityMainBinding::inflate)
    private val viewModel by viewModel<MainViewModel>()
    private var alertLocationDialog: AlertDialog? = null

    private val permissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            if (result) if (isLocationEnabled()) getCurrentLocation() else showLocationMessage()
            else showPermissionSnackBar(R.string.permission_denied)
        }

    private val locationRequest =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (!isLocationEnabled()) showLocationMessage()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        checkPermissions()
        subscribeUI()
    }

    private fun checkPermissions() {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        if (ContextCompat.checkSelfPermission(
                this,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        ) if (isLocationEnabled()) getCurrentLocation() else showLocationMessage()
        else permissionRequest.launch(permission)
    }

    private fun showPermissionSnackBar(@StringRes text: Int) {
        val view: View = findViewById(android.R.id.content)
        Snackbar.make(view, text, Snackbar.LENGTH_INDEFINITE)
            .setTextColor(Color.WHITE)
            .setAction(R.string.go_to_settings) {
                val intent = Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", packageName, null)
                )
                startActivity(intent)
            }
            .show()
    }

    private fun isLocationEnabled() =
        with(getSystemService(Context.LOCATION_SERVICE) as LocationManager) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) isLocationEnabled
            else isProviderEnabled(LocationManager.GPS_PROVIDER)
        }

    private fun showLocationMessage() {
        if (alertLocationDialog == null) {
            alertLocationDialog = AlertDialog.Builder(this).setTitle(R.string.location_title)
                .setMessage(R.string.location_message)
                .setNegativeButton(R.string.location_ok) { _, _ ->
                    locationRequest.launch(
                        Intent(
                            Settings.ACTION_LOCATION_SOURCE_SETTINGS
                        )
                    )
                }
                .setCancelable(false).create()
        }
        alertLocationDialog?.show()
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        LocationServices.getFusedLocationProviderClient(this).lastLocation.addOnSuccessListener { loc: Location? ->
            loc?.let {
                viewModel.apply {
                    location = it
                    getForecast()
                }
            } ?: toast(R.string.location_title)
        }
    }

    private fun subscribeUI() {
        lifecycleScope.launch {
            for (event in viewModel.forecastChannel) {
                hideLoading()
                when (event) {
                    is ForecastEvent.Loading -> showLoading()
                    is ForecastEvent.SuccessForecast -> updateUI(event.forecastResponse)
                    is ForecastEvent.Error -> showError(event.error) {
                        viewModel.getForecast()
                    }
                    is ForecastEvent.UncheckedError -> toast(event.getMessage())
                }
            }
        }
    }

    private fun updateUI(forecastResponse: ForecastResponse) = with(binding) {
        cityTv.text = forecastResponse.city.name
        forecastsRv.adapter = ForecastAdapter().apply {
            setData(forecastResponse.list.mapIndexed { index, forecast ->
                Forecast(
                    forecast.temp,
                    forecast.weather,
                    SimpleDateFormat(
                        "EEE",
                        Locale.getDefault()
                    ).format(System.currentTimeMillis() + index * TimeUnit.DAYS.toMillis(1)),
                    getString(R.string.average_temp, ((forecast.temp.max + forecast.temp.min) / 2).roundToInt())
                )
            })
            setOnItemSelectedListener {
                showSharingDialog("${forecastResponse.city.name}: ${it.day}: ${it.avgTemp}")
            }
        }
    }

    private fun showLoading() = with(binding) {
        if (progressBar.isGone) {
            window?.setFlags(
                android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
            progressBar.isVisible = true
        }
    }

    private fun hideLoading() = with(binding) {
        if (progressBar.isVisible) {
            window?.clearFlags(android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            progressBar.isVisible = false
        }
    }

    private fun showError(error: ErrorResponseApp, tryAgainListener: (() -> Unit)) {
        AlertDialog.Builder(this)
            .setCancelable(false)
            .setTitle(error.title)
            .setMessage(error.message)
            .setPositiveButton(error.button) { dialog, _ ->
                dialog.dismiss()
                if (error.internetError) tryAgainListener.invoke()
            }
            .create()
            .show()
    }

    private fun toast(@StringRes message: Int) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun showSharingDialog(text: String) {
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, text)
        startActivity(Intent.createChooser(intent, "Share with:"))
    }
}