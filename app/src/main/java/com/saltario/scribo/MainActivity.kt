package com.saltario.scribo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.saltario.scribo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}