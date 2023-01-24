package com.example.iot_sensehat

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
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
    private val sensorList= arrayListOf<String>("Temperature","Pressure","Humidity","Pitch","Roll","Yaw")
    private lateinit var selectSensor:String
    private lateinit var serverAdress:String
    private lateinit var chart:GraphView
    private lateinit var signal: Array<LineGraphSeries<DataPoint>>

    private lateinit var measurement:Measurement
    private var filterTimer: Timer? = null
    private var k:Int=0
    private var sampleMax:Int = 0
    private var sampleTime:Double=0.1

    private var temperatureOrgValue=true
    private var pressureOrgValue=true
    private var humidityOrgValue=true
    private var orientationOrgValue=true

    val args: SensorMenuFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentSensorGraphBinding.inflate(layoutInflater,container,false)


        serverAdress = args.url
        measurement= Measurement((URL("http://${serverAdress}/server/getMeasurement.php")))
        setDefaultItemOnSpinner(SensorGraphFragmentArgs.fromBundle(requireArguments()))
        chartInit(0.0,100.0,0.0,100.0,"Temperature [C]")

        val sensorSpinner:Spinner=binding.spinner
        val adapter=ArrayAdapter(this.requireContext(),android.R.layout.simple_spinner_item,sensorList).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_item)
            sensorSpinner.adapter=it
        }
        binding.seekBar.apply {
            this.max=10
            this.min=1
        }

        sensorSpinner.onItemSelectedListener=this

        binding.seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            var progressChangedValue = 0.0
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                progressChangedValue = progress/10.0
                sampleTime=progressChangedValue
                updateChart(selectSensor)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // TODO Auto-generated method stub
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                Toast.makeText(
                    context, "Sample time : $sampleTime",
                    Toast.LENGTH_SHORT
                ).show()

            }
        })

        binding.imageView.setOnClickListener{
            Navigation.findNavController(it)
                .navigate(
                    SensorGraphFragmentDirections.actionFragmentSensorGraphToFragmentSettings()
                )
        }

        sampleMax= (chart.viewport.getMaxX(false)/sampleTime).toInt()

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
            filterTimer!!.scheduleAtFixedRate(filterTimerTask,0,(sampleTime*1000).toLong())
        }

        binding.pressureSwitch.setOnClickListener{
            pressureOrgValue = !binding.pressureSwitch.isChecked
            if(selectSensor=="Pressure")
                updateChart(selectSensor)
        }
        binding.humiditySwitch.setOnClickListener{
            humidityOrgValue = !binding.humiditySwitch.isChecked
            if(selectSensor=="Humidity")
            updateChart(selectSensor)
        }
        binding.temperatureSwitch.setOnClickListener{
            temperatureOrgValue = !binding.temperatureSwitch.isChecked
            if(selectSensor=="Temperature")
                updateChart(selectSensor)
        }
        binding.orientationSwitch.setOnClickListener{
            orientationOrgValue = !binding.orientationSwitch.isChecked
            if(selectSensor=="Yaw" || selectSensor=="Pitch" || selectSensor=="Roll")
                updateChart(selectSensor)
        }

        return binding.root
    }
    private fun FilterProcedure() {
        if (k <= sampleMax) {
            val x: Number?=when(selectSensor){
                "Temperature"->{
                    if(!temperatureOrgValue && measurement.getMeasurement()[0]!=null)
                        measurement.getMeasurement()[0]!!.toDouble()+273.15
                    else
                        measurement.getMeasurement()[0]
                }
                "Pressure"->{
                    if(!pressureOrgValue && measurement.getMeasurement()[1]!=null)
                        measurement.getMeasurement()[1]!!.toDouble()
                    else
                        measurement.getMeasurement()[1]
                }
                "Humidity"->{
                    if(!humidityOrgValue && measurement.getMeasurement()[2]!=null)
                        measurement.getMeasurement()[2]!!.toDouble()
                    else
                        measurement.getMeasurement()[2]
                }
                "Pitch"->{
                    if(!orientationOrgValue && measurement.getMeasurement()[3]!=null)
                        measurement.getMeasurement()[3]!!.toDouble()*180.0/Math.PI
                    else
                        measurement.getMeasurement()[3]
                }
                "Roll"->{
                    if(!orientationOrgValue && measurement.getMeasurement()[4]!=null)
                        measurement.getMeasurement()[4]!!.toDouble()*180.0/Math.PI
                    else
                        measurement.getMeasurement()[4]
                }
                "Yaw"->{
                    if(!orientationOrgValue && measurement.getMeasurement()[5]!=null)
                        measurement.getMeasurement()[5]!!.toDouble()*180.0/Math.PI
                    else
                        measurement.getMeasurement()[5]
                }
                else -> {null}
            }
            if (x!=null)
            {
                signal[0].appendData(DataPoint(k*sampleTime,x.toDouble()),false,sampleMax)
                chart.onDataChanged(true, true)
                Log.d("value","${k*sampleTime}, ${sampleMax}")
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

        selectSensor=p0.getItemAtPosition(p2).toString()

        updateChart(selectSensor)

    }
    fun updateChart(selectSensor:String?){
        chart.removeAllSeries()
        if (selectSensor=="Humidity")
            if(humidityOrgValue)
                chartInit(0.0,1000.0,0.0,100.0,"humidity [%]")
            else
                chartInit(0.0,1000.0,0.0,100.0,"humidity [0-100]")

        if (selectSensor=="Pressure")
            if(pressureOrgValue)
                chartInit(0.0,1000.0,260.0,1260.0,"pressure [mbar]")
            else
                chartInit(0.0,1000.0,260.0,1260.0,"pressure [hPa]")
        if (selectSensor=="Temperature")
            if(temperatureOrgValue)
                chartInit(0.0,1000.0,-30.0,105.0,"temperature [C]")
            else
                chartInit(0.0,1000.0,-30.0+273.15,105.0+273.15,"temperature [K]")
        if (selectSensor=="Pitch")
            if(orientationOrgValue)
                chartInit(0.0,1000.0,-Math.PI,Math.PI,"pitch [rad]")
            else
                chartInit(0.0,1000.0,-180.0,180.0,"pitch [°]")
        if (selectSensor=="Roll")
            if(orientationOrgValue)
                chartInit(0.0,1000.0,-Math.PI,Math.PI,"roll [rad]")
            else
                chartInit(0.0,1000.0,-180.0,180.0,"roll [°]")
        if (selectSensor=="Yaw")
            if(orientationOrgValue)
                chartInit(0.0,1000.0,-Math.PI,Math.PI,"yaw [rad]")
            else
                chartInit(0.0,1000.0,-180.0,180.0,"yaw [°]")
        k=0
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }
    private fun chartInit(minx:Double,maxx:Double,miny:Double,maxy:Double,unit:String){
        chart=binding.chart
        signal = arrayOf(
            LineGraphSeries(arrayOf<DataPoint>())
        )

        chart.addSeries(signal[0])
        chart.viewport.isXAxisBoundsManual = true
        chart.viewport.setMinX(minx)
        chart.viewport.setMaxX(maxx)
        chart.viewport.isYAxisBoundsManual = true
        chart.viewport.setMinY(miny)
        chart.viewport.setMaxY(maxy)

        signal[0].color = Color.BLUE

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