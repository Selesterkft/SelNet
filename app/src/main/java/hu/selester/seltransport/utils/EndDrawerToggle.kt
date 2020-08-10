package hu.selester.seltransport.utils

import android.view.View
import android.widget.ImageButton
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import hu.selester.seltransport.R


class EndDrawerToggle(
    private val mDrawerLayout: DrawerLayout,
    toolbar: Toolbar
) : DrawerLayout.DrawerListener {

    init {
        toolbar.findViewById<ImageButton>(R.id.toolbar_menu).setOnClickListener { toggle() }
    }

    private fun toggle() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.END)) {
            mDrawerLayout.closeDrawer(GravityCompat.END)
        } else {
            mDrawerLayout.openDrawer(GravityCompat.END)
        }
    }

    override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
    }

    override fun onDrawerOpened(drawerView: View) {
    }

    override fun onDrawerClosed(drawerView: View) {
    }

    override fun onDrawerStateChanged(newState: Int) {
    }
}