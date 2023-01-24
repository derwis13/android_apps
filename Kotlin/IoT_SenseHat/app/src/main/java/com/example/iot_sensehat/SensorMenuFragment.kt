package com.example.iot_sensehat

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.iot_sensehat.databinding.FragmentSensorMenuBinding
import java.net.URL
import java.util.*


class SensorMenuFragment: Fragment(),RecyclerViewInterface {

    private lateinit var binding: FragmentSensorMenuBinding
    private var filterTimer: Timer? = null
    private lateinit var measurement:Measurement
    private var measurementPrecision:Int=3
    private lateinit var serverAdress:String

    var sensorModels=ArrayList<SensorModel>()

    private lateinit var sensorAdapter:Sensor_RecyclerViewAdapter

    val args: SensorMenuFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        if (savedInstanceState==null)
            sensorModels.clear()

        binding= FragmentSensorMenuBinding.inflate(layoutInflater,container,false)

        serverAdress = args.url

        measurement= Measurement((URL("http://${serverAdress}/server/getMeasurement.php")))

        binding.imageView.setOnClickListener{
            findNavController(it)
                .navigate(
                    SensorMenuFragmentDirections.actionFragmentSensorMenuToFragmentSettings()
                )
        }

        val recyclerView=binding.recyclerView
        setUpSensorModels()

        sensorAdapter=Sensor_RecyclerViewAdapter(context, sensorModels,this)
        recyclerView.adapter=sensorAdapter
        recyclerView.layoutManager=LinearLayoutManager(context)


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
        if(sensorModels.isNotEmpty()) {
            sensorModels[0].sensorMeasurement =
                "${measurement.getMeasurement()[0]?.format(measurementPrecision)} C"
            sensorModels[1].sensorMeasurement =
                "${measurement.getMeasurement()[1]?.format(measurementPrecision)} mbar"
            sensorModels[2].sensorMeasurement =
                "${measurement.getMeasurement()[2]?.format(measurementPrecision)} %"
            sensorModels[3].sensorMeasurement =
                "${measurement.getMeasurement()[3]?.format(measurementPrecision)} rad"
            sensorModels[4].sensorMeasurement =
                "${measurement.getMeasurement()[4]?.format(measurementPrecision)} rad"
            sensorModels[5].sensorMeasurement =
                "${measurement.getMeasurement()[5]?.format(measurementPrecision)} rad"
            sensorModels[6].sensorMeasurement =
                "x axis: ${measurement.getMeasurement()[6]}\n" +
                        "y axis: ${measurement.getMeasurement()[7]}\n" +
                        "middle: ${measurement.getMeasurement()[8]}"


            activity?.runOnUiThread(Runnable {
                // Stuff that updates the UI
                sensorAdapter.notifyDataSetChanged()
            })

        }


    }
    fun Number.format(digits: Int) = "%.${digits}f".format(this)

    private fun setUpSensorModels(){
        val sensorName=resources.getStringArray(R.array.sensor_name)
        val sensorDescription=resources.getStringArray(R.array.sensor_descr)
        val sensorMeasurement=resources.getStringArray(R.array.sensor_measurement)

        for (i in 0..sensorName.size-1){
            sensorModels.add(SensorModel(sensorName[i],sensorDescription[i],sensorMeasurement[i]))
        }
    }

    override fun onItemClick(position: Int,view:View) {
        if(position<6)
            findNavController(view)
                .navigate(SensorMenuFragmentDirections.actionFragmentSensorMenuToFragmentSensorGraph(
                    sensorModels[position].sensorName,"${serverAdress}"))
        if (position == 7) {
            findNavController(view)
                .navigate(SensorMenuFragmentDirections.actionFragmentSensorMenuToFragmentOutputMenager("${serverAdress}"))
        }



    }
}