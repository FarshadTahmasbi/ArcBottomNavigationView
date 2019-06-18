package com.androidisland.bottomnavsample

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.animation.BounceInterpolator
import androidx.core.view.ViewCompat
import androidx.core.view.children
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView

class MyView(context: Context?, attrs: AttributeSet?) : BottomNavigationView(context, attrs) {

    companion object {
        private const val STATE_CURVED = 1
        private const val STATE_RECT = 2
        private const val CURVE_MAX_POINTS = 100
    }


    private var state = STATE_CURVED
    private var animator: ValueAnimator? = null
    private lateinit var pointSegments: MutableMap<Int, MutableList<PointF>>
    val controlPath = Path()

    val margin = 20.0f
    val radius = 80.0f

    private lateinit var currentPath: Path
    private val curvePath = Path()
    private val rectPath = Path()
    //For debug purpose only
    private val circle = Path()

    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0x55000000
        style = Paint.Style.FILL
    }

    val controlPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLUE
        style = Paint.Style.FILL
        strokeWidth = 12.0f
    }

    init {
        ViewCompat.setElevation(this, 0.0f)
        postDelayed({
            animateState()
        }, 1000)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
//        curvePath.apply {
//            reset()
//            val start = PointF(0.0f, radius)
//            val p1 = PointF(w / 2 - 1.2f * radius - 4.0f * margin, start.y)
//            val p2 = PointF(w / 2.toFloat() - 0.8f * radius, start.y)
//            val p3 = PointF(w / 2 - 1.0f * radius - margin * .8f, start.y + radius + margin)
//            val p4 = PointF(w / 2.toFloat(), start.y + radius + margin)
//
//            moveTo(start.x, start.y)
//            lineTo(p1.x, p1.y)
//            cubicTo(p2.x, p2.y, p3.x, p3.y, p4.x, p4.y)
//
//            val p5 = PointF(w - p3.x, p3.y)
//            val p6 = PointF(w - p2.x, p2.y)
//            val p7 = PointF(w - p1.x, p1.y)
//
//            cubicTo(p5.x, p5.y, p6.x, p6.y, p7.x, p7.y)
//            lineTo(w.toFloat(), start.y)
//            lineTo(w.toFloat(), h.toFloat())
//            lineTo(start.x, h.toFloat())
//            close()
//
//            rectPath.apply {
//                reset()
//                moveTo(0.0f, radius)
//                lineTo(w.toFloat(), radius)
//                lineTo(w.toFloat(), h.toFloat() + radius)
//                lineTo(0.0f, h.toFloat() + radius)
//                close()
//            }
//
//            circle.apply {
//                reset()
//                addCircle(w / 2.toFloat(), radius, radius, Path.Direction.CW)
//            }
//
//            controlPath.apply {
//                //                moveTo(start.x, start.y)
//                moveTo(p1.x, p1.y)
//                lineTo(p2.x, p2.y)
//                lineTo(p3.x, p3.y)
//                lineTo(p4.x, p4.y)
//                lineTo(p5.x, p5.y)
//                lineTo(p6.x, p6.y)
//                lineTo(p7.x, p7.y)
//            }
//        }

//        currentPath = rectPath

        /********************/
//        val pm = PathMeasure()
//        val point = FloatArray(2) { 0.0f }
//
//        val corners = getVisibleCorners()
//        val curveStart = curveStart(w.toFloat())
//        val curveEnd = curveEnd(w.toFloat())
//
//        points.add(PointF(corners[0].x, corners[1].y))
//        points.add(PointF(curveStart.x, curveStart.y))
//
//        pm.setPath(createCurvePath(w.toFloat()), false)
//        for (index in 0..CURVE_MAX_POINTS) {
//            pm.getPosTan(pm.length * index / CURVE_MAX_POINTS.toFloat(), point, null)
//            points.add(PointF(point[0], point[1]))
//        }
//
//        points.add(PointF(curveEnd.x, curveEnd.y))
//        points.add(PointF(corners[1].x, corners[1].y))
//
//        points.add(PointF(corners[2].x, corners[2].y))
//        points.add(PointF(corners[3].x, corners[3].y))
//        points.add(PointF(corners[0].x, corners[0].y))

        pointSegments = createPointSegments(w.toFloat())
        currentPath = pointSegmentsToPath(pointSegments)
        Log.d("test123", "${pointSegments.size}")

//            .apply { setPath(curvePath, false) }
//        currentPath.reset()
//        for (index in 0..10000) {
//            val distance = pm.length * index / 1000.0f
//            pm.getPosTan(distance, point, null)
//            if (isCorner(point)) {
//                Log.d("test37", "corner found->[${point[0]}, ${point[1]}]")
//            }
//            if (points.lastIndex > 1) {
//                if (points.last().x == point[0] || points.last().y == point[1]) {
//                    continue
//                } else {
//                    points.add(PointF(point[0], point[1]))
//                }
//            } else {
//                points.add(PointF(point[0], point[1]))
//            }
//            if (index == 0) currentPath.moveTo(point[0], point[1])
//            else currentPath.lineTo(point[0], point[1])
//        }
//
//        Log.d("test80", "point size= ${points.size}")
//        Log.d("test77", "size-> $w x $h")
//        points.forEachIndexed { i, p ->
//            Log.d("test85", "point[$i]=$p")
//        }

//        val pm = PathMeasure()
//        pm.setPath(rectPath, false)
//        Log.d("test123", "${pm.length}")
//        pm.setPath(curvePath, false)
//        Log.d("test123", "${pm.length}")
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        children.forEach {
            if (it is BottomNavigationMenuView) {
                (it.layoutParams as LayoutParams).gravity = Gravity.BOTTOM
                return@forEach
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(width, measuredHeight + radius.toInt())
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.apply {
            //            clipPath(path)
//            clipRect(Rect(0, 0, 100, 100))
//            drawPath(path, paint)
//            drawPath(controlPath, paint2)
//            drawPath(circle, cPath)
        }
//        canvas?.clipRect(Rect(0, 0, 100, 100))
//        canvas?.clipPath(path)
        super.onDraw(canvas)
    }

    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)
    }

    override fun draw(canvas: Canvas?) {
        canvas?.apply {
            //            drawPath(circle, circlePaint)
            drawPath(currentPath, controlPaint)
//            clipPath(currentPath)
        }
//        super.draw(canvas)
    }

    private fun animateState() {
        animator?.apply {
            cancel()
        }

        animator = ValueAnimator.ofFloat(1.0f, 0.0f)
            .apply {
                duration = 1000
                interpolator = BounceInterpolator()
                val bound = getVisibleBound()
                val top = bound.top
                val curvePoints = pointSegments[1]?.toMutableList()!!
                val dy = FloatArray(curvePoints.size) { index ->
                    if (isCorner(curvePoints[index])) 0.0f
                    else curvePoints[index].y - top
                }
                addUpdateListener {
                    currentPath.reset()
                    val percent = it.animatedValue as Float
                    pointSegments[1]?.forEachIndexed { index, point ->
//                        if (point.y > bound.top && point.y < bound.bottom) {
                            point.y = top + dy[index] * percent
//                        }
                    }
                    currentPath = pointSegmentsToPath(pointSegments)
                    invalidate()
                }
                start()
            }
    }

    private fun isCorner(point: FloatArray): Boolean {
        getVisibleCorners().forEach {
            if (point[0] == it.x && point[1] == it.y) return true
        }
        return false
    }

    private fun isCorner(point: PointF) = isCorner(floatArrayOf(point.x, point.y))


    private fun getVisibleBound(): RectF {
        return RectF(0.0f, radius, width.toFloat(), height.toFloat())
    }

    private fun getVisibleCorners(): List<PointF> {
        val bound = getVisibleBound()
        return listOf(
            PointF(bound.left, bound.top),
            PointF(bound.right, bound.top),
            PointF(bound.right, bound.bottom),
            PointF(bound.left, bound.bottom)
        )
    }

    private fun curveStart(width: Float) =
        PointF(width / 2 - 1.2f * radius - 4.0f * margin, getVisibleCorners()[0].y)

    private fun curveEnd(width: Float) = PointF().apply {
        val curveStart = curveStart(width)
        x = width - curveStart.x
        y = curveStart.y
    }

    private fun createCurvePath(width: Float): Path {
        return Path().apply {
            val topLeft = getVisibleCorners()[0]
            val start = curveStart(width)
            val p2 = PointF(width / 2.toFloat() - 0.8f * radius, topLeft.y)
            val p3 = PointF(width / 2 - 1.0f * radius - margin * .8f, topLeft.y + radius + margin)
            val p4 = PointF(width / 2.toFloat(), topLeft.y + radius + margin)

            moveTo(start.x, start.y)
            cubicTo(p2.x, p2.y, p3.x, p3.y, p4.x, p4.y)

            val p5 = PointF(width - p3.x, p3.y)
            val p6 = PointF(width - p2.x, p2.y)
            val end = curveEnd(width)
            cubicTo(p5.x, p5.y, p6.x, p6.y, end.x, end.y)
        }
    }

    /**
     * Top left line of curve
     */
    private fun createTopLeftPath(width: Float): Path {
        return Path().apply {
            val topLeft = getVisibleCorners()[0]
            val curveStart = curveStart(width)
            moveTo(topLeft.x, topLeft.y)
            lineTo(curveStart.x, curveStart.y)
        }
    }

    /**
     * Top right line of curve
     */
    private fun createTopRightPath(width: Float): Path {
        return Path().apply {
            val topRight = getVisibleCorners()[1]
            val curveEnd = curveEnd(width)
            moveTo(curveEnd.x, curveEnd.y)
            lineTo(topRight.x, topRight.y)
        }
    }

    /**
     * Three lines in right bottom and left
     */
    private fun createRightBottomLeftPath(): Path {
        return Path().apply {
            val corners = getVisibleCorners()
            moveTo(corners[1].x, corners[1].y)
            lineTo(corners[2].x, corners[2].y)
            lineTo(corners[3].x, corners[3].y)
            lineTo(corners[0].x, corners[0].y)
        }
    }

    private fun createPointSegments(width: Float): MutableMap<Int, MutableList<PointF>> {
        val pointMap = mutableMapOf<Int, MutableList<PointF>>()
        val pm = PathMeasure()
        val point = FloatArray(2) { 0.0f }

        val corners = getVisibleCorners()
        val curveStart = curveStart(width)
        val curveEnd = curveEnd(width)

        return pointMap.apply {
            //top-right segment
            put(
                0, mutableListOf(
                    PointF(corners[0].x, corners[1].y),
                    PointF(curveStart.x, curveStart.y)
                )
            )

            //Curve points
            val curvePoints = mutableListOf<PointF>()
            pm.setPath(createCurvePath(width), false)
            for (index in 0..CURVE_MAX_POINTS) {
                pm.getPosTan(pm.length * index / CURVE_MAX_POINTS.toFloat(), point, null)
                curvePoints.add(PointF(point[0], point[1]))
            }
            put(1, curvePoints)


            //top-left segment
            put(
                2, mutableListOf(
                    PointF(curveEnd.x, curveEnd.y),
                    PointF(corners[1].x, corners[1].y)
                )
            )

            //right-bottom-left segment
            put(
                3, mutableListOf(
                    PointF(corners[2].x, corners[2].y),
                    PointF(corners[3].x, corners[3].y),
                    PointF(corners[0].x, corners[0].y)

                )
            )
        }
    }

    private fun pointSegmentsToPath(segments: MutableMap<Int, MutableList<PointF>>): Path {
        return Path().apply {
            segments.forEach {
                it.value.forEachIndexed { index, point ->
                    if (it.key == 0 && index == 0) moveTo(point.x, point.y)
                    else lineTo(point.x, point.y)
                }
            }
        }
    }
}
