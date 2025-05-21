package com.example.todo.domain.usecase

import com.example.todo.EventRepository
import com.example.todo.data.Priority
import com.example.todo.data.Status
import com.example.todo.domain.Card
import com.example.todo.domain.EventInterface
import java.time.LocalDate

class GetUseCase(private var eventInterface: EventInterface = EventRepository) {
    suspend operator fun invoke(
        title: String?,
        description: String?,
        deadline: LocalDate?,
        status: Status?,
        priority: Priority?,
        creationDate: LocalDate?,
        editDate: LocalDate?
    ): List<Card> {
        return eventInterface.getEvents(
            title,
            description,
            deadline,
            status,
            priority,
            creationDate,
            editDate
        )
    }
}