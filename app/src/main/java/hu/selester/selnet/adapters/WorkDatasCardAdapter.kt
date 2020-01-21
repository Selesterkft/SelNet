package hu.selester.selnet.adapters

import android.annotation.SuppressLint
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import hu.selester.selnet.database.tables.TasksTable
import hu.selester.selnet.fragments.CardFragment

@SuppressLint("WrongConstant")
class WorkDatasCardAdapter(
    fragmentManager: FragmentManager,
    private var dataList: List<TasksTable>
) : FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        Log.i("TAG", "FRG Num: " + position.toString())
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