package fr.isen.beucher.isensmartcompanion.api

import fr.isen.beucher.isensmartcompanion.Event
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("events.json")
    fun getEvents(): Call<List<Event>>
}