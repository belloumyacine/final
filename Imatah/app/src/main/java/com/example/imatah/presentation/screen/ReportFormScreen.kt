package com.example.imatah.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.imatah.data.model.Report
import java.util.*

@Composable
fun ReportFormScreen(
    report: Report? = null,
    onSave: (Report) -> Unit,
    onCancel: () -> Unit
) {
    var title by remember { mutableStateOf(report?.title ?: "") }
    var description by remember { mutableStateOf(report?.description ?: "") }
    var status by remember { mutableStateOf(report?.status ?: "قيد الانتظار") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = if (report == null) "إضافة تقرير جديد" else "تعديل التقرير",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("العنوان") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("الوصف") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        Spacer(modifier = Modifier.height(8.dp))

        ExposedDropdownMenuBox(
            expanded = false,
            onExpandedChange = { }
        ) {
            OutlinedTextField(
                value = status,
                onValueChange = { },
                label = { Text("الحالة") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true
            )

            DropdownMenu(
                expanded = false,
                onDismissRequest = { }
            ) {
                listOf("قيد الانتظار", "قيد التنفيذ", "مكتمل").forEach { statusOption ->
                    DropdownMenuItem(
                        text = { Text(statusOption) },
                        onClick = { status = statusOption }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onCancel) {
                Text("إلغاء")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    val newReport = Report(
                        id = report?.id ?: UUID.randomUUID(),
                        title = title,
                        description = description,
                        status = status,
                        createdAt = report?.createdAt ?: Date(),
                        userId = report?.userId ?: UUID.randomUUID() // TODO: Get from auth
                    )
                    onSave(newReport)
                }
            ) {
                Text("حفظ")
            }
        }
    }
} 