package com.example.studysmart.presentation.task

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.studysmart.presentation.components.DeleteDialog
import com.example.studysmart.presentation.components.SubjectListBottomSheet
import com.example.studysmart.presentation.components.TaskCheckBox
import com.example.studysmart.presentation.components.TaskDatePicker
import com.example.studysmart.util.Priority
import com.example.studysmart.util.SnackbarEvent
import com.example.studysmart.util.changeMillisToDateSting
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


data class TaskScreenNavArgs(
    val taskId: Int?,
    val subjectId: Int?
)

@Destination(navArgsDelegate = TaskScreenNavArgs::class, )
@Composable
fun TaskScreenRoute(navigator: DestinationsNavigator) {
    val taskViewModel: TaskScreenViewModel = hiltViewModel()
    val state by taskViewModel.state.collectAsStateWithLifecycle()
    TaskScreen(
        onBackButtonClick = {
            navigator.navigateUp()
        },
        state = state,
        onEvent = taskViewModel::onEvent,
        snackbarEvent = taskViewModel.snackbarEventFlow
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskScreen(
    modifier: Modifier = Modifier,
    onBackButtonClick: () -> Unit,
    state: TaskState,
    onEvent: (TaskEvent) -> Unit,
    snackbarEvent: SharedFlow<SnackbarEvent>,
) {

    var taskTitleError by remember {
        mutableStateOf<String?>(null)
    }
    taskTitleError = when {
        state.title.isBlank() -> "Please enter task title"
        state.title.length < 4 -> "Task title is too short"
        state.title.length > 30 -> "Task title is too long"
        else -> null
    }

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    LaunchedEffect(key1 = true) {
        snackbarEvent.collectLatest { event ->
            when (event) {
                is SnackbarEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = event.duration
                    )
                }
            }
        }
    }

    var isDeleteSubjectDialogOpen by rememberSaveable {
        mutableStateOf(false)
    }

    var isTaskDatePickerDialogOpen by rememberSaveable {
        mutableStateOf(false)
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis(),
        selectableDates = object : SelectableDates {
            override fun isSelectableYear(year: Int): Boolean {
                return year in 2020..2030 // Restrict to years between 2020 and 2030
            }

            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= System.currentTimeMillis() // Allow only future dates
            }
        }
    )

    val bottomSheetState = rememberModalBottomSheetState()

    var isSubjectListBottomSheetOpen by remember {
        mutableStateOf(false)
    }
    val scope = rememberCoroutineScope()

    DeleteDialog(
        isOpen = isDeleteSubjectDialogOpen,
        title = "Delete Task ?",
        bodyText = "Are you sure you want to delete this task? This action can not be undone.",
        onDismissRequest = {
            isDeleteSubjectDialogOpen = false
        },
        onConfirmationButtonClick = {
            onEvent(TaskEvent.DeleteTask)
            isDeleteSubjectDialogOpen = false
        }
    )

    TaskDatePicker(
        state = datePickerState,
        isOpen = isTaskDatePickerDialogOpen,
        onDismissRequest = {
            isTaskDatePickerDialogOpen = false
        },
        onConfirmationButtonClicked = {
            onEvent(TaskEvent.OnDateChange(millis = datePickerState.selectedDateMillis))
            isTaskDatePickerDialogOpen = false
        }
    )

    SubjectListBottomSheet(
        sheetState = bottomSheetState,
        isOpen = isSubjectListBottomSheetOpen,
        subjects = state.subjects,
        onSubjectClicked = {
            scope.launch {
                bottomSheetState.hide()
            }.invokeOnCompletion {
                if (!bottomSheetState.isVisible) {
                    isSubjectListBottomSheetOpen = false
                }
            }

            onEvent(TaskEvent.OnRelatedSubjectSelect(it))
        },
        onDismissRequest = {
            isSubjectListBottomSheetOpen = false
        }
    )



    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            TaskScreenTopAppBar(
                isTaskExist = state.currentTaskId != null,
                isCompleted = state.isTaskComplete,
                checkBoxBarColor = Color.Red,
                onBackButtonClick = onBackButtonClick,
                onDeleteButtonClick = {
                    isDeleteSubjectDialogOpen = true
                },
                onCheckBoxClick = {
                    onEvent(TaskEvent.OnIsCompleteChange)
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(state = rememberScrollState())
                .padding(12.dp)
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.title,
                onValueChange = {
                    onEvent(TaskEvent.OnTitleChange(it))
                },
                label = { Text("Title") },
                singleLine = true,
                isError = taskTitleError != null && taskTitleError!!.isNotBlank(),
                supportingText = { Text(taskTitleError.orEmpty()) }

            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.description,
                onValueChange = {
                    onEvent(TaskEvent.OnDescriptionChange(it))
                },
                label = { Text("Description") }

            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                "Due Date",
                style = MaterialTheme.typography.bodySmall
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    datePickerState.selectedDateMillis.changeMillisToDateSting(),
                    style = MaterialTheme.typography.bodyLarge
                )
                IconButton(onClick = {
                    isTaskDatePickerDialogOpen = true
                }) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Select Due Date"
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                "Priority",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Priority.entries.forEach { priority ->
                    PriorityButton(
                        modifier = Modifier.weight(1f),
                        label = priority.title,
                        backgroundColor = priority.color,
                        borderColor = if (priority == state.priority) {
                            Color.White
                        } else {
                            Color.Transparent
                        },

                        labelColor = if (priority == state.priority) {
                            Color.White
                        } else {
                            Color.White.copy(alpha = 0.7f)
                        },

                        onClick = {
                            onEvent(TaskEvent.OnPriorityChange(priority))
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
            Text(
                "Related to subject",
                style = MaterialTheme.typography.bodySmall
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val firstSubject = state.subjects.firstOrNull()?.name ?: ""
                Text(
                    state.relatedToSubject ?: firstSubject,
                    style = MaterialTheme.typography.bodyLarge
                )
                IconButton(onClick = {
                    isSubjectListBottomSheetOpen = true
                }) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Select Subject"
                    )
                }
            }
            Button(
                onClick = {
                    onEvent(TaskEvent.SaveTask(
                        onSuccessfullyDone = {
                            onBackButtonClick()
                        }
                    ))
                },
                enabled = taskTitleError == null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Text("Save")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreenTopAppBar(
    modifier: Modifier = Modifier,
    isTaskExist: Boolean,
    isCompleted: Boolean,
    checkBoxBarColor: Color,
    onBackButtonClick: () -> Unit,
    onDeleteButtonClick: () -> Unit,
    onCheckBoxClick: () -> Unit,

    ) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onBackButtonClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Navigate Back"
                )
            }
        },
        title = {
            Text(
                "Task",
                style = MaterialTheme.typography.headlineSmall,
            )
        },
        actions = {
            if (isTaskExist) {
                TaskCheckBox(
                    isComplete = isCompleted,
                    borderColor = checkBoxBarColor,
                    onClick = onCheckBoxClick
                )
            }
            IconButton(onClick = onDeleteButtonClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Task"
                )
            }
        }
    )
}

@Composable
fun PriorityButton(
    modifier: Modifier = Modifier,
    label: String,
    backgroundColor: Color,
    borderColor: Color,
    labelColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .background(color = backgroundColor)
            .clickable { onClick.invoke() }
            .padding(5.dp)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(5.dp),
            )
            .padding(5.dp),
        contentAlignment = Alignment.Center

    ) {
        Text(label, color = labelColor)
    }

}