package com.example.studysmart.presentation.components


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.studysmart.R
import com.example.studysmart.domain.model.Session
import com.example.studysmart.util.changeMillisToDateSting
import com.example.studysmart.util.toHours

fun LazyListScope.studySessionList(
    sectionTitle: String,
    sessionList: List<Session>,
    emptyListText: String,
    onDeleteIconClick: (session: Session) -> Unit

) {
    item {
        Text(
            sectionTitle,
            modifier = Modifier.padding(12.dp),
            style = MaterialTheme.typography.bodySmall
        )
    }
    if (sessionList.isEmpty()) {
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_lamp),
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
        items(sessionList) { session ->
            StudySessionCard(
                session = session,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                onDeleteIconClick = {
                    onDeleteIconClick(session)
                }

            )

        }
    }

}

@Composable
private fun StudySessionCard(
    modifier: Modifier = Modifier,
    session: Session,
    onDeleteIconClick: () -> Unit
) {
    Card(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.padding(start = 12.dp)
            ) {
                Text(
                    session.relatedToSubject,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    session.date.changeMillisToDateSting(),
                    style = MaterialTheme.typography.bodySmall
                )

            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                "${session.duration.toHours()} hr",
                style = MaterialTheme.typography.bodySmall
            )
            IconButton(
                onClick = onDeleteIconClick
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Session"
                )
            }

        }
    }

}

