package hu.selester.selnet.Adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import hu.selester.selnet.Fragments.TransDataFragment
import hu.selester.selnet.Fragments.TransPhotoFragment

class TransDataAdapter(fragmentManager: FragmentManager): FragmentPagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment? {
        when(position){
            0 -> return TransDataFragment()
            1 -> return TransPhotoFragment()
        }
        return null
    }

    override fun getCount(): Int {
        return 2
    }
}