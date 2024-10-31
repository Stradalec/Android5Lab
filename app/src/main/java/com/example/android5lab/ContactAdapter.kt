package com.example.android5lab

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber

class ContactAdapter(private  var contacts: List<Contact>) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>(){
    private  var privateContactList: List<Contact> = contacts
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

    fun saveList(inputContacts: List<Contact>){
        privateContactList = inputContacts
        contacts = inputContacts
        notifyDataSetChanged()
    }

    fun filter(filter: String){
        Timber.d("Trying filter")
        if (filter.isEmpty()) {
            contacts = privateContactList
            Timber.d("Nothing to filter")
        } else {
            contacts = privateContactList.filter {
                it.name.contains(filter, ignoreCase = true) ||
                        it.phone.contains(filter,ignoreCase = true) ||
                        it.type.contains(filter, ignoreCase = true)
            }
            Timber.d("Something to filter")
        }
        notifyDataSetChanged()

    }
}