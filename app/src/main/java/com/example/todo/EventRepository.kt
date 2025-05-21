package com.example.todo

import android.util.Log
import com.example.todo.domain.Card
import com.example.todo.data.CardDTO
import com.example.todo.data.Priority
import com.example.todo.data.Retrofit
import com.example.todo.data.Status
import com.example.todo.domain.EventInterface
import java.time.LocalDate

object EventRepository : EventInterface {
    override suspend fun createEvent(cardDTO: CardDTO){
        Retrofit.Event.createEvent(cardDTO)
    }

    override suspend fun getEvents(
        title: String?,
        description: String?,
        deadline: LocalDate?,
        status: Status?,
        priority: Priority?,
        creationDate: LocalDate?,
        editDate: LocalDate?
    ): List<Card> {
        val cardList:List<Card> = Retrofit.Event.getEvents(title, description, deadline, status, priority, creationDate, editDate)
        return cardList
    }

    override suspend fun deleteEvent(id: Long) {
        Retrofit.Event.deleteEvent(id)
    }

    override suspend fun editEvent(id: Long, cardDTO: CardDTO) {
        Retrofit.Event.editEvent(id,cardDTO)
    }

    override suspend fun setComplete(id: Long) {
        Retrofit.Event.setComplete(id)
    }

    override suspend fun setUnComplete(id: Long) {
        Retrofit.Event.setUnComplete(id)
    }

    override suspend fun getById(id:Long):Card{
        return Retrofit.Event.getById(id)
    }

    override suspend fun clearAll(){
        Retrofit.Event.clearAll()
    }

}