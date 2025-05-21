package com.example.todo.domain.usecase

import com.example.todo.EventRepository
import com.example.todo.data.CardDTO
import com.example.todo.domain.EventInterface

class CreateUseCase(private var eventInterface: EventInterface = EventRepository) {
    suspend operator fun invoke(cardDTO: CardDTO){
        eventInterface.createEvent(cardDTO)
    }
}