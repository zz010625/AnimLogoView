package com.example.animlogoview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    private lateinit var animLogoView:AnimLogoView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        animLogoView = findViewById(R.id.logo)
        animLogoView.setTextSize(40)
        animLogoView.setTextPadding(30)
        animLogoView.setPicWidthAndHeight(100,100)
        animLogoView.setLogoTexts("13456")
        animLogoView.setPicPath(R.mipmap.zzz)
        animLogoView.startAnim()

    }

}