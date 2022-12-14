package com.example.myapplication

import android.content.Context
import android.hardware.SensorManager
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Pair
import android.util.Size
import android.util.SizeF
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.myapplication.databinding.ActivityMapsBinding
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.InputStream
import java.lang.Exception
import java.lang.Math.cos
import java.lang.Math.sin
import java.lang.StrictMath.cos

import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private var signCategoryList= arrayListOf<String>(
        "trafficlight",
        "speedlimit",
        "crosswalk",
        "stop")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }
    fun readFile(file: File){
        val inputStream=FileInputStream(file)
        val text=inputStream.read()
        Log.d("read_text_test","$text")
    }

    fun readFile(filename: String):String {
        val filepath=applicationContext.getExternalFilesDir(null)
        val dir= File(filepath!!.absolutePath+"/annotations/")
        val file=File(dir, filename + ".txt")
        var text:String=""
        try {
            val inputStream = FileInputStream(file)
            //val byteArray = ByteArray(inputStream.length().toInt())
            val byteArray = ByteArray(inputStream.available())
            inputStream.read(byteArray)
            //text = inputStream.read(byteArray).toString()

            text = byteArray.toString()
            inputStream.close()
        }catch (e: FileNotFoundException){
            e.printStackTrace()
        }
        return text

    }
    fun readFile1(filename: String):String {
        val filepath=applicationContext.getExternalFilesDir(null)
        val dir= File(filepath!!.absolutePath+"/annotations/")
        val file=File(dir, filename + ".txt")
        var text:String=""
        try {
            text=file.bufferedReader().use { it.readText() }
            //val inputStream=File("s.txt")

        }catch (e: FileNotFoundException){
            e.printStackTrace()
        }
        return text

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    fun calculateToCoordinates(coordinate:LatLng, angle:Float, distance:Float):LatLng{
        val dx=Math.sin(angle*Math.PI)*distance
        val dy=Math.cos(angle*Math.PI)*distance

        //Log.d("coordinate","$dx, $dy")


        val lat=coordinate.latitude+(180.0/Math.PI)*(dy/6378137.0)
        val lon=coordinate.longitude+(180.0/Math.PI)*(dx/6378137.0)/Math.cos(Math.PI/180.0*coordinate.latitude)
        return LatLng(lat,lon)

    }
    fun calculateToCoordinates(coordinate:LatLng, angle:FloatArray, distance:Double, angle1:Double):LatLng{
        val rotationMatrix=FloatArray(9)
        SensorManager.getRotationMatrixFromVector(rotationMatrix,angle)
        //angle1=angle1.toDouble()

        //Log.d("distnace_","$dx0 $dy0 $distance")
//        val dx=distance*(
//                Math.cos(angle[0].toDouble())*Math.sin(angle[1].toDouble())*Math.cos(angle[2].toDouble())+
//                Math.sin(angle[0].toDouble())*Math.sin(angle[2].toDouble()))
//        val dy=distance*(
//                Math.cos(angle[0].toDouble())*Math.sin(angle[1].toDouble())*Math.sin(angle[2].toDouble())-
//                Math.sin(angle[0].toDouble())*Math.cos(angle[2].toDouble()))

        val dx0=distance*(
                Math.cos(angle[0].toDouble())*Math.sin(angle[1].toDouble())*Math.cos(angle[2].toDouble())+
                Math.sin(angle[0].toDouble())*Math.sin(angle[2].toDouble()))
        val dy0=distance*(
                Math.cos(angle[0].toDouble())*Math.sin(angle[1].toDouble())*Math.sin(angle[2].toDouble())-
                Math.sin(angle[0].toDouble())*Math.cos(angle[2].toDouble()))

//        val dx=dx0*Math.cos(angle1)-dy0*Math.sin(angle1)
//        val dy=dx0*Math.sin(angle1)+dy0*Math.cos(angle1)

//        val dx=dx0*Math.cos(angle1)
//        val dy=dy0

        val dy=dy0*Math.cos(angle1)
        val dx=dx0
//        val dx=distance*rotationMatrix[2]
//        val dy=distance*rotationMatrix[5]
//        val dz=distance*rotationMatrix[8]



//        val dx=coordinate[0]*rotationMatrix[0]+coordinate[1]*rotationMatrix[1]+coordinate[2]*rotationMatrix[2]
//        val dy=coordinate[0]*rotationMatrix[3]+coordinate[1]*rotationMatrix[4]+coordinate[2]*rotationMatrix[5]
//        val dz=coordinate[0]*rotationMatrix[6]+coordinate[1]*rotationMatrix[7]+coordinate[2]*rotationMatrix[8]



        val lat=coordinate.latitude+(180.0/Math.PI)*(dy/6378137.0)
        val lon=coordinate.longitude+(180.0/Math.PI)*(dx/6378137.0)/Math.cos(Math.PI/180.0*coordinate.latitude)

        //val lat=coordinate.latitude+(dy/6378137.0)
        //val lon=coordinate.longitude+(dx/6378137.0)/Math.cos(coordinate.latitude)

        return LatLng(lat,lon)

    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        //val filepath=applicationContext.getExternalFilesDir(null)
        //readFile(File(filepath!!.absolutePath+"/annotations/","annotation_image.txt"))

        // Add a marker in Sydney and move the camera
        val list=readFile1("annotations_videos").split("\n")
//        list.forEach {
//            try {
//                val list1=it.split(";")
//
//                //val coordinate= floatArrayOf(list1[7].toFloat(),list1[8].toFloat(),list1[9].toFloat())
//                val coordinate=LatLng(list1[7].toDouble(),list1[8].toDouble())
//                val orientationAngles= floatArrayOf(list1[10].toFloat(),list1[11].toFloat(),list1[12].toFloat())
//                val dist=list1[13].toDouble()
//                val signCategory=signCategoryList[list1[5].toInt()]
//                val certitude=list1[6]
//
//                //Log.d("annotation","$lat, $lng, $alt, $dist")
//
//                val sign=calculateToCoordinates(coordinate,orientationAngles,dist/1000,list1[13].toDouble())
//                val result=FloatArray(1)
//                Location.distanceBetween(coordinate.latitude,coordinate.longitude,sign.latitude,sign.longitude,result)
//                mMap.addMarker(MarkerOptions().position(sign).title("$signCategory road sign, $certitude %"))
//                mMap.addMarker(MarkerOptions().position(coordinate).title("basic_position $certitude"))
//                Log.d("coordinate","${result[0]}, $dist")
//
//
//
//            }catch (e:Exception){
//
//            }
//        }
        list.forEach {
            try {
                val list1=it.split(";")

                //val coordinate= floatArrayOf(list1[7].toFloat(),list1[8].toFloat(),list1[9].toFloat())
                val coordinate=LatLng(list1[8].toDouble(),list1[9].toDouble())
                val orientationAngles= floatArrayOf(list1[11].toFloat(),list1[12].toFloat(),list1[13].toFloat())
                val dist=list1[14].toDouble()
                val signCategory=signCategoryList[list1[6].toInt()]
                val certitude=list1[7]

                //Log.d("annotation","$lat, $lng, $alt, $dist")

                val sign=calculateToCoordinates(coordinate,orientationAngles,dist/1000,list1[14].toDouble())
                val result=FloatArray(1)
                Location.distanceBetween(coordinate.latitude,coordinate.longitude,sign.latitude,sign.longitude,result)
                mMap.addMarker(MarkerOptions().position(sign).title("$signCategory road sign, $certitude %"))
                //mMap.addMarker(MarkerOptions().position(coordinate).title("basic_position $certitude"))
                Log.d("coordinate","${result[0]}, $dist")



            }catch (e:Exception){

            }
        }

        val sign_1 = LatLng(52.6443724,16.0854135 )
//
//
//        //Toast.makeText(applicationContext,"$lat $lon ",Toast.LENGTH_LONG).show()
//        val sign_2 = calculateToCoordinates(sing_1,1f,100000f)
//
//        mMap.addMarker(MarkerOptions().position(sing_1).title("Marker in Sydney"))
        //mMap.addMarker(MarkerOptions().position(sign_1).title("Actual position"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sign_1))
//        val gc:Geocoder= Geocoder(applicationContext, Locale.getDefault())
//        val adress=gc.getFromLocation(52.500266,16.244431,2)
//        for (adres in adress!!)
//            Log.d("test_","${adres}")
        //Toast.makeText(applicationContext,"adress is save",Toast.LENGTH_LONG).show()
    }
}