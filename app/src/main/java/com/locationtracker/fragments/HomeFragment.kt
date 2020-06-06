package com.locationtracker.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Handler
import android.os.Message
import android.provider.Settings
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.appbase.*
import com.appbase.components.Locator
import com.appbase.components.SharePrefUtils
import com.appbase.components.SpeedController
import com.appbase.fragments.BaseFragment
import com.appbase.models.vos.ReturnResult
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.locationtracker.BuildConfig
import com.locationtracker.R
import com.locationtracker.activities.ContactListActivity
import com.locationtracker.activities.ContactListActivity.Companion.CLINICS_LIST
import com.locationtracker.activities.ContactListActivity.Companion.CONTACT_LIST
import com.locationtracker.activities.MainActivity
import com.locationtracker.activities.SelfExaminationActivity
import com.locationtracker.adapters.AboutUsAdapter
import com.locationtracker.adapters.ImageViewPagerAdapter
import com.locationtracker.sources.cache.data.AboutUsVO
import com.locationtracker.sources.cache.data.BannerVO
import com.locationtracker.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.layout_settings_sheet_about_app.view.*
import kotlinx.android.synthetic.main.layout_settings_sheet_about_app.view.closeBtn
import kotlinx.android.synthetic.main.layout_settings_sheet_about_us.*
import kotlinx.android.synthetic.main.layout_settings_sheet_about_us.view.*
import java.lang.ref.WeakReference
import java.lang.reflect.Field
import java.util.*
import javax.inject.Inject


class HomeFragment : BaseFragment(),
    Locator.Listener {

    private val handler = CustomHandler()

    @Inject
    lateinit var sharePrefUtils: SharePrefUtils
    private val mImagePagerAdapter: ImageViewPagerAdapter by lazy {
        ImageViewPagerAdapter(
            parentActivity
        )
    }

    private lateinit var sliderPage: SliderRunner
    private var mScroller: Field? = null

    private val locationManager: LocationManager by lazy {
        parentActivity.getSystemService(
            AppCompatActivity.LOCATION_SERVICE
        ) as LocationManager
    }
    private val aboutUsSheet: BottomSheetDialog by lazy {
        BottomSheetDialog(
            context!!,
            R.style.SheetDialog
        )
    }
    private val aboutAPPSheet: BottomSheetDialog by lazy {
        BottomSheetDialog(
            context!!,
            R.style.SheetDialog
        )
    }
    private val locator: Locator by lazy { Locator(view!!.context, locationManager) }

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    override var fragmentLayout: Int = R.layout.fragment_home

    val parentActivity: MainActivity by lazy { activity as MainActivity }

    override val viewModel: MainViewModel by lazy { parentActivity.getHomeViewModel() }

    override fun onError() {

    }

    override fun loadData() {

    }

    override fun initViews(view: View) {
        checkGPS()
        versionNumberTv.text = getString(R.string.version_number, BuildConfig.VERSION_NAME)

        viewModel.locationStatusLD.observe(this, androidx.lifecycle.Observer {
            if (it is ReturnResult.PositiveResult) {

                if (isWriteStoragePermissionGranted()) {
                    writeFileToDisk(
                        "Android/locationData/",
                        "location.txt",
                        parentActivity.getErrorContentMsg(it)
                        , false
                    )
                } else {
                    showLogD("Permission is not granted. so no log is written")
                }
            } else
                parentActivity.showSnackBar(view, it)
        })


        checkSymptomsBtn.setOnClickListener {
            startActivity(SelfExaminationActivity.newIntent(parentActivity))
        }

        goToFocClinics.setOnClickListener {
            startActivity(ContactListActivity.newIntent(parentActivity, CLINICS_LIST))
        }


        goToContacts.setOnClickListener {
            startActivity(ContactListActivity.newIntent(parentActivity, CONTACT_LIST))
        }
        aboutAppBtn.setOnClickListener {
            this.showAboutAppDialog()
        }
        aboutUsBtn.setOnClickListener {
            this.showAboutUsDialog()
//            parentActivity.showLongToast("လောလောဆယ်မထည့််ရသေးပါ 😁")
        }

        //for scrolling effect
        try {
            mScroller = ViewPager::class.java.getDeclaredField("mScroller")
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        }
        mScroller!!.isAccessible = true

        setUpViewPager(viewModel.loadBannerList())



        if (sharePrefUtils.load(SharePrefUtils.KEYB.IS_FIRST_TIME_USER)) {
            showLogE("HE is a first time user , so today date is recorded !")
            sharePrefUtils.save(
                SharePrefUtils.KEYS.INSTALLED_DATE,
                getDateFormatter().format(Calendar.getInstance().time)
            )

            sharePrefUtils.save(SharePrefUtils.KEYB.IS_FIRST_TIME_USER, false)
        }
        parentActivity.askPermissions()
    }


    override fun onLocationFound(location: Location?) {
        latitude = location?.latitude ?: 0.0
        longitude = location?.longitude ?: 0.0
        viewModel.getGeoEncodeData(location!!)
        showLogE("Location founded : ")

        locationManager.removeUpdates(locator)
    }

    override fun onLocationNotFound() {
        showLogE("Location not founded ")
        showShortToast("Location Getting Failed")
        locationManager.removeUpdates(locator)

    }


    /*   fun trackLocation() {
           parentActivity.startLocationTrackingService()
       }
   */


    private fun isLocationEnabled(): Boolean =
        locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

    private fun isWriteStoragePermissionGranted() =
        ContextCompat.checkSelfPermission(
            context!!,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED


    private fun showForceGPSEnableDialog() {
        val alertDialog = AlertDialog.Builder(parentActivity)
        alertDialog.setTitle("Enable Location")
        alertDialog.setMessage("Your locations setting is not enabled. Please enabled it in settings menu.")
        alertDialog.setPositiveButton("Location Settings") { dialog, which ->
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }
        alertDialog.setNegativeButton(
            "Cancel"
        ) { dialog, which -> dialog?.dismiss() }
        val alert = alertDialog.create()
        alert.show()
    }

    private fun showAboutAppDialog() {
        val dialogView = parentActivity.createView(R.layout.layout_settings_sheet_about_app)
        dialogView.closeBtn.setOnClickListener { aboutAPPSheet.dismiss() }
        aboutAPPSheet.setContentView(dialogView)
        aboutAPPSheet.show()
    }

    private fun showAboutUsDialog() {
        val dialogView = parentActivity.createView(R.layout.layout_settings_sheet_about_us)
        dialogView.closeBtn.setOnClickListener { aboutUsSheet.dismiss() }
        val devTeamAdapter = AboutUsAdapter(context!!)
        val taAdapter = AboutUsAdapter(context!!)
        val stAdapter = AboutUsAdapter(context!!)
        dialogView.devTeamRv.configure(context!!,devTeamAdapter)
        dialogView.technicalAdvisorRv.configure(context!!,taAdapter)
        dialogView.specialThanksRv.configure(context!!,stAdapter)
        devTeamAdapter.appendNewData(AboutUsVO.getDevTeamList())
        taAdapter.appendNewData(AboutUsVO.getTechnicalAdvisorList())
        stAdapter.appendNewData(AboutUsVO.getSpecialThanksList())
        aboutUsSheet.setContentView(dialogView)
        aboutUsSheet.show()
    }

    class CustomHandler : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
        }
    }

    private fun setUpViewPager(data: List<BannerVO>) {
        sliderPage = SliderRunner(adViewPager, data.size)
        mImagePagerAdapter.setData(data)
        adViewPager.adapter = mImagePagerAdapter

        dotIndicator.setViewPager(adViewPager)
        handler.removeCallbacks(sliderPage)
        handler.postDelayed(sliderPage, 3000)

        adViewPager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            @SuppressLint("SyntheticAccessor")
            override fun onPageSelected(position: Int) {
                if (dotIndicator != null) {
                    dotIndicator.selection = position
                    sliderPage.setCurrent(current = position)
                    handler.removeCallbacks(sliderPage)
                    handler.removeCallbacksAndMessages(null)
                    handler.postDelayed(sliderPage, 3000)
                    checkGPS()
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
        try {
            mScroller!![adViewPager] = SpeedController(context, DecelerateInterpolator(), true)
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
    }

    fun checkGPS() {
        if (isLocationEnabled()) {
            warningLayout.visibility = GONE
        } else {
            warningLayout.visibility = VISIBLE
        }
    }
}

class SliderRunner internal constructor(adViewPager: ViewPager, private val bannerImageSize: Int) :
    Runnable {
    private var currentPage = 0
    private var adViewPagerWeakReference: WeakReference<ViewPager?> = WeakReference(adViewPager)
    override fun run() {
        currentPage++
        if (currentPage == bannerImageSize) {
            currentPage = 0
        }
        if (adViewPagerWeakReference.get() != null) adViewPagerWeakReference.get()!!
            .setCurrentItem(currentPage, true)


    }

    fun setCurrent(current: Int) {
        this.currentPage = current
    }


}
