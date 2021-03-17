package com.example.artfestproject1
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.widget.Toast
import com.example.artfestproject1.databinding.ActivitySendBinding
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class SendMailActivity : AppCompatActivity() {
    protected var mMyApp: MyApp? = null
    private var mSession: Session? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_send)
        val binding: ActivitySendBinding = ActivitySendBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // app reference
        mMyApp = this.applicationContext as MyApp

        // val username="dh990922@gmail.com"
        //val password="qfxurezqtrmmvufl"
        binding.send.setOnClickListener{
            val mail="dh990922.dif07@nctu.edu.tw"
            val subject=binding.mailTitle.getText().toString().trim()
            val message=binding.mailContent.getText().toString()
            val javaMailAPI = javamailapi(this, mail, subject,message)

            javaMailAPI.execute()

        }
    }
    override fun onResume(){
        super.onResume()
        // app reference relink
        mMyApp!!.setCurrentActivity(this);
    }
}