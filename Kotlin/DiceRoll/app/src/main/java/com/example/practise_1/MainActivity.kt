package com.example.practise_1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {
    var RandomInt_:Int?=null
    lateinit var diceImage:ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val rollButton:Button=findViewById(R.id.roll_button)
        rollButton.setOnClickListener{rollDice()}


        val countButton:Button=findViewById(R.id.count_button)
        countButton.setOnClickListener{countUp(RandomInt_)}

    }

    private fun rollDice(){
        val RandomInt=(1..6).random()
        Toast.makeText(this,"Button clicked",Toast.LENGTH_SHORT).show()
        val resultText: TextView =findViewById(R.id.resultText)

        diceImage=findViewById(R.id.dice_image)
        setImage(RandomInt)


        resultText.text=RandomInt.toString()
        RandomInt_=RandomInt
    }
    private fun countUp(RandomInt: Int?){
        val resultText: TextView=findViewById(R.id.resultText)
        if (RandomInt != null && RandomInt!=6) {
            resultText.text=(RandomInt+1).toString()
        }
        if (RandomInt==null)
            resultText.text=(1).toString()

        setImage(RandomInt?.plus(1))
    }
    private fun setImage(RandomInt: Int?){
        val drawableResource=when(RandomInt){
            1->R.drawable.dice_1
            2->R.drawable.dice_2
            3->R.drawable.dice_3
            4->R.drawable.dice_4
            5->R.drawable.dice_5
            else->R.drawable.dice_6
        }
        diceImage.setImageResource(drawableResource)
    }
}