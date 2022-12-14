package com.example.restapplication

import org.json.JSONArray

interface VolleyResponseListener {
    fun onError(message:String)
    fun onResponse(response:JSONArray)
}