package com.example.todo

import BaseTest
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.text.TextLayoutResult
import com.example.todo.data.Priority
import com.example.todo.presentation.viewmodel.EventViewModel
import com.example.todo.ui.theme.Green
import com.example.todo.ui.theme.Orange
import com.example.todo.ui.theme.Red
import kotlinx.coroutines.delay
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.util.Locale

class Screen(private val composeTestRule: ComposeTestRule) {
    val vm = EventViewModel()

    fun SemanticsNodeInteraction.assertTextColor(
        color: Color
    ): SemanticsNodeInteraction = assert(isOfColor(color))

    private fun isOfColor(color: Color): SemanticsMatcher = SemanticsMatcher(
        "${SemanticsProperties.Text.name} is of color '$color'"
    ) {
        val textLayoutResults = mutableListOf<TextLayoutResult>()
        it.config.getOrNull(SemanticsActions.GetTextLayoutResult)
            ?.action
            ?.invoke(textLayoutResults)
        return@SemanticsMatcher if (textLayoutResults.isEmpty()) {
            false
        } else {
            textLayoutResults.first().layoutInput.style.color == color
        }
    }

    fun clickCreateButton() {
        composeTestRule.onNodeWithTag("create_button").performClick()
    }

    fun createEvent(title: String, description: String, deadline: LocalDate, priority: Priority) {
        composeTestRule.onNode(
            hasTestTag("event_title_input"),
            useUnmergedTree = true
        ).performTextInput(title)
        composeTestRule.onNodeWithTag("event_description_input").performTextInput(description)
        composeTestRule.onNodeWithTag(priority.toString(), useUnmergedTree = true).performClick()
        composeTestRule.onNodeWithTag("calendar", useUnmergedTree = true).performClick()
        composeTestRule.onNodeWithText(deadline.dayOfWeek.toString()
            .lowercase(Locale.getDefault())
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() } + ", " + deadline.month.toString()
            .lowercase(Locale.getDefault())
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() } + " " + deadline.dayOfMonth + ", " + deadline.year)
            .performClick()
        composeTestRule.onNodeWithText("OK").performClick()
        composeTestRule.onNodeWithText("Save").performClick()
    }

    fun clickSortButton() {
        composeTestRule.onNodeWithTag("show_filter").performClick()
    }

    fun createNewEvent(title: String, description: String = "") {
        composeTestRule.onNode(
            hasTestTag("event_title_input"),
            useUnmergedTree = true
        ).performTextInput(title)
        composeTestRule.onNodeWithTag("event_description_input").performTextInput(description)
        composeTestRule.onNodeWithText("Save").performClick()
    }

    fun editEvent(
        newTitle: String,
        newDescription: String,
        newDeadline: LocalDate,
        newPriority: Priority
    ) {
        composeTestRule.onNodeWithTag("event_title_input", useUnmergedTree = true)
            .performTextInput(newTitle)
        composeTestRule.onNodeWithTag("event_description_input", useUnmergedTree = true)
            .performTextInput(newDescription)
        composeTestRule.onNodeWithTag(newPriority.toString(), useUnmergedTree = true).performClick()
        composeTestRule.onNodeWithTag("calendar", useUnmergedTree = true).performClick()
        composeTestRule.onNodeWithText(newDeadline.dayOfWeek.toString()
            .lowercase(Locale.getDefault())
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() } + ", " + newDeadline.month.toString()
            .lowercase(Locale.getDefault())
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() } + " " + newDeadline.dayOfMonth + ", " + newDeadline.year)
            .performClick()
        composeTestRule.onNodeWithText("OK").performClick()
        composeTestRule.onNodeWithText("Save").performClick()
    }

    fun clickEditButton() {
        composeTestRule.onNodeWithText("Edit").performClick()
    }

    fun clickDeleteButton() {
        composeTestRule.onNodeWithText("Delete").performClick()
    }

    fun clickDoneButton() {
        composeTestRule.onNodeWithText("Done").performClick()
    }

    fun assertEventExists(title: String) {
        composeTestRule.onNodeWithTag("event").assertExists()
    }

    fun assertEventDoesNotExists(title: String) {
        composeTestRule.onNodeWithTag("event").assertDoesNotExist()
    }

    fun assertEventTitle(title: String) {
        composeTestRule.onNodeWithTag("event_title", useUnmergedTree = true)
        composeTestRule.onNodeWithText(title, useUnmergedTree = true).assertExists()
    }

    fun assertEventDescription(description: String) {
        composeTestRule.onNodeWithTag("event_description", useUnmergedTree = true)
        composeTestRule.onNodeWithText(description, useUnmergedTree = true).assertExists()
    }

    fun assertPriority(priority: String) {
        composeTestRule.onNodeWithTag("event_priority", useUnmergedTree = true).assertExists()
        composeTestRule.onNodeWithText(priority).assertExists()
    }

    fun assertDeadline(deadline: String) {
        composeTestRule.onNodeWithTag("event_deadline", useUnmergedTree = true).assertExists()
        composeTestRule.onNodeWithText(deadline, useUnmergedTree = true).assertExists()
    }

    fun assertColor(color: Color) {
        composeTestRule.onNodeWithTag("event_status", useUnmergedTree = true).assertTextColor(color)
    }

    fun assertStatus(status: String) {
        composeTestRule.onNodeWithText(status, useUnmergedTree = true).assertExists()
    }

    fun assertEventDoesNotExist() {
        composeTestRule.onNodeWithTag("event_title").assertDoesNotExist()
    }

    fun pickSortAttr() {
        composeTestRule.onNodeWithTag("Deadline").performClick()
        composeTestRule.onNodeWithText("Save").performClick()
    }

    fun assertOrder(){
        composeTestRule.onNodeWithTag("event_list")
            .onChildren()
            .apply {
                get(0).onChildren().apply { get(0).assertTextContains(LocalDate.now().plusDays(1).toString())}
                get(1).onChildren().apply { get(0).assertTextContains(LocalDate.now().plusDays(2).toString())}
            }
    }
}

class EventTest : BaseTest() {

    @Before
    fun clearDatabase() {
        screen.vm.clearAll()
        composeTestRule.waitUntil(5000) {
            screen.vm.events.value.isEmpty()
        }
    }

    @Test
    fun shouldSort() = run {
        val testTitleFirst = "test1"
        val testTitleSecond = "test2"

        step("Click create button") {
            screen.clickCreateButton()
        }

        step("Enter event details and save") {
            screen.createEvent(testTitleFirst, "", LocalDate.now().plusDays(1), Priority.Critical)
        }

        step("Click create button") {
            screen.clickCreateButton()
        }

        step("Enter event details and save") {
            screen.createEvent(testTitleSecond, "", LocalDate.now().plusDays(2), Priority.Critical)
        }

        step("Open sort menu"){
            screen.clickSortButton()
        }

        step("Pick sort attribute"){
            screen.pickSortAttr()
        }

        step("Assert true order"){
            screen.assertOrder()
            screen.vm.clearAll()
        }
    }

    @Test
    fun shouldCreateNewEventWithTitleFour() = run {
        val testTitle = "Test"

        step("Click create button") {
            screen.clickCreateButton()
        }

        step("Enter event details and save") {
            screen.createNewEvent(testTitle, "")
        }

        step("Verify event appears in list") {
            screen.assertEventExists(testTitle)
            screen.vm.clearAll()
        }
    }

    @Test
    fun shouldCreateNewEventWithTitleThree() = run {
        val testTitle = "Tes"
        step("Click create button") {
            screen.clickCreateButton()
        }

        step("Enter event details and save") {
            screen.createNewEvent(testTitle, "")
        }

        step("Verify event does not appears in list") {
            screen.assertEventDoesNotExists(testTitle)
            screen.vm.clearAll()
        }
    }
    @Test
    fun shouldEditEventSecond() = run {
        val testTitle = " Test"
        step("Click create button") {
            screen.clickCreateButton()
        }

        step("Enter event details and save") {
            screen.createNewEvent(testTitle, "")
        }

        step("Click edit button") {
            screen.clickEditButton()
        }

        step("Change deadline") {
            screen.editEvent(
                "",
                "",
                LocalDate.now().minusDays(1),
                Priority.Critical
            )
        }

        step("Verify new changes") {
            screen.assertColor(Red)
            screen.assertStatus("Overdue")
            screen.vm.clearAll()
        }
    }

    @Test
    fun shouldBecomeLate() = run {
        val testTitle = "Test"
        step("Click create button") {
            screen.clickCreateButton()
        }

        step("Enter event details and save") {
            screen.createNewEvent(testTitle, "")
        }

        step("Click edit button") {
            screen.clickEditButton()
        }

        step("Change deadline") {
            screen.editEvent(
                "",
                "",
                LocalDate.now().minusDays(1),
                Priority.Critical
            )
        }
        step("Make completed") {
            screen.clickDoneButton()
        }

        step("Check status") {
            screen.assertStatus("Late")
            screen.vm.clearAll()
        }
    }

    @Test
    fun shouldBecomeOverdue() = run {

        val testTitle = " Test"

        step("Click create button") {
            screen.clickCreateButton()
        }

        step("Enter event details and save") {
            screen.createNewEvent(testTitle, "")
        }

        step("Click edit button") {
            screen.clickEditButton()
        }

        step("Change deadline") {
            screen.editEvent(
                "",
                "",
                LocalDate.now().minusDays(1),
                Priority.Critical
            )
        }
        step("Make completed") {
            screen.clickDoneButton()
        }

        step("Make uncompleted") {
            screen.clickDoneButton()

        }

        step("Check status") {
            screen.assertStatus("Overdue")
            screen.vm.clearAll()
        }
    }

    @Test
    fun shouldBecomeCompleted() = run {

        val testTitle = " Test"

        step("Click create button") {
            screen.clickCreateButton()
        }

        step("Enter event details and save") {
            screen.createNewEvent(testTitle, "")
        }

        step("Click edit button") {
            screen.clickEditButton()
        }

        step("Change deadline") {
            screen.editEvent(
                "",
                "",
                LocalDate.now().minusDays(1),
                Priority.Critical
            )
        }

        step("Click edit button") {
            screen.clickEditButton()
        }

        step("Make changes") {
            screen.clickDoneButton()
            screen.editEvent(
                "",
                "",
                LocalDate.now().plusDays(4),
                Priority.Critical
            )
        }

        step("Check color and status") {
            screen.assertColor(Green)
            screen.assertStatus("Completed")
            screen.vm.clearAll()
        }
    }

    @Test
    fun shouldEditEventFirst() = run {
        val testTitle = " Test"

        step("Click create button") {
            screen.clickCreateButton()
        }

        step("Enter event details and save") {
            screen.createNewEvent(testTitle, "")
        }

        step("Click edit button") {
            screen.clickEditButton()
        }

        step("Change fields") {
            screen.editEvent(
                "new title",
                "new desc",
                LocalDate.now().plusDays(1),
                Priority.Critical
            )
        }

        step("Verify changes") {
            screen.assertEventTitle("new title Test")
            screen.assertDeadline(LocalDate.now().plusDays(1).toString())
            screen.assertEventDescription("new desc")
            screen.assertPriority("Critical")
            screen.assertColor(Orange)
            screen.assertStatus("Active")
            screen.vm.clearAll()
        }
    }

    @Test
    fun macrosPriorityShouldWork() = run {

        step("Click create button") {
            screen.clickCreateButton()
        }

        step("Create event") {
            screen.createNewEvent("!1new event", "")
        }

        step("Check priority") {
            screen.assertPriority("Critical")
            screen.vm.clearAll()
        }
    }

    @Test
    fun macrosDeadlineShouldWork() = run {

        step("Click create button") {
            screen.clickCreateButton()
        }

        step("Create event") {
            screen.createNewEvent(
                "!before ${
                    LocalDate.now().plusDays(4).dayOfMonth
                }.05.${LocalDate.now().plusDays(4).year}new event", ""
            )
        }

        step("Check deadline") {
            screen.assertDeadline(LocalDate.now().plusDays(4).toString())
            screen.vm.clearAll()
        }
    }

    @Test
    fun bothMacrosShouldWork() = run {

        step("Click create button") {
            screen.clickCreateButton()
        }

        step("Create event") {
            screen.createNewEvent(
                "!1!before ${
                    LocalDate.now().plusDays(4).dayOfMonth
                }.05.${LocalDate.now().plusDays(4).year}new event", ""
            )
        }

        step("Check fields") {
            screen.assertDeadline(LocalDate.now().plusDays(4).toString())
            screen.assertPriority("Critical")
            screen.vm.clearAll()
        }
    }

    @Test
    fun bothMacrosShouldNotWorkWithUserInput() = run {

        step("Click create button") {
            screen.clickCreateButton()
        }

        step("Create event") {
            screen.createEvent(
                "!before ${
                    LocalDate.now().plusDays(1).dayOfMonth
                }.05.${LocalDate.now().plusDays(1).year}new event",
                "",
                LocalDate.now().plusDays(4),
                Priority.Low
            )
        }

        step("Check fields") {
            screen.assertDeadline(LocalDate.now().plusDays(4).toString())
            screen.assertPriority("Low")
            screen.vm.clearAll()
        }
    }

    @Test
    fun shouldDeleteEventFromList() = run {
        val testTitle = "Todo to Delete"

        step("Create event to delete") {
            screen.clickCreateButton()
            screen.createNewEvent(testTitle)
            screen.assertEventExists(testTitle)
        }

        step("Delete the event") {
            screen.clickDeleteButton()
        }

        step("Verify event is gone") {
            screen.assertEventDoesNotExist()
            screen.vm.clearAll()
        }
    }
}


