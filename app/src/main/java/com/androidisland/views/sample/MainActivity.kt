package com.androidisland.views.sample

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.androidisland.views.ArcBottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toggle_btn.setOnClickListener {
            arcBottomNav.toggleState()
        }
        arcBottomNav.apply {
            setOnNavigationItemSelectedListener {
                Log.d("test123", "selected!")
                true
            }
            buttonClickListener = {
                Log.d("test123", "Button clicked!")
//            it.state = ArcBottomNavigationView.State.FLAT
//            it.buttonMargin = 16.toPixel()
            }
        }

        state_switch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                buttonView.text = "Arc is on"
                arcBottomNav.state = ArcBottomNavigationView.State.ARC
            } else {
                buttonView.text = "Arc is off"
                arcBottomNav.state = ArcBottomNavigationView.State.FLAT

            }
        }
    }
}
