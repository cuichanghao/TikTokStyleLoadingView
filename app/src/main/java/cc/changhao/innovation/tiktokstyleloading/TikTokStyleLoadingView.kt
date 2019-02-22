package cc.changhao.innovation.tiktokstyleloading

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.animation.ValueAnimator
import android.util.Log
import android.view.animation.BounceInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import java.util.logging.Logger


/**
 * TikTok風のLoadingView
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

    private var mPositionOffSet = MOVE_DISTANCE/2
    private var mAdjust = CIRCLE_RADIUS / (CIRCLE_WEIGHT_ADJUST + 1)
    private var mCircleWeightAdjust = CIRCLE_WEIGHT_ADJUST * mAdjust

    companion object {
        /**
         * 円の大きさ
         */
        const val CIRCLE_RADIUS = 22f

        /**
         * 1より大きい数字を設定
         * 小さいほど円の大きさ変化が激しい
         * 多きいほど円の変化が小さい
         */
        const val CIRCLE_WEIGHT_ADJUST = 5f

        /**
         * animation間隔、小さいほど早い
         */
        const val ANIMATION_DURATION = 1000L

        /**
         * 円の移動幅
         */
        const val MOVE_DISTANCE = 36f

        /**
         * 円の軌跡
         */
        const val MOVE_DISTANCE_DEGREE = 180f

        /**
         * 90のradians = Π/2
         */
        const val RADIANS_90 = 1.5707963267948966
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

        mMovePositionA = mPositionOffSet + mPerDegreeDistance * mMoveDegree
        mMovePositionB = mPositionOffSet + MOVE_DISTANCE - mPerDegreeDistance * mMoveDegree

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
        val circleAnimator = ValueAnimator.ofFloat(0f, 360f)
        circleAnimator.duration = ANIMATION_DURATION
        circleAnimator.repeatCount = ValueAnimator.INFINITE
        circleAnimator.interpolator = LinearInterpolator()
        circleAnimator.addUpdateListener { animation ->
            val newValue = animation.animatedValue as Float

            val radians = Math.toRadians(newValue.toDouble())

            mRadiusA = Math.sin(radians).toFloat() * mAdjust + mCircleWeightAdjust
            mRadiusB = Math.cos(radians + RADIANS_90).toFloat() * mAdjust + mCircleWeightAdjust
            mAnimatedValue = (animation.animatedValue as Float)


            // 境界線では値が激しく変化し描画がちらつくので0と360、且つonDraw内部の%計算結果を考慮し180も無視
            if( mAnimatedValue == 0f
                || mAnimatedValue == 180f
                || mAnimatedValue == 360f
            ) return@addUpdateListener

            invalidate()
        }
        circleAnimator.start()
    }
}