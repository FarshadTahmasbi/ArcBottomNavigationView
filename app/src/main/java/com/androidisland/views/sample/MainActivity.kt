package com.androidisland.views.sample

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.androidisland.views.ArcBottomNavigationView
import com.androidisland.views.toDip
import com.androidisland.views.toPixel
import com.google.android.material.shape.MaterialShapeDrawable
import kotlinx.android.synthetic.main.activity_main.*
import petrov.kristiyan.colorpicker.ColorPicker

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
                selection_text.text = "${it.title}"
                true
            }
            selectedItemId = R.id.menu_home
            buttonClickListener = {
                Toast.makeText(context, "Button Clicked!", Toast.LENGTH_SHORT)
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

        deselect_check.setOnCheckedChangeListener { _, b ->
            arc_bottom_nav.deselectOnButtonClick = b
        }

        button_size_seek.max = (MAX_SIZE - MIN_SIZE).toInt()
        button_size_seek.setOnSeekBarChangeListener(object : SimpleSeekBarChangeListener() {
            override fun onProgressChanged(p0: SeekBar?, progress: Int, p2: Boolean) {
                icon_size_seek.max = progress
                val size = MIN_SIZE + progress
                arc_bottom_nav.buttonSize = size.toPixel()
                button_size_text.text = "Button size:${size.toInt()} dp"
            }
        })
        button_size_seek.progress = (arc_bottom_nav.buttonSize.toDip() - MIN_SIZE).toInt()

        icon_size_seek.max = (MAX_SIZE - MIN_SIZE).toInt()
        icon_size_seek.setOnSeekBarChangeListener(object : SimpleSeekBarChangeListener() {
            override fun onProgressChanged(p0: SeekBar?, progress: Int, p2: Boolean) {
                val size = MIN_SIZE + progress
                arc_bottom_nav.buttonIconSize = size.toPixel()
                icon_size_text.text = "Icon size:${size.toInt()} dp"
            }
        })
        icon_size_seek.progress = (arc_bottom_nav.buttonIconSize.toDip() - MIN_SIZE).toInt()

        button_margin_seek.setOnSeekBarChangeListener(object : SimpleSeekBarChangeListener() {
            override fun onProgressChanged(p0: SeekBar?, progress: Int, p2: Boolean) {
                arc_bottom_nav.buttonMargin = (progress + 8).toPixel()
                button_margin_text.text = "Button margin: ${progress + 8} dp"
            }
        })
        button_margin_seek.progress = (arc_bottom_nav.buttonMargin.toDip() - 8).toInt()

        button_stroke_seek.setOnSeekBarChangeListener(object : SimpleSeekBarChangeListener() {
            override fun onProgressChanged(p0: SeekBar?, progress: Int, p2: Boolean) {
                arc_bottom_nav.buttonStrokeWidth = progress.toPixel()
                button_stroke_text.text = "Button stroke width: $progress dp"
            }
        })
        button_stroke_seek.progress = arc_bottom_nav.buttonStrokeWidth.toDip().toInt()

        val backColor = (arc_bottom_nav.background as MaterialShapeDrawable).fillColor?.defaultColor!!
        back_color.setBackgroundColor(backColor)
        back_color.setOnClickListener { view ->
            openColorDialog(backColor) { color ->
                arc_bottom_nav.background = ColorDrawable(color)
                view.setBackgroundColor(color)
            }
        }

        button_tint_color.setBackgroundColor(arc_bottom_nav.buttonBackgroundTint)
        button_tint_color.setOnClickListener { view ->
            openColorDialog(arc_bottom_nav.buttonBackgroundTint) { color ->
                arc_bottom_nav.buttonBackgroundTint = color
                view.setBackgroundColor(color)
            }
        }

        icon_tint_color.setBackgroundColor(arc_bottom_nav.buttonIconTint)
        icon_tint_color.setOnClickListener { view ->
            openColorDialog(arc_bottom_nav.buttonIconTint) { color ->
                arc_bottom_nav.buttonIconTint = color
                view.setBackgroundColor(color)
            }
        }

        button_stroke_color.setBackgroundColor(arc_bottom_nav.buttonStrokeColor)
        button_stroke_color.setOnClickListener { view ->
            openColorDialog(arc_bottom_nav.buttonStrokeColor) { color ->
                arc_bottom_nav.buttonStrokeColor = color
                view.setBackgroundColor(color)
            }
        }

        ripple_color.setBackgroundColor(arc_bottom_nav.buttonRippleColor)
        ripple_color.setOnClickListener { view ->
            openColorDialog(arc_bottom_nav.buttonRippleColor) { color ->
                arc_bottom_nav.buttonRippleColor = color
                view.setBackgroundColor(color)
            }
        }

        git_text.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.git_url)))
            startActivity(Intent.createChooser(intent, null))
        }
    }

    private fun openColorDialog(defaultColor: Int, listener: (color: Int) -> Unit) {
        ColorPicker(this)
            .setDefaultColorButton(defaultColor)
            .setColors(R.array.colors)
            .setColumns(3)
            .disableDefaultButtons(true)
            .setOnFastChooseColorListener(object : ColorPicker.OnFastChooseColorListener {
                override fun setOnFastChooseColorListener(position: Int, color: Int) {
                    listener(color)
                }

                override fun onCancel() {
                }

            })
            .show()
    }

    private open class SimpleSeekBarChangeListener : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(p0: SeekBar?, progress: Int, p2: Boolean) {
        }

        override fun onStartTrackingTouch(p0: SeekBar?) {
        }

        override fun onStopTrackingTouch(p0: SeekBar?) {
        }
    }
}
