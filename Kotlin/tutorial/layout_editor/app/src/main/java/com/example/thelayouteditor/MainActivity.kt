package com.example.thelayouteditor

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    val extra_message:String="Message"
    public val EXTRA_MESSAGE:String = "com.example.android.twoactivities.extra.MESSAGE"
    public val TEXT_REQUEST=1
    private lateinit var mReplyHeadTextView: TextView
    private lateinit var mReplyTextView: TextView
    private lateinit var mMessageEditText:EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mMessageEditText=findViewById(R.id.editText_main)
        mReplyHeadTextView=findViewById(R.id.text_header_reply)
        mReplyTextView=findViewById(R.id.text_message_reply)

    }
    fun showToast(view:View){

    }
    fun countUp(view: View){

    }

    fun launchSecondActivity(view: View) {
        Toast.makeText(this,"click",Toast.LENGTH_SHORT).show()
        var intent=Intent(this,MainActivity2::class.java)
        val message=mMessageEditText.text.toString()
        intent.putExtra(EXTRA_MESSAGE,message)
        startActivityForResult(intent,TEXT_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==TEXT_REQUEST)
            if(resultCode== RESULT_OK){
                var reply=data!!.getStringExtra(MainActivity2().EXTRA_REPLY)
                mReplyHeadTextView.visibility=View.VISIBLE
                mReplyTextView.setText(reply)
                mReplyTextView.visibility=View.VISIBLE

            }
    }
}