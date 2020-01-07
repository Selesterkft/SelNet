package hu.selester.selnet.Adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import hu.selester.selnet.Fragments.TransDataFragment
import hu.selester.selnet.Fragments.TransPhotoFragment

class TransDataAdapter(fragmentManager: FragmentManager): FragmentPagerAdapter(fragmentManager) {

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