package com.goldmedal.crm.data.repositories

import android.os.Build
import com.goldmedal.crm.data.db.AppDatabase
import com.goldmedal.crm.data.db.entities.User
import com.goldmedal.crm.data.network.MyApi
import com.goldmedal.crm.data.network.SafeApiRequest
import com.goldmedal.crm.data.network.responses.*
import com.goldmedal.crm.data.preferences.PreferenceProvider


class UserRepository(
        private val api: MyApi,
        private val db: AppDatabase,
        private val prefs: PreferenceProvider
) : SafeApiRequest() {

    suspend fun authenticateLogin(
        strUserName: String,
        strDeviceId: String,
        strFCMToken: String
    ): SessionResponse {
        return apiRequest {
            api.authenticateLogin(
                strUserName,
                strDeviceId,
                "ANDROID",
                Build.MANUFACTURER + " - " + Build.MODEL,
                Build.VERSION.SDK_INT.toString(),
                strFCMToken
            )
        }

    }

    suspend fun getOtp(
        strUserName: String,
        strDeviceId: String,
        strFCMToken: String
    ): GetOtpResponse {
        return apiRequest {
            api.getOtp(
                strUserName,
                strDeviceId,
                "ANDROID",
                Build.MANUFACTURER + " - " + Build.MODEL,
                Build.VERSION.SDK_INT.toString(),
                strFCMToken
            )
        }

    }


    suspend fun authenticatePassword(
        logNo: Int,
        strSessionId: String,
        strPassword: String
    ): LoginResponse {
        return apiRequest {
            api.authenticatePassword(
                logNo,
                strSessionId,
                strPassword
            )
        }

    }


    suspend fun updateProfilePic(strProfileString: String,userId: Int): UpdateProfilePhotoResponse {
        return apiRequest { api.updateProfilePhoto(strProfileString,userId) }
    }


    suspend fun getProfileDetail(userId: Int): ProfileDetailResponse {
        return apiRequest { api.profileDetail(userId) }
    }

    suspend fun appUpdateLocal(url: String): UpdateAppResponse {
        return apiRequest { api.updateLocalApp("https://test2.goldmedalindia.in/api/UpdateCRM","ClientSecret",url) }
    }



    //    suspend fun userGetOtp(strMobileNo: String,strDeviceId: String): SendOtpResponse {
//        return apiRequest { api.sendOtp(strMobileNo, strDeviceId, GlobalConstant.CLIENT_ID, GlobalConstant.CLIENT_SECRET) }
//
//    }
//
    suspend fun verifyOtp(
        strMobileNo: String,
        strOtp: String,
        strRequestNo: String,
        strDeviceId: String,
        strFCMToken: String
    ): LoginResponse {
        return apiRequest {
            api.verifyOtp(
                strMobileNo,
                strOtp,
                strRequestNo,
                strDeviceId,
                "ANDROID",
                Build.MANUFACTURER + " - " + Build.MODEL,
                Build.VERSION.SDK_INT.toString(),
                strFCMToken
            )
        }

    }
//
//    suspend fun userResetPassword(strMobileNo: String, strNewPassword: String, strOldPassword: String): ResetPasswordResponse {
//        return apiRequest { api.resetPassword(strMobileNo, strNewPassword, strOldPassword, GlobalConstant.CLIENT_ID, GlobalConstant.CLIENT_SECRET) }
//
//    }

    suspend fun saveProfilePic(profilePicLink: String?) = db.getUserDao().updateProfilePicture(profilePicLink)

//saveprofile{
// db
// }
    suspend fun saveUser(user: User?) = db.getUserDao().upsert(user)

    fun getUser() = db.getUserDao().getUser()


    suspend fun logoutUser() = db.getUserDao().logoutUser()

    fun clearUserCache() = prefs.saveLastSavedAt(null)

    fun introInit() = prefs.introInit(true)

    fun isIntroInit() = prefs.isIntroInit()

}
