package com.example.todo.domain.usecase

import com.example.todo.EventRepository
import com.example.todo.domain.EventInterface

class MarkAsCompleteUseCase (private var eventInterface: EventInterface = EventRepository) {
    suspend operator fun invoke(id:Long){
        eventInterface.setComplete(id)
    }
}