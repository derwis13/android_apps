package com.example.iot_sensehat

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.iot_sensehat.databinding.FragmentSensorMenuBinding
import com.google.gson.Gson
import com.jjoe64.graphview.series.DataPoint
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class SensorMenuFragment: Fragment() {

    private lateinit var binding: FragmentSensorMenuBinding
    private var filterTimer: Timer? = null

    private var samplesCounter:Int=0
    private var sampleMax:Int?=null
    private var measurement=Measurement(URL("http://192.168.43.39/getMeasurement.php"))
    private var measurementPrecision:Int=3



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding=FragmentSensorMenuBinding.inflate(layoutInflater,container,false)
        binding.temperatureCase.setOnClickListener { view:View->view.findNavController().navigate(SensorMenuFragmentDirections.actionFragmentSensorMenuToFragmentSensorGraph("temperature")) }
        binding.humidityCase.setOnClickListener { view:View->view.findNavController().navigate(SensorMenuFragmentDirections.actionFragmentSensorMenuToFragmentSensorGraph("humidity")) }
        binding.pressureCase.setOnClickListener { view:View->view.findNavController().navigate(SensorMenuFragmentDirections.actionFragmentSensorMenuToFragmentSensorGraph("pressure")) }
        binding.outputMenagerCase.setOnClickListener { view:View->view.findNavController().navigate(SensorMenuFragmentDirections.actionFragmentSensorMenuToFragmentOutputMenager()) }

        if(filterTimer==null)
        {
            filterTimer = Timer()
            val filterTimerTask: TimerTask = object : TimerTask() {
                override fun run() {
                    measurement.connectToServer().start()
                    updateUI()
                }
            }
            filterTimer!!.scheduleAtFixedRate(filterTimerTask,0,1000)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        filterTimer?.cancel()
        filterTimer=null
    }
    @SuppressLint("SetTextI18n")
    private fun updateUI()
    {
        binding.humidityMeasurement.setText("${measurement.getMeasurement().third?.format(measurementPrecision)} %")
        binding.temperatureMeasurement.setText("${measurement.getMeasurement().first?.format(measurementPrecision)} C")
        binding.pressureMeasurement.setText("${measurement.getMeasurement().second?.format(measurementPrecision)} mbar")
    }
    fun Double.format(digits: Int) = "%.${digits}f".format(this)
}