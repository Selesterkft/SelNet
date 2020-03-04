package hu.selester.selnet.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import hu.selester.selnet.database.SelTransportDatabase
import hu.selester.selnet.objects.SessionClass
import hu.selester.selnet.R
import kotlinx.android.synthetic.main.card_content.view.*

class FullMapFragment: Fragment(), OnMapReadyCallback{

    lateinit var mMap: GoogleMap

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.frg_fullmap, container, false)
        rootView.mapView.onCreate(null)
        rootView.mapView.getMapAsync(this)
        return rootView
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        val db = SelTransportDatabase.getInstance(context!!)!!
        val dataList = db.transportDataDao().getAll(SessionClass.getValue("workCode")!!)
        mMap = googleMap!!
        mMap.uiSettings.setAllGesturesEnabled(true)

        for(i in dataList.indices){
            if(dataList[i].lat != 0.0 && dataList[i].lng != 0.0){
                val coord = LatLng(dataList[i].lat,dataList[i].lng)
                mMap.addMarker(MarkerOptions().position(coord).title(dataList[i].name))
            }
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom( LatLng(dataList[dataList.size-1].lat,dataList[dataList.size-1].lng) ,5f))


/*
        val path: MutableList<List<LatLng>> = ArrayList()
        val urlDirections = "https://maps.googleapis.com/maps/api/directions/json?origin=10.3181466,123.9029382&destination=10.311795,123.915864&key=AIzaSyDf4AnKue9H3jP1ozcGQOXAodccTgUYpIM"
        val directionsRequest = object : StringRequest(
            Request.Method.GET, urlDirections, Response.Listener<String> {
                response ->
            val jsonResponse = JSONObject(response)
            // Get routes
            val routes = jsonResponse.getJSONArray("routes")
            val legs = routes.getJSONObject(0).getJSONArray("legs")
            val steps = legs.getJSONObject(0).getJSONArray("steps")
            for (i in 0 until steps.length()) {
                val points = steps.getJSONObject(i).getJSONObject("polyline").getString("points")
                path.add(PolyUtil.decode(points))
            }
            for (i in 0 until path.size) {
                mMap.addPolyline(PolylineOptions().addAll(path[i]).color(Color.RED))
            }
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom( LatLng(10.311795,123.915864) ,22f))
        }, Response.ErrorListener {
                _ ->
        }){}
        val requestQueue = Volley.newRequestQueue(context)
        requestQueue.add(directionsRequest)
*/

    }
}