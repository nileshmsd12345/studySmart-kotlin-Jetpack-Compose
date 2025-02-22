package com.example.studysmart.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.studysmart.R
import com.example.studysmart.domain.model.Task
import com.example.studysmart.util.Priority
import com.example.studysmart.util.changeMillisToDateSting

fun LazyListScope.tasksList(
    sectionTitle: String,
    taskList: List<Task>,
    emptyListText: String,
    onTaskCardClick: (Int?) -> Unit,
    onCheckBoxClick: (Task) -> Unit
) {
    item {
        Text(
            sectionTitle,
            modifier = Modifier.padding(12.dp),
            style = MaterialTheme.typography.bodySmall
        )
    }
    if (taskList.isEmpty()) {
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_tasks),
                    contentDescription = emptyListText,
                    modifier = Modifier
                        .size(120.dp),
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    emptyListText,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }

        }

    } else {
        items(taskList) { task ->
            TaskCard(
                modifier = Modifier.padding(vertical = 4.dp, horizontal = 12.dp),
                task = task,
                onClick = {
                    onTaskCardClick(task.taskId)
                },
                onCheckBoxClick = {
                    onCheckBoxClick(task)
                }
            )

        }
    }

}

@Composable
private fun TaskCard(
    modifier: Modifier = Modifier,
    task: Task,
    onCheckBoxClick: () -> Unit,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = modifier.clickable {
            onClick.invoke()
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TaskCheckBox(isComplete = task.isComplete,
                borderColor = Priority.fromInt(task.priority).color,
                onClick = {
                    onCheckBoxClick.invoke()
                }
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    task.title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleMedium,
                    textDecoration = if (task.isComplete) {
                        TextDecoration.LineThrough
                    } else TextDecoration.None
                )
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    task.dueDate.changeMillisToDateSting(),
                    style = MaterialTheme.typography.bodySmall
                )

            }

        }
    }

}