package com.example.studysmart.presentation.components

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDatePicker(
    modifier: Modifier = Modifier,
    state: DatePickerState,
    isOpen: Boolean,
    confirmationButtonText: String = "OK",
    dismissButtonText: String = "Cancel",
    onDismissRequest: () -> Unit,
    onConfirmationButtonClicked: () -> Unit
) {
    if (isOpen) {
        DatePickerDialog(
            onDismissRequest = onDismissRequest,
            confirmButton = {
                TextButton(
                    onClick = onConfirmationButtonClicked
                ) {
                    Text(confirmationButtonText)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onConfirmationButtonClicked
                ) {
                    Text(dismissButtonText)
                }
            },
            content = {
                DatePicker(
                    state = state,
                    )
            }
        )
    }

}