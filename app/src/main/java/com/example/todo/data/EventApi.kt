package com.example.todo.data

import com.example.todo.domain.Card
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import java.time.LocalDate

interface EventApi {

    @POST("/events/create")
    suspend fun createEvent(@Body cardDTO: CardDTO)

    @GET("/events/get")
    suspend fun getEvents(
        @Query("title") title: String?,
        @Query("description") description: String?,
        @Query("deadline") deadline: LocalDate?,
        @Query("status") status: Status?,
        @Query("priority") priority: Priority?,
        @Query("createdDate") creationDate: LocalDate?,
        @Query("editDate") editDate: LocalDate?
    ): List<Card>

    @DELETE("/events/delete/{id}")
    suspend fun deleteEvent(
        @Path("id") id: Long
    )

    @DELETE("/events/delete/all")
    suspend fun clearAll()

    @PUT("/events/edit/{id}")
    suspend fun editEvent(
        @Path("id") id: Long, @Body cardDTO: CardDTO
    )

    @PUT("/events/markAsComplete/{id}")
    suspend fun setComplete(
        @Path("id") id: Long
    )

    @PUT("/events/markAsUnComplete/{id}")
    suspend fun setUnComplete(
        @Path("id") id: Long
    )

    @GET("events/getById/{id}")
    suspend fun getById(
        @Path("id") id:Long
    ):Card
}