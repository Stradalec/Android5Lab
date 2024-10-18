package com.example.android5lab

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import timber.log.Timber
import java.io.IOException
import java.sql.Wrapper


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Timber.plant(Timber.DebugTree())
        var recyclerView: RecyclerView = findViewById(R.id.rView)
        val layoutManager = GridLayoutManager(this, 2)
        recyclerView.layoutManager = layoutManager

        var PhotoLinks: List<String> = listOf("1")
        var PhotoAdapter = PhotoAdapter(this, PhotoLinks)
        recyclerView.adapter = PhotoAdapter

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
                        "\"https://farm${photo.farm}.staticflickr.com/${photo.server}/${photo.id}_${photo.secret}_z.jpg\""
                    }
                    Timber.i("Copied link: $photoLinks")

                    wrapper.photos.photo.forEachIndexed { index, photo ->
                        if (index % 5 == 0) {
                            Timber.d("ID: ${photo.id}, Owner: ${photo.owner}, Secret = ${photo.secret}, Server = ${photo.server}, Farm = ${photo.farm}, Title: ${photo.title}, IsPublic = ${photo.isItPublic}, IsFriend = ${photo.isFriend},IsFamily= ${photo.isFamily}")
                        }
                    }
                    runOnUiThread {

                        PhotoAdapter.updateList(photoLinks)

                    }
                }
            } catch (e: IOException) {
                Timber.d("I hate this  program and my life")
            }

        }.start()


    }
}

data class Photo(
    val id: String,
    val owner: String,
    val secret: String,
    val server: Double,
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

class PhotoAdapter(private val context: Context, private var photoLinks: List<String>) :
    RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {
    inner class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.recyclerViewIV)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val link = photoLinks[position]
                    copyToClipboard(link)
                    Timber.i("Copied link: $link")
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rview_item, parent, false)
        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val PhotoUrl = photoLinks[position]
        Glide.with(context).load(PhotoUrl).into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return photoLinks.size
    }

    fun updateList(links: List<String>) {
        photoLinks = links
        Timber.d("Links in Adapter $photoLinks")
        notifyDataSetChanged()
    }

    private fun copyToClipboard(link: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Photo link", link)
        clipboard.setPrimaryClip(clip)
    }
}