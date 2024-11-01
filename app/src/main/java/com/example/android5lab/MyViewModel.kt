package com.example.android5lab

import androidx.lifecycle.ViewModel
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import timber.log.Timber
import java.io.IOException

class MyViewModel : ViewModel() {
    val client = OkHttpClient()
    fun ParsePhotos(adapter: MyAdapter) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                var photoLinks: List<String> = listOf("1")
                val request = Request.Builder()
                    .url("https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=ff49fcd4d4a08aa6aafb6ea3de826464&tags=cat&format=json&nojsoncallback=1")
                    .build()
                client.newCall(request).execute().body()?.string()
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

                }
            }
            catch (e: IOException) {
                Timber.d("This Coroutines is hard")
            }
        }
    }
}