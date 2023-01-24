package com.example.iot_sensehat

import com.google.gson.Gson
import java.io.InputStreamReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

class Measurement(private var urlServer: URL) {
    private var humidity:Double?=null
    private var temperature:Double?=null
    private var pressure:Double?=null
    private var pich:Double?=null
    private var roll:Double?=null
    private var yaw:Double?=null
    private var x_axis_button:Int?=null
    private var y_axis_button:Int?=null
    private var middle_button_button:Int?=null

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
                }
            }catch (e:Exception){

            }
        }
    }
    fun takeMeasurement(request: Request){
        pressure=request.pressure
        humidity=request.humidity
        temperature=request.temperature
        pich=request.pitch
        roll=request.roll
        yaw=request.yaw
        x_axis_button=request.x_axis
        y_axis_button=request.y_axis
        middle_button_button=request.middle_button
    }
    fun getMeasurement(): ArrayList<Number?> {
        return arrayListOf(temperature,pressure,humidity,pich,roll,yaw,x_axis_button,y_axis_button,middle_button_button)
    }


}