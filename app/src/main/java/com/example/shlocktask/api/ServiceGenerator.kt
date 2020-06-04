

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ServiceGenerator {
    var context:Context
    var httpClient: OkHttpClient.Builder
     var builder: Retrofit.Builder
    constructor(c: Context) {
        this.context = c
        val logging = HttpLoggingInterceptor()

        // set your desired log level
        logging.level = HttpLoggingInterceptor.Level.BODY
        httpClient = OkHttpClient.Builder().connectTimeout(200, TimeUnit.SECONDS).readTimeout(200, TimeUnit.SECONDS)
        httpClient.addInterceptor(logging)
        builder = Retrofit.Builder()
                .baseUrl("http://www.json-generator.com/api/json/get/")
                .addConverterFactory(GsonConverterFactory.create())
    }

    fun <S> createService(serviceClass: Class<S>?): S {
        val retrofit = builder.client(httpClient.build()).build()
        return retrofit.create(serviceClass)
    }
}