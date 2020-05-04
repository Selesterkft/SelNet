package hu.selester.selnet.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.fragment.app.Fragment
import hu.selester.selnet.databinding.FrgInfoBinding

class TransportInfoFragment : Fragment() {
    lateinit var name: String
    lateinit var address: String
    lateinit var shortInfo: String
    lateinit var longInfo: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        name = arguments!!.getString("name")!!
        address = arguments!!.getString("address")!!
        shortInfo = arguments!!.getString("short_info")!!
        longInfo = arguments!!.getString("long_info")!!

        val binding = FrgInfoBinding.inflate(inflater)
        binding.namePlaceholder.text = name
        binding.addressPlaceholder.text = address
        binding.infoPlaceholder.text = shortInfo

        binding.detailButton.visibility = if (longInfo == "") View.GONE else View.VISIBLE
        binding.detailButton.setOnClickListener{
            val detailPopup = PopupWindow(this.context)
            
        }

        return binding.root
    }
}
