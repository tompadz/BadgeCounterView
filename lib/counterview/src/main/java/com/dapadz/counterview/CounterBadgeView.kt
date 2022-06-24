package com.dapadz.counterview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup.MarginLayoutParams
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.ScaleAnimation
import android.widget.TextSwitcher
import android.widget.TextView
import androidx.annotation.ColorRes


open class CounterBadgeView:TextSwitcher {

    private val TAG = "CounterView"

    /**
     * Changeable variables for setting the view
     */

    //the maximum number in the counter
    private var counterMaxValue = 99

    //show counter when number is less than or equal to zero
    private var showIfValueZero = true

    //default counter value / or counter value after change
    private var counterValue = 0

    //styles
    private var counterTextColor = Color.WHITE
    private var counterBackgroundColor = Color.BLUE
    private var counterTextSize = 12f * resources.displayMetrics.scaledDensity;
    private var paddingVertical = 5f
    private var paddingHorizontal = 5f

    //needed to cancel text change animation
    private var isMaxValue = false

    //needed to determine the rendering of the canvas
    private var isCircle = true

    //text view sizes
    //needed to recalculate the actual size of the main view
    private var firstTextSize = listOf(0, 0)
    private var secondTextSize = listOf(0, 0)

    //to determine the correct dimensions, you need to check the old and new counter values
    private var oldCounterValue = 0

    //recalculated view height and width
    private var _height = 0
    private var _width = 0

    //check visibility child text view
    private var isChildVisible = true

    fun getValue():Int = counterValue
    fun getMaxValue():Int = counterMaxValue

    /**
     * Setting vertical and horizontal indents for the counter text
     */
    open fun setPadding(vertical : Float? = null, horizontal : Float? = null) {
        if (vertical != null)
            paddingVertical = vertical
        if (horizontal != null)
            paddingHorizontal = horizontal
    }


    /**
     * Sets the background of the counter
     */
    open fun setCounterBackgroundColor(@ColorRes color : Int) {
        counterBackgroundColor = color
        updateBackground()
    }

    /**
     * Sets the background of the counter
     */
    open fun setCounterBackgroundColor(color : String) {
        counterBackgroundColor = Color.parseColor(color)
        updateBackground()
    }

    /**
     * Sets the maximum counter value
     * after reaching this number, the counter will not change
     */
    open fun setMaxCounterValue(value : Int) {
        counterMaxValue = value
    }

    /**
     * Sets the starting value for the counter
     */
    open fun setDefaultCounterValue(value : Int) {
        oldCounterValue = counterValue
        counterValue = value
    }

    /**
     * Sets whether to show
     * counter if its value is less than or equal to zero
     */
    open fun setShowIfValueZero(state : Boolean) {
        showIfValueZero = state
    }

    /**
     * Sets the color of the counter text
     */
    open fun setCounterTextColor(@ColorRes color : Int) {
        counterTextColor = color
        updateChild()
    }

    /**
     * Sets the color of the counter text
     */
    open fun setCounterTextColor(color : String) {
        counterTextColor = Color.parseColor(color)
        updateChild()
    }

    /**
     * Sets the size of the counter text
     */
    open fun setCounterTextSize(size : Float) {
        counterTextSize = size
        updateChild()
    }

    constructor(context : Context) : super(context)
    constructor(context : Context, attrs : AttributeSet) : super(context, attrs) {
        val inflate = LayoutInflater.from(context)
        inflate.inflate(R.layout.counter_view, this, true)
        initAttrs(attrs)
    }

    /**
     * We initialize the default values for the view and check the styles
     */
    @SuppressLint("CustomViewStyleable")
    private fun initAttrs(attrs : AttributeSet?) {
        if (attrs == null) return
        val typeArray = context.obtainStyledAttributes(attrs, R.styleable.CounterBadgeView)

        counterTextColor =
            typeArray.getColor(R.styleable.CounterBadgeView_counterViewTextColor, counterTextColor)

        counterTextSize =
            typeArray.getDimension(R.styleable.CounterBadgeView_counterTextSize, counterTextSize)

        counterBackgroundColor = typeArray.getColor(
            R.styleable.CounterBadgeView_counterViewBackgroundColor,
            counterBackgroundColor
        )

        counterMaxValue = typeArray.getInt(R.styleable.CounterBadgeView_counterMaxValue, counterMaxValue)

        showIfValueZero =
            typeArray.getBoolean(R.styleable.CounterBadgeView_counterShowIfZero, showIfValueZero)

        counterValue =
            typeArray.getInt(R.styleable.CounterBadgeView_counterViewDefaultValue, counterValue)
        if (counterValue > counterMaxValue) counterValue = counterMaxValue
        isCircle = counterValue.toString().length == 1

        paddingVertical =
            typeArray.getDimension(R.styleable.CounterBadgeView_counterPaddingVertical, paddingVertical)

        paddingHorizontal = typeArray.getDimension(
            R.styleable.CounterBadgeView_counterPaddingHorizontal,
            paddingHorizontal
        )

        //set default animations
        setInAnimation(context, R.anim.slide_up)
        setOutAnimation(context, R.anim.slide_down)

        updateChild()
        setText(counterValue.toString())
        updateSize()

        typeArray.recycle()
    }

    /**
     * Updates styles in all child text views
     */
    private fun updateChild() {
        for (index in 0 until childCount) {
            getChildAt(index).apply {
                this as TextView
                this.setTextColor(counterTextColor)
                this.setTextSize(TypedValue.COMPLEX_UNIT_PX, counterTextSize)
            }
        }
    }

    /**
     * We pass through the list of child text views and takes
     * dimensions based on the text in these views
     */
    private fun updateSize() {
        for (index in 0 until childCount) {
            getChildAt(index).apply {
                this as TextView
                when (index) {
                    0 -> firstTextSize = listOf(
                        this.paint.measureText(this.text.toString()).toInt(),
                        this.textSize.toInt()
                    )
                    1 -> secondTextSize = listOf(
                        this.paint.measureText(this.text.toString()).toInt(),
                        this.textSize.toInt()
                    )
                }
            }
        }
    }

    /**
     * Hides all child views
     */
    private fun updateChildVisible(state : Boolean) {
        for (index in 0 until childCount) {
            getChildAt(index).apply {
                this as TextView
                this.visibility = if (state) VISIBLE else GONE
            }
        }
        isChildVisible = state
    }

    /**
     * Sets a new value for the counter
     */
    open fun setValue(value : Int) {

        isCircle = value.toString().length == 1
        oldCounterValue = counterValue

        //change the animation depending on the new value
        if (oldCounterValue < value) {
            setInAnimation(context, R.anim.slide_down_in)
            setOutAnimation(context, R.anim.slide_down)
        } else {
            setInAnimation(context, R.anim.slide_up_in)
            setOutAnimation(context, R.anim.slide_up_out)
        }


        if (value <= 0 && ! showIfValueZero) {
            if (isChildVisible) {
                counterHideAnim()
            }
        }else {
            if (!isChildVisible) {
                updateChildVisible(true)
                this@CounterBadgeView.visibility = VISIBLE
            }
        }

        when {
            value <= 0 -> {
                if (showIfValueZero) {
                    super.setCurrentText("0")
                }
                counterValue = 0
                isMaxValue = false
            }
            value in 1 until counterMaxValue -> {
                super.setText(value.toString())
                counterValue = value
                isMaxValue = false
            }
            value >= counterMaxValue -> {
                if (isMaxValue) {
                    super.setCurrentText("+$counterMaxValue")
                    counterValue = counterMaxValue
                } else {
                    super.setText("+$counterMaxValue")
                    counterValue = counterMaxValue
                    isMaxValue = true
                }
            }
        }
    }

    /**
     * Update the background view
     * check what exactly you need to draw a circle or rectangle,
     * then draw the canvas and set the background view
     */
    private fun updateBackground() {

        val w =  _width
        val h =  _height

        val counterRectRadius = (w * 2).coerceAtMost(h * 2) * 1.5f

        val p = (((h) * 2) / 4 * Math.PI)
        val counterCircleRadius = p / Math.PI

        val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)

        val radius = if (isCircle) counterCircleRadius else counterRectRadius

        val drawPaint = Paint()
        drawPaint.color = counterBackgroundColor
        drawPaint.style = Paint.Style.FILL
        drawPaint.isAntiAlias = true
        drawPaint.isDither = true

        canvas.drawRoundRect(
            (0).toFloat(),
            (0).toFloat(),
            (w - 0).toFloat(),
            (h - 0).toFloat(),
            radius.toFloat(),
            radius.toFloat(),
            drawPaint
        )

        this.background = BitmapDrawable(resources, bmp)

        counterChangeValueAnim()
    }

    /**
     * Animation to hide the view when its number is less than or equal to 0
     */
    protected open fun counterHideAnim() {
        val scaleAnimation = ScaleAnimation(
            1.0f, 0.0f, 1.0f, 0.0f,
            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f
        )
        val animation = AnimationSet(true)
        animation.addAnimation(scaleAnimation)
        animation.duration = 100
        animation.fillAfter = true

        this.startAnimation(animation)
        this.animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationEnd(p0 : Animation?) {
                updateChildVisible(false)
                this@CounterBadgeView.visibility = GONE
            }
            override fun onAnimationStart(p0 : Animation?) {}
            override fun onAnimationRepeat(p0 : Animation?) {}
        })
    }


    /**
     * Animation of changing the value in the counter
     */
    protected open fun counterChangeValueAnim() {
        val scaleAnimation = ScaleAnimation(
            1.0f, 1.1f, 1.0f, 1.1f,
            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f
        )
        scaleAnimation.repeatCount = 1
        scaleAnimation.repeatMode = Animation.REVERSE
        val animation = AnimationSet(true)
        animation.addAnimation(scaleAnimation)
        animation.duration = 70
        animation.fillAfter = true

        this.startAnimation(animation)
    }

    /**
     * We recalculate the size of the view, calculate the real size from the child text views
     */
    override fun onMeasure(widthMeasureSpec : Int, heightMeasureSpec : Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        updateSize()

        val textHeight = firstTextSize[1]

        val textWidth =
            if (firstTextSize[0] < secondTextSize[0]) {
                secondTextSize[0]
            } else {
                if (oldCounterValue.toString().length > counterValue.toString().length) {
                    secondTextSize[0]
                } else
                    firstTextSize[0]
            }

        val verticalSpaceAroundText = (textHeight * .12f + 6 + paddingVertical).toInt()
        val horizontalSpaceAroundText = ((textHeight * .24f) + 8 + paddingHorizontal).toInt()

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val desiredHeight = (counterTextSize + 2 * verticalSpaceAroundText).toInt()
        val desiredWidth =
            if (isCircle) desiredHeight else (textWidth + 2 * horizontalSpaceAroundText)

        _width = when (widthMode) {
            MeasureSpec.EXACTLY -> {
                widthSize
            }
            MeasureSpec.AT_MOST -> {
                desiredWidth
            }
            else -> {
                desiredWidth
            }
        }

        _height = when (heightMode) {
            MeasureSpec.EXACTLY -> {
                heightSize
            }
            MeasureSpec.AT_MOST -> {
                desiredHeight
            }
            else -> {
                desiredHeight
            }
        }

        setMeasuredDimension(_width, _height)
        updateBackground()
    }

}