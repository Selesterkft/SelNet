package hu.selester.seltransport.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import hu.selester.seltransport.databinding.FrgInfoBinding

class TransportInfoFragment : Fragment() {
    lateinit var name: String
    lateinit var loadAddress: String
    lateinit var unloadAddress: String
    lateinit var remarks: String
    //lateinit var longInfo: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        name = requireArguments().getString("name")!!
        loadAddress = requireArguments().getString("load_address")!!
        unloadAddress = requireArguments().getString("unload_address")!!
        remarks = requireArguments().getString("remarks")!!
        //longInfo = requireArguments().getString("long_info")!!

        val binding = FrgInfoBinding.inflate(inflater)
        binding.namePlaceholder.text = name
        binding.loadPlaceholder.text = loadAddress
        binding.unloadPlaceholder.text = unloadAddress
        binding.infoPlaceholder.text = remarks

        return binding.root
    }
}
