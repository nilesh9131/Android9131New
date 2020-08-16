package com.example.nileshpc.motionlayout

import android.animation.*
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import com.example.nileshpc.R
import kotlinx.android.synthetic.main.activity_animation_base.*

/**
 * This class is base class to hold the animation point,main concentration will be on MotionLayout
 *
 */
class AnimationBasicActivity : AppCompatActivity() {

    lateinit var star: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_animation_base)
        initView()
    }

    fun initView() {
        star = findViewById(R.id.star)

        rotateButton.setOnClickListener {
            rotater()
        }

        translateButton.setOnClickListener {
            translater()
        }

        scaleButton.setOnClickListener {
            scaler()
        }

        fadeButton.setOnClickListener {
            fader()
        }

        colorizeButton.setOnClickListener {
            colorizer()
        }

        showerButton.setOnClickListener {
            shower()
        }
        motionButton.setOnClickListener {
            launchMotion()
        }
    }

    private fun rotater() {
        val animate = ObjectAnimator.ofFloat(star, View.ROTATION, -360f, 0f)
        animate.duration = 1000
        animate.disableViewDuringAnimation(rotateButton)
        animate.start()

    }

    private fun translater() {
        val animator = ObjectAnimator.ofFloat(star, View.TRANSLATION_X, 200f)
        animator.repeatCount = 1
        animator.repeatMode = ObjectAnimator.REVERSE
        animator.disableViewDuringAnimation(translateButton)
        animator.start()
    }

    private fun scaler() {
        val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 4f)
        val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 4f)
        val animator = ObjectAnimator.ofPropertyValuesHolder(star, scaleX, scaleY)
        animator.repeatCount = 1
        animator.repeatMode = ObjectAnimator.REVERSE
        animator.disableViewDuringAnimation(scaleButton)
        animator.start()
    }

    private fun fader() {
        val faderAnimator = ObjectAnimator.ofFloat(star, View.ALPHA, 0f)
        faderAnimator.repeatCount = 1
        faderAnimator.repeatMode = ObjectAnimator.REVERSE
        faderAnimator.disableViewDuringAnimation(fadeButton)
        faderAnimator.start()
    }

    private fun colorizer() {

        /* val animatortemp =
             ObjectAnimator.ofInt(star.parent, "backgroundColor", Color.BLACK, Color.RED).start()*/

        val animator =
            ObjectAnimator.ofArgb(star.parent, "backgroundColor", Color.BLACK, Color.RED)
        animator.setDuration(500)
        animator.repeatCount = 1
        animator.repeatMode = ObjectAnimator.REVERSE
        animator.disableViewDuringAnimation(colorizeButton)
        animator.start()
    }

    private fun shower() {
        //Created new view to be animated round and down
        val container = star.parent as ViewGroup
        val containerW = container.width
        val containerH = container.height
        var starW: Float = star.width.toFloat()
        var starH: Float = star.height.toFloat()
        //add new view to container
        val newStar = AppCompatImageView(this)
        newStar.setImageResource(R.drawable.ic_star)
        newStar.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        container.addView(newStar)
        //transational scaling
        newStar.scaleX = Math.random().toFloat() * 1.5f + .1f
        newStar.scaleY = newStar.scaleX
        starW *= newStar.scaleX
        starH *= newStar.scaleY
        newStar.translationX = Math.random().toFloat() *
                containerW - starW / 2
        //animating two objectanimator
        val mover = ObjectAnimator.ofFloat(
            newStar, View.TRANSLATION_Y,
            -starH, containerH + starH
        )
        mover.interpolator = AccelerateInterpolator(1f)
        val rotator = ObjectAnimator.ofFloat(
            newStar, View.ROTATION,
            (Math.random() * 1080).toFloat()
        )
        rotator.interpolator = LinearInterpolator()
        //parallel animation
        val set = AnimatorSet()
        set.playTogether(mover, rotator)
        set.duration = (Math.random() * 1500 + 500).toLong()

        set.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                container.removeView(newStar)
            }
        })
        set.start()

    }

    fun launchMotion() {
        val intent = Intent(this@AnimationBasicActivity, MotionBaseActivity::class.java)
        startActivity(intent)
    }

    private fun ObjectAnimator.disableViewDuringAnimation(view: View) {
        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                view.isEnabled = true
            }

            override fun onAnimationStart(animation: Animator?) {
                super.onAnimationStart(animation)
                view.isEnabled = false
            }
        })
    }
}