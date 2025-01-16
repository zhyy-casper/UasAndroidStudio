package com.example.lotengnews

import com.example.lotengnews.model.Berita
import retrofit2.Response
import retrofit2.http.GET

interface BeritaApi {
    @GET("berita.json")
    suspend fun getBeritas(): Response<List<Berita>>

}