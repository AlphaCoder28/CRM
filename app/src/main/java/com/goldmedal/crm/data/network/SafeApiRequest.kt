package com.goldmedal.crm.data.network

import com.goldmedal.crm.util.ApiException
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response

abstract class SafeApiRequest {

    suspend fun<T: Any> apiRequest(call: suspend () -> Response<T>) : T{
        val response = call.invoke()


        if(response.isSuccessful){
            print("RESPONSE - - - -"+response.body())
            return response.body()!!
        }else{
            val error = response.errorBody()?.string()
            print("ERROR - - - -"+error)

            val message = StringBuilder()
            error?.let{
                try{
                    message.append(JSONObject(it).getString("message"))
                }catch(e: JSONException){ }
                message.append("\n")
            }
            message.append("Error Code: ${response.code()}")
            print("MESSAGE - - - -"+message.toString())
            throw ApiException(message.toString())
        }


    }

}