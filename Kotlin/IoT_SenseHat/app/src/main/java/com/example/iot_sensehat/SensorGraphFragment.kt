package com.example.iot_sensehat

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.iot_sensehat.databinding.FragmentSensorGraphBinding
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.LegendRenderer
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import java.net.URL
import java.util.*

@Suppress("UNREACHABLE_CODE")
class SensorGraphFragment:Fragment(), AdapterView.OnItemSelectedListener {
    private lateinit var binding:FragmentSensorGraphBinding
    private val sensorList= arrayListOf<String>("temperature","pressure","humidity")
    private var selectSensor:String?=null
    private lateinit var chart:GraphView
    //private lateinit var signal:LineGraphSeries[]
    private lateinit var signal: Array<LineGraphSeries<DataPoint>>

    private var measurement=Measurement(URL("http://192.168.43.39/getMeasurement.php"))
    private var measurementPrecision:Int=3
    private var filterTimer: Timer? = null
    private var k:Int=0
    private var sampleMax:Int = 0
    private var sampleTime:Double=0.1
    var series: LineGraphSeries<DataPoint>?=null
    //var s:ArrayList<DataPoint>?=null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentSensorGraphBinding.inflate(layoutInflater,container,false)

        setDefaultItemOnSpinner(SensorGraphFragmentArgs.fromBundle(requireArguments()))
        chartInit(0.0,100.0,0.0,100.0,"Temperature [C]")

        val sensorSpinner:Spinner=binding.spinner
        val adapter=ArrayAdapter(this.requireContext(),android.R.layout.simple_spinner_item,sensorList).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_item)
            sensorSpinner.adapter=it
        }

        sensorSpinner.onItemSelectedListener=this

        binding.button2.setOnClickListener {
            chart.viewport.setMaxX(100.0)
            updateChartConfiguration() }
        binding.button.setOnClickListener {
            chart.viewport.setMinX(20.0)
            updateChartConfiguration()
        }

        sampleMax= (chart.viewport.getMaxX(false)/0.1).toInt()

        if(filterTimer==null)
        {
            k=0
            filterTimer = Timer()
            val filterTimerTask: TimerTask = object : TimerTask() {
                override fun run() {
                    measurement.connectToServer().start()
                    FilterProcedure()
                }
            }
            filterTimer!!.scheduleAtFixedRate(filterTimerTask,0,100)
        }



        return binding.root
    }
    private fun FilterProcedure() {
        if (k <= sampleMax) {
            val x: Double?=when(selectSensor){
                "temperature"->measurement.getMeasurement().first
                "pressure"->measurement.getMeasurement().second
                "humidity"->measurement.getMeasurement().third
                else -> {null}
            }
            if (x!=null)
            {
                signal[0].appendData(DataPoint(k*sampleTime,x),false,sampleMax)
                chart.onDataChanged(true, true)
                k++
            }

        } else {
            filterTimer!!.cancel()
            filterTimer = null
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        filterTimer?.cancel()
        filterTimer=null
    }


    fun setDefaultItemOnSpinner(arg: SensorGraphFragmentArgs) {
        selectSensor=arg.sensorType
        sensorList.remove(arg.sensorType)
        sensorList.add(0,arg.sensorType)
    }

    override fun onItemSelected(p0: AdapterView<*>, p1: View?, p2: Int, p3: Long) {
        chart.removeAllSeries()
        selectSensor=p0.getItemAtPosition(p2).toString()
        if (selectSensor=="humidity")
            chartInit(0.0,100.0,0.0,100.0,"humidity [%]")
        if (selectSensor=="pressure")
            chartInit(0.0,100.0,260.0,1260.0,"pressure [mbar]")
        if (selectSensor=="temperature")
            chartInit(0.0,100.0,-30.0,105.0,"temperature [C]")
        k=0

    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }
    private fun chartInit(minx:Double,maxx:Double,miny:Double,maxy:Double,unit:String){
        chart=binding.chart
        signal = arrayOf<LineGraphSeries<DataPoint>>(
            LineGraphSeries(arrayOf<DataPoint>())
            //LineGraphSeries(arrayOf<DataPoint>())
        )
        chart.addSeries(signal[0])
        //chart.addSeries(signal[1])
        chart.viewport.isXAxisBoundsManual = true
        chart.viewport.setMinX(minx)
        chart.viewport.setMaxX(maxx)
        chart.viewport.isYAxisBoundsManual = true
        chart.viewport.setMinY(miny)
        chart.viewport.setMaxY(maxy)

        //signal[0].title = "Original test signal"
        signal[0].color = Color.BLUE
        //signal[1].title = "Filtered test signal"
        //signal[1].color = Color.GREEN

        chart.legendRenderer.isVisible = true
        chart.legendRenderer.align = LegendRenderer.LegendAlign.TOP
        chart.legendRenderer.textSize = 30f

        chart.gridLabelRenderer.textSize = 20f
        chart.gridLabelRenderer.verticalAxisTitle = Space(7)+ unit
        chart.gridLabelRenderer.horizontalAxisTitle = Space(11)+"Time [s]"
        chart.gridLabelRenderer.numHorizontalLabels = 9
        chart.gridLabelRenderer.numVerticalLabels = 7
        chart.gridLabelRenderer.padding = 35
    }
    private fun updateChartConfiguration(){
        chart.onDataChanged(true,false)
        Toast.makeText(context,"Update Graph!",Toast.LENGTH_SHORT).show()
    }
    private fun Space(n: Int): String? {
        return String(CharArray(n)).replace('\u0000', ' ')
    }
}