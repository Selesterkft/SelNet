package hu.selester.seltransport.utils

import android.app.Activity
import android.view.View
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.widget.Toolbar.LayoutParams
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import hu.selester.seltransport.R
import kotlin.math.max
import kotlin.math.min


class EndDrawerToggle(
    activity: Activity,
    drawerLayout: DrawerLayout,
    toolbar: Toolbar,
    openDrawerContentDescRes: Int,
    closeDrawerContentDescRes: Int
) : DrawerLayout.DrawerListener {

    private var mDrawerLayout: DrawerLayout = drawerLayout
    private var mArrowDrawable = DrawerArrowDrawable(toolbar.context)
    private var mToggleButton = AppCompatImageButton(
        toolbar.context, null,
        R.attr.toolbarNavigationButtonStyle
    )
    private var mOpenDrawerContentDesc = activity.getString(openDrawerContentDescRes)
    private var mCloseDrawerContentDesc = activity.getString(closeDrawerContentDescRes)

    init {
        mArrowDrawable.direction = DrawerArrowDrawable.ARROW_DIRECTION_END
        toolbar.addView(mToggleButton, LayoutParams(GravityCompat.END))

        mToggleButton.setImageDrawable(mArrowDrawable)
        mToggleButton.setOnClickListener { toggle() }
    }

    fun syncState() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.END)) {
            setPosition(1f)
        } else {
            setPosition(0f)
        }
    }

    private fun toggle() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.END)) {
            mDrawerLayout.closeDrawer(GravityCompat.END)
        } else {
            mDrawerLayout.openDrawer(GravityCompat.END)
        }
    }

    private fun setPosition(position: Float) {
        if (position == 1f) {
            mArrowDrawable.setVerticalMirror(true)
            mToggleButton.contentDescription = mCloseDrawerContentDesc
        } else if (position == 0f) {
            mArrowDrawable.setVerticalMirror(false)
            mToggleButton.contentDescription = mOpenDrawerContentDesc
        }
        mArrowDrawable.progress = position
    }

    override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
        setPosition(min(1f, max(0f, slideOffset)))
    }

    override fun onDrawerOpened(drawerView: View) {
        setPosition(1f)
    }

    override fun onDrawerClosed(drawerView: View) {
        setPosition(0f)
    }

    override fun onDrawerStateChanged(newState: Int) {
    }
}