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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
        lateinit var privateContactList: List<Contact>
        lateinit var MyAdapter: ContactAdapter
        val recyclerView: RecyclerView = findViewById(R.id.rView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        CoroutineScope(Dispatchers.IO).launch {
            val contacts = getContacts()
            privateContactList = contacts
            withContext(Dispatchers.Main){
                MyAdapter = ContactAdapter(contacts)
                recyclerView.adapter = MyAdapter
                //displayAll(MyAdapter)
            }
        }


        searchButton.setOnClickListener{
            val filter = searchEditText.text.toString()
            Timber.d("Search started")
            val filtered = filtered(privateContactList, filter)
            MyAdapter.updateContacts(filtered)
        }
    }

    private suspend fun getContacts() : List<Contact>{
        val wrapper: List<Contact> = emptyList()
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://drive.google.com/u/0/uc?id=1-KO-9GA3NzSgIc1dkAsNm8Dqw0fuPxcR&=download").build()
        val response = client.newCall(request).execute()
        val body =  response.body()?.string()
        if (body != null) {
            val gson = GsonBuilder().create()
            val wrapper: List<Contact> = gson.fromJson(body, Array<Contact>::class.java).toList()
            Timber.d("Русский язык")
            val Contactlist: List<Contact> = wrapper
            wrapper.forEach { contact ->
                Timber.d("Name: ${contact.name}, Phone: ${contact.phone}, Type: ${contact.type}")
            }

        }
        return withContext(Dispatchers.IO){
            wrapper
        }
    }

    private fun filtered(inputContact: List<Contact>, filter: String): List<Contact>{
        Timber.d("Trying filter")
        if (filter.isEmpty()) {
            inputContact
            Timber.d("Nothing to filter")
        } else {
            inputContact.filter {
                it.name.contains(filter, ignoreCase = true) ||
                        it.phone.contains(filter,ignoreCase = true) ||
                        it.type.contains(filter, ignoreCase = true)
            }
            Timber.d("Something to filter")
        }
        return inputContact
    }

    private  fun displayAll(inputAdapter: ContactAdapter) {
        val recyclerView: RecyclerView = findViewById(R.id.rView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = inputAdapter
    }
}



