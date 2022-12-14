package com.example.iot_sensehat

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import java.io.InputStreamReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

class Measurement(private var urlServer: URL) {
    private var humidity:Double?=null
    private var temperature:Double?=null
    private var pressure:Double?=null

    fun connectToServer():Thread{
        return Thread {
            try {
                val connection=urlServer.openConnection() as HttpURLConnection
                if (connection.responseCode == 200) {
                    val inputSystem = connection.inputStream
                    val inputStreamReader = InputStreamReader(inputSystem, "UTF-8")
                    val request = Gson().fromJson(inputStreamReader, Request::class.java)
                    takeMeasurement(request)
                    inputStreamReader.close()
                    inputSystem.close()
                } else {
                    //binding.baseCurrency.text = "Failed Connection"
                }
            }catch (e:Exception){

            }
            //Log.d("urlConection", "${connection.responseMessage}")




        }
    }
    fun takeMeasurement(request: Request){
        pressure=request.pressure
        humidity=request.humidity
        temperature=request.temperature
    }
    fun getMeasurement(): Triple<Double?, Double?, Double?> {
        return Triple<Double?,Double?,Double?>(temperature,pressure,humidity)
    }

}