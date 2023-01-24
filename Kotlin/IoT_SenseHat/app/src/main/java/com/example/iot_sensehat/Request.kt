package com.example.iot_sensehat

class Request(
    var temperature: Double,
    var humidity:Double,
    var pressure:Double,
    var pitch:Double,
    var roll:Double,
    var yaw:Double,
    var x_axis:Int,
    var y_axis:Int,
    var middle_button:Int
)