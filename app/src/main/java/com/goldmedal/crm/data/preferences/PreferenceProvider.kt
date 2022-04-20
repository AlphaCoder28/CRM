package com.goldmedal.crm.data.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager


private const val KEY_SAVED_AT = "key_saved_at"
private const val KEY_INTRO = "key_intro"
class PreferenceProvider (context: Context){



    private val appContext = context.applicationContext

    private val  preference: SharedPreferences
    get() = PreferenceManager.getDefaultSharedPreferences(appContext)

    fun saveLastSavedAt(savedAt: String?){
        preference.edit().putString(KEY_SAVED_AT,savedAt).apply()

    }

    fun introInit(wasShown: Boolean) {
        preference.edit().putBoolean(KEY_INTRO,wasShown).apply()
    }


    fun isIntroInit(): Boolean{
        return preference.getBoolean(KEY_INTRO,false)
    }


    fun getLastSavedAt(): String?{
        return preference.getString(KEY_SAVED_AT,null)
    }
}