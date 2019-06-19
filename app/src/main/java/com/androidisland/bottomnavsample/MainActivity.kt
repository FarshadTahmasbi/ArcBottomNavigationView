package com.androidisland.bottomnavsample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ViewGroup.LayoutParams.MATCH_PARENT
        toggle.setOnClickListener {
            bottomNavView.toggleTransition()
        }
        bottomNavView.setOnNavigationItemSelectedListener {
//            Log.d("test123", "selected!")
            true
        }
    }
}
