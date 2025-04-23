package com.example.imatah.presentation.screens.comments

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.imatah.data.model.Comment
import com.example.imatah.presentation.viewmodel.CommentViewModel
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsScreen(
    reportId: UUID,
    onNavigateBack: () -> Unit,
    viewModel: CommentViewModel = viewModel()
) {
    val comments by viewModel.comments.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf<Comment?>(null) }
    var showDeleteDialog by remember { mutableStateOf<Comment?>(null) }

    LaunchedEffect(reportId) {
        viewModel.loadComments(reportId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("التعليقات") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "رجوع")
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "إضافة تعليق")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (error != null) {
                Text(
                    text = error!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(comments) { comment ->
                        CommentItem(
                            comment = comment,
                            onEdit = { showEditDialog = comment },
                            onDelete = { showDeleteDialog = comment }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddEditCommentDialog(
            reportId = reportId,
            onDismiss = { showAddDialog = false },
            onConfirm = { content ->
                viewModel.addComment(
                    Comment(
                        reportId = reportId,
                        content = content
                    )
                )
                showAddDialog = false
            }
        )
    }

    showEditDialog?.let { comment ->
        AddEditCommentDialog(
            comment = comment,
            onDismiss = { showEditDialog = null },
            onConfirm = { content ->
                viewModel.updateComment(
                    comment.copy(content = content)
                )
                showEditDialog = null
            }
        )
    }

    showDeleteDialog?.let { comment ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("حذف التعليق") },
            text = { Text("هل أنت متأكد من حذف هذا التعليق؟") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteComment(comment.id, reportId)
                        showDeleteDialog = null
                    }
                ) {
                    Text("حذف")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentItem(
    comment: Comment,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = comment.content,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = comment.createdAt.toString(),
                    style = MaterialTheme.typography.bodySmall
                )
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "تعديل")
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "حذف")
                    }
                }
            }
        }
    }
}

@Composable
fun AddEditCommentDialog(
    reportId: UUID? = null,
    comment: Comment? = null,
    onDismiss: () -> Unit,
    onConfirm: (content: String) -> Unit
) {
    var content by remember { mutableStateOf(comment?.content ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (comment == null) "إضافة تعليق" else "تعديل التعليق") },
        text = {
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("محتوى التعليق") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (content.isNotBlank()) {
                        onConfirm(content)
                    }
                }
            ) {
                Text(if (comment == null) "إضافة" else "تحديث")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
} 