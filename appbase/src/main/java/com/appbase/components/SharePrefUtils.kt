package com.appbase.components

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.apbase.R
import com.appbase.showLogD
import java.util.*
import javax.inject.Inject

/**
 * created by Daniel McCoy @ 28th, May , 2019
 */
class SharePrefUtils @Inject constructor(
    private val context: Context,
    private val sharedPreferences: SharedPreferences
) {

    //todo add android 10 support
    constructor(context: Context) : this(context , PreferenceManager.getDefaultSharedPreferences(context))

    fun clearALL() {
        val keyList: List<KEYS> = ArrayList(
            EnumSet.allOf(
                KEYS::class.java
            )
        )
        showLogD(" -> $keyList <- ")
        for (key in keyList) {
            if (key.label != KEYS.CURRENT_LANGUAGE.label) {
                sharedPreferences.edit().putString(context.getString(key.label), key.defaultValue)
                    .apply()
            }
        }
    }

    fun delete(key: KEYS) {
        sharedPreferences.edit().remove(context.getString(key.label)).apply()
    }

    fun delete(key: KEYB) {
        sharedPreferences.edit().remove(context.getString(key.label)).apply()
    }

    fun delete(key: KEYI) {
        sharedPreferences.edit().remove(context.getString(key.label)).apply()
    }

    fun save(key: KEYS, value: String?) {
        sharedPreferences.edit().putString(context.getString(key.label), value).apply()
    }

    fun load(key: KEYS): String? {
        return sharedPreferences.getString(context.getString(key.label), key.defaultValue)
    }

    fun save(key: KEYI, value: Int) {
        sharedPreferences.edit().putInt(context.getString(key.label), value).apply()
    }

    fun load(key: KEYI): Int {
        return sharedPreferences.getInt(context.getString(key.label), key.defaultValue)
    }

    fun save(key: KEYB, value: Boolean) {
        sharedPreferences.edit().putBoolean(context.getString(key.label), value).apply()
    }

    fun load(key: KEYB): Boolean {
        return sharedPreferences.getBoolean(context.getString(key.label), key.defaultValue)
    }

    /**
     * Customized Method
     */
    /* fun saveUserData(userVO: UserVO) {
         save(KEYS.MES_ID, userVO.getMesId())
         save(KEYS.USER_NAME, userVO.getName())
         save(KEYS.USER_EMAIL, userVO.getEmail())
         save(KEYS.USER_ADDRESS, userVO.getAddress())
         save(KEYS.USER_DOB, userVO.getDob())
         save(KEYI.USER_GENDER, userVO.getGender())
         save(KEYS.USER_PHONE_NUMBER, userVO.getPhone())
         save(KEYS.USER_PHOTO, userVO.getPhotoUrl())
         save(KEYS.USER_OCCUPATION, userVO.getOccupation())
         save(KEYB.IS_IN_TRIAL, userVO.isTrial())
         save(KEYB.IS_IN_PREMIUM, userVO.isPremium())
         save(KEYB.HAS_USED_TRIAL, userVO.hasUsedTrial())
         save(KEYB.IS_ACTIVE, userVO.isActive())
         save(KEYS.LAST_TRANSACTION_ID, userVO.getLastTransactionID())
         save(KEYS.USER_EXPIRE_AT, userVO.getExpiredAt())
         save(KEYS.USER_EXPIRE_DATE, userVO.getExpiredDate())
     }

     fun deleteUserData() {
         delete(KEYS.MES_ID)
         delete(KEYS.USER_NAME)
         delete(KEYS.USER_EMAIL)
         delete(KEYS.USER_ADDRESS)
         delete(KEYS.USER_DOB)
         delete(KEYI.USER_GENDER)
         delete(KEYS.USER_PHONE_NUMBER)
         delete(KEYS.USER_PHOTO)
         delete(KEYS.USER_OCCUPATION)
         delete(KEYB.IS_IN_TRIAL)
         delete(KEYB.IS_IN_PREMIUM)
         delete(KEYB.HAS_USED_TRIAL)
         delete(KEYB.IS_ACTIVE)
         delete(KEYS.LAST_TRANSACTION_ID)
         delete(KEYS.USER_EXPIRE_AT)
         delete(KEYS.USER_EXPIRE_DATE)
     }*/

    /**
     * Define your keys here and set Default Values here
     * In case if u had more SP_VAlUES , just modify here
     */
    enum class KEYS(val label: Int, val defaultValue: String) {
        //langauge settings
        CURRENT_LANGUAGE(R.string.SP_LANGUAGE, "en")
    }

    enum class KEYB(val label: Int, val defaultValue: Boolean) {

    }

    enum class KEYI(val label: Int, val defaultValue: Int) {
    }


}