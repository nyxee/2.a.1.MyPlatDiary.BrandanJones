package com.janus.a2a1myplatdiarybrandanjones

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.Toast
import androidx.core.view.GestureDetectorCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.janus.a2a1myplatdiarybrandanjones.ui.main.EventFragment
import com.janus.a2a1myplatdiarybrandanjones.ui.main.MainFragment
import com.janus.a2a1myplatdiarybrandanjones.ui.main.MainViewModel
import kotlin.math.abs

class MainActivity : AppCompatActivity() {
    val TAG = MainActivity::class.simpleName
    private lateinit var detector: GestureDetectorCompat
    private lateinit var eventFragment: EventFragment
    private lateinit var mainFragment: MainFragment
    private lateinit var activeFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        eventFragment = EventFragment.newInstance()
        mainFragment = MainFragment.newInstance()
        //val viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, mainFragment)
                    .commitNow()
            activeFragment = mainFragment
        }

        detector = GestureDetectorCompat(this, DiaryGestureListener())
    }

    //IF we return true, it means we have handled the touch event,
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return if (detector.onTouchEvent(event)){
            true
        }else
            super.onTouchEvent(event)

    }
    inner class DiaryGestureListener: GestureDetector.SimpleOnGestureListener(){
        private val SWIPE_THREASHOLD = 100
        private val SWIPE_VELOCITY_THRESHOLD = 100


        override fun onFling(
            downEvent: MotionEvent?, //pressing down on the screen..
            moveEvent: MotionEvent?, //swiping across the screen
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            val diffX = moveEvent?.x?.minus(downEvent!!.x) ?: 0.0F
            val diffY = moveEvent?.y?.minus(downEvent!!.y) ?: 0.0F

            return if (abs(diffX) > abs(diffY)) {
                if (abs(diffX) > SWIPE_THREASHOLD && abs(velocityX) > SWIPE_VELOCITY_THRESHOLD){
                    if (diffX > 0){
                        //right swipe
                        this@MainActivity.onSwipeRight()
                    }else {
                        //left swipe
                        this@MainActivity.onSwipeLeft()
                    }
                    true //<- indicate that the event is handled.
                }else
                    super.onFling(downEvent, moveEvent, velocityX, velocityY)
            }else{
                if (abs(diffY) > SWIPE_THREASHOLD && abs(velocityY) > SWIPE_VELOCITY_THRESHOLD){
                    if (diffY > 0){
                        //downward swipe
                        this@MainActivity.onSwipeLBottom()

                    }else {
                        //upward swipe
                        this@MainActivity.onSwipeTop()
                    }
                    true //<- indicate that the event is handled.
                }else
                    super.onFling(downEvent, moveEvent, velocityX, velocityY)
            }

        }
    }

    /**
     * For swipe from the Top to Bottom (Downwards)
     */
    private fun onSwipeLBottom() {
        Toast.makeText(this, "Bottom/Downward Swipe", Toast.LENGTH_SHORT).show()
    }
    /**
     * For swipe from the Bottom to top (Upwards)
     */
    private fun onSwipeTop() {
        Toast.makeText(this, "Top/Upward Swipe", Toast.LENGTH_SHORT).show()

    }
    /**
     * For swipe from the Right (to left
     */
    internal fun onSwipeLeft() {
        Toast.makeText(this, "Left Swipe", Toast.LENGTH_SHORT).show()
        if (activeFragment == mainFragment) {
            Log.v(TAG, "Right Swipe")
//            mainFragment.storeSpecimen()
            mainFragment.saveSpecimen()

            supportFragmentManager.beginTransaction()
                .replace(R.id.container, eventFragment)
                .commitNow()
            activeFragment = eventFragment
        }
    }
    /**
     * For swipe from the Top
     */
    internal fun onSwipeRight() {
        Toast.makeText(this, "Right Swipe", Toast.LENGTH_SHORT).show()
        if (activeFragment == eventFragment) {
            Log.v(TAG, "Left Swipe")
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, mainFragment)
                .commitNow()
            activeFragment = mainFragment
        }
    }

}
