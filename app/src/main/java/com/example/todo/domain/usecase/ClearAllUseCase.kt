package com.example.todo.domain.usecase

import com.example.todo.EventRepository
import com.example.todo.data.CardDTO
import com.example.todo.domain.EventInterface

class ClearAllUseCase(private var eventInterface: EventInterface = EventRepository) {
    suspend operator fun invoke(){
        eventInterface.clearAll()
    }
}