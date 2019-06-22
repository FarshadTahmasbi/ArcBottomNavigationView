package com.androidisland.views.sample

import android.os.Bundle
import android.util.Log
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.androidisland.views.ArcBottomNavigationView
import com.androidisland.views.toDip
import com.androidisland.views.toPixel
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Created by Farshad Tahmasbi on June 22,2019.
 * Copyright(c) 2019, All rights reserved.
 * https://github.com/FarshadTahmasbi/ArcBottomNavigationView
 * Email: farshad.tmb@gmail.com
 */

class MainActivity : AppCompatActivity() {

    companion object {
        private val MIN_SIZE = 24.0f
        private const val MAX_SIZE = 64.0f
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        arc_bottom_nav.apply {
            setOnNavigationItemSelectedListener {
                selection_text.text = "${it.title} selected"
                true
            }
            selectedItemId = R.id.menu_home
            buttonClickListener = {
                Toast.makeText(context, "ArcBottomNAvigationView Button Clicked!", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        state_switch.setOnCheckedChangeListener { compoundButton, checked ->
            if (checked) {
                arc_bottom_nav.state = ArcBottomNavigationView.State.ARC
                compoundButton.text = "Arc State"
            } else {
                arc_bottom_nav.state = ArcBottomNavigationView.State.FLAT
                compoundButton.text = "Flat State"
            }
        }

        button_size_seek.max = (MAX_SIZE - MIN_SIZE).toInt()
        button_size_seek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, progress: Int, p2: Boolean) {
                icon_size_seek.max = progress
                val size = MIN_SIZE + progress
                arc_bottom_nav.buttonSize = size.toPixel()
                button_size_text.text = "Button size:${size.toInt()} dp"
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })
        button_size_seek.progress = (arc_bottom_nav.buttonSize.toDip() - MIN_SIZE).toInt()

        icon_size_seek.max = (MAX_SIZE - MIN_SIZE).toInt()
        icon_size_seek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, progress: Int, p2: Boolean) {
                val size = MIN_SIZE + progress
                arc_bottom_nav.buttonIconSize = size.toPixel()
                icon_size_text.text = "Icon size:${size.toInt()} dp"
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })
        icon_size_seek.progress = (arc_bottom_nav.buttonIconSize.toDip() - MIN_SIZE).toInt()



        Log.d("test123", "${56.toPixel()}")
        Log.d("test123", "${56.toPixel().toDip()}")
//        toggle_btn.setOnClickListener {
//            arcBottomNav.toggleState()
//        }
//        arcBottomNav.apply {
//            setOnNavigationItemSelectedListener {
//                Log.d("test123", "selected!")
//                true
//            }
//            buttonClickListener = {
//                Log.d("test123", "Button clicked!")
////            it.state = ArcBottomNavigationView.State.FLAT
////            it.buttonMargin = 16.toPixel()
//            }
//        }

//        state_switch.setOnCheckedChangeListener { buttonView, isChecked ->
//            if (isChecked) {
//                buttonView.text = "Arc is on"
//                arcBottomNav.state = ArcBottomNavigationView.State.ARC
//            } else {
//                buttonView.text = "Arc is off"
//                arcBottomNav.state = ArcBottomNavigationView.State.FLAT
//
//            }
//        }

//        size_seek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
//            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
////                arcBottomNav.buttonSize = progress.toPixel()
//                arcBottomNav.buttonIconSize = progress.toPixel()
////                (arcBottomNav.getChildAt(1) as MaterialButton).apply {
////                    iconSize = progress.toPixel().toInt()
////                    iconGravity = MaterialButton.ICON_GRAVITY_TEXT_START
//////                    iconPadding = 0
//////                    setPadding(iconSize/2, 0,0,0)
////                    Log.d("test123", "progress:${progress.toPixel()}, iconSize=$iconSize")
////                }
//            }

//            override fun onStartTrackingTouch(seekBar: SeekBar?) {
//            }
//
//            override fun onStopTrackingTouch(seekBar: SeekBar?) {
//            }
//
//        })
    }
}
