package com.example.android5lab

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
                    Timber.d("Thread")
                    val ContactList: List<Contact> = wrapper
                    wrapper.forEach { contact ->
                        Timber.d("Name: ${contact.name}, Phone: ${contact.phone}, Type: ${contact.type}")
                    }
                    runOnUiThread {
                        displayAll(ContactList)
                    }
                }

            }

            catch (e: IOException){
                Timber.d("Request not work")
            }
        }.start()
    }
    private  fun displayAll( inputContacts: List<Contact> ) {
        val recyclerView: RecyclerView = findViewById(R.id.rView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        var ContactAdapter =  ContactAdapter(inputContacts)
        recyclerView.adapter = ContactAdapter
    }
}

data class Contact(
    val name: String,
    val phone: String,
    val type: String
)

class ContactAdapter(private  val contacts: List<Contact>) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>(){
    class  ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textName: TextView = itemView.findViewById(R.id.textName)
        val textPhone: TextView = itemView.findViewById(R.id.textPhone)
        val textType: TextView = itemView.findViewById(R.id.textType)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rview_item,parent,false)
        return  ContactViewHolder(view)
    }

    override fun getItemCount(): Int {
        return contacts.size
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contacts[position]
        holder.textName.text = contact.name
        holder.textPhone.text = contact.phone
        holder.textType.text = contact.type
    }
}