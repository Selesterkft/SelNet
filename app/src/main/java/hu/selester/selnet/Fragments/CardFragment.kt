package hu.selester.selnet.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import hu.selester.selnet.Database.Tables.TasksTable
import hu.selester.selnet.Objects.SessionClass
import hu.selester.selnet.R
import kotlinx.android.synthetic.main.card_content.view.*


class CardFragment: Fragment(), OnMapReadyCallback{

    lateinit var data: TasksTable
    lateinit var rootView: View
    lateinit var mMap:GoogleMap

    companion object {
        fun newInstance(data: TasksTable): CardFragment{
            val bundle = Bundle()
            bundle.putSerializable("data", data)
            var cardFragment = CardFragment()
            cardFragment.arguments = bundle
            return cardFragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if(arguments!!.get("data") != null){
            data = arguments!!.get("data") as TasksTable
            Log.i("TAG",data.toString())
        }
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.i("TAG","CRAETE VIEW")
        rootView = inflater.inflate(R.layout.card_content, container, false)
        rootView.card_content_name.text         = data.company
        rootView.card_content_district.text     = data.district
        rootView.card_content_city.text         = data.city
        rootView.card_content_fulladdress.text  = data.address
        rootView.mapView.onCreate(null)
        rootView.mapView.getMapAsync(this)
        rootView.cardContent.setOnClickListener {
            SessionClass.setValue("choose_name",data.company)
            SessionClass.setValue("choose_district",data.district)
            SessionClass.setValue("choose_city",data.city)
            SessionClass.setValue("choose_address",data.address)
            SessionClass.setValue("choose_addressId",data.addressId.toString())
            activity!!.supportFragmentManager.beginTransaction().replace(R.id.fragment_container,TransDataFragment()).addToBackStack("app").commit()
        }
        return rootView
    }

    override fun onMapReady(googleMap: GoogleMap) {
        Log.i("TAG","CRAETE MAP")
        mMap = googleMap
        val coord = LatLng(data.lat, data.lng)
        mMap.uiSettings.isZoomControlsEnabled = false
        mMap.uiSettings.isMapToolbarEnabled = false
        mMap.addMarker(MarkerOptions().position(coord).title(data.company))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coord,12f))
    }
}