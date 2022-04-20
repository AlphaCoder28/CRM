package com.goldmedal.crm.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import com.goldmedal.crm.util.ApiException
import com.goldmedal.crm.util.NoInternetException

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL


class NetworkConnectionInterceptor(
    context: Context
) : Interceptor {

    private val applicationContext = context.applicationContext

    override fun intercept(chain: Interceptor.Chain): Response {

    if(!hasInternetConnected()){
        throw ApiException("Make sure you have an active data connection")
        throw NoInternetException("Make sure you have an active data connection")
        Log.d("NoInternetException", "Inside   ********")
    }

        print("Outside second *******")
        return chain.proceed(chain.request())

    }

    fun hasInternetConnected(): Boolean {
        if (isInternetAvailable()) {
            try {
                val connection = URL("https://www.google.com").openConnection() as HttpURLConnection
                connection.setRequestProperty("User-Agent", "ConnectionTest")
                connection.setRequestProperty("Connection", "close")
                connection.connectTimeout = 1000 // configurable
                connection.connect()
                Log.d("NoInternetException", "hasInternetConnected: ${(connection.responseCode == 200)}")
                return (connection.responseCode == 200)
            } catch (e: IOException) {
                Log.d("NoInternetException", "Error checking internet connection", e)
            }
        } else {
            Log.d("NoInternetException", "No network available!")
        }
        Log.d("NoInternetException", "hasInternetConnected: false")
        return false
    }


    private fun isInternetAvailable() : Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var result = false
            val connectivityManager =
                    applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
            connectivityManager?.let {

                it.getNetworkCapabilities(connectivityManager.activeNetwork)?.apply {
                    result = when {
                        hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                        hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                        else -> false
                    }
                }
            }
            return result
        }
        else{
            val connectivityManager =
                    applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            connectivityManager.activeNetworkInfo.also {
                return it != null && it.isConnected
            }
        }
    }
    }
