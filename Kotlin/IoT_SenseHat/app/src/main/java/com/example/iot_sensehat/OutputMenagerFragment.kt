package com.example.iot_sensehat

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.iot_sensehat.databinding.FragmentOutputMenagerBinding
import yuku.ambilwarna.AmbilWarnaDialog
import com.android.volley.Request


class OutputMenagerFragment: Fragment() {
    private lateinit var binding:FragmentOutputMenagerBinding
    private var colorPicker:AmbilWarnaDialog?=null
    private var defaultColor: Int =Color.BLUE
    private lateinit var serverAdress:String

    val params: MutableMap<String, String> = HashMap()

    val args: SensorMenuFragmentArgs by navArgs()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentOutputMenagerBinding.inflate(layoutInflater,container,false)

        serverAdress = args.url

        val listView= listOf<View>(
            binding.led00,binding.led01,binding.led02,binding.led03,binding.led04,binding.led05,binding.led06,binding.led07,
            binding.led10,binding.led11,binding.led12,binding.led13,binding.led14,binding.led15,binding.led16,binding.led17,
            binding.led20,binding.led21,binding.led22,binding.led23,binding.led24,binding.led25,binding.led26,binding.led27,
            binding.led30,binding.led31,binding.led32,binding.led33,binding.led34,binding.led35,binding.led36,binding.led37,
            binding.led40,binding.led41,binding.led42,binding.led43,binding.led44,binding.led45,binding.led46,binding.led47,
            binding.led50,binding.led51,binding.led52,binding.led53,binding.led54,binding.led55,binding.led56,binding.led57,
            binding.led60,binding.led61,binding.led62,binding.led63,binding.led64,binding.led65,binding.led66,binding.led67,
            binding.led70,binding.led71,binding.led72,binding.led73,binding.led74,binding.led75,binding.led76,binding.led77)
        for (i in listView)
        {
            i.setOnClickListener {
                i.setBackgroundColor(defaultColor)

                params["axis_x"]=i.tag.toString()[1].toString()
                params["axis_y"]=i.tag.toString()[0].toString()

                params["r"]=defaultColor.red.toString()
                params["g"]=defaultColor.green.toString()
                params["b"]=defaultColor.blue.toString()

                params["reset"]="0"

                sendRequestToServer(params).start()
            }
        }
        binding.imageView.setOnClickListener{
            Navigation.findNavController(it)
                .navigate(
                    OutputMenagerFragmentDirections.actionFragmentOutputMenagerToFragmentSettings()
                )
        }

        binding.button5.setOnClickListener {listView.forEach {
            it.setBackgroundColor(Color.BLACK)

            params["axis_x"]="0"
            params["axis_y"]="0"

            params["r"]="0"
            params["g"]="0"
            params["b"]="0"

            params["reset"]="1"

            sendRequestToServer(params).start()
        }  }
        binding.chooseColorButton.setOnClickListener { openColorPicker() }
        updateColorPreview()
        binding.button3.setOnClickListener { Toast.makeText(context,"change is saved!",Toast.LENGTH_SHORT).show() }
        
        return binding.root
    }
    private fun openColorPicker(){
        colorPicker= AmbilWarnaDialog(context,defaultColor,object:AmbilWarnaDialog.OnAmbilWarnaListener{
            override fun onCancel(dialog: AmbilWarnaDialog?) {
            }

            override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {
                defaultColor=color
                updateColorPreview()
            }
        }).also { it.show() }


    }
    private fun updateColorPreview(){
        binding.chooseColorPreview.setBackgroundColor(defaultColor)
    }
    fun sendRequestToServer(params:Map<String, String>):Thread{
        val url="http://${serverAdress}/server/post.php"
        return Thread{
            val queue = Volley.newRequestQueue(context)
            val request: StringRequest = object : StringRequest(
                Request.Method.POST, url,
                Response.Listener {},
                Response.ErrorListener { })
            {
                override fun getParams(): Map<String, String> {return params}

            }
            queue.add(request);
        }
    }
}
