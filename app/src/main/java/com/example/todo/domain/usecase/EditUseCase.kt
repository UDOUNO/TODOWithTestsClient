package com.example.todo.domain.usecase

import com.example.todo.EventRepository
import com.example.todo.data.CardDTO
import com.example.todo.domain.EventInterface

class EditUseCase (private var eventInterface: EventInterface = EventRepository) {
    suspend operator fun invoke(id:Long, cardDTO: CardDTO){
        eventInterface.editEvent(id, cardDTO)
    }
}