package com.androidisland.views.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.androidisland.views.ArcBottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ViewGroup.LayoutParams.MATCH_PARENT
        toggle_btn.setOnClickListener {
            bottomNavView.toggleTransition()
        }
        bottomNavView.setOnNavigationItemSelectedListener {
            Log.d("test123", "selected!")
            true
        }
        bottomNavView.buttonClickListener = {
            Log.d("test123", "Button clicked!")
            it.state = ArcBottomNavigationView.State.FLAT
        }
        state_switch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                buttonView.text = "Arc is on"
                bottomNavView.state = ArcBottomNavigationView.State.ARC
            } else {
                buttonView.text = "Arc is off"
                bottomNavView.state = ArcBottomNavigationView.State.FLAT

            }
        }
    }
}
