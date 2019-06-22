package com.androidisland.views.sample

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.androidisland.views.ArcBottomNavigationView
import com.androidisland.views.toPixel
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

/**
 * Created by Farshad Tahmasbi on June 22,2019.
 * Copyright(c) 2019, All rights reserved.
 * https://github.com/FarshadTahmasbi/ArcBottomNavigationView
 * Email: farshad.tmb@gmail.com
 */

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

        size_seek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                arcBottomNav.buttonSize = progress.toPixel()
//                (arcBottomNav.getChildAt(1) as MaterialButton).apply {
//                    iconSize = progress.toPixel().toInt()
//                    iconGravity = MaterialButton.ICON_GRAVITY_TEXT_START
////                    iconPadding = 0
////                    setPadding(iconSize/2, 0,0,0)
//                    Log.d("test123", "progress:${progress.toPixel()}, iconSize=$iconSize")
//                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })
    }
}
