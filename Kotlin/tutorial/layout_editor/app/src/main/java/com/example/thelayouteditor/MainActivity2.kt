package com.example.thelayouteditor

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity2 : AppCompatActivity() {
    public val EXTRA_REPLY:String= "com.example.android.twoactivities.extra.REPLY"
    private lateinit var mReply:EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        val intent=intent
        val message=intent.getStringExtra(MainActivity().EXTRA_MESSAGE)

        var textView:TextView=findViewById(R.id.text_message)
        mReply=findViewById(R.id.editText_second)


        //Toast.makeText(this,MainActivity().EXTRA_MESSAGE,Toast.LENGTH_SHORT).show()

        textView.setText(message)

    }

    fun returnReply(view: View) {
        var reply=mReply.text
        var replyIntent=Intent().also {
            it.putExtra(EXTRA_REPLY,reply)
            setResult(RESULT_OK,it)
            finish()
        }
    }
}