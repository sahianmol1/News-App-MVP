package com.example.mvppractice.di

import android.content.Context
import com.example.mvppractice.common.utils.Constants.BASE_URL
import com.example.mvppractice.service.api.NewsApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRetrofit(@ApplicationContext context: Context): Retrofit = Retrofit
        .Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun providesNewsApi(retrofit: Retrofit): NewsApi =
        retrofit.create(NewsApi::class.java)

    // Check why dagger is not able to provide this dependency
//    @Provides
//    @Singleton
//    fun providesCountriesJson(@ApplicationContext context: Context): InputStream {
//        return context.resources.openRawResource(R.raw.countries)
//    }
}