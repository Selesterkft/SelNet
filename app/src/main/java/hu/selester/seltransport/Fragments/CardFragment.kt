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
import hu.selester.seltransport.Objects.SessionClass
import hu.selester.seltransport.R
import kotlinx.android.synthetic.main.card_content.view.*


class CardFragment: Fragment(), OnMapReadyCallback{

    lateinit var data: TransportDatasTable
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
            data = arguments!!.get("data") as TransportDatasTable
            Log.i("TAG",data.toString())
        }
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.i("TAG","CRAETE VIEW")
        rootView = inflater.inflate(R.layout.card_content, container, false)
        rootView.card_content_name.text         = data.name
        rootView.card_content_district.text     = data.district
        rootView.card_content_city.text         = data.city
        rootView.card_content_fulladdress.text  = data.address
        rootView.mapView.onCreate(null)
        rootView.mapView.getMapAsync(this)
        rootView.cardContent.setOnClickListener {
            SessionClass.setValue("choose_name",data.name)
            SessionClass.setValue("choose_district",data.district)
            SessionClass.setValue("choose_city",data.city)
            SessionClass.setValue("choose_address",data.address)
            SessionClass.setValue("choose_addressId",data.addressID.toString())
            activity!!.supportFragmentManager.beginTransaction().replace(R.id.fragment_container,TransFragment()).addToBackStack("app").commit()
        }
        return rootView
    }

    override fun onMapReady(googleMap: GoogleMap) {
        Log.i("TAG","CRAETE MAP")
        mMap = googleMap
        val coord = LatLng(data.lat, data.lng)
        mMap.uiSettings.isZoomControlsEnabled = false
        mMap.uiSettings.isMapToolbarEnabled = false
        mMap.addMarker(MarkerOptions().position(coord).title(data.name))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coord,12f))
    }

}