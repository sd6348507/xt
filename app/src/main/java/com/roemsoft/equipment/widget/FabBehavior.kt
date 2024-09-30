package com.roemsoft.equipment.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.roemsoft.common.animation_dsl.animSet

class FabBehavior constructor(context: Context, attrs: AttributeSet) : FloatingActionButton.Behavior() {

    private val SCROLL_DY = 10

    /*constructor(context: Context, attrs: AttributeSet) {
        super
    }*/

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: FloatingActionButton,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int
    ): Boolean {
        return axes == View.SCROLL_AXIS_VERTICAL || super.onStartNestedScroll(
            coordinatorLayout,
            child,
            directTargetChild,
            target,
            axes,
            type
        )
    }

    override fun onNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: FloatingActionButton,
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int,
        consumed: IntArray
    ) {
        super.onNestedScroll(
            coordinatorLayout,
            child,
            target,
            dxConsumed,
            dyConsumed,
            dxUnconsumed,
            dyUnconsumed,
            type,
            consumed
        )

        if (dyConsumed > SCROLL_DY) {
            val bottomMargin = (child.layoutParams as CoordinatorLayout.LayoutParams).bottomMargin
       //     child.animate().translationY((child.height + bottomMargin).toFloat()).setInterpolator(AccelerateInterpolator())
       //     ViewCompat.animate(child).scaleX(0f).scaleY(0f).start()
            animSet {
                objectAnim {
                    this.target = child
                    translationY = floatArrayOf(child.translationY, (child.height + bottomMargin).toFloat())
                    scaleX = floatArrayOf(child.scaleX, 0.5f)
                    scaleY = floatArrayOf(child.scaleY, 0.5f)
                    alpha = floatArrayOf(child.alpha, 0.5f)
                }
                duration = 500L
                interpolator = AccelerateInterpolator()
            //    onEnd = { coordinatorLayout.requestFocus() }
            //    onCancel = { coordinatorLayout.requestFocus() }
            }.start()
        } else if (dyConsumed < -SCROLL_DY) {
            animSet {
                objectAnim {
                    this.target = child
                    translationY = floatArrayOf(child.translationY, 0f)
                    scaleX = floatArrayOf(child.scaleX, 1f)
                    scaleY = floatArrayOf(child.scaleY, 1f)
                    alpha = floatArrayOf(child.alpha, 1f)
                }
                duration = 500L
                interpolator = AccelerateInterpolator()
            //    onEnd = { coordinatorLayout.requestFocus() }
            //    onCancel = { coordinatorLayout.requestFocus() }
            }.start()
        }
    }
}