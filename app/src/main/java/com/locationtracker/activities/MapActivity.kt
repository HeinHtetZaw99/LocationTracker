package com.locationtracker.activities

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.appbase.activities.BaseActivity

import com.locationtracker.R
import com.locationtracker.sources.cache.data.LocationEntity
import com.locationtracker.sources.cache.mapper.GeoPointListMapper
import com.locationtracker.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.activity_map.*
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polyline
import javax.inject.Inject


class MapActivity : BaseActivity<MainViewModel>() {

    @Inject
    lateinit var geoPointListMapper: GeoPointListMapper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        initUI()
    }

    private fun initMap() {
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);
        val myTile: OnlineTileSourceBase = XYTileSource(
            "cartodb",
            1,
            20,
            256,
            ".png",
            arrayOf("https://cartodb-basemaps-a.global.ssl.fastly.net/light_all/"),
            "© OpenStreetMap contributors"
        )
        mapView.setTileSource(myTile);
        mapView.controller.setZoom(18.0)

        //replace with user home location
        val startPoint = GeoPoint(16.8350692, 96.1283548) //setting Home here
        mapView.controller.setCenter(startPoint)


        viewModel.getLocationHistoryByDate("10 04 2020")

    }

    private fun addPolyLine(geoPoints: List<GeoPoint>) {

//add your points here
//add your points here
        val line = Polyline() //see note below!
        line.outlinePaint.color = ContextCompat.getColor(this, R.color.colorAccent)
        line.setPoints(geoPoints)
        line.setOnClickListener { polyline, mapView, eventPos ->

            false
        }
        mapView.overlayManager.add(line)
    }

    override val layoutResId: Int = R.layout.activity_map
        
    override val rootLayout: View? by lazy { null }
       
    override val viewModel: MainViewModel by contractedViewModels()

    override fun loadData() {
        
    }

    override fun onNetworkError() {
        
    }

    override fun retry() {
        
    }

    override fun initUI() {
        initMap()
        viewModel.locationListLD.observe(this , Observer {
            addPolyLine(geoPointListMapper.map(it))
            zoomTheMap(it)
        })
    }

    private fun zoomTheMap(it: List<LocationEntity>) {

    }

    override fun logOut() {
        
    }

}
