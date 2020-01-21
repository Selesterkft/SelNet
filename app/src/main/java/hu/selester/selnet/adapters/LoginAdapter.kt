package hu.selester.selnet.adapters

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentManager
import hu.selester.selnet.fragments.LoginAccountFragment
import hu.selester.selnet.fragments.LoginCodeFragment

@SuppressLint("WrongConstant")
class LoginAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> return LoginCodeFragment.newInstance()
            1 -> return LoginAccountFragment.newInstance()
        }
        return Fragment()
    }

    override fun getCount(): Int {
        return 2
    }
}