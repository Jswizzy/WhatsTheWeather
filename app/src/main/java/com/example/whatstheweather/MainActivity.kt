package com.example.whatstheweather

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.net.URL
import java.net.URLEncoder
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun getWeather(view: View) {
        val city = URLEncoder.encode(locationEditText.text.toString(), "UTF-8")
        val task = DownloadTask()
        val apiGetWeather = "http://api.openweathermap.org/data/2.5/weather?q=$city&APPID=${BuildConfig.ApiKey}"
        val s = task.execute(apiGetWeather).get()

        if (s == null) {
            weatherTextView.text = NOT_FOUND
            temperatureTextView.text = ""
            Toast.makeText(this, NOT_FOUND, Toast.LENGTH_SHORT).show()
        } else {
            val json = JSONObject(s)
            val sb = StringBuilder()
            val weather = json.getJSONArray("weather")

            (0 until weather.length()).forEach { i ->
                weather.getJSONObject(i).apply {
                    sb.appendln("${get("main")}: ${get("description")}")
                }
            }

            val temp = json.getJSONObject("main").getDouble("temp").kelvinToFahrenheit()

            weatherTextView.text = sb.toString()
            temperatureTextView.text = temp
        }

        // Hide software keyboard
        val mgr = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        mgr.hideSoftInputFromWindow(locationEditText.windowToken, 0)
    }

    private fun Double.kelvinToFahrenheit(): String? {
        val f = (this - 273.1) * 9 / 5 + 32 // convert kelvin to fahrenheit
        return DecimalFormat("#.##°F").format(f) // format output
    }


    companion object {
        class DownloadTask : AsyncTask<String, Unit, String?>() {
            override fun doInBackground(vararg url: String): String? {
                return URL(url.first()).runCatching { readText() }.getOrNull()
            }
        }
    }
}

private const val NOT_FOUND = "Location Not Found"