package com.example.android5lab

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.Request
import timber.log.Timber
import java.io.IOException

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        Timber.plant(Timber.DebugTree())
        val searchButton: Button = findViewById(R.id.btn_search)
        val searchEditText: EditText = findViewById(R.id.et_search)
        lateinit var MyAdapter: ContactAdapter
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://drive.google.com/u/0/uc?id=1-KO-9GA3NzSgIc1dkAsNm8Dqw0fuPxcR&=download").build()
        Thread {
            try {
                val response = client.newCall(request).execute()
                val body =  response.body()?.string()
                if (body != null) {
                    val gson = GsonBuilder().create()
                    val wrapper: List<Contact> = gson.fromJson(body, Array<Contact>::class.java).toList()
                    Timber.d("Русский язык")
                    val ContactList: List<Contact> = wrapper
                    wrapper.forEach { contact ->
                        Timber.d("Name: ${contact.name}, Phone: ${contact.phone}, Type: ${contact.type}")
                    }
                    runOnUiThread {
                        MyAdapter = ContactAdapter(ContactList)
                        MyAdapter.saveList(ContactList)
                        displayAll(MyAdapter)
                    }
                }

            }

            catch (e: IOException){
                Timber.d("Request not work")
            }
        }.start()

        searchButton.setOnClickListener{
            val filter = searchEditText.text.toString()
            Timber.d("Search started")
            MyAdapter.filter(filter)
        }
    }
    private  fun displayAll(inputAdapter: ContactAdapter) {
        val recyclerView: RecyclerView = findViewById(R.id.rView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = inputAdapter
    }
}



