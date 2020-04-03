package com.appbase.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.apbase.R
import com.appbase.components.Connectivity
import com.appbase.components.EmptyLoadingViewPod
import com.appbase.components.SharePrefUtils
import com.appbase.components.interfaces.GenericErrorMessageFactory
import com.appbase.components.show
import com.appbase.models.vos.ReturnResult
import com.appbase.models.vos.ReturnResult.ErrorResult
import com.appbase.showLogD
import com.appbase.showLogE
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.pv.viewmodels.BaseViewModel
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import io.socket.client.Socket
import java.util.*
import javax.inject.Inject
import kotlin.reflect.KClass

/**Created by Daniel McCoy @ 25th Feb 2020*/
abstract class BaseActivity<VM : BaseViewModel> : AppCompatActivity() , HasSupportFragmentInjector{

    @get:LayoutRes
    abstract val layoutResId: Int

    abstract val rootLayout: View?

  /*  @Inject
    lateinit var sharePrefUtils: SharePrefUtils*/

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var genericErrorMessageFactory: GenericErrorMessageFactory

    protected val notificationManager: NotificationManager by lazy { getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }

    protected abstract val viewModel: VM

    abstract fun loadData()

    abstract fun onNetworkError()

    abstract fun retry()

    abstract fun initUI()

    abstract fun logOut()

    private var displayMetrics: DisplayMetrics? = null

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        changeLanguage(sharePrefUtils.load(SharePrefUtils.KEYS.CURRENT_LANGUAGE) ?: "en")
        setContentView(layoutResId)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    override fun onResume() {
        super.onResume()
//        val languageTobeLoaded = sharePrefUtils.load(SharePrefUtils.KEYS.CURRENT_LANGUAGE) ?: "en"
//        showLogD("Language to be loaded : $languageTobeLoaded")
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
    fun getScreenHeight(context: Context, percentage: Double): Int {
        if (displayMetrics == null)
            initDisplayMetrics(context)
        return (displayMetrics!!.heightPixels * percentage).toInt()
    }

    /**method for getting the current screen's width with desired percentage */
    fun getScreenWidth(context: Context, percentage: Double): Int {
        if (displayMetrics == null)
            initDisplayMetrics(context)
        return (displayMetrics!!.widthPixels * percentage).toInt()
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


    fun initDisplayMetrics(context: Context) {
        displayMetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
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
    protected fun disableAppBar(appBarLayout: AppBarLayout) {
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
            lp.height = getScreenHeight(this, overrideParams)
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
    fun slideUp(view: View, anchorGravity: Int) {
        showLogD("Slided Up")
        val transition: Transition = Slide(anchorGravity)
        transition.duration = 300
        transition.addTarget(view)
        TransitionManager.beginDelayedTransition(rootLayout as ViewGroup, transition)
        view.visibility = View.VISIBLE
    }

    // slide the view from its current position to below itself
    fun slideDown(view: View, anchorGravity: Int) {
        showLogD("Slided Down")
        val transition: Transition = Slide(anchorGravity)
        transition.duration = 300
        transition.addTarget(view)
        TransitionManager.beginDelayedTransition(rootLayout as ViewGroup, transition)
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



}