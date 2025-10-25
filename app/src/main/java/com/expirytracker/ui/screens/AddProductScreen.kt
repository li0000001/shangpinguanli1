package com.expirytracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.expirytracker.ui.components.DatePickerField
import com.expirytracker.viewmodel.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    viewModel: ProductViewModel,
    onNavigateBack: () -> Unit
) {
    var productName by remember { mutableStateOf("") }
    var productionDate by remember { mutableStateOf<Long?>(null) }
    var shelfLifeDays by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf<Long?>(null) }
    var reminderDaysBefore by remember { mutableStateOf("3") }
    var showError by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf("") }
    
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    
    LaunchedEffect(errorMessage) {
        if (errorMessage != null) {
            showError = true
            errorText = errorMessage ?: ""
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "添加商品",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "返回",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = productName,
                onValueChange = { productName = it },
                label = { Text("商品名称 *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "方式一：生产日期 + 保质期天数",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            DatePickerField(
                label = "生产日期",
                selectedDate = productionDate,
                onDateSelected = { productionDate = it }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = shelfLifeDays,
                onValueChange = { shelfLifeDays = it.filter { char -> char.isDigit() } },
                label = { Text("保质期天数") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Divider()
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "方式二：直接输入保质日期",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            DatePickerField(
                label = "保质日期",
                selectedDate = expiryDate,
                onDateSelected = { expiryDate = it }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Divider()
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "提醒设置",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedTextField(
                value = reminderDaysBefore,
                onValueChange = { reminderDaysBefore = it.filter { char -> char.isDigit() } },
                label = { Text("提前提醒天数 *") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = {
                    when {
                        productName.isBlank() -> {
                            errorText = "请输入商品名称"
                            showError = true
                        }
                        reminderDaysBefore.isBlank() -> {
                            errorText = "请输入提醒天数"
                            showError = true
                        }
                        expiryDate == null && (productionDate == null || shelfLifeDays.isBlank()) -> {
                            errorText = "请输入保质日期，或者输入生产日期和保质期天数"
                            showError = true
                        }
                        else -> {
                            viewModel.addProduct(
                                name = productName,
                                productionDate = productionDate,
                                shelfLifeDays = shelfLifeDays.toIntOrNull(),
                                expiryDate = expiryDate,
                                reminderDaysBefore = reminderDaysBefore.toIntOrNull() ?: 3
                            )
                            onNavigateBack()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("保存商品", fontSize = 16.sp)
                }
            }
            
            if (showError) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = errorText,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
