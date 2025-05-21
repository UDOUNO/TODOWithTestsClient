package com.example.todo.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.collection.emptyDoubleList
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.magnifier
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.todo.R
import com.example.todo.data.CardDTO
import com.example.todo.data.Priority
import com.example.todo.data.Status
import com.example.todo.domain.Card
import com.example.todo.presentation.viewmodel.EventViewModel
import com.example.todo.ui.theme.Green
import com.example.todo.ui.theme.Orange
import com.example.todo.ui.theme.PurpleGrey80
import com.example.todo.ui.theme.Red
import java.time.DayOfWeek
import java.time.LocalDate

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Cards(EventViewModel())
        }
    }
}

@Composable
fun Cards(vm: EventViewModel) {

    val cardList = vm.events.collectAsState()
    var showDialogCreate by remember { mutableStateOf(false) }
    var showFilterDialog by remember { mutableStateOf(false) }

    if (showDialogCreate) {
        Dialog(
            onDismissRequest = { showDialogCreate = false },
            properties = DialogProperties(usePlatformDefaultWidth = false),

        ) {
            CreateEvent(vm, {showDialogCreate = false})
        }
    }

    if (showFilterDialog) {
        Dialog(
            onDismissRequest = { showFilterDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            EditFilters(vm,{showFilterDialog = false})
        }
    }

    Scaffold(modifier = Modifier.fillMaxWidth(), floatingActionButton = {
        FloatingActionButton(modifier = Modifier.testTag("create_button"), onClick = { showDialogCreate = true }) {
            Icon(Icons.Default.Add, contentDescription = "create_button")
        }
        FloatingActionButton(
            onClick = { showFilterDialog = true },
            modifier = Modifier
                .offset(y = -80.dp)
                .testTag("show_filter")
        ) {
            Icon(Icons.Default.Build, contentDescription = "show_filter")
        }
    }) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding,
            modifier = Modifier
                .fillMaxSize()
                .testTag("event_list")
        ) {
            if (cardList.value.isNotEmpty()) {
                items(cardList.value) { card ->
                    ListItem(card = card, vm)
                }
            }
        }
    }

}

@Composable
fun ListItem(card: Card, vm: EventViewModel) {

    var showDialogEdit by remember { mutableStateOf(false) }
    val id = remember { mutableStateOf("") }
    var borderColor = PurpleGrey80

    if (showDialogEdit) {
        Dialog(
            onDismissRequest = { showDialogEdit = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            EditEvent(vm, card, {showDialogEdit = false})
        }
    }

    if (card.status == Status.Overdue) {
        borderColor = Red
    }
    if (card.status == Status.Active && card.deadline != null && (LocalDate.now().dayOfMonth - card.deadline.dayOfMonth <= 3)) {
        borderColor = Orange
    }
    if (card.status == Status.Active && (card.deadline == null || card.deadline.dayOfMonth - LocalDate.now().dayOfMonth > 3)) {
        borderColor = Color.Gray
    }
    if (card.status == Status.Late || card.status == Status.Completed) {
        borderColor = Green
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(6.dp)
            .testTag("event"),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .border(
                    width = 2.dp, color = borderColor
                )
                .background(colorResource(R.color.white))
                .fillMaxWidth()
                .clickable { id.value = card.id.toString() }
                .padding(16.dp)
                .testTag("card ${card.id}"),
            colors = CardColors(Color.White, Color.Gray, Color.Gray, Color.Gray)
        ) {
            Row(modifier = Modifier.padding(bottom = 20.dp)) {
                Button(onClick = {
                    if (card.status == Status.Active || card.status == Status.Overdue) vm.setComplete(
                        card.id
                    ) else vm.setUnComplete(card.id)
                }) { Text("Done") }
                Button(
                    onClick = { showDialogEdit = true },
                    modifier = Modifier.padding(start = 60.dp)
                ) { Text("Edit") }
                Button(
                    onClick = { vm.deleteEvent(card.id) },
                    modifier = Modifier.padding(start = 65.dp)
                ) { Text("Delete") }
            }
            Text(
                text = card.title,
                modifier = Modifier
                    .padding(4.dp)
                    .background((colorResource(R.color.white)))
                    .testTag("event_title"),
                fontSize = 20.sp,
                color = Color.Black,
                maxLines = 1
            )
            Text(
                text = if (card.description.isNullOrEmpty()) "" else card.description,
                modifier = Modifier
                    .testTag("event_description")
                    .padding(8.dp)
                    .background((colorResource(R.color.white))),
                fontSize = 20.sp,
                maxLines = 1
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background((colorResource(R.color.white))),
            ) {
                Box(
                    modifier = Modifier
                        .background(colorResource(R.color.white))
                        .fillMaxWidth(0.47f)
                        .border(
                            width = 2.dp, color = borderColor, shape = RoundedCornerShape(16.dp)
                        )
                ) {
                    Text(
                        text = card.status.toString(),
                        color = borderColor,
                        modifier = Modifier
                            .padding(8.dp)
                            .testTag("event_status")
                            .align(Alignment.Center),
                        fontSize = 16.sp,
                        maxLines = 1
                    )
                }
                Spacer(
                    modifier = Modifier.padding(8.dp)
                )
                Box(
                    modifier = Modifier
                        .background(colorResource(R.color.white))
                        .fillMaxWidth()
                        .border(
                            width = 2.dp, color = Color.Gray, shape = RoundedCornerShape(16.dp)
                        )
                ) {
                    Text(
                        text = card.priority.toString(),
                        modifier = Modifier
                            .padding(8.dp)
                            .testTag("event_priority")
                            .align(Alignment.Center),
                        fontSize = 16.sp,
                        maxLines = 1
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background((colorResource(R.color.white))),
            ) {
                Box(
                    modifier = Modifier
                        .padding(top = 6.dp)
                        .background(
                            Color.Gray, RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)
                        )
                        .fillMaxWidth(0.5f)
                ) {
                    Text(
                        color = Color.White,
                        text = card.createdDate.toString(),
                        modifier = Modifier
                            .padding(8.dp)
                            .testTag("event_created_date")
                            .align(Alignment.Center),
                        fontSize = 14.sp,
                        maxLines = 1
                    )
                }
                Spacer(
                    modifier = Modifier.padding(0.5.dp)
                )
                Box(
                    modifier = Modifier
                        .padding(top = 6.dp)
                        .background(
                            Color.Gray, RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp)
                        )
                        .fillMaxWidth()
                ) {
                    Text(
                        color = Color.White,
                        text = if(card.deadline != null) card.deadline.toString() else "not stated",
                        modifier = Modifier
                            .testTag("event_deadline")
                            .padding(8.dp)
                            .align(Alignment.Center),
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEvent(vm: EventViewModel, close: ()-> Unit) {

    val options = listOf("Medium", "High", "Critical", "Low")
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var priority: Priority? = null
    if (selectedOption == options[0]) {
        priority = Priority.Medium
    } else if (selectedOption == options[1]) {
        priority = Priority.High
    } else if (selectedOption == options[2]) {
        priority = Priority.Critical
    } else if (selectedOption == options[3]) {
        priority = Priority.Low
    }

    val name = remember { mutableStateOf("") }
    val desc = remember { mutableStateOf("") }
    val deadline = remember { mutableStateOf(LocalDate.now()) }
    var datePickerUsed by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    Column {
        OutlinedTextField(
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White, unfocusedContainerColor = Color.White
            ),
            value = name.value,
            onValueChange = { newText -> name.value = newText },
            placeholder = { Text(stringResource(R.string.name)) },
            maxLines = 1,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(vertical = 16.dp)
                .testTag("event_title_input"),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        )
        OutlinedTextField(
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White, unfocusedContainerColor = Color.White
            ),
            value = desc.value,
            onValueChange = { newText -> desc.value = newText },
            placeholder = { Text(stringResource(R.string.Desc)) },
            maxLines = 1,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(vertical = 16.dp)
                .testTag("event_description_input"),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        )
        OutlinedTextField(
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White, unfocusedContainerColor = Color.White
            ),
            value = deadline.value.toString(),
            onValueChange = { newText -> deadline.value.toString() },
            maxLines = 1,
            trailingIcon = {
                IconButton(
                    onClick = {
                        showDatePicker = true
                    }, modifier = Modifier
                        .padding(end = 12.dp)
                        .size(22.dp)
                        .testTag("calendar")
                        .paint(
                            painterResource(R.drawable.calendar),
                            contentScale = ContentScale.Fit
                        )
                ) {}
            },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(vertical = 16.dp)
                .testTag("event_deadline_input"),
        )
        Column(
            Modifier
                .padding(start = 120.dp)
                .background(Color.White)
        ) {

            options.forEach { option ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable {
                            selectedOption = if (selectedOption == option) null else option
                        }
                        .testTag(option)
                ) {
                    Checkbox(
                        checked = selectedOption == option,
                        onCheckedChange = { isChecked ->
                            selectedOption = if (isChecked) option else null
                        }
                    )
                    Text(option, modifier = Modifier.padding(end = 20.dp).testTag("event_priority_input"))
                }
            }
        }
        if (showDatePicker) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = deadline.value.toEpochDay() * 86400000
            )
            DatePickerDialog(onDismissRequest = { showDatePicker = false }, confirmButton = {
                Button(onClick = {
                    datePickerUsed = true
                    datePickerState.selectedDateMillis?.let { millis ->
                        deadline.value = LocalDate.ofEpochDay(millis / 86400000)
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            }) {
                DatePicker(state = datePickerState)
            }
        }
        Button(onClick = ({
            if (name.value.isNotEmpty() && name.value.count() >= 4) vm.createEvent(
                CardDTO(
                    title = name.value,
                    description = desc.value,
                    deadline = if (datePickerUsed) deadline.value else null,
                    priority = priority
                )
            )
            close()
        })) {
            Text("Save")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditEvent(vm: EventViewModel, card: Card, close: () -> Unit) {
    val options = listOf("Medium", "High", "Critical", "Low")
    var selectedOption by remember { mutableStateOf<String?>(card.priority.toString()) }
    var priority: Priority? = card.priority
    if (selectedOption == options[0]) {
        priority = Priority.Medium
    } else if (selectedOption == options[1]) {
        priority = Priority.High
    } else if (selectedOption == options[2]) {
        priority = Priority.Critical
    } else if (selectedOption == options[3]) {
        priority = Priority.Low
    }

    val name = remember { mutableStateOf(card.title) }
    val desc = remember { mutableStateOf(card.description) }
    val deadline = remember { mutableStateOf(card.deadline) }
    var showDatePicker by remember { mutableStateOf(false) }
    Column {
        OutlinedTextField(
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White, unfocusedContainerColor = Color.White
            ),
            value = name.value,
            onValueChange = { newText -> name.value = newText },
            placeholder = { Text(stringResource(R.string.name)) },
            maxLines = 1,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(vertical = 16.dp)
                .testTag("event_title_input"),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        )
        OutlinedTextField(
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White, unfocusedContainerColor = Color.White
            ),
            value = (if (desc.value.isNullOrEmpty()) "" else desc.value).toString(),
            onValueChange = { newText -> desc.value = newText },
            placeholder = { Text(stringResource(R.string.Desc)) },
            maxLines = 1,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(vertical = 16.dp)
                .testTag("event_description_input"),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        )
        OutlinedTextField(
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White, unfocusedContainerColor = Color.White
            ),
            value = deadline.value.toString(),
            onValueChange = { newText -> deadline.value.toString() },
            maxLines = 1,
            trailingIcon = {
                IconButton(
                    onClick = {
                        showDatePicker = true
                    }, modifier = Modifier
                        .padding(end = 12.dp)
                        .size(22.dp)
                        .paint(
                            painterResource(R.drawable.calendar),
                            contentScale = ContentScale.Fit
                        )
                        .testTag("calendar")
                ) {}
            },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(vertical = 16.dp)
        )
        Column(
            Modifier
                .padding(start = 120.dp)
                .background(Color.White)
        ) {

            options.forEach { option ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable {
                            selectedOption = if (selectedOption == option) null else option
                        }
                        .testTag(option)
                ) {
                    Checkbox(
                        checked = selectedOption == option,
                        onCheckedChange = { isChecked ->
                            selectedOption = if (isChecked) option else null
                        }
                    )
                    Text(option, modifier = Modifier.padding(end = 20.dp).testTag("event_priority_input"))
                }
            }
        }
        if (showDatePicker) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = LocalDate.now().toEpochDay() * 86400000
            )
            DatePickerDialog(onDismissRequest = { showDatePicker = false }, confirmButton = {
                Button(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        deadline.value = LocalDate.ofEpochDay(millis / 86400000)
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            }) {
                DatePicker(state = datePickerState)
            }
        }
        Button(onClick = ({
            if (deadline.value != null && name.value.isNotEmpty() && name.value.count() >= 4) vm.editEvent(
                card.id,
                CardDTO(
                    title = name.value,
                    description = desc.value,
                    deadline = deadline.value,
                    priority = priority
                )
            )
            else if(deadline.value == null && name.value.isNotEmpty() && name.value.count() >= 4) vm.editEvent(
                card.id,
                CardDTO(
                    title = name.value,
                    description = desc.value,
                    deadline = null,
                    priority = priority
                )

            )
            close()
        })) {
            Text("Save")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditFilters(vm: EventViewModel, close: () -> Unit) {
    val options = listOf("Status", "Priority", "Deadline", "Title", "Description", "CreationDate","EditDate")
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var status: Status? = null
    var priority: Priority? = null
    val name = remember { mutableStateOf("") }
    val desc = remember { mutableStateOf("") }
    val deadline = remember { mutableStateOf(LocalDate.now()) }
    val createdDate = remember { mutableStateOf(LocalDate.now()) }
    val editDate = remember { mutableStateOf(LocalDate.now()) }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(modifier = Modifier.fillMaxWidth(), Arrangement.Absolute.Center) {
            Column(
                Modifier
                    .background(Color.White)
            ) {

                options.forEach { option ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable {
                                selectedOption =
                                    if (selectedOption == option) null else option
                            }
                            .testTag(option)
                    ) {
                        Checkbox(
                            checked = selectedOption == option,
                            onCheckedChange = { isChecked ->
                                selectedOption = if (isChecked) option else null
                            }
                        )
                        Text(option, modifier = Modifier.padding(end = 20.dp))
                    }
                }
            }
        }
        Button(onClick = ({
            name.value = if(selectedOption == "Title") "zzzz" else ""
            desc.value = if(selectedOption == "Description") "z" else ""
            priority = if(selectedOption == "Priority") Priority.Medium else null
            status = if(selectedOption == "Status") Status.Active else null
            deadline.value = if (selectedOption == "Deadline") LocalDate.now() else null
            editDate.value = if (selectedOption == "EditDate") LocalDate.now() else null
            createdDate.value = if (selectedOption == "CreationDate") LocalDate.now() else null

            vm.getEvents(
                name.value.ifEmpty {
                    null
                },
                desc.value.ifEmpty {
                    null
                },
                creationDate = createdDate.value,
                editDate = editDate.value,
                status = status,
                deadline = deadline.value,
                priority = priority
            )
            close()
        }), Modifier.padding(top = 40.dp)) {
            Text("Save")
        }
    }
}


