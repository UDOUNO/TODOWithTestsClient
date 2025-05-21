package com.example.todo.data

import com.example.todo.LocalDateSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class CardDTO(
    val title:String,
    val description:String?,
    @Serializable(with = LocalDateSerializer::class)
    val deadline: LocalDate?,
    val priority: Priority?
)