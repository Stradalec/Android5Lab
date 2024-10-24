package com.example.android5lab

import android.R.attr.data
import android.content.ClipData
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.Request
import timber.log.Timber
import java.io.IOException


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Timber.plant(Timber.DebugTree())

        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=ff49fcd4d4a08aa6aafb6ea3de826464&tags=cat&format=json&nojsoncallback=1")
            .build()

        Thread {
            var photoLinks: List<String> = listOf("1")
            try {
                val response = client.newCall(request).execute()

                val body = response.body()?.string()

                if (body != null) {
                    val gson = GsonBuilder()
                        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                        .create()

                    val wrapper = gson.fromJson(body, com.example.android5lab.Wrapper::class.java)

                    photoLinks = wrapper.photos.photo.map { photo ->
                        "https://farm${photo.farm}.staticflickr.com/${photo.server}/${photo.id}_${photo.secret}_z.jpg"
                    }
                    Timber.i("Copied link: $photoLinks")

                    wrapper.photos.photo.forEachIndexed { index, photo ->
                        if (index % 5 == 0) {
                            Timber.d("ID: ${photo.id}, Owner: ${photo.owner}, Secret = ${photo.secret}, Server = ${photo.server}, Farm = ${photo.farm}, Title: ${photo.title}, IsPublic = ${photo.isItPublic}, IsFriend = ${photo.isFriend},IsFamily= ${photo.isFamily}")
                        }
                    }
                    runOnUiThread {
                        displayImageList(photoLinks)
                    }
                }
            } catch (e: IOException) {
                Timber.d("I hate this  program and my life")
            }

        }.start()



    }
    private fun displayImageList(imageUrlList: List<String>) {
        val recyclerView: RecyclerView = findViewById(R.id.rView)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = MyAdapter(imageUrlList)
    }
}

data class Photo(
    val id: String,
    val owner: String,
    val secret: String,
    val server: String,
    val farm: Int,
    val title: String,
    val isItPublic: Int,
    val isFriend: Int,
    val isFamily: Int
)

data class PhotoPage(
    val page: Int,
    val pages: Int,
    val perpage: Int,
    val total: Int,
    val photo: List<Photo>
)

data class Wrapper(
    val photos: PhotoPage
)


class MyAdapter(private val imageUrlList: List<String>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageViewItem: ImageView = view.findViewById(R.id.recyclerViewIV)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rview_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Glide.with(holder.imageViewItem)
            .load(imageUrlList[position])
            .centerCrop()
            .into(holder.imageViewItem)
        holder.imageViewItem.setOnClickListener{
            Timber.i("$imageUrlList[position]")
        }
    }

    override fun getItemCount(): Int {
        return imageUrlList.size
    }
}

