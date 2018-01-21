package com.jacquessmuts.ob1db

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.widget.TextView

/**
 * Created by Jacques Smuts on 1/21/2018.
 * This is to rotate the text in a 3D space so it kinda looks like the Star Wars intro thing
 */
class SkewedTextView : TextView{

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)

    val rotateAnimation = Rotate3dAnimation(8f, -8f, 50f, 80f, 60f, false)

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas!!.save()
        startAnimation(rotateAnimation)
        canvas!!.skew(-0.5f, -0.5f)
        canvas.restore()
    }
}