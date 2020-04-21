package com.locationtracker.fragments

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.DatePicker
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.appbase.components.EmptyLoadingViewPod
import com.appbase.components.SmartRecyclerView
import com.appbase.configure
import com.appbase.fragments.BaseFragment
import com.appbase.fragments.SelectDateFragment
import com.appbase.models.vos.ReturnResult
import com.appbase.showLogD
import com.appbase.showLogE
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.locationtracker.LocationOverlayItemConverter
import com.locationtracker.R
import com.locationtracker.activities.MainActivity
import com.locationtracker.activities.MapActivity
import com.locationtracker.sources.cache.data.LocationEntity
import com.locationtracker.sources.cache.mapper.GeoPointListMapper
import com.locationtracker.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.fragment_history.*
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.OverlayItem
import org.osmdroid.views.overlay.Polyline
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class HistoryFragment : BaseFragment(), LocationHistoryFragment.OnListFragmentInteractionListener,
    DatePickerDialog.OnDateSetListener , OnMapReadyCallback {
    override var fragmentLayout: Int = R.layout.fragment_history

    @Inject
    lateinit var geoPointListMapper: GeoPointListMapper

    @Inject
    lateinit var overLayMapper: LocationOverlayItemConverter

    private val historyRV: SmartRecyclerView by lazy { historyRv as SmartRecyclerView }
    private val emptyView: EmptyLoadingViewPod by lazy { emptyViewPod as EmptyLoadingViewPod }
    private lateinit var mMap: GoogleMap
    private lateinit var adapter: MyLocationHistoryRecyclerViewAdapter
    override val viewModel: MainViewModel by lazy { parentActivity.getHomeViewModel() }
    private val parentActivity: MainActivity by lazy { activity as MainActivity }
    private var currentSelectedDate: String = ""

    private val datePicker: SelectDateFragment by lazy {
        SelectDateFragment(this).apply {
            isCancelable = true
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
    }

    override fun onError() {

    }

    override fun loadData() {
        viewModel.getLocationHistoryByDate(currentSelectedDate)
    }

    override fun initViews(view: View) {

        adapter = MyLocationHistoryRecyclerViewAdapter(this)
        swipeRefreshLayout.isEnabled = false

        currentSelectedDate =
            SimpleDateFormat("dd MM yyyy", Locale.ENGLISH).format(Calendar.getInstance().time)
        dateSelector.text = currentSelectedDate

        historyRV.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                when (newState) {
                    RecyclerView.SCROLL_STATE_IDLE -> {
                        parentActivity.changeNavigationBarVisibility(
                            View.VISIBLE
                        )
                    }
                    RecyclerView.SCROLL_STATE_DRAGGING -> {
                        parentActivity.changeNavigationBarVisibility(
                            View.GONE
                        )
                    }
                    RecyclerView.SCROLL_STATE_SETTLING -> println("Scroll Settling")
                }
            }
        })



        historyRV.configure(context!!, adapter, emptyView)
        emptyView.setCustomErrorView(parentActivity.createView(R.layout.layout_empty_view))
        emptyView.setOverrideDefault(true)



        dateSelector.setOnClickListener {
            datePicker.show(childFragmentManager, "dateSelector")
        }
        initMap()


        viewModel.getLocationHistoryByDate(currentSelectedDate)

        viewModel.locationListLD.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            swipeRefreshLayout.isRefreshing = false
            if (it.isNotEmpty()) {
                ("adding PolyLine for ${it.size} locations")
                val convertedList = LocationEntity.convertForList(it)
                showUserOnMap(convertedList)
                adapter.appendNewData(convertedList)
            } else {
                parentActivity.showSnackBar(view, ReturnResult.ErrorResult("No location to show"))
                adapter.appendNewData(emptyList())
            }
        })


        viewModel.locationHistoryStatusLD.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            swipeRefreshLayout.isRefreshing = false
            parentActivity.showSnackBar(view, it)
            adapter.appendNewData(emptyList())
        })
        loadData()
    }

    private fun goToMap() {
        startActivity(MapActivity.newIntent(parentActivity, currentSelectedDate))
    }

    override fun onListFragmentInteraction(adapterPosition: Int) {

    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()
        calendar[Calendar.YEAR] = year
        calendar[Calendar.MONTH] = month
        calendar[Calendar.DAY_OF_MONTH] = dayOfMonth
        @SuppressLint("SimpleDateFormat")
        currentSelectedDate = SimpleDateFormat("dd MM yyyy", Locale.ENGLISH).format(calendar.time)
        dateSelector.text = currentSelectedDate
        swipeRefreshLayout.isRefreshing = true
        viewModel.getLocationHistoryByDate(currentSelectedDate)
    }


    private fun initMap() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.mapView) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun showUserOnMap(locationList: List<LocationEntity>) {

    }

    private fun addMarker(overLayItem: OverlayItem) {

    }

    private fun addPolyLine(geoPoints: List<GeoPoint>) {

    }

    fun showCurrentLocationOnMap(currentLocation: Location?) {
        if (currentLocation != null) {
            val lat =currentLocation.latitude
            val lng = currentLocation.longitude
            val currentGPSPosition = LatLng(currentLocation.latitude, currentLocation.longitude)
              mMap.addMarker(MarkerOptions().position(currentGPSPosition).title("Marker in Yangon"))
            mMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    currentGPSPosition, 17.0f
                )
            )
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        showLogD("onMapReady Called")
        mMap = googleMap
        // Add a marker in Sydney and move the camera
        val yangon = LatLng(16.9143, 96.1527)

      /*  mMap.addMarker(MarkerOptions().position(yangon).title("Marker in Yangon"))*/

        mMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                yangon, 10.0f
            )
        )
    }
}
