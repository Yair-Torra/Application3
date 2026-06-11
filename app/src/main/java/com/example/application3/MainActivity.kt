package com.example.application3

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import java.io.InputStream
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: RevistaAdapter
    private lateinit var rvRevistas: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        rvRevistas = findViewById(R.id.rvRevistas)
        rvRevistas.layoutManager = LinearLayoutManager(this)
        adapter = RevistaAdapter(emptyList())
        rvRevistas.adapter = adapter

        setupGlide()
        fetchRevistas()
    }

    private fun setupGlide() {
        val client = getUnsafeOkHttpClient()
        Glide.get(this).registry.replace(
            GlideUrl::class.java,
            InputStream::class.java,
            OkHttpUrlLoader.Factory(client)
        )
    }

    private fun getUnsafeOkHttpClient(): OkHttpClient {
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        })

        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, SecureRandom())

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier { _, _ -> true }
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJfeDF1c2VyZGV2IiwiaWF0IjoxNzgxMjA2NDgwLCJleHAiOjE3ODEyOTI4ODB9.ut9t7jNdM2ubQhp0EZCCytNYR2IQQPmlyoO51V2laGE")
                    .build()
                chain.proceed(request)
            }
            .addInterceptor(logging)
            .build()
    }

    private fun fetchRevistas() {
        val client = getUnsafeOkHttpClient()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://apiws.uteq.edu.ec/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(RevistaApi::class.java)
        api.getRevistas().enqueue(object : Callback<List<Revista>> {
            override fun onResponse(call: Call<List<Revista>>, response: Response<List<Revista>>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        Log.d("MainActivity", "Data size: ${it.size}")
                        adapter.updateData(it)
                    }
                } else {
                    Log.e("MainActivity", "Error code: ${response.code()}")
                    Toast.makeText(this@MainActivity, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Revista>>, t: Throwable) {
                Log.e("MainActivity", "Failure: ${t.message}", t)
                Toast.makeText(this@MainActivity, "Failure: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
