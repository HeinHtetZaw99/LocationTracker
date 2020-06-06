package com.appbase.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.NetworkOnMainThreadException
import android.os.StrictMode
import android.provider.Settings
import android.transition.Slide
import android.transition.Transition
import android.transition.TransitionManager
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.annotation.ColorRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.apbase.R
import com.appbase.components.Connectivity
import com.appbase.components.EmptyLoadingViewPod
import com.appbase.components.SingleEventLiveData
import com.appbase.components.interfaces.GenericErrorMessageFactory
import com.appbase.components.show
import com.appbase.models.vos.ReturnResult
import com.appbase.models.vos.ReturnResult.ErrorResult
import com.appbase.showLogD
import com.appbase.showLogE
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import com.pv.viewmodels.BaseViewModel
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import java.io.IOException
import java.util.*
import javax.inject.Inject
import kotlin.reflect.KClass

/**Created by Daniel McCoy @ 25th Feb 2020*/
abstract class BaseActivity<VM : BaseViewModel> : AppCompatActivity(), HasSupportFragmentInjector {

    @get:LayoutRes
    abstract val layoutResId: Int

    abstract val rootLayout: ViewGroup?

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var genericErrorMessageFactory: GenericErrorMessageFactory

    protected val notificationManager: NotificationManager by lazy { getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }

    private var mFirebaseAnalytics: FirebaseAnalytics? = null

    protected abstract val viewModel: VM

    abstract fun loadData()

    abstract fun onNetworkError()

    abstract fun retry()

    abstract fun initUI()

    abstract fun logOut()

    private val displayMetrics: DisplayMetrics by lazy {
        DisplayMetrics().also {
            windowManager.defaultDisplay.getMetrics(it)
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        val policy: StrictMode.ThreadPolicy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
//        changeLanguage(sharePrefUtils.load(SharePrefUtils.KEYS.CURRENT_LANGUAGE) ?: "en")
        setContentView(layoutResId)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    override fun onResume() {
        super.onResume()
//        val languageTobeLoaded = sharePrefUtils.load(SharePrefUtils.KEYS.CURRENT_LANGUAGE) ?: "en"
//        ("Language to be loaded : $languageTobeLoaded")
//        changeLanguage(languageTobeLoaded)
        rootLayout?.setOnClickListener { hideSoftKeyboard(this) }


    }


    /**For handling network calls
     * Added due to the app crash when ConnectException or SocketTimeoutException was thrown from NetworkInterceptor and
     * Retrofit can't handle it, causing the app to crash inevitably
     * Network calls will only be executed only when there is active network connection
     * If not, the empty view will be changed to NETWORK_ERROR view */
    fun fetchDataFormSources(view: View, emptyLoadingView: EmptyLoadingViewPod?) {
        if (Connectivity.isConnected(this)) {
            loadData()
        } else {
            emptyLoadingView?.setCurrentState(EmptyLoadingViewPod.EmptyViewState.NETWORK_ERROR)
            onNetworkError()
            showSnackBar(
                view,
                ErrorResult(getString(R.string.error_msg_no_network))
            )
        }
    }

    /**For Showing snackbar with action btn
     * @param view -> parent view where the snackbar will be displayed
     * @param returnResult -> error data with status
     */
    fun showSnackBar(view: View, returnResult: ReturnResult) {
        val snackBar: Snackbar
        when (returnResult) {
            is ErrorResult -> {
                snackBar = Snackbar.make(
                    view,
                    getErrorContentMsg(returnResult),
                    BaseTransientBottomBar.LENGTH_LONG
                )
                snackBar.view.setBackgroundColor(
                    ContextCompat.getColor(
                        view.context,
                        R.color.error
                    )
                )
            }
            is ReturnResult.NetworkErrorResult -> {
                snackBar = Snackbar.make(
                    view,
                    view.context.getString(R.string.error_msg_no_network),
                    BaseTransientBottomBar.LENGTH_LONG
                )
                snackBar.view.setBackgroundColor(
                    ContextCompat.getColor(
                        view.context,
                        R.color.error
                    )
                )

            }
            is ReturnResult.PositiveResult -> {
                snackBar = Snackbar.make(
                    view,
                    getErrorContentMsg(returnResult),
                    BaseTransientBottomBar.LENGTH_LONG
                )
                snackBar.view.setBackgroundColor(
                    ContextCompat.getColor(
                        view.context,
                        R.color.colorPrimary
                    )
                )
            }
            else -> {
                //do nothing
                showLogE("There's nothing to show with snackbar")
                return
            }
        }
        snackBar.setAction(view.context.getText(R.string.btn_msg_ok)) {
            retry()
            snackBar.dismiss()
        }
        snackBar.setActionTextColor(ContextCompat.getColor(view.context, R.color.white))
        snackBar.show()
    }

    /**method for getting the current screen's height with desired percentage */
    fun getScreenHeight(percentage: Double): Int {
        return (displayMetrics.heightPixels * percentage).toInt()
    }

    /**method for getting the current screen's width with desired percentage */
    fun getScreenWidth(percentage: Double): Int {
        return (displayMetrics.widthPixels * percentage).toInt()
    }

    fun changeStatusColor(@ColorRes color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(this, color)
        }
    }

    /**Method for creating dialogs with custom views
     * @param view -> view inflated with desired layout
     * @param gravity -> gravity of the dialog to be displayed
     * @param cancelable -> whether the dialog is cancelable or not by touching outside of the dialog
     */
    fun createCustomDialog(
        context: Context?,
        view: View?,
        gravity: Int,
        cancelable: Boolean
    ): AlertDialog? {
        val dialog = AlertDialog.Builder(context).create()
        dialog.setView(view)
        val window = dialog.window
        window?.setGravity(gravity)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        val layoutParams = window?.attributes
        layoutParams?.y = 40 // bottom margin
        window?.attributes = layoutParams
        dialog.setCancelable(cancelable)
        return dialog
    }

    /**Method for modifying and overriding alertDialogs width and height as default width and height is almost 95% */
    fun modifyWindowsParamsAndShow(dialog: AlertDialog, width: Int, height: Int) {
        dialog.window!!.setBackgroundDrawableResource(R.color.transparent)
        dialog.show()
        dialog.window!!.setLayout(width, height)
    }

    /**helper method for returning String resources from ErrorVO
     * ErrorVO.getErrorMsg() can be either a String or a String Resource ID ( For Localization Purpose) */
    fun getErrorContentMsg(returnResult: ReturnResult): String {
        when (returnResult) {
            is ErrorResult -> {
                return returnResult.let {
                    return@let if (it.errorMsg is Int)
                        getString(it.errorMsg as Int)
                    else
                        it.errorMsg.toString()
                }
            }
            is ReturnResult.PositiveResult -> {
                return returnResult.let {
                    return@let if (it.msg is Int)
                        getString(it.msg as Int)
                    else
                        it.msg.toString()
                }
            }
            else -> return ""
        }
    }


    /**
     * Helper function for easily init of viewModel
     */
    inline fun <reified VM : BaseViewModel> contractedViewModels(): Lazy<VM> =
        ViewModelLazy(VM::class)


    inner class ViewModelLazy<VM : ViewModel>(
        private val viewModelClass: KClass<VM>
    ) : Lazy<VM> {
        private var cached: VM? = null

        override val value: VM
            get() {
                var viewModel = cached
                if (viewModel == null) {
                    viewModel = ViewModelProvider(
                        this@BaseActivity,
                        viewModelFactory
                    ).get(viewModelClass.java)
                    cached = viewModel
                }
                return viewModel
            }

        override fun isInitialized() = cached != null
    }

    /**
     * This is for restricting the appbar draggable behavior
     * @param  appBarLayout -> the appBar that is needed to take action on
     */
    fun disableAppBar(appBarLayout: AppBarLayout) {
        lockAppBarClosed(appBarLayout)
        changeAppbarLayoutDraggableBehavior(appBarLayout, false)
    }

    /**Method for changing appBar Draggable or !Draggable */
    fun changeAppbarLayoutDraggableBehavior(appBarLayout: AppBarLayout, isDraggable: Boolean) {
        if (appBarLayout.layoutParams != null) {
            val layoutParams =
                appBarLayout.layoutParams as CoordinatorLayout.LayoutParams
            val appBarLayoutBehaviour =
                AppBarLayout.Behavior()
            appBarLayoutBehaviour.setDragCallback(object :
                AppBarLayout.Behavior.DragCallback() {
                override fun canDrag(appBarLayout: AppBarLayout): Boolean {
                    return isDraggable
                }
            })
            layoutParams.behavior = appBarLayoutBehaviour
        }
    }


    /**Method for shrinking appBar to ActionBar size
     * @param mAppBarLayout -> the appBar that is needed to take action on
     */
    fun lockAppBarClosed(mAppBarLayout: AppBarLayout) {
        // Calculate ActionBar height
        var actionBarHeight = 0
        val tv = TypedValue()
        if (theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, displayMetrics)
        }
        mAppBarLayout.setExpanded(false, true)
        mAppBarLayout.isActivated = false
        val lp =
            mAppBarLayout.layoutParams as CoordinatorLayout.LayoutParams
        lp.height = actionBarHeight
    }

    /**Method for shrinking appBar to ActionBar size
     * @param mAppBarLayout -> the appBar that is needed to take action on
     * @param overrideParams -> if others  -> the appBar will be resized to desire ratio of the screen height ( DEFAULT is 65 % )
     * 1 -> the appBar's height will be WRAP_CONTENT
     */
    fun unlockAppBarOpen(mAppBarLayout: AppBarLayout, overrideParams: Double) {
        mAppBarLayout.setExpanded(true, true)
        mAppBarLayout.isActivated = true
        val lp =
            mAppBarLayout.layoutParams as CoordinatorLayout.LayoutParams
        if (overrideParams != 1.0) {
            lp.height = getScreenHeight(overrideParams)
        } else {
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT
        }
        mAppBarLayout.layoutParams = lp
    }


    fun showErrorView(returnResult: ReturnResult, emptyView: EmptyLoadingViewPod) {
        when (returnResult) {
            ReturnResult.NetworkErrorResult -> emptyView.show(EmptyLoadingViewPod.EmptyViewState.NETWORK_ERROR)
            else -> emptyView.show(EmptyLoadingViewPod.EmptyViewState.UNEXPECTED_ERROR)
        }
    }


    fun createView(@LayoutRes layout: Int) = LayoutInflater.from(this).inflate(layout, null)!!


    /**method for hiding keyboard from the screen*/
    fun hideSoftKeyboard(activity: Activity) {
        val inputMethodManager =
            activity.getSystemService(
                Activity.INPUT_METHOD_SERVICE
            ) as InputMethodManager
        try {
            inputMethodManager.hideSoftInputFromWindow(
                (activity.currentFocus)?.windowToken, 0
            )
        } catch (npe: NullPointerException) {
            showLogE("hideSoftKeyboard", npe.toString())
        }
    }

    /**  Create the NotificationChannel, but only on API 26+ because
    the NotificationChannel class is new and not in the support library
    Create the NotificationChannel, but only on API 26+ because
    the NotificationChannel class is new and not in the support library*/
    fun createNotificationChannel(
        channelName: String,
        channelDescription: String,
        importance: Int
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(channelName, channelDescription, importance)
            channel.setShowBadge(true)
            /*  Register the channel with the system; you can't change the importance
            or other notification behaviors after this */
            notificationManager.createNotificationChannel(channel)
        }
    }


    fun goToAppSettings() {
        startActivity(
            Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", packageName, null)
            ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }


    // slide the view from below itself to the current position
    fun slideUp(view: View, anchorGravity: Int, rootLayout: ViewGroup) {
        val transition: Transition = Slide(anchorGravity)
        transition.duration = 300
        transition.addTarget(view)
        TransitionManager.beginDelayedTransition(rootLayout as ViewGroup, transition)
        view.visibility = View.VISIBLE
    }

    // slide the view from its current position to below itself
    fun slideDown(view: View, anchorGravity: Int, rootLayout: ViewGroup) {
        val transition: Transition = Slide(anchorGravity)
        transition.duration = 300
        transition.addTarget(view)
        TransitionManager.beginDelayedTransition(rootLayout, transition)
        view.visibility = View.GONE
    }


    fun changeLanguage(languageToLoad: String) {
        showLogD("Language to be loaded :  $languageToLoad")
        val res = resources
        val conf = res.configuration
        conf.setLocale(Locale(languageToLoad))
        res.updateConfiguration(conf, res.displayMetrics)

    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = dispatchingAndroidInjector

    /**adding animation to the fragment transition*/
    fun FragmentTransaction.addAnimation(reversed: Boolean) =
        if (reversed) {
            this.setCustomAnimations(
                R.anim.anim_enter_from_left,
                R.anim.anim_exit_to_right,
                R.anim.anim_exit_to_left,
                R.anim.anim_enter_from_right
            )
        } else this.setCustomAnimations(
            R.anim.anim_enter_from_right,
            R.anim.anim_exit_to_left,
            R.anim.anim_enter_from_left,
            R.anim.anim_exit_to_right
        )

    open fun getCurrentUserHomeTown(
        location: Location?,
        context: Context,
        currentAddressLD: SingleEventLiveData<String?>
    ) {
        if (Geocoder.isPresent()) {
            if (location != null) {
                val latitude = location.latitude
                val longitude = location.longitude
                val geoCoder = Geocoder(context, Locale.US) //it is Geocoder
                val builder = StringBuilder()
                try {
                    val address =
                        geoCoder.getFromLocation(latitude, longitude, 3)
                    if (address.size > 0) {
                        val addressData = address[0]
                        builder.append(addressData.locality).append("\n")
                        builder.append(addressData.countryName)
                        currentAddressLD.postValue(builder.toString())
                        showLogD("Final Address : $builder")
                    } else currentAddressLD.postValue("")
                } catch (e: java.lang.NullPointerException) {
                    ("Exception in getting Location" + e.message)
                    currentAddressLD.postValue("")
                } catch (e: NetworkOnMainThreadException) {
                    showLogD("Network on Main Thread Exception occurred")
                    currentAddressLD.postValue("")
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            } else {
                showLogD("Location not available")
                currentAddressLD.postValue("")
            }
        } else {
            currentAddressLD.postValue("")
            showLogD("Geo coder is not available")
        }
    }

    fun callToPhone(number: String) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$number")
        startActivity(intent)
    }


    fun logEvent(functionName: FunctionType, bundle: Bundle) {
        mFirebaseAnalytics!!.logEvent(functionName.data, bundle)
    }

    sealed class FunctionType(val data: String) {
        object CSVConversion : FunctionType("csv_conversion")
        object CheckSymptoms : FunctionType("ရောဂါစစ်")
        object LocationTracking : FunctionType("နေရာစစ်")
    }
}