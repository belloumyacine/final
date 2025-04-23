package com.example.imatah.presentation.screens.reports

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.imatah.data.model.Report
import com.example.imatah.data.model.ReportStatus
import com.example.imatah.presentation.viewmodel.ReportViewModel
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditReportScreen(
    reportId: UUID? = null,
    onNavigateBack: () -> Unit,
    viewModel: ReportViewModel = viewModel()
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var status by remember { mutableStateOf(ReportStatus.PENDING) }
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(reportId) {
        if (reportId != null) {
            viewModel.loadReport(reportId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (reportId == null) "إضافة تقرير" else "تعديل التقرير") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "رجوع")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("عنوان التقرير") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("وصف التقرير") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            ExposedDropdownMenuBox(
                expanded = false,
                onExpandedChange = { }
            ) {
                OutlinedTextField(
                    value = status.name,
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
                    ReportStatus.values().forEach { statusOption ->
                        DropdownMenuItem(
                            text = { Text(statusOption.name) },
                            onClick = { status = statusOption }
                        )
                    }
                }
            }

            if (error != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = error!!,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    val report = Report(
                        id = reportId ?: UUID.randomUUID(),
                        title = title,
                        description = description,
                        status = status
                    )
                    if (reportId == null) {
                        viewModel.addReport(report)
                    } else {
                        viewModel.updateReport(report)
                    }
                    onNavigateBack()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading && title.isNotBlank() && description.isNotBlank()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(if (reportId == null) "إضافة" else "تحديث")
                }
            }
        }
    }
} 