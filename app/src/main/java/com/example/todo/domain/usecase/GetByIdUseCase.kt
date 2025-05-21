package com.example.todo.domain.usecase

import com.example.todo.EventRepository
import com.example.todo.domain.Card
import com.example.todo.domain.EventInterface

class GetByIdUseCase (private var eventInterface: EventInterface = EventRepository) {
    suspend operator fun invoke(id:Long): Card {
        return eventInterface.getById(id)
    }
}