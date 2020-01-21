package hu.selester.selnet.adapters

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import hu.selester.selnet.fragments.TransDataFragment
import hu.selester.selnet.fragments.TransPhotoFragment

@SuppressLint("WrongConstant")
class TransDataAdapter(fragmentManager: FragmentManager): FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        when(position){
            0 -> return TransDataFragment()
            1 -> return TransPhotoFragment()
        }
        return Fragment()
    }

    override fun getCount(): Int {
        return 2
    }
}