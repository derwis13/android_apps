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
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
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
        FirebaseApp.initializeApp(this)

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
    fun readDatabase(){

        var database: DatabaseReference = Firebase.database.reference
        database.get().addOnCompleteListener {
            if(it.result.exists()){
                var dataSnapshot=it.result
                dataSnapshot.children.forEach{
                    it.child("frames").children.forEach {
                        it.child("detectedObjects").children.forEach{
                            Log.d("firebase","${it.child("distance").value}")
                        }

                    }
                    //
                }

            }
        }
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


        val dx0=distance*(
                Math.cos(angle[0].toDouble())*Math.sin(angle[1].toDouble())*Math.cos(angle[2].toDouble())+
                Math.sin(angle[0].toDouble())*Math.sin(angle[2].toDouble()))
        val dy0=distance*(
                Math.cos(angle[0].toDouble())*Math.sin(angle[1].toDouble())*Math.sin(angle[2].toDouble())-
                Math.sin(angle[0].toDouble())*Math.cos(angle[2].toDouble()))

//        val dx=dx0*Math.cos(angle1)-dy0*Math.sin(angle1)
//        val dy=dx0*Math.sin(angle1)+dy0*Math.cos(angle1)

        val dx=dx0*Math.cos(angle1)
        val dy=dy0

//        val dy=dy0*Math.cos(angle1)
//        val dx=dx0


//        val lat=coordinate.latitude+(180.0/Math.PI)*(dy/6378137.0)
//        val lon=coordinate.longitude+(180.0/Math.PI)*(dx/6378137.0)/Math.cos(Math.PI/180.0*coordinate.latitude)

        val lat=coordinate.latitude+(dy/6378137.0)
        val lon=coordinate.longitude+(dx/6378137.0)/Math.cos(coordinate.latitude)

        return LatLng(lat,lon)

    }
    override fun onMapReady(googleMap: GoogleMap) {

        mMap = googleMap


        // Add a marker in Sydney and move the camera
//        val list=readFile1("annotations_videos").split("\n")

        readDatabase()

//        list.forEach {
//            try {
//                val list1=it.split(";")
//
//                //val coordinate= floatArrayOf(list1[7].toFloat(),list1[8].toFloat(),list1[9].toFloat())
//                val coordinate=LatLng(list1[8].toDouble(),list1[9].toDouble())
//                val orientationAngles= floatArrayOf(list1[11].toFloat(),list1[12].toFloat(),list1[13].toFloat())
//                val dist=list1[14].toDouble()
//                val signCategory=signCategoryList[list1[6].toInt()]
//                val certitude=list1[7]
//
//                //Log.d("annotation","$lat, $lng, $alt, $dist")
//
//                val sign=calculateToCoordinates(coordinate,orientationAngles,dist/100,list1[14].toDouble())
//                val result=FloatArray(1)
//                Location.distanceBetween(coordinate.latitude,coordinate.longitude,sign.latitude,sign.longitude,result)
//                //mMap.addMarker(MarkerOptions().position(sign).title("$signCategory road sign, $certitude %"))
//                if(signCategory=="stop"){
//                    Log.d("position","$coordinate")
//                    mMap.addMarker(MarkerOptions().position(coordinate).title("basic_position $certitude $signCategory"))
//                }
//                Log.d("coordinate"," ${list1[0]} ${list1[1]} ${list1[6]} ${list1[7]} ${result[0]}, $dist")
//
//
//
//            }catch (e:Exception){
//
//            }
//        }

        val sign_1 = LatLng(52.6443724,16.0854135 )
        val sing_s=LatLng(52.64478,16.0860804)
        val sing_p=LatLng(52.6448729,16.0863454)
//        0.52734375;52.64478;16.0860804
//        52.6448729;16.0863454
//
//
//        //Toast.makeText(applicationContext,"$lat $lon ",Toast.LENGTH_LONG).show()
//        val sign_2 = calculateToCoordinates(sing_1,1f,100000f)
//
//        mMap.addMarker(MarkerOptions().position(sing_1).title("Marker in Sydney"))
        //mMap.addMarker(MarkerOptions().position(sign_1).title("Actual position"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sign_1))
        //mMap.addMarker(MarkerOptions().position(sing_s).title("Start position"))
        //mMap.addMarker(MarkerOptions().position(sing_p).title("Stop position"))
//        val gc:Geocoder= Geocoder(applicationContext, Locale.getDefault())
//        val adress=gc.getFromLocation(52.500266,16.244431,2)
//        for (adres in adress!!)
//            Log.d("test_","${adres}")
        //Toast.makeText(applicationContext,"adress is save",Toast.LENGTH_LONG).show()
    }
}