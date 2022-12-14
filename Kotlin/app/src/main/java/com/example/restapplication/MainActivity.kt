package com.example.restapplication

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.restapplication.databinding.ActivityLayoutBinding
import org.w3c.dom.Text


class MainActivity : AppCompatActivity() {
    var text=""
    private lateinit var binding: ActivityLayoutBinding
    private lateinit var server:ServerIoT
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ActivityLayoutBinding.inflate(layoutInflater).also{binding=it}
        setContentView(binding.root)
        val editText=binding.inputTextView.text
        binding.button.setOnClickListener { binding.outputTextView.setText(editText) }
        server=ServerIoT(applicationContext)


    }
    fun editUrl(view: View){

        //val doneButton=findViewById<Button>(R.id.button)

//        editText.visibility=View.VISIBLE
//        doneButton.visibility=View.VISIBLE
//        view.visibility=View.GONE

//        editText.requestFocus()
//        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//        imm.showSoftInput(editText, 0)




        //Toast.makeText(applicationContext,"${editText.text}",Toast.LENGTH_SHORT).show()
        //val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        //inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)

    }
}