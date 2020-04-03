package com.appbase.components

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.appbase.nonintersect

@SuppressLint("LogNotTimber")
/*Created By Daniel McCoy at 23rd March 2020*/
class PermissionListUtil(private val PERMISSION_ASKER : Int){

    private val tag = "PERMISSIONUTILS"
    private val IS_FIRST_TIME_ASKING_PERMISSIONS = "SP_Is_First_Time_Asking_Permissions"

    /*
    * Check if version is marshmallow and above.
    * Used in deciding to ask runtime permission
    * */
    private fun shouldAskPermission() : Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    }

    fun shouldAskPermission(context : Context , permission : String) : Boolean {
        return if (shouldAskPermission())
            ActivityCompat.checkSelfPermission(context , permission) != PackageManager.PERMISSION_GRANTED
        else false
    }


    @TargetApi(Build.VERSION_CODES.M)
    fun checkAndAskPermissions(activity : Activity , permissionList : Array<String> , listener : PermissionListAskListener) {
        if (permissionList.isEmpty())
            throw RuntimeException("Permission list can't be empty")

        val unGrantedPermissionList = ArrayList<String>()

        for (permission in permissionList) {
            if (shouldAskPermission(activity , permission))
                unGrantedPermissionList.add(permission)
        }

        val grantList = permissionList.toList().nonintersect(unGrantedPermissionList).toTypedArray()

        if (unGrantedPermissionList.isEmpty()) {
            listener.onAllPermissionGranted()
            Log.d(tag , "onAllPermissionGranted() called")
            return
        } else {
            for (grantedPermission in grantList) {
                listener.onPermissionGranted(grantedPermission)
                Log.d(tag , "onPermissionGranted($grantedPermission) called")
            }
            askPermissions(activity , unGrantedPermissionList.toTypedArray() , listener)
        }

    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun askPermission(activity : Activity , permission : String , listener : PermissionListAskListener) {
        /** If permission denied previously
         * */
        if (activity.shouldShowRequestPermissionRationale(permission)) {
            Log.d(tag , "onPermissionPreviouslyDenied($permission) called")
            listener.onPermissionPreviouslyDenied(permission)
            requestPermission(activity , permission)
        } else { /*
                 * Permission denied or first time requested
                 * */
            if (isFirstTimeAskingPermission(activity , permission)) {
                setIsFirstTimeAskingPermission(activity , permission , false)
                Log.d(tag , "onNeedPermission($permission) called")
                listener.onNeedPermission(permission)
                requestPermission(activity , permission)

            } else { /*
                     * Handle the feature without permission or ask user to manually allow permission
                     * */
                Log.d(tag , "onPermissionDisabled($permission) called")
                listener.onPermissionDisabled(permission)
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun askPermissions(activity : Activity , permission : Array<String> , listener : PermissionListAskListener) {
        /** If permission denied previously
         * */
        permission.forEach {
            askPermission(activity , it , listener)
        }
    }


    private fun setIsFirstTimeAskingPermission(context : Context , permission : String , isFirstTime : Boolean) {
        val sharedPreference =
            context.getSharedPreferences(IS_FIRST_TIME_ASKING_PERMISSIONS , Context.MODE_PRIVATE)
        sharedPreference.edit().putBoolean(permission , isFirstTime).apply()
    }

    private fun isFirstTimeAskingPermission(context : Context , permission : String) : Boolean {
        return context.getSharedPreferences(IS_FIRST_TIME_ASKING_PERMISSIONS , Context.MODE_PRIVATE)
            .getBoolean(permission , true)
    }

    fun requestPermission(activity : Activity , permissionList : Array<String>) {
        if (PERMISSION_ASKER == -1) {
            throw RuntimeException("PERMISSION_ASKER is not set yet")
        } else
            ActivityCompat.requestPermissions(
                activity , permissionList ,
                PERMISSION_ASKER
            )
    }

    fun requestPermission(activity : Activity , permission : String) {
        requestPermission(activity , arrayOf(permission))
    }


    /*
   * Callback on various cases on checking permission
   *
   * 1.  Below M, runtime permission not needed. In that case onPermissionGranted() would be called.
   *     If permission is already granted, onPermissionGranted() would be called.
   *
   * 2.  Above M, if the permission is being asked first time onNeedPermission() would be called.
   *
   * 3.  Above M, if the permission is previously asked but not granted, onPermissionPreviouslyDenied()
   *     would be called.
   *
   * 4.  Above M, if the permission is disabled by device policy or the user checked "Never ask again"
   *     check box on previous request permission, onPermissionDisabled() would be called.
   * */
    interface PermissionListAskListener {
        /*
         * Callback to ask permission
         * */
        fun onNeedPermission(permission : String)

        /*
         * Callback on permission denied
         * */
        fun onPermissionPreviouslyDenied(permission : String)

        /*
         * Callback on permission "Never show again" checked and denied
         * */
        fun onPermissionDisabled(permission : String)

        /*
         * Callback on permission granted
         * */
        fun onPermissionGranted(permission : String)


        fun onAllPermissionGranted()

        /*
        * Callback on all permission requested , but this doesn't mean all permission are granted
        * */
        fun onAllPermissionRequested()
    }


}