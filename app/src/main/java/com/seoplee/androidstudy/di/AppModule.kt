package com.seoplee.androidstudy.di

import android.content.Context
import androidx.room.Room
import com.seoplee.androidstudy.BuildConfig
import com.seoplee.androidstudy.MyApp
import com.seoplee.androidstudy.data.network.ServerApi
import com.seoplee.androidstudy.data.network.Url
import com.seoplee.androidstudy.data.room.AppDataBase
import com.seoplee.androidstudy.data.room.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

@Module
@InstallIn(SingletonComponent::class)
object AppModule{

    @Provides
    fun provideBaseUrl() = Url.SERVER_URL

//    @Singleton
//    @Provides
//    fun provideOkHttpClient() = if (BuildConfig.DEBUG){
//        val loggingInterceptor = HttpLoggingInterceptor()
//        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
//        OkHttpClient.Builder()
//            .addInterceptor(loggingInterceptor)
//            .build()
//    }else{
//        OkHttpClient
//            .Builder()
//            .build()
//    }


    @Singleton
    @Provides
    fun getUnsafeOkHttpClient(): OkHttpClient {

        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {

            }

            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {

            }

            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
            }
        })

        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, SecureRandom())

        val sslSocketFactory = sslContext.socketFactory

        val builder = OkHttpClient.Builder()
        builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
        builder.hostnameVerifier { hostname, session -> true }

        val interceptor = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG) {
            interceptor.level = HttpLoggingInterceptor.Level.BODY
        } else {
            interceptor.level = HttpLoggingInterceptor.Level.NONE
        }

        return builder
            .connectTimeout(20, TimeUnit.SECONDS)
            .addInterceptor(interceptor)
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient, BASE_URL:String): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .build()

    @Provides
    @Singleton
    fun provideServerApi(retrofit: Retrofit) = retrofit.create(ServerApi::class.java)

    @Provides
    @Singleton
    fun provideLocalDB() : AppDataBase = Room
        .databaseBuilder(MyApp.appContext!!, AppDataBase::class.java, AppDataBase.DB_NAME)
        .allowMainThreadQueries()
        .build()

    @Provides
    @Singleton
    fun provideUserDao(appDataBase: AppDataBase): UserDao = appDataBase.UserDao()

    @Provides
    fun providesIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

}