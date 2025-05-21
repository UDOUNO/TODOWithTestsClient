package com.example.todo.domain

import com.example.todo.LocalDateSerializer
import com.example.todo.data.Priority
import com.example.todo.data.Status
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
class Card(
    val id:Long,
    val title:String,
    val description:String?,
    @Serializable(with = LocalDateSerializer::class)
    val deadline: LocalDate?,
    val status: Status,
    val priority: Priority,
    @Serializable(with = LocalDateSerializer::class)
    val createdDate: LocalDate?,
    @Serializable(with = LocalDateSerializer::class)
    val editDate: LocalDate?
)