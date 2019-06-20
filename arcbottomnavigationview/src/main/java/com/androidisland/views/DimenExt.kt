package com.androidisland.views

import android.content.res.Resources
import android.util.TypedValue

fun Number.toPixel(): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        toFloat(),
        Resources.getSystem().displayMetrics
    )
}

fun Number.toDip(): Float {
    return toFloat() / Resources.getSystem().displayMetrics.density
}