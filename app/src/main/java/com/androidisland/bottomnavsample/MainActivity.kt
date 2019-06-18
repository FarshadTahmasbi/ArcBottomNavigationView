package com.androidisland.bottomnavsample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ViewGroup.LayoutParams.MATCH_PARENT
    }
}
