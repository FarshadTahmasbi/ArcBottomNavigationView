package com.androidisland.views

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.view.menu.MenuBuilder
import androidx.core.view.ViewCompat
import androidx.core.view.children
import androidx.core.view.iterator
import androidx.core.view.size
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlin.random.Random


class ArcBottomNavigationView : BottomNavigationView {

    companion object {
        private const val DEFAULT_RADIUS = 1
        private const val CURVE_MAX_POINTS = 100
    }

    private var currentState: State =
        State.FLAT
    //        set(value) {
//            field = value
//            updateInvisibleMenuItem(field)
//        }
    private var animator: ValueAnimator? = null
    private lateinit var visibleBound: RectF
    //Keeps curved state points, only change when size changed
    private lateinit var curvedBoundPoints: MutableList<PointF>
    //Keeps flat state points, only change when size changed
    private lateinit var flatBoundPoints: MutableList<PointF>
    //Keeps points for current state, every time we assign it a list
    //it changes currentPath and redraws view
    //assignment should only happen in animation updates
    private var currentPoints: MutableList<PointF> = mutableListOf()
        set(value) {
            field = value
            currentPath = pointsToPath(field)
            invalidate()
        }
    private var currentPath = Path()
    private val invisibleMenuItemId = Random(System.currentTimeMillis()).nextInt()
    private val navMenu = menu as MenuBuilder
    private var itemSelectedListener: OnNavigationItemSelectedListener? = null
    private var selectedItemIndex = 0


    val controlPath = Path()
    val margin = 20.0f
    val radius = 80.0f

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

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, 0){
        ViewCompat.setElevation(this, 0.0f)

    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        visibleBound = getVisibleBound()
        curvedBoundPoints = createCurveBoundPoints(w.toFloat())
        flatBoundPoints = createFlatBoundPoints(curvedBoundPoints)
        currentPoints = if (currentState == State.FLAT) flatBoundPoints else curvedBoundPoints
    }

    override fun isItemHorizontalTranslationEnabled(): Boolean {
        return false
    }

    override fun setItemHorizontalTranslationEnabled(itemHorizontalTranslationEnabled: Boolean) {

    }

    @SuppressLint("RestrictedApi")
    override fun onFinishInflate() {
        super.onFinishInflate()
        children.forEach {
            if (it is BottomNavigationMenuView) {
                (it.layoutParams as LayoutParams).gravity = Gravity.BOTTOM
                return@forEach
            }
        }
        if (menu.size % 2 != 0) throw IllegalStateException("Item menu size should be even")
        regenerateMenu()
        navMenu.setCallback(object : MenuBuilder.Callback {
            override fun onMenuModeChange(menu: MenuBuilder?) {
            }

            override fun onMenuItemSelected(menu: MenuBuilder?, item: MenuItem?): Boolean {
                item?.apply {
                    selectedItemIndex = menu?.children?.indexOf(item) ?: -1
                    Log.d("test123", "selected=====> $selectedItemIndex")
                    if (itemId != invisibleMenuItemId) {
                        itemSelectedListener?.onNavigationItemSelected(this)
                    }
                }
                return false
            }
        })
    }

    private fun regenerateMenu() {
        val items = mutableListOf<MenuItem>().apply {
            menu.iterator().forEach {
                add(it)
            }
        }
        menu.clear()
        items.forEachIndexed { index, item ->
            val order = if (index < items.size / 2) 1 else items.size
            menu.add(item.groupId, item.itemId, order, item.title).apply {
                icon = item.icon
            }
        }
        val invisibleItem = menu.findItem(invisibleMenuItemId)
        if (invisibleItem == null) {
            menu.add(Menu.NONE, invisibleMenuItemId, menu.size / 2, "").apply {
                isEnabled = false
                isChecked = false
                isCheckable = false
            }
            if (selectedItemIndex >= menu.size / 2) selectedItemIndex++
        }
        updateInvisibleMenuItem(currentState)
    }

    private fun invisibleItemExists() = menu.size() % 2 == 1

    private fun invisibleMenuItem() = findViewById<View>(invisibleMenuItemId)

    private fun updateInvisibleMenuItem(state: State) {
        val item = menu.findItem(invisibleMenuItemId)
        item.isVisible = state != State.FLAT
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(width, measuredHeight + radius.toInt())
    }

    override fun onDraw(canvas: Canvas?) {
//        canvas?.apply {
//            drawPath(currentPath, controlPaint)
//        }
        canvas?.clipPath(currentPath)
        super.onDraw(canvas)
    }

    override fun dispatchDraw(canvas: Canvas?) {
        canvas?.clipPath(currentPath)
        super.dispatchDraw(canvas)
    }

    override fun draw(canvas: Canvas?) {
        canvas?.clipPath(currentPath)
//        canvas?.apply {
//            clipPath(currentPath)
//        }
//        background?.apply {
//            if (this is ColorDrawable) {
//                controlPaint.color = color
//                canvas?.drawPath(currentPath, controlPaint)
//            }
//        }
        super.draw(canvas)
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
        return listOf(
            PointF(visibleBound.left, visibleBound.top),
            PointF(visibleBound.right, visibleBound.top),
            PointF(visibleBound.right, visibleBound.bottom),
            PointF(visibleBound.left, visibleBound.bottom)
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

    private fun createCurveBoundPoints(width: Float): MutableList<PointF> {
        return mutableListOf<PointF>().apply {

            val corners = getVisibleCorners()
            val curveStart = curveStart(width)
            val curveEnd = curveEnd(width)

            //top-right segment
            add(PointF(corners[0].x, corners[1].y))
            add(PointF(curveStart.x, curveStart.y))

            //Curve points
            val pm = PathMeasure()
            val point = FloatArray(2) { 0.0f }
            pm.setPath(createCurvePath(width), false)
            for (index in 0..CURVE_MAX_POINTS) {
                pm.getPosTan(pm.length * index / CURVE_MAX_POINTS.toFloat(), point, null)
                add(PointF(point[0], point[1]))
            }

            //top-left segment
            add(PointF(curveEnd.x, curveEnd.y))
            add(PointF(corners[1].x, corners[1].y))


            //right-bottom-left segment
            add(PointF(corners[2].x, corners[2].y))
            add(PointF(corners[3].x, corners[3].y))
            add(PointF(corners[0].x, corners[0].y))
        }
    }

    private fun createFlatBoundPoints(curveBoundPoints: MutableList<PointF>): MutableList<PointF> {
        return curveBoundPoints.clone().apply {
            forEach { point ->
                if (shouldAnimate(point)) {
                    point.y = visibleBound.top
                }
            }
        }
    }

    private fun pointsToPath(points: MutableList<PointF>): Path {
        return Path().apply {
            points.forEachIndexed { index, point ->
                if (index == 0) moveTo(point.x, point.y)
                else lineTo(point.x, point.y)
            }
        }
    }

    /**
     * Check if the point is supposed to animate or not
     */
    private fun shouldAnimate(point: PointF) = point.y > visibleBound.top && point.y < visibleBound.bottom

    private fun transitionTo(state: State, duration: Long = 300) {
        if (state == currentState) return
        animator?.apply {
            cancel()
        }

        animator = ValueAnimator.ofFloat(0.0f, 1.0f)
            .apply {
                this.duration = duration
                interpolator = FastOutSlowInInterpolator()
                val top = visibleBound.top
                //If dest state is FLAT init points are curved else flat
                val points =
                    if (state == State.FLAT) curvedBoundPoints.clone()
                    else flatBoundPoints.clone()
                val dest =
                    if (state == State.FLAT) flatBoundPoints else curvedBoundPoints

                val dy = FloatArray(points.size) { index ->
                    if (state == State.FLAT) {
                        points[index].y - dest[index].y
                    } else {
                        dest[index].y - points[index].y
                    }
                }
                addUpdateListener {
                    val factor =
                        if (state == State.FLAT) 1.0f - it.animatedValue as Float else it.animatedValue as Float
                    points.forEachIndexed { index, point ->
                        //                        if (shouldAnimate(point)) {
                        if (!isCorner(point))
                            point.y = top + dy[index] * factor
//                        }
                    }
                    currentPoints = points
                }
                addListener(object : SimpleAnimatorListener() {
                    override fun onAnimationStart(animation: Animator?, isReverse: Boolean) {
                        super.onAnimationStart(animation, isReverse)
                        updateInvisibleMenuItem(state)
                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        currentState = state
                    }
                })
                start()
            }
    }

    private fun toggleState(state: State) = if (state == State.FLAT) State.CURVED else State.FLAT


    public fun toggleTransition() {
        transitionTo(toggleState(currentState))
    }

//    fun removeShiftMode() {
//        val menuView = getChildAt(0) as BottomNavigationMenuView
//        try {
//            val shiftingMode = menuView.javaClass.getDeclaredField("mShiftingMode")
//            shiftingMode.isAccessible = true
//            shiftingMode.setBoolean(menuView, false)
//            shiftingMode.isAccessible = false
//            for (i in 0 until menuView.childCount) {
//                val item = menuView.getChildAt(i) as BottomNavigationItemView
//                item.setShifting(false)
//                item.setShiftingMode(false)
//                // set once again checked value, so view will be updated
//                item.setChecked(item.itemData.isChecked)
//            }
//        } catch (e: NoSuchFieldException) {
//            Log.e("ERROR NO SUCH FIELD", "Unable to get shift mode field")
//        } catch (e: IllegalAccessException) {
//            Log.e("ERROR ILLEGAL ALG", "Unable to change value of shift mode")
//        }
//    }

    private fun MutableList<PointF>.clone(): MutableList<PointF> {
        return mutableListOf<PointF>().also { list ->
            forEach { point ->
                list.add(PointF(point.x, point.y))
            }
        }
    }

    override fun setOnNavigationItemSelectedListener(listener: OnNavigationItemSelectedListener?) {
        itemSelectedListener = listener
    }

    private enum class State {
        CURVED, FLAT
    }

    private open class SimpleAnimatorListener : Animator.AnimatorListener {

        override fun onAnimationRepeat(animation: Animator?) {

        }

        override fun onAnimationEnd(animation: Animator?) {
        }

        override fun onAnimationCancel(animation: Animator?) {
        }

        override fun onAnimationStart(animation: Animator?) {
        }
    }

    //TODO cancel anim on size change
    //TODO transition without anim
}
