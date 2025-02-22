package com.example.studysmart.presentation.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@Composable
fun DeleteDialog(
    modifier: Modifier = Modifier,
    isOpen: Boolean,
    title: String,
    bodyText: String,
    onDismissRequest: () -> Unit,
    onConfirmationButtonClick: () -> Unit
) {


    if (isOpen) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            modifier = Modifier,
            title = {
                Text(
                    title
                )
            },
            text = {
                Text(
                    bodyText
                )
            },
            dismissButton = {
                TextButton(
                    onClick = onDismissRequest,
                ) {
                    Text("Cancel")
                }
            },
            confirmButton = {
                TextButton(
                    onClick = onConfirmationButtonClick,
                ) {
                    Text("Delete")
                }
            },

            )
    }

}