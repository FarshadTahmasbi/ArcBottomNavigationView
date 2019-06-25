package com.androidisland.views

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuItemImpl
import androidx.core.internal.view.SupportMenu
import androidx.core.view.*
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import kotlin.random.Random

/**
 * Created by Farshad Tahmasbi on June 22,2019.
 * Copyright(c) 2019, All rights reserved.
 * https://github.com/FarshadTahmasbi/ArcBottomNavigationView
 * Email: farshad.tmb@gmail.com
 */

@SuppressLint("RestrictedApi")
open class ArcBottomNavigationView : BottomNavigationView {

    companion object {
        private const val TAG = "ArcBottomNavigationView"
        //Dimens are in Dp
        private const val DEFAULT_BUTTON_SIZE = 56
        private const val DEFAULT_BUTTON_MARGIN = 8
        private const val DEFAULT_BUTTON_STROKE_WIDTH = 0
        private const val DEFAULT_BUTTON_STROKE_COLOR = Color.TRANSPARENT

        private const val DEFAULT_ANIM_DURATION = 200L
        private const val CURVE_MAX_POINTS = 100
    }

    private var itemSelectedListener: BottomNavigationView.OnNavigationItemSelectedListener? = null
    private var button: ArcButton? = null
    private var menuView: BottomNavigationMenuView? = null

    var buttonSize: Float = DEFAULT_BUTTON_SIZE.toPixel()
        set(value) {
            field = value
            buttonRadius = value / 2
            if (buttonIconSize > field) buttonIconSize = field
            requestLayout()
        }

    var buttonMargin = DEFAULT_BUTTON_MARGIN.toPixel()
        set(value) {
            if (field != value) {
                field = value
                updatePoints(width)
            }
        }
    private var buttonRadius = (DEFAULT_BUTTON_SIZE / 2).toPixel()
        set(value) {
            field = value
            button?.apply {
                cornerRadius = value.toInt()
            }
        }
    var buttonIcon: Drawable? = null
        set(value) {
            field = value
            button?.apply {
                icon = value
            }
        }
    var buttonIconSize: Float = buttonSize
        set(value) {
            field = Math.min(buttonSize, value)
            button?.apply {
                iconSize = field.toInt()
            }
        }

    var buttonStrokeWidth: Float = 0.0f
        set(value) {
            field = value
            button?.apply {
                strokeWidth = value.toInt()
            }
        }

    var buttonStrokeColor: Int = Color.TRANSPARENT
        set(value) {
            field = value
            button?.apply {
                strokeColor = ColorStateList.valueOf(value)
            }
        }

    var buttonIconTint: Int = Color.TRANSPARENT
        set(value) {
            field = value
            button?.apply {
                if (value != Color.TRANSPARENT)
                    iconTint = ColorStateList.valueOf(value)
            }
        }

    var buttonBackgroundTint: Int = Color.TRANSPARENT
        set(value) {
            field = value
            button?.apply {
                if (value != Color.TRANSPARENT)
                    supportBackgroundTintList = ColorStateList.valueOf(value)
            }
        }

    var buttonRippleColor: Int = Color.TRANSPARENT
        set(value) {
            field = value
            button?.apply {
                rippleColor = ColorStateList.valueOf(value)
            }
        }

    var typeface: Typeface? = null
        set(value) {
            field = value
            value?.let {
                menuView?.apply {
                    for (index in 0 until childCount) {
                        val item = getChildAt(index)
                        val smallItemText = item.findViewById<View>(com.google.android.material.R.id.smallLabel)
                        if (smallItemText is TextView) {
                            smallItemText.typeface = value
                        }
                        val largeItemText = item.findViewById<View>(com.google.android.material.R.id.largeLabel)
                        if (largeItemText is TextView) {
                            largeItemText.typeface = value
                            largeItemText.setPadding(0, 0, 0, 0)
                        }
                    }
                }
            }
        }

    private var currentState: State = State.FLAT
    var state: State = currentState
        set(value) {
            field = value
            transitionTo(value)
        }

    private var animator: ValueAnimator? = null
    private var visibleBound: RectF = RectF(0.0f, 0.0f, 0.0f, 0.0f)
    private val visibleBoundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    //Keeps curved state points, only change when size changed
    private var arcBoundPoints: MutableList<PointF> = mutableListOf()
    //Keeps flat state points, only change when size changed
    private var flatBoundPoints: MutableList<PointF> = mutableListOf()
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
    private val invisibleMenuItemId =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            View.generateViewId()
        } else {
            Random(System.currentTimeMillis()).nextInt()
        }
    var buttonClickListener: ((arcBottomNavView: ArcBottomNavigationView) -> Unit)? = null
    var deselectOnButtonClick: Boolean = false
    var arcAnimationListener: ArcAnimationListener? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, 0) {
        buttonStrokeWidth = DEFAULT_BUTTON_STROKE_WIDTH.toPixel()
        buttonStrokeColor = DEFAULT_BUTTON_STROKE_COLOR

        var typeface: Typeface? = null
        val typedValue = TypedValue()
        val typedArray = context.obtainStyledAttributes(typedValue.data, intArrayOf(R.attr.colorAccent))
        buttonBackgroundTint = typedArray.getColor(0, Color.BLACK)
        typedArray.recycle()

        attrs?.apply {
            val ta = context.obtainStyledAttributes(this, R.styleable.ArcBottomNavigationView)
            buttonSize = ta.getDimension(R.styleable.ArcBottomNavigationView_ai_buttonSize, buttonSize)
            buttonMargin = ta.getDimension(R.styleable.ArcBottomNavigationView_ai_buttonMargin, buttonMargin)
            if (buttonMargin < DEFAULT_BUTTON_MARGIN.toPixel()) buttonMargin = DEFAULT_BUTTON_MARGIN.toPixel()
            if (ta.hasValue(R.styleable.ArcBottomNavigationView_ai_buttonIcon)) {
                val iconResId = ta.getResourceId(R.styleable.ArcBottomNavigationView_ai_buttonIcon, 0)
                if (iconResId > 0)
                    buttonIcon = VectorDrawableCompat.create(resources, iconResId, null)
            }
            buttonIconSize = ta.getDimension(R.styleable.ArcBottomNavigationView_ai_buttonIconSize, buttonIconSize)
            buttonStrokeWidth =
                ta.getDimension(R.styleable.ArcBottomNavigationView_ai_buttonStrokeWidth, buttonStrokeWidth)
            buttonStrokeColor = ta.getColor(R.styleable.ArcBottomNavigationView_ai_buttonStrokeColor, buttonStrokeColor)
            buttonBackgroundTint =
                ta.getColor(R.styleable.ArcBottomNavigationView_ai_buttonBackgroundTint, buttonBackgroundTint)
            buttonRippleColor =
                ta.getColor(R.styleable.ArcBottomNavigationView_ai_buttonRippleColor, buttonRippleColor)
            buttonIconTint = ta.getColor(R.styleable.ArcBottomNavigationView_ai_buttonIconTint, buttonIconTint)
            val state = ta.getInt(R.styleable.ArcBottomNavigationView_ai_state, 1)
            currentState = if (state == 1) State.FLAT else State.ARC
            if (ta.hasValue(R.styleable.ArcBottomNavigationView_ai_fontPath)) {
                typeface = Typeface.createFromAsset(
                    context.assets,
                    ta.getString(R.styleable.ArcBottomNavigationView_ai_fontPath)
                )
            }
            deselectOnButtonClick = ta.getBoolean(R.styleable.ArcBottomNavigationView_ai_deselectOnButtonClick, false)
            ta.recycle()
        }

        ViewCompat.setElevation(this, 0.0f)
        menuView = getChildAt(0) as BottomNavigationMenuView
        menuView?.layoutParams.apply {
            (this as LayoutParams).gravity = Gravity.BOTTOM
        }
        if (menu.size % 2 != 0) throw IllegalStateException("Item menu size should be even")
        regenerateMenu()
        this.typeface = typeface

        //Creates button
        button = ArcButton(context)
            .apply {
                layoutParams = LayoutParams(buttonSize.toInt(), buttonSize.toInt(), Gravity.TOP or Gravity.CENTER)
                cornerRadius = (buttonSize / 2).toInt()
                gravity = Gravity.CENTER
                iconPadding = 0
                setPadding(0)
                iconGravity = MaterialButton.ICON_GRAVITY_TEXT_START
                iconSize = buttonIconSize.toInt()
                icon = buttonIcon
                iconTintMode = PorterDuff.Mode.SRC_IN
                if (buttonIconTint != Color.TRANSPARENT)
                    iconTint = ColorStateList.valueOf(buttonIconTint)
                strokeWidth = buttonStrokeWidth.toInt()
                strokeColor = ColorStateList.valueOf(buttonStrokeColor)
                if (buttonBackgroundTint != Color.TRANSPARENT)
                    supportBackgroundTintList = ColorStateList.valueOf(buttonBackgroundTint)
                rippleColor = ColorStateList.valueOf(buttonRippleColor)
                visibility = if (currentState == State.FLAT) View.INVISIBLE else View.VISIBLE
                setOnClickListener {
                    if (deselectOnButtonClick) {
                        selectedItemId = invisibleMenuItemId
                        itemSelectedListener?.onNavigationItemSelected(menu.findItem(invisibleMenuItemId))
                    }
                    buttonClickListener?.invoke(this@ArcBottomNavigationView)
                }
                setTextColor(Color.TRANSPARENT)
                addView(this, 1)
            }
        ViewCompat.setElevation(button!!, 0.0f)
    }

    final override fun getChildAt(index: Int): View {
        return super.getChildAt(index)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        visibleBound = getVisibleBound()
        updatePoints(w)
    }

    private fun updatePoints(width: Int) {
        if (width > 0) {
            arcBoundPoints = createArcBoundPoints(width.toFloat())
            flatBoundPoints = createFlatBoundPoints(arcBoundPoints)
            currentPoints = if (currentState == State.FLAT) flatBoundPoints else arcBoundPoints
        }
    }

    override fun isItemHorizontalTranslationEnabled(): Boolean {
        return false
    }

    override fun setItemHorizontalTranslationEnabled(itemHorizontalTranslationEnabled: Boolean) {
    }

    override fun setOnNavigationItemSelectedListener(listener: OnNavigationItemSelectedListener?) {
        super.setOnNavigationItemSelectedListener(listener)
        itemSelectedListener = listener
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
                isVisible = false
            }
        }
        updateInvisibleMenuItem(currentState)
    }

    private fun updateInvisibleMenuItem(state: State) {
        val item = menu.findItem(invisibleMenuItemId)
        item.isVisible = state != State.FLAT
    }

    private fun getBackgroundColor(): Int? {
        return background.run {
            if (this is ColorDrawable) color
            else null
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var width = 0
        var height = buttonRadius.toInt()
        menuView?.apply {
            measure(
                widthMeasureSpec,
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
            )
            width += measuredWidth
            height += measuredHeight
        }
        button?.measure(
            MeasureSpec.makeMeasureSpec(buttonSize.toInt(), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(buttonSize.toInt(), MeasureSpec.EXACTLY)
        )
        setMeasuredDimension(width, height)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        menuView?.layout(
            visibleBound.left.toInt(),
            visibleBound.top.toInt(),
            visibleBound.right.toInt(),
            visibleBound.bottom.toInt()
        )
        button?.layout(
            (visibleBound.right / 2 - buttonRadius).toInt(),
            (visibleBound.top - buttonRadius).toInt(),
            (visibleBound.right / 2 + buttonRadius).toInt(),
            (visibleBound.top + buttonRadius).toInt()
        )
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.apply {
            restore()
            getBackgroundColor()?.let {
                visibleBoundPaint.color = it
            }
//            if (!isInEditMode) {
//                drawPath(currentPath, visibleBoundPaint)
//            } else {
//                drawPath(createEditModePath(), visibleBoundPaint)
//            }
        }
    }

    override fun draw(canvas: Canvas?) {
        canvas?.apply {
            save()
            if (!isInEditMode)
                clipPath(currentPath)
            else
                clipPath(createEditModePath())
        }
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
        return RectF(0.0f, buttonRadius, width.toFloat(), height.toFloat())
    }


    private fun getVisibleCorners(): List<PointF> {
        return listOf(
            PointF(visibleBound.left, visibleBound.top),
            PointF(visibleBound.right, visibleBound.top),
            PointF(visibleBound.right, visibleBound.bottom),
            PointF(visibleBound.left, visibleBound.bottom)
        )
    }

    private fun arcStart(width: Float) =
        PointF(width / 2 - 1.2f * buttonRadius - 4.0f * buttonMargin, getVisibleCorners()[0].y)

    private fun arcEnd(width: Float) = PointF().apply {
        val curveStart = arcStart(width)
        x = width - curveStart.x
        y = curveStart.y
    }

    private fun createArcPath(width: Float): Path {
        return Path().apply {
            val topLeft = getVisibleCorners()[0]
            val start = arcStart(width)
            val p2 = PointF(width / 2.toFloat() - 0.8f * buttonRadius, topLeft.y)
            val p3 =
                PointF(width / 2 - 1.0f * buttonRadius - buttonMargin * .8f, topLeft.y + buttonRadius + buttonMargin)
            val p4 = PointF(width / 2.toFloat(), topLeft.y + buttonRadius + buttonMargin)

            moveTo(start.x, start.y)
            cubicTo(p2.x, p2.y, p3.x, p3.y, p4.x, p4.y)

            val p5 = PointF(width - p3.x, p3.y)
            val p6 = PointF(width - p2.x, p2.y)
            val end = arcEnd(width)
            cubicTo(p5.x, p5.y, p6.x, p6.y, end.x, end.y)
        }
    }

    /**
     * Creates a path to show in edit mode for layout preview
     */
    private fun createEditModePath(): Path {
        return Path().apply {
            if (currentState == State.FLAT) {
                moveTo(visibleBound.left, visibleBound.top)
                lineTo(visibleBound.right, visibleBound.top)
                lineTo(visibleBound.right, visibleBound.bottom)
                lineTo(visibleBound.left, visibleBound.bottom)
                close()
            } else if (currentState == State.ARC) {
                val corners = getVisibleCorners()
                val topLeft = corners[0]
                val start = arcStart(width.toFloat())
                val p2 = PointF(width / 2.toFloat() - 0.8f * buttonRadius, topLeft.y)
                val p3 =
                    PointF(
                        width / 2 - 1.0f * buttonRadius - buttonMargin * .8f,
                        topLeft.y + buttonRadius + buttonMargin
                    )
                val p4 = PointF(width / 2.toFloat(), topLeft.y + buttonRadius + buttonMargin)

                moveTo(topLeft.x, topLeft.y)
                lineTo(start.x, start.y)
                cubicTo(p2.x, p2.y, p3.x, p3.y, p4.x, p4.y)

                val p5 = PointF(width - p3.x, p3.y)
                val p6 = PointF(width - p2.x, p2.y)
                val end = arcEnd(width.toFloat())
                cubicTo(p5.x, p5.y, p6.x, p6.y, end.x, end.y)
                lineTo(corners[1].x, corners[1].y)
                lineTo(corners[2].x, corners[2].y)
                lineTo(corners[3].x, corners[3].y)
                close()
            }
        }
    }

    /**
     * Top left line of curve
     */
    private fun createTopLeftPath(width: Float): Path {
        return Path().apply {
            val topLeft = getVisibleCorners()[0]
            val curveStart = arcStart(width)
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
            val curveEnd = arcEnd(width)
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

    private fun createArcBoundPoints(width: Float): MutableList<PointF> {
        return mutableListOf<PointF>().apply {

            val corners = getVisibleCorners()
            val arcStart = arcStart(width)
            val arcEnd = arcEnd(width)

            //top-right segment
            add(PointF(corners[0].x, corners[1].y))
            add(PointF(arcStart.x, arcStart.y))

            //Curve points
            val pm = PathMeasure()
            val point = FloatArray(2) { 0.0f }
            pm.setPath(createArcPath(width), false)
            for (index in 0..CURVE_MAX_POINTS) {
                pm.getPosTan(pm.length * index / CURVE_MAX_POINTS.toFloat(), point, null)
                add(PointF(point[0], point[1]))
            }

            //top-left segment
            add(PointF(arcEnd.x, arcEnd.y))
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

    private fun transitionTo(state: State, duration: Long = DEFAULT_ANIM_DURATION) {
        if (state == currentState) return
        if (measuredWidth == 0 || measuredHeight == 0) {
            currentState = state
            menu.findItem(invisibleMenuItemId)?.apply {
                isVisible = currentState == State.ARC
            }
            button?.apply {
                scaleX = if (currentState == State.ARC) 1.0f else 0.0f
                scaleY = if (currentState == State.ARC) 1.0f else 0.0f
                visibility = if (currentState == State.ARC) View.VISIBLE else View.INVISIBLE
            }
            return
        }

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
                    if (state == State.FLAT) arcBoundPoints.clone()
                    else flatBoundPoints.clone()
                val dest =
                    if (state == State.FLAT) flatBoundPoints else arcBoundPoints

                val dy = FloatArray(points.size) { index ->
                    if (state == State.FLAT) {
                        points[index].y - dest[index].y
                    } else {
                        dest[index].y - points[index].y
                    }
                }
                addUpdateListener {
                    if (points.isNotEmpty()) {
                        val animatedValue = it.animatedValue as Float
                        val factor = if (state == State.FLAT) 1.0f - animatedValue else animatedValue
                        points.forEachIndexed { index, point ->
                            if (!isCorner(point))
                                point.y = top + dy[index] * factor
                        }
                        currentPoints = points
                        animateButton(animatedValue, state)
                        arcAnimationListener?.onArcAnimationUpdate(animatedValue, currentState, state)
                        onArcAnimationUpdate(animatedValue, currentState, state)
                    }
                }
                addListener(object : SimpleAnimatorListener() {
                    override fun onAnimationStart(animation: Animator?, isReverse: Boolean) {
                        super.onAnimationStart(animation, isReverse)
                        updateInvisibleMenuItem(state)
                        arcAnimationListener?.onArcAnimationStart(currentState, state)
                        onArcAnimationStart(currentState, state)
                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        val from = currentState
                        currentState = state
                        arcAnimationListener?.onArcAnimationEnd(from, state)
                        onArcAnimationEnd(from, state)
                    }

                    override fun onAnimationCancel(animation: Animator?) {
                        super.onAnimationCancel(animation)
                        currentState = state
                    }
                })
                start()
            }
    }

    private fun toggleState(state: State) = if (state == State.FLAT) State.ARC else State.FLAT


    public fun toggleState() {
        state = toggleState(currentState)
    }

    private fun MutableList<PointF>.clone(): MutableList<PointF> {
        return mutableListOf<PointF>().also { list ->
            forEach { point ->
                list.add(PointF(point.x, point.y))
            }
        }
    }

    protected fun onArcAnimationStart(from: State, to: State) {
    }

    protected fun onArcAnimationUpdate(offset: Float, from: State, to: State) {
    }

    protected fun onArcAnimationEnd(from: State, to: State) {

    }

    private fun animateButton(offset: Float, to: State) {
        val scale = if (to == State.FLAT) 1.0f - offset else offset
        if (to == State.ARC && offset == 0.0f) button?.visibility = View.VISIBLE
        button?.scaleX = scale
        button?.scaleY = scale
        if (to == State.FLAT && offset == 1.0f) button?.visibility = View.INVISIBLE
    }

    private fun log(msg: String) = Log.d(TAG, msg)

    enum class State {
        ARC, FLAT
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

    interface ArcAnimationListener {
        fun onArcAnimationStart(from: State, to: State)
        fun onArcAnimationUpdate(offset: Float, from: State, to: State)
        fun onArcAnimationEnd(from: State, to: State)
    }
}
