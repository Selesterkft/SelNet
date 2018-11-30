package hu.selester.seltransport.Fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import hu.selester.seltransport.Database.Tables.TransportDatasTable
import hu.selester.seltransport.R
import kotlinx.android.synthetic.main.card_content.view.*


class CardFragment: Fragment(), OnMapReadyCallback{

    lateinit var dataList: TransportDatasTable
    lateinit var rootView: View
    lateinit var mMap:GoogleMap

    companion object {
        fun newInstance(data: TransportDatasTable): CardFragment{
            val bundle = Bundle()
            bundle.putSerializable("data", data)
            var cardFragment = CardFragment()
            cardFragment.arguments = bundle
            return cardFragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if(arguments!!.get("data") != null){
            dataList = arguments!!.get("data") as TransportDatasTable
            Log.i("TAG",dataList.toString())
        }
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.i("TAG","CRAETE VIEW")
        rootView = inflater.inflate(R.layout.card_content, container, false)
        rootView.card_content_name.text         = dataList.name
        rootView.card_content_district.text     = dataList.district
        rootView.card_content_city.text         = dataList.city
        rootView.card_content_fulladdress.text  = dataList.address
        rootView.mapView.onCreate(null)
        rootView.mapView.getMapAsync(this)
        rootView.cardContent.setOnClickListener {
            activity!!.supportFragmentManager.beginTransaction().replace(R.id.fragment_container,StatusFragment()).addToBackStack("app").commit()
        }

        return rootView
    }

    override fun onMapReady(googleMap: GoogleMap) {
        Log.i("TAG","CRAETE MAP")
        mMap = googleMap
        val coord = LatLng(dataList.lat, dataList.lng)
        mMap.uiSettings.isZoomControlsEnabled = false
        mMap.uiSettings.isMapToolbarEnabled = false
        mMap.addMarker(MarkerOptions().position(coord).title(dataList.name))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coord,12f))
    }

}