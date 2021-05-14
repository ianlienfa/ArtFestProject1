package com.example.artfestproject1

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import com.example.artfestproject1.databinding.ActivityDetailBinding
class DetailActivity : AppCompatActivity() {
    protected var mMyApp: MyApp? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // app reference
        mMyApp = this.applicationContext as MyApp

        val binding = ActivityDetailBinding.inflate(layoutInflater)
        val rootview = binding.root
        setContentView(rootview)
        binding.nfcOpen.setOnClickListener {

            val intent_to_nfc = Intent(this, ExplainActivity::class.java)
            intent_to_nfc.putExtra("fromWhere", "detail")
            startActivity(intent_to_nfc)

        }
        binding.tomain.setOnClickListener {

            val intent_to_main = Intent(this, MainActivity::class.java)
            startActivity(intent_to_main)

        }
    }
}