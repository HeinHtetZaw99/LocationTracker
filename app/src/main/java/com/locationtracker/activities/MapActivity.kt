package com.locationtracker.activities

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.appbase.activities.BaseActivity
import com.appbase.showLogD

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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        initUI()
    }

    override val layoutResId: Int = R.layout.activity_map
        
    override val rootLayout: ViewGroup? by lazy { null }
       
    override val viewModel: MainViewModel by contractedViewModels()

    override fun loadData() {
        
    }

    override fun onNetworkError() {
        
    }

    override fun retry() {
        
    }

    override fun initUI() {

    }

    private fun zoomTheMap(it: List<LocationEntity>) {

    }

    override fun logOut() {
        
    }


    companion object{
        const val DATE = "date"

        fun newIntent(context : Context , date : String) = Intent(context,MapActivity::class.java).apply {
            putExtra(DATE, date)
        }
    }

}
