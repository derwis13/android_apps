package com.example.restapplication

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray

class ServerIoT(context:Context,volleyResponseListener: VolleyResponseListener) {
    private var url:String=""
    private lateinit var listener:VolleyResponseListener
    private lateinit var queue:RequestQueue
    init {
        queue=Volley.newRequestQueue(context.applicationContext)
        listener=volleyResponseListener

    }
    fun getUrl():String {return url}

    fun setUrl(url:String){this.url=url}

    fun getMeasurements() {
        val request =
            JsonArrayRequest(Request.Method.GET, url, null, Response.Listener<JSONArray>() {
                fun onResponse(response: JSONArray?) {
                    // Call ViewModel response listener
                    listener.onResponse(response!!)
                }
            }, Response.ErrorListener {
                fun onErrorResponse(error: VolleyError) {
                    val msg = error.message
                    // Call ViewModel error listener
                    if (msg != null) listener.onError(msg) else listener.onError("UNKNOWN ERROR")
                }
            }
            )
        queue.add(request)
    }



}