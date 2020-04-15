package com.locationtracker.fragments

import android.annotation.SuppressLint
import android.app.DatePickerDialog
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
    DatePickerDialog.OnDateSetListener {
    override var fragmentLayout: Int = R.layout.fragment_history

    @Inject
    lateinit var geoPointListMapper: GeoPointListMapper

    @Inject
    lateinit var overLayMapper: LocationOverlayItemConverter

    private val historyRV: SmartRecyclerView by lazy { historyRv as SmartRecyclerView }
    private val emptyView: EmptyLoadingViewPod by lazy { emptyViewPod as EmptyLoadingViewPod }

    //    private val seeMapFloatBtn: ExtendedFloatingActionButton by lazy { seeMapBtn as ExtendedFloatingActionButton }
    private lateinit var adapter: MyLocationHistoryRecyclerViewAdapter
    override val viewModel: MainViewModel by lazy { parentActivity.getHomeViewModel() }
    private val parentActivity: MainActivity by lazy { activity as MainActivity }
    private var currentSelectedDate: String = ""
    private val handler = Handler()
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

    override fun onResume() {
        super.onResume()
//        seeMapFloatBtn.shrink()
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
//                        parentActivity.slideUp(seeMapFloatBtn, Gravity.END, historyRootLayout)
                    }
                    RecyclerView.SCROLL_STATE_DRAGGING -> {
                        parentActivity.changeNavigationBarVisibility(
                            View.GONE
                        )
//                        parentActivity.slideDown(seeMapFloatBtn, Gravity.END, historyRootLayout)
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
                showLogD("adding PolyLine for ${it.size} locations")
                val convertedList = LocationEntity.convertForList(it)
                showUserOnMap(convertedList)

                adapter.appendNewData(convertedList)
            } else {
                parentActivity.showSnackBar(view, ReturnResult.ErrorResult("No location to show"))
            }
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

        viewModel.getLocationHistoryByDate(currentSelectedDate)
        swipeRefreshLayout.isRefreshing = true
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
        val startPoint = GeoPoint(16.835, 96.128) //setting Home here as default
        mapView.controller.setCenter(startPoint)
        mapView.setHasTransientState(true)


    }

    private fun showUserOnMap(locationList: List<LocationEntity>) {
        mapView.overlays.clear();
        mapView.invalidate()
        if (locationList.isEmpty())
            showLogE("No Points to locate on map")
        if (locationList.size == 1)
            addMarker(overLayMapper.map(locationList.first()))
        else
            addPolyLine(geoPointListMapper.map(locationList))
    }

    private fun addMarker(overLayItem: OverlayItem) {
        mapView.controller.setCenter(overLayItem.point)
        //the overlay
        val mapViewPoint =
            ItemizedIconOverlay(
                arrayListOf(overLayItem).toList(),
                ContextCompat.getDrawable(context!!, R.drawable.ic_location_small),
                object : ItemizedIconOverlay.OnItemGestureListener<OverlayItem?> {
                    override fun onItemSingleTapUp(index: Int, item: OverlayItem?): Boolean {
                        return true
                    }

                    override fun onItemLongPress(index: Int, item: OverlayItem?): Boolean {
                        return false
                    }
                }, context
            )
        mapViewPoint.setDrawFocusedItem(true)
/*
        mapViewPoint.setOnFocusChangeListener { overlay, newFocus ->
            showLogD("MapViewPoints focus changed")
            mapViewPoint.focus = null //removing focus is necessary , otherwise u can't focus it again
        }*/
        mapView.overlays.add(mapViewPoint)
    }

    private fun addPolyLine(geoPoints: List<GeoPoint>) {
        mapView.controller.setCenter(geoPoints.first())
        showLogD("GEOPointList : $geoPoints")
        mapView.invalidate()
        val line = Polyline() //see note below!
        line.outlinePaint.color = ContextCompat.getColor(context!!, R.color.colorPrimary)
        line.setPoints(geoPoints)
        mapView.overlayManager.add(line)
    }

}
