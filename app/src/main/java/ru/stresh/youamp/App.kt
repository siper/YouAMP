package ru.stresh.youamp

import android.app.Application
import coil.Coil
import coil.ImageLoader
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import org.koin.android.ext.android.get
import ru.stresh.youamp.core.api.provider.ApiProvider

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        setupDi(this)
        setupCoil()
    }

    private fun setupCoil() {
        val apiProvider: ApiProvider = get()
        Coil.setImageLoader(
            ImageLoader
                .Builder(this)
                .okHttpClient {
                    OkHttpClient
                        .Builder()
                        .addInterceptor { chain ->
                            chain.proceed(chain.request())
                                .newBuilder()
                                .removeHeader("cache-control")
                                .removeHeader("expires")
                                .addHeader("cache-control", "public, max-age=604800, no-transform")
                                .build()
                        }
                        .addInterceptor { chain ->
                            val api = runBlocking { apiProvider.getApi() }

                            val request = chain.request()
                            val newUrl = api.appendAuth(
                                request
                                    .url
                                    .toUri()
                                    .toString()
                            )

                            chain.proceed(
                                request
                                    .newBuilder()
                                    .url(newUrl)
                                    .build()
                            )
                        }
                        .build()
                }
                .crossfade(true)
                .build()
        )
    }
}