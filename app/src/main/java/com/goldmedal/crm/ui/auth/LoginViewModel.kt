package com.goldmedal.crm.ui.auth

import android.view.View
import androidx.lifecycle.ViewModel
import com.goldmedal.crm.data.network.GlobalConstant.SUCCESS_CODE
import com.goldmedal.crm.data.repositories.UserRepository
import com.goldmedal.crm.util.ApiException
import com.goldmedal.crm.util.Coroutines
import com.goldmedal.crm.util.NoInternetException
import java.net.SocketTimeoutException

//<!--added by shetty 6 jan 21-->
class LoginViewModel(
    private val repository: UserRepository
) : ViewModel() {

    var strEmail: String? = null
    var strDeviceId: String? = null
    var strFCMToken: String? = null
    var strMobileNo: String? = null
    var strRequestNo: String? = null
    var strOtp: String? = null
    var strPassword: String? = null
    var authListener: AuthListener<Any>? = null

    fun getLoggedInUser() = repository.getUser()

    fun introInit() = repository.introInit()
    fun isIntroInit() = repository.isIntroInit()



    fun logoutUser() =

        Coroutines.main {
            repository.logoutUser()
            repository.clearUserCache()
        }


    fun onLoginButtonClick(view: View) {
        if (strEmail.isNullOrEmpty()) {
            authListener?.onValidationError("Invalid email")
            return
        } else if (strPassword.isNullOrEmpty()) {
            authListener?.onValidationError("Invalid password")
            return
        } else if (strDeviceId.isNullOrEmpty()) {
            authListener?.onValidationError("Invalid deviceId")
            return
        }
//            else if (strFCMToken.isNullOrEmpty()) {
//                authListener?.onFailure("Invalid token")
//                return
//            }


        authListener?.onStarted()



        strFCMToken = "test"



        Coroutines.main {
            try {
                val loginResponse =
                    repository.authenticateLogin(strEmail!!, strDeviceId!!, strFCMToken!!)
                if (loginResponse.StatusCode.equals(SUCCESS_CODE)) {
                    if (!loginResponse.sessionData?.isNullOrEmpty()!!) {
                        loginResponse.sessionData?.let {
                            authListener?.onSuccess(it, "authenticate_email")
                            return@main
                        }
                    } else {
                        authListener?.onFailure(
                            loginResponse.StatusCodeMessage!!,
                            "authenticate_email",
                            false
                        )
                    }
                } else {
                    val errorResponse = loginResponse.Errors
                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let {
                            authListener?.onFailure(
                                it,
                                "authenticate_email",
                                false
                            )
                        }

                    }
                }
            } catch (e: ApiException) {
                authListener?.onFailure(e.message!!, "authenticate_email", true)
            } catch (e: NoInternetException) {
                authListener?.onFailure(e.message!!, "authenticate_email", true)
            }
        }

    }


    fun onUpdateProfilePhoto(userId: Int,ProfilePhoto:String) {
        if (userId == null) {
            authListener?.onValidationError("User id cannot be nil")
            return
        }

        authListener?.onStarted()

        Coroutines.main {
            try {
                val updateprofileResponse = repository.updateProfilePic(ProfilePhoto,userId)

                if (!updateprofileResponse.updatePhoto?.isNullOrEmpty()!!) {
                    updateprofileResponse.updatePhoto.let {
                        authListener?.onSuccess(it, "profile_photo_update")
                        repository.saveProfilePic(it[0].ProfilePhoto)
                        return@main
                    }
                } else {
                    val errorResponse = updateprofileResponse.Errors
                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let {
                            authListener?.onFailure(it, "profile_photo_update", false)
                        }
                    }
                }

            } catch (e: ApiException) {
                authListener?.onFailure(e.message!!, "profile_photo_update", true)
            } catch (e: NoInternetException) {
                authListener?.onFailure(e.message!!, "profile_photo_update", true)
            } catch (e: SocketTimeoutException) {
                authListener?.onFailure(e.message!!, "profile_photo_update", true)
            }
        }
    }


    fun getProfileDetail(userId: Int) {
        if (userId == null) {
            authListener?.onValidationError("User id cannot be nil")
            return
        }

        authListener?.onStarted()

        Coroutines.main {
            try {
                val profileDetailResponse = repository.getProfileDetail(userId)

                if (!profileDetailResponse.profileDetailMain?.isNullOrEmpty()!!) {
                    profileDetailResponse.profileDetailMain.let {
                        authListener?.onSuccess(it, "profile_detail")
                        return@main
                    }
                } else {
                    val errorResponse = profileDetailResponse.Errors
                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let {
                            authListener?.onFailure(it, "profile_detail", false)
                        }
                    }
                }

            } catch (e: ApiException) {
                authListener?.onFailure(e.message!!, "profile_detail", true)
            } catch (e: NoInternetException) {
                authListener?.onFailure(e.message!!, "profile_detail", true)
            } catch (e: SocketTimeoutException) {
                authListener?.onFailure(e.message!!, "profile_detail", true)
            }
        }
    }


    fun appUpdate(url:String) {

        authListener?.onStarted()

        Coroutines.main {
            try {
                val updateResponse =
                    repository.appUpdateLocal(url)
                if (updateResponse.count()>0) {
                    if (!updateResponse[0].appUpdateData.isNullOrEmpty()) {
                        updateResponse[0].appUpdateData.let {
                            authListener?.onSuccess(it, "app_update")
                            return@main
                        }
                    } else {
                        authListener?.onFailure(
                            updateResponse[0].message!!,
                            "app_update",
                            false
                        )
                    }
                }
            } catch (e: ApiException) {
                authListener?.onFailure(e.message!!, "app_update", true)
            } catch (e: NoInternetException) {
                authListener?.onFailure(e.message!!, "app_update", true)
            }
        }

    }



    fun authenticatePassword(logNo: Int?, strSessionId: String?) {

        authListener?.onStarted()

        Coroutines.main {
            try {
                val loginResponse =
                    repository.authenticatePassword(logNo!!, strSessionId!!, strPassword!!)
                if (loginResponse.StatusCode.equals(SUCCESS_CODE)) {
                    if (!loginResponse.user?.isNullOrEmpty()!!) {
                        loginResponse.user?.let {
                            authListener?.onSuccess(it, "authenticate_password")
                            repository.saveUser(it[0])
                            return@main
                        }
                    } else {
                        authListener?.onFailure(
                            loginResponse.StatusCodeMessage!!,
                            "authenticate_password",
                            false
                        )
                    }
                } else {
                    val errorResponse = loginResponse.Errors
                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let {
                            authListener?.onFailure(
                                it,
                                "authenticate_password",
                                false
                            )
                        }

                    }
                }
            } catch (e: ApiException) {
                authListener?.onFailure(e.message!!, "authenticate_password", true)
            } catch (e: NoInternetException) {
                authListener?.onFailure(e.message!!, "authenticate_password", true)
            }
        }

    }

    fun onGetOtpClick(view: View) {

        if (strDeviceId.isNullOrEmpty()) {
            authListener?.onValidationError("Invalid Device id")
            return
        } else if (strMobileNo.isNullOrEmpty()) {
            authListener?.onValidationError("Invalid mobile no.")
            return
        }

//            else if (strFCMToken.isNullOrEmpty()) {
//                authListener?.onFailure("Invalid token")
//                return
//            }
        //   }
//        else{
//            if (!strCaptcha.equals(strGeneratedCaptcha)) {
//                authListener?.onFailure("Invalid Captcha")
//                return
//            }else if (strDeviceId.isNullOrEmpty()) {
//                authListener?.onFailure("Invalid deviceId")
//                return
//            }
////            else if (strFCMToken.isNullOrEmpty()) {
////                authListener?.onFailure("Invalid token")
////                return
////            }
//        }

        authListener?.onStarted()

        //   print("Captcha - - - " + strGeneratedCaptcha + "- - -  -" + strCaptcha)

        strFCMToken = "test"

        //strGeneratedCaptcha?.let { authListener?.setCaptcha(it) }

        Coroutines.main {
            try {
                val loginResponse = repository.getOtp(strMobileNo!!, strDeviceId!!, strFCMToken!!)
                if (loginResponse.StatusCode.equals(SUCCESS_CODE)) {

                    if (!loginResponse.otpData?.isNullOrEmpty()!!) {
                        loginResponse.otpData?.let {
                            authListener?.onSuccess(it, "get_otp")
                            return@main
                        }
                    } else {
                        authListener?.onFailure(loginResponse.StatusCodeMessage!!, "get_otp", false)
                    }
                } else {
                    val errorResponse = loginResponse.Errors
                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let {
                            authListener?.onFailure(
                                it,
                                "get_otp",
                                false
                            )
                        }

                    }
                }
            } catch (e: ApiException) {
                authListener?.onFailure(e.message!!, "get_otp", true)
            } catch (e: NoInternetException) {
                authListener?.onFailure(e.message!!, "get_otp", true)
            }
        }
    }

    fun onVerifyOtpClick(view: View) {
        if (strMobileNo.isNullOrEmpty()) {
            authListener?.onValidationError("Invalid email or mobile no.")
            return
        } else if (strOtp.isNullOrEmpty()) {
            authListener?.onValidationError("enter otp")
            return
        } else if (strRequestNo.isNullOrEmpty()) {
            authListener?.onValidationError("enter request no")
            return
        } else if (strDeviceId.isNullOrEmpty()) {
            authListener?.onValidationError("Invalid Device Id")
            return
        }

        strFCMToken = "test"

        authListener?.onStarted()

        Coroutines.main {
            try {
                val loginResponse = repository.verifyOtp(
                    strMobileNo!!,
                    strOtp!!,
                    strRequestNo!!,
                    strDeviceId!!,
                    strFCMToken!!
                )

                if (loginResponse.StatusCode.equals(SUCCESS_CODE)) {

                    if (!loginResponse.user?.isNullOrEmpty()!!) {
                        loginResponse.user.let {
                            repository.saveUser(it[0])
                            authListener?.onSuccess(it, "verify_otp")

                            return@main
                        }
                    } else {
                        authListener?.onFailure(
                            loginResponse.StatusCodeMessage!!,
                            "verify_otp",
                            false
                        )
                    }
                } else {
                    val errorResponse = loginResponse.Errors

                    if (!errorResponse?.isNullOrEmpty()!!) {
                        errorResponse[0]?.ErrorMsg?.let {
                            authListener?.onFailure(
                                it,
                                "verify_otp",
                                false
                            )
                        }

                    }

                }
            } catch (e: ApiException) {
                authListener?.onFailure(e.message!!, "verify_otp", true)
            } catch (e: NoInternetException) {
                authListener?.onFailure(e.message!!, "verify_otp", true)
            }

        }
    }

}