package com.example.todo.domain

import com.example.todo.data.Priority
import com.example.todo.data.Status
import com.example.todo.data.CardDTO
import java.time.LocalDate

interface EventInterface {
    suspend fun createEvent(cardDTO: CardDTO)
    suspend fun getEvents(
        title: String?,
        description: String?,
        deadline: LocalDate?,
        status: Status?,
        priority: Priority?,
        creationDate: LocalDate?,
        editDate: LocalDate?
    ): List<Card>

    suspend fun deleteEvent(id: Long)
    suspend fun editEvent(id: Long, cardDTO: CardDTO)
    suspend fun setComplete(id: Long)
    suspend fun setUnComplete(id: Long)
    suspend fun getById(id:Long):Card
    suspend fun clearAll()
}