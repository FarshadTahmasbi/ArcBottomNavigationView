package com.androidisland.views

import android.content.res.Resources
import android.util.TypedValue


/**
 * Created by Farshad Tahmasbi on June 22,2019.
 * Copyright(c) 2019, All rights reserved.
 * https://github.com/FarshadTahmasbi/ArcBottomNavigationView
 * Email: farshad.tmb@gmail.com
 */

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