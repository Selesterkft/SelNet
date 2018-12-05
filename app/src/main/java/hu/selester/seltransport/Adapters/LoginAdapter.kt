package hu.selester.seltransport.Adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import hu.selester.seltransport.Fragments.LoginAccountFragment
import hu.selester.seltransport.Fragments.LoginCodeFragment

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