package cc.changhao.innovation.tiktokstyleloading

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.animation.ValueAnimator
import android.view.animation.LinearInterpolator


/**
 * TikTok风格的加载动画
 *
 * @author cui changhao
 */
class TikTokStyleLoadingView @JvmOverloads constructor(context: Context,
                                                       attrs: AttributeSet? = null): View(context, attrs) {

    private var mPaintRed = Paint()
    private var mPaintBlue = Paint()
    private var mAnimatedValue = 0f
    private var mPerDegreeDistance = MOVE_DISTANCE/MOVE_DISTANCE_DEGREE
    private var mMovePositionA = 0f
    private var mMovePositionB = 0f

    private var mIsOddRotation = true
    private var mMoveDegree = 0f
    private var mRadiusA = CIRCLE_RADIUS
    private var mRadiusB = CIRCLE_RADIUS

    companion object {
        /**
         * 圆的大小
         */
        const val CIRCLE_RADIUS = 20f

        /**
         * 设置比1大的数
         * 越小的数圆的大小变换越大
         * 越大的数圆的大小变化越小
         */
        const val CIRCLE_WEIGHT_ADJUST = 5f

        /**
         * 动画间隔，越小越快
         */
        const val ANIMATION_DURATION = 1000L

        /**
         * 圆的移动幅度
         */
        const val MOVE_DISTANCE = 40f

        /**
         * 圆的轨迹
         */
        const val MOVE_DISTANCE_DEGREE = 180f
    }

    init {
        mPaintRed.color = resources.getColor(android.R.color.holo_red_light)
        mPaintBlue.color = resources.getColor(android.R.color.holo_blue_bright)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(MOVE_DISTANCE.toInt()*2, CIRCLE_RADIUS.toInt()*2)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        mMoveDegree = if(mAnimatedValue < MOVE_DISTANCE_DEGREE) {
            mIsOddRotation = false
            mAnimatedValue % MOVE_DISTANCE_DEGREE
        } else {
            mIsOddRotation = true
            MOVE_DISTANCE_DEGREE - mAnimatedValue % MOVE_DISTANCE_DEGREE
        }

        mMovePositionA = mPerDegreeDistance * mMoveDegree + MOVE_DISTANCE/2
        mMovePositionB = MOVE_DISTANCE - mPerDegreeDistance * mMoveDegree + MOVE_DISTANCE/2

        // 两个圆的转动带来的类似3D效果的前后切换
        if(mIsOddRotation) {
            canvas.drawCircle(mMovePositionA, CIRCLE_RADIUS, mRadiusA, mPaintRed)
            canvas.drawCircle(mMovePositionB, CIRCLE_RADIUS, mRadiusB, mPaintBlue)
        } else {
            canvas.drawCircle(mMovePositionB, CIRCLE_RADIUS, mRadiusB, mPaintBlue)
            canvas.drawCircle(mMovePositionA, CIRCLE_RADIUS, mRadiusA, mPaintRed)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        animateArch()
    }

    private fun animateArch() {
        val frontEndExtend = ValueAnimator.ofFloat(0f, 360f)
        frontEndExtend.duration = ANIMATION_DURATION
        frontEndExtend.repeatCount = ValueAnimator.INFINITE
        frontEndExtend.interpolator = LinearInterpolator()
        frontEndExtend.addUpdateListener { animation ->
            val newValue = animation.animatedValue as Float

            mRadiusA = (Math.sin(Math.toRadians(newValue.toDouble())).toFloat() + CIRCLE_WEIGHT_ADJUST) / (CIRCLE_WEIGHT_ADJUST + 1)  * CIRCLE_RADIUS
            mRadiusB = (Math.cos(Math.toRadians(newValue.toDouble() + 90)).toFloat() + CIRCLE_WEIGHT_ADJUST) / (CIRCLE_WEIGHT_ADJUST + 1)  * CIRCLE_RADIUS
            mAnimatedValue = (animation.animatedValue as Float)
            invalidate()
        }
        frontEndExtend.start()
    }
}