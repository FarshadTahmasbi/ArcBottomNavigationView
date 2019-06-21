package com.androidisland.views.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import com.androidisland.views.ArcBottomNavigationView
import com.androidisland.views.toPixel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ViewGroup.LayoutParams.MATCH_PARENT
        toggle_btn.setOnClickListener {
            arcBottomNav.toggleState()
        }
        arcBottomNav.setOnNavigationItemSelectedListener {
            Log.d("test123", "selected!")
            true
        }
        arcBottomNav.buttonClickListener = {
            Log.d("test123", "Button clicked!")
//            it.state = ArcBottomNavigationView.State.FLAT
//            it.buttonMargin = 16.toPixel()
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

        val m1 = bottomNav.menu.getItem(0)
        val m2 = bottomNav.menu.getItem(1)
        val m3 = bottomNav.menu.getItem(2)
        val m4 = bottomNav.menu.getItem(3)
        val new = bottomNav.menu.add(Menu.NONE, 0, 5, "New!").apply {
            icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_home_black, theme)
            this.isEnabled = false
            this.isVisible = false
        }
    }
}
