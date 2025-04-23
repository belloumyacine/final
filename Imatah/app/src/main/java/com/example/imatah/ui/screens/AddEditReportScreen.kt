package com.example.imatah.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.imatah.data.model.Report
import com.example.imatah.ui.viewmodel.ReportViewModel
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditReportScreen(
    report: Report? = null,
    onNavigateBack: () -> Unit,
    viewModel: ReportViewModel = hiltViewModel()
) {
    var title by remember { mutableStateOf(report?.title ?: "") }
    var description by remember { mutableStateOf(report?.description ?: "") }
    var status by remember { mutableStateOf(report?.status ?: "pending") }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (report == null) "إضافة تقرير" else "تعديل التقرير") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "رجوع")
                    }
                },
                actions = {
                    if (report != null) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "حذف")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("عنوان التقرير") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("وصف التقرير") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            ExposedDropdownMenuBox(
                expanded = false,
                onExpandedChange = { }
            ) {
                OutlinedTextField(
                    value = when (status) {
                        "pending" -> "قيد الانتظار"
                        "in_progress" -> "قيد التنفيذ"
                        "completed" -> "مكتمل"
                        else -> status
                    },
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("الحالة") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                DropdownMenu(
                    expanded = false,
                    onDismissRequest = { }
                ) {
                    DropdownMenuItem(
                        text = { Text("قيد الانتظار") },
                        onClick = { status = "pending" }
                    )
                    DropdownMenuItem(
                        text = { Text("قيد التنفيذ") },
                        onClick = { status = "in_progress" }
                    )
                    DropdownMenuItem(
                        text = { Text("مكتمل") },
                        onClick = { status = "completed" }
                    )
                }
            }

            Button(
                onClick = {
                    val newReport = Report(
                        id = report?.id,
                        title = title,
                        description = description,
                        status = status,
                        userId = report?.userId ?: UUID.randomUUID()
                    )
                    if (report == null) {
                        viewModel.addReport(newReport)
                    } else {
                        viewModel.updateReport(newReport)
                    }
                    onNavigateBack()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = title.isNotBlank()
            ) {
                Text(if (report == null) "إضافة" else "حفظ")
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("حذف التقرير") },
            text = { Text("هل أنت متأكد من حذف هذا التقرير؟") },
            confirmButton = {
                TextButton(
                    onClick = {
                        report?.id?.let { viewModel.deleteReport(it) }
                        showDeleteDialog = false
                        onNavigateBack()
                    }
                ) {
                    Text("حذف")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
} 