/*
package com.prvulovic.rekvestlin_core.legacy

import android.os.Looper
import androidx.lifecycle.LiveData
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonIOException
import com.google.gson.JsonParser
import com.google.gson.stream.JsonToken
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.IOException
import java.util.concurrent.TimeUnit

abstract class BaseRequest<T> : LiveData<Resource<T?>>(), Callback {

    protected val BASE_URL = ""

    protected lateinit var request: Request.Builder

    var response: Response? = null

    override fun onFailure(call: Call, e: IOException) {
        e.printStackTrace()
        onFailure(null, e.message, e)
    }

    override fun onResponse(call: Call, response: Response) {
        if (!response.isSuccessful) {
            val ioException = IOException("Unexpected code $response")
            ioException.printStackTrace()
            onFailure(response, response.message, ioException)
            response.body?.close()
            return
        }
        try {
            onSuccess(response)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            response.body?.close()
        }
    }

    fun runAsync(force: Boolean): LiveData<Resource<T?>> {
        if (value == null || (value?.status != Status.LOADING && (value?.status == Status.ERROR || force))) {
            setData(Resource.loading(getData()))
            OkHttp.getInstance().newCall(buildRequest()).enqueue(this)
        }
        return this
    }

    fun run(): Resource<T?> {
        try {
            response = OkHttp.getInstance().newCall(buildRequest()).execute()
            if(response?.isSuccessful == false){
                val ioException = IOException("Unexpected code $response")
                ioException.printStackTrace()
                try {
                    return onFailure(response, response?.message, ioException)
                }finally {
                    response?.body?.close()
                }
            }
            return try{
                onSuccess(response)
            }catch (e: Exception){
                e.printStackTrace()
                onFailure(response, response?.message, e)
            }finally {
                response?.body?.close()
            }
        }catch (e: IOException){
            e.printStackTrace()
            return onFailure(null, e.message, e)
        }
    }

    open fun onFailure(response: Response?, message: String?, e: Exception): Resource<T?> {
        val processedMessage = response?.let {
            try {
                val error = parseToJson(response).asJsonObject
                return@let error["error"].asString
            } catch (e1: Exception) {
                e1.printStackTrace()
                return@let message
            }
        } ?: message
        val resource = Resource.error(getData(), e, processedMessage, this)
        postValue(resource)
        return resource
    }

    @Suppress("UNCHECKED_CAST")
    fun getData(): T? = value?.let { (value as Resource<T>).data }

    fun setData(data: Resource<T?>) {
        if (Looper.getMainLooper() == Looper.myLooper()) value = data else postValue(data)
    }

    protected fun parseToJson(response: Response?): JsonElement {
        val jsonReader = gson.newJsonReader(response?.body?.charStream())
        val json = JsonParser.parseReader(jsonReader)
        if (jsonReader.peek() != JsonToken.END_DOCUMENT) {
            throw JsonIOException("JSON document was not fully consumed.")
        }
        return json
    }

    open fun buildRequest(): Request {
        return request.build()
    }

    protected abstract fun onSuccess(response: Response?): Resource<T?>

    companion object {
        protected val JSON = "application/json; charset=utf-8".toMediaTypeOrNull()
        protected val MEDIA_TYPE_PNG = "image/png".toMediaTypeOrNull()
        protected val MEDIA_TYPE_JPEG = "image/jpeg".toMediaTypeOrNull()
        protected val MEDIA_TYPE_WEBP = "image/webp".toMediaTypeOrNull()

        val gson = Gson()
    }
}

enum class Status {
    LOADING,
    SUCCESS,
    ERROR
}

@Suppress("DataClassPrivateConstructor")
data class Resource<T> private constructor(
    val data: T?,
    val status: Status,
    val e: Exception?,
    val msg: String?
) {
    @Suppress("UNCHECKED_CAST")
    companion object {
        @JvmStatic
        fun <T> success(data: T?) = Resource(data, Status.SUCCESS, null, null)

        @JvmStatic
        fun <T> loading(data: T?, request: BaseRequest<T>? = null) = Resource(data, Status.LOADING, null, null)

        @JvmStatic
        fun <T> error(data: T?, e: Exception?, msg: String?) =
            Resource(data, Status.ERROR, e, msg)
    }
}

class OkHttp {

    companion object {

        @Volatile
        private var INSTANCE: OkHttpClient? = null

        fun getInstance(): OkHttpClient =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildOkHttpClient().also { INSTANCE = it }
            }

        private fun buildOkHttpClient(): OkHttpClient {
            val b = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
            return b.build()
        }
    }

}*/
