package com.locationtracker.fragments

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.location.Location
import android.view.Gravity
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.DatePicker
import androidx.annotation.DrawableRes
import androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.appbase.components.EmptyLoadingViewPod
import com.appbase.components.SharePrefUtils
import com.appbase.components.SmartRecyclerView
import com.appbase.configure
import com.appbase.fragments.BaseFragment
import com.appbase.getDateFormatter
import com.appbase.models.vos.ReturnResult
import com.appbase.showLogD
import com.appbase.showLogE
import com.archit.calendardaterangepicker.customviews.CalendarListener
import com.archit.calendardaterangepicker.customviews.DateRangeCalendarView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.locationtracker.LocationOverlayItemConverter
import com.locationtracker.R
import com.locationtracker.activities.MainActivity
import com.locationtracker.sources.cache.data.LocationEntity
import com.locationtracker.sources.cache.mapper.LatLngListMapper
import com.locationtracker.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.fragment_history.*
import kotlinx.android.synthetic.main.layout_settings_sheet_cnvert_to_csv.view.*
import kotlinx.android.synthetic.main.layout_settings_sheet_date_picker.view.*
import org.osmdroid.views.overlay.OverlayItem
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class HistoryFragment : BaseFragment(),
    DatePickerDialog.OnDateSetListener, OnMapReadyCallback, CalendarListener {
    override var fragmentLayout: Int = R.layout.fragment_history
    private val defaultMapHeight: Int by lazy { parentActivity.getScreenHeight(0.5) }
    private val handler = HomeFragment.CustomHandler()

    @Inject
    lateinit var sharePrefUtils: SharePrefUtils

    private lateinit var dateRangeCalendarView: DateRangeCalendarView


    private var selectedStartDate: Calendar? = null

    private var selectedEndDate: Calendar? = null


    @Inject
    lateinit var latLngListMapper: LatLngListMapper

    @Inject
    lateinit var overLayMapper: LocationOverlayItemConverter

    private val datePickerSheet: BottomSheetDialog by lazy {
        BottomSheetDialog(
            context!!,
            R.style.SheetDialog
        )
    }

    private val csvConfirmationSheet: BottomSheetDialog by lazy {
        BottomSheetDialog(
            context!!,
            R.style.SheetDialog
        )
    }

    private fun createDatePickerSheet() {
        val datePickerView = parentActivity.createView(R.layout.layout_settings_sheet_date_picker)
        dateRangeCalendarView = datePickerView.calendarView
        datePickerView.calendarView.setCalendarListener(this)
        datePickerView.calendarView.setSelectableDateRange(selectedStartDate!!, selectedEndDate!!)
        if (selectedEndDate!!.time == selectedStartDate!!.time)
            datePickerView.calendarView.setSelectedDateRange(
                selectedStartDate!!,
                selectedStartDate!!
            )
        else
            datePickerView.calendarView.setSelectedDateRange(selectedStartDate!!, selectedEndDate!!)

        datePickerView.okBtn.setOnClickListener {
            loadData()
            datePickerSheet.dismiss()
        }
        datePickerSheet.setContentView(datePickerView)
    }

    private fun showCsvConfirmationSheetSheet() {
        val csvConfirmationView =
            parentActivity.createView(R.layout.layout_settings_sheet_cnvert_to_csv)
        csvConfirmationView.startDateTv.text = getDateFormatter().format(selectedStartDate!!.time)
        csvConfirmationView.endDateTv.text = getDateFormatter().format(selectedEndDate!!.time)
        csvConfirmationView.savedAsCSVBtn.setOnClickListener {
            parentActivity.convertToCSV()

            csvConfirmationSheet.dismiss()
        }
        csvConfirmationSheet.setContentView(csvConfirmationView)
        csvConfirmationSheet.show()
    }

    private val historyRV: SmartRecyclerView by lazy { historyRv as SmartRecyclerView }
    private val emptyView: EmptyLoadingViewPod by lazy { emptyViewPod as EmptyLoadingViewPod }
    private var mMap: GoogleMap? = null
    private lateinit var adapter: MyLocationHistoryRecyclerViewAdapter
    override val viewModel: MainViewModel by lazy { parentActivity.getHomeViewModel() }
    private val parentActivity: MainActivity by lazy { activity as MainActivity }
    private var currentSelectedDate: String = ""


    override fun onError() {

    }


    override fun onResume() {
        super.onResume()
        if (parentActivity.isLocationPermissionGranted()) {
            showLogE("defaultMapHeight : $defaultMapHeight")
            locationPermissionDisabledView.visibility = GONE
            initViews(view!!)
            convertToCSVBtn.visibility = VISIBLE
        } else {
            locationPermissionDisabledView.visibility = VISIBLE
            parentActivity.unlockAppBarOpen(appbar, 0.0)
            parentActivity.changeAppbarLayoutDraggableBehavior(appbar, false)
            convertToCSVBtn.visibility = GONE
        }
    }

    @SuppressLint("SetTextI18n")
    override fun loadData() {
        showLogE(
            "PICKER",
            "selectedStartDate = ${getDateFormatter().format(selectedStartDate!!.time)} and selectedEndDate = ${getDateFormatter().format(
                selectedEndDate!!.time
            )}"
        )
        dateSelector.text =
            "${getDateFormatter().format(selectedStartDate!!.time)} - ${getDateFormatter().format(
                selectedEndDate!!.time
            )}"
        viewModel.getLocationHistoryByDate(
            getDateFormatter().format(selectedStartDate!!.time),
            getDateFormatter().format(selectedEndDate!!.time)
        )
    }

    @SuppressLint("SetTextI18n")
    override fun initViews(view: View) {

        val params: LayoutParams =
            appbar.layoutParams as LayoutParams
        val behavior: AppBarLayout.Behavior = AppBarLayout.Behavior()
        behavior.setDragCallback(object : AppBarLayout.Behavior.DragCallback() {
            override fun canDrag(appBarLayout: AppBarLayout): Boolean {
                return false
            }
        })
        params.behavior = behavior
        parentActivity.unlockAppBarOpen(appbar, 0.5)
        parentActivity.changeAppbarLayoutDraggableBehavior(appbar, true)


        initMap()

        selectedStartDate = Calendar.getInstance()
            .also {
                val lastDate = sharePrefUtils.load(SharePrefUtils.KEYS.INSTALLED_DATE)
                if (lastDate != null && lastDate != "") {
                    it.time = getDate(lastDate) ?: Calendar.getInstance().time
                } else {
                    it.time = Calendar.getInstance().time
                }
            }
        selectedEndDate = Calendar.getInstance()
        createDatePickerSheet()

        locateMySelfBtn.setOnClickListener { showCurrentLocationOnMap(parentActivity.currentLocation) }

        adapter = MyLocationHistoryRecyclerViewAdapter(this)

        swipeRefreshLayout.setOnRefreshListener {
            loadData()
        }

        currentSelectedDate =
            SimpleDateFormat("dd MM yyyy", Locale.ENGLISH).format(Calendar.getInstance().time)
        dateSelector.text =
            "${getDateFormatter().format(selectedStartDate?.time)} - ${getDateFormatter().format(
                selectedEndDate?.time
            )}"

        historyRV.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                when (newState) {
                    RecyclerView.SCROLL_STATE_IDLE -> {
                        handler.postDelayed({
                            parentActivity.changeNavigationBarVisibility(
                                View.VISIBLE
                            )

                            parentActivity.slideUp(convertToCSVBtn, Gravity.END, historyRootLayout)
                        }, 1000)
                    }
                    RecyclerView.SCROLL_STATE_DRAGGING -> {
                        parentActivity.changeNavigationBarVisibility(
                            View.GONE
                        )
                        parentActivity.slideDown(convertToCSVBtn, Gravity.END, historyRootLayout)
                    }
                    RecyclerView.SCROLL_STATE_SETTLING -> println("Scroll Settling")
                }
            }
        })

        convertToCSVBtn.shrink()
        convertToCSVBtn.setOnClickListener {
            if (convertToCSVBtn.isExtended) {
                convertToCSVBtn.shrink()
                showCsvConfirmationSheetSheet()
            } else {
                convertToCSVBtn.extend()
            }
        }

        enableLocationPermissionBtn.setOnClickListener {
            parentActivity.isManuallyInvokedPermissionAsking = true
            parentActivity.checkLocationPermission()
        }

        historyRV.configure(context!!, adapter, emptyView)
        emptyView.setCustomErrorView(parentActivity.createView(R.layout.layout_empty_view))
        emptyView.setOverrideDefault(true)



        dateSelector.setOnClickListener {
            datePickerSheet.show()
//            datePicker.show(childFragmentManager, "dateSelector")
        }




        viewModel.locationListLD.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            mMap?.clear()
            swipeRefreshLayout.isRefreshing = false
            if (it.isNotEmpty()) {
                ("adding PolyLine for ${it.size} locations")
                val convertedList = LocationEntity.convertForList(it)
                showUserOnMap(it)
                adapter.appendNewData(convertedList)
            } else {
                parentActivity.showSnackBar(view, ReturnResult.ErrorResult("No location to show"))
                adapter.appendNewData(emptyList())
            }
            showCurrentLocationOnMap(parentActivity.currentLocation)
        })


        viewModel.locationHistoryStatusLD.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            swipeRefreshLayout.isRefreshing = false
            parentActivity.showSnackBar(view, it)
            adapter.appendNewData(emptyList())
        })
        loadData()
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
//        viewModel.getLocationHistoryByDate(currentSelectedDate)
        loadData()
    }


    private fun initMap() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.mapView) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    private fun showUserOnMap(locationList: List<LocationEntity>) {
        mMap?.clear()
        addPolyLine(latLngListMapper.map(locationList))
    }

    private fun addMarker(overLayItem: OverlayItem) {

    }

    private fun addPolyLine(geoPoints: List<LatLng>) {
        val options: PolylineOptions = PolylineOptions().width(5f).color(Color.BLUE).geodesic(true)
        options.addAll(geoPoints)
        mMap?.addPolyline(options)
    }

    fun showCurrentLocationOnMap(currentLocation: Location?) {
        try {
            if (currentLocation != null && mMap != null) {
                val lat = currentLocation.latitude
                val lng = currentLocation.longitude

                val currentGPSPosition = LatLng(currentLocation.latitude, currentLocation.longitude)
                mMap?.addMarker(
                    MarkerOptions().position(currentGPSPosition).title("Marker in Yangon").icon(
                        bitmapDescriptorFromVector(context!!, R.drawable.ic_pin_large)
                    )
                )
                mMap?.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        currentGPSPosition, 17.0f
                    )
                )
            }
        } catch (e: UninitializedPropertyAccessException) {
            showLogE(e)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        showLogD("onMapReady Called")
        mMap = googleMap
        mMap!!.uiSettings.isZoomControlsEnabled = true
        // Add a marker in Sydney and move the camera
        val yangon = LatLng(16.9143, 96.1527)

        /*  mMap.addMarker(MarkerOptions().position(yangon).title("Marker in Yangon"))*/

        mMap!!.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                yangon, 10.0f
            )
        )
    }

    override fun onDateRangeSelected(startDate: Calendar, endDate: Calendar) {
        try {
            dateRangeCalendarView.setSelectedDateRange(startDate, endDate)
            selectedStartDate = startDate
            selectedEndDate = endDate
        } catch (e: IllegalArgumentException) {
            dateRangeCalendarView.setSelectedDateRange(startDate, startDate)
            selectedStartDate = startDate
            selectedEndDate = startDate
            showLogE("Error in setDatesOnCalendar : ", e)
        }
        showLogE(
            "PICKER",
            "selectedStartDate = ${getDateFormatter().format(selectedStartDate!!.time)} and selectedEndDate = ${getDateFormatter().format(
                selectedEndDate!!.time
            )}"
        )
    }

    override fun onFirstDateSelected(startDate: Calendar) {
        selectedStartDate = startDate
        selectedEndDate = startDate
        showLogE(
            "PICKER",
            "onFirstDateSelected(${getDateFormatter().format(selectedStartDate!!.time)}called"
        )
    }

    private fun getDate(date: String): Date? {
        return try {
            getDateFormatter().parse(date)
        } catch (e: ParseException) {
            showLogE("Error in parsing in getDate", e)
            null
        }
    }

    private fun bitmapDescriptorFromVector(
        context: Context,
        @DrawableRes vectorDrawableResourceId: Int
    ): BitmapDescriptor? {
        val vectorDrawable: Drawable =
            ContextCompat.getDrawable(context, vectorDrawableResourceId)!!
        vectorDrawable.setBounds(
            0,
            0,
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight
        )
        val bitmap: Bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}
