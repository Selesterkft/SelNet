package hu.selester.selnet.Adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.util.Log
import hu.selester.selnet.Database.Tables.TasksTable
import hu.selester.selnet.Fragments.CardFragment

class WorkDatasCardAdapter(fragmentManager: FragmentManager, private var dataList: List<TasksTable>): FragmentPagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
        Log.i("TAG","FRG Num: "+position.toString())
        return CardFragment.newInstance(dataList[position])
    }

    override fun getCount(): Int {
        return dataList.size
    }

    override fun getPageWidth(position: Int): Float {
        return 1f
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return dataList[position].seqnum.toString()
    }
}