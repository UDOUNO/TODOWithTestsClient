package com.example.todo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todo.data.CardDTO
import com.example.todo.data.Priority
import com.example.todo.data.Status
import com.example.todo.domain.Card
import com.example.todo.domain.CardList
import com.example.todo.domain.usecase.ClearAllUseCase
import com.example.todo.domain.usecase.CreateUseCase
import com.example.todo.domain.usecase.DeleteUseCase
import com.example.todo.domain.usecase.EditUseCase
import com.example.todo.domain.usecase.GetByIdUseCase
import com.example.todo.domain.usecase.GetUseCase
import com.example.todo.domain.usecase.MarkAsCompleteUseCase
import com.example.todo.domain.usecase.MarkAsUnCompleteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class EventViewModel(
    private val getUseCase: GetUseCase = GetUseCase(),
    private val clearAllUseCase: ClearAllUseCase = ClearAllUseCase(),
    private val createUseCase: CreateUseCase = CreateUseCase(),
    private val editUseCase: EditUseCase = EditUseCase(),
    private val getByIdUseCase: GetByIdUseCase = GetByIdUseCase(),
    private val deleteUseCase: DeleteUseCase = DeleteUseCase(),
    private val markAsUnCompleteUseCase: MarkAsUnCompleteUseCase = MarkAsUnCompleteUseCase(),
    private val markAsCompleteUseCase: MarkAsCompleteUseCase = MarkAsCompleteUseCase()
) : ViewModel() {
    private var _events = MutableStateFlow<List<Card>>(emptyList())
    var events = _events.asStateFlow()

    init {
       getEvents(null, null, null, null, null, null, null)
    }

    fun clearAll(){
        viewModelScope.launch {
            clearAllUseCase()
            val resp = getUseCase(null,null,null,null,null,null,null)
            _events.value = resp
        }
    }

    fun getEvents(
        title: String?,
        description: String?,
        deadline: LocalDate?,
        status: Status?,
        priority: Priority?,
        creationDate: LocalDate?,
        editDate: LocalDate?
    ) {
        viewModelScope.launch {
            val resp =
                getUseCase(title, description, deadline, status, priority, creationDate, editDate)
            _events.value = resp
        }
    }

    fun createEvent(cardDTO: CardDTO) {
        viewModelScope.launch {
            createUseCase(cardDTO)
            _events.value = getUseCase(null,null,null,null,null,null,null)
        }
    }

    fun editEvent(id: Long, cardDTO: CardDTO) {
        viewModelScope.launch {
            editUseCase(id, cardDTO)
            updateTaskEdit(id)
        }
    }

    fun deleteEvent(id: Long) {
        viewModelScope.launch {
            deleteUseCase(id)
            updateTaskDelete(id)
        }
    }

    fun setComplete(id: Long) {
        viewModelScope.launch {
            markAsCompleteUseCase(id)
            updateTaskEdit(id)
        }
    }

    fun setUnComplete(id: Long) {
        viewModelScope.launch {
            markAsUnCompleteUseCase(id)
            updateTaskEdit(id)
        }
    }

    private fun updateTaskEdit(id: Long) {
        viewModelScope.launch {
            val temps = events.value.toMutableList()
            for (temp in temps.indices) {
                if (temps[temp].id == id) {
                    temps[temp] = getByIdUseCase(id)
                }
            }
            _events.value = temps
        }
    }

    private fun updateTaskDelete(id: Long) {
        viewModelScope.launch {
            val temps = events.value.toMutableList()
            for (temp in temps.indices) {
                if (temps[temp].id == id) {
                    temps.removeAt(temp)
                    break
                }
            }
            _events.value = temps
        }
    }
}