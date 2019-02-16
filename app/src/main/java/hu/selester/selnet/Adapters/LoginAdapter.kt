package hu.selester.selnet.Adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import hu.selester.selnet.Fragments.LoginAccountFragment
import hu.selester.selnet.Fragments.LoginCodeFragment

class LoginAdapter(fragmentManager: FragmentManager): FragmentPagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment? {
        when(position){
            0 -> return LoginCodeFragment.newInstance()
            1 -> return LoginAccountFragment.newInstance()
        }
        return null
    }

    override fun getCount(): Int {
        return 2
    }
}