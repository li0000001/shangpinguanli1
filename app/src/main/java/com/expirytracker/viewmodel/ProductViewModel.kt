package com.expirytracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.expirytracker.data.database.ProductEntity
import com.expirytracker.data.repository.ProductRepository
import com.expirytracker.utils.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProductViewModel(
    private val repository: ProductRepository
) : ViewModel() {

    val products: StateFlow<List<ProductEntity>> = repository.getAllProducts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun addProduct(
        name: String,
        productionDate: Long?,
        shelfLifeDays: Int?,
        expiryDate: Long?,
        reminderDaysBefore: Int
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null

                val finalExpiryDate = when {
                    expiryDate != null -> expiryDate
                    productionDate != null && shelfLifeDays != null -> 
                        DateUtils.calculateExpiryDate(productionDate, shelfLifeDays)
                    else -> {
                        _errorMessage.value = "请输入有效的日期信息"
                        _isLoading.value = false
                        return@launch
                    }
                }

                val product = ProductEntity(
                    name = name,
                    productionDate = productionDate,
                    shelfLifeDays = shelfLifeDays,
                    expiryDate = finalExpiryDate,
                    reminderDaysBefore = reminderDaysBefore
                )

                repository.addProduct(product)
            } catch (e: Exception) {
                _errorMessage.value = "添加商品失败: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateProduct(product: ProductEntity) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                repository.updateProduct(product)
            } catch (e: Exception) {
                _errorMessage.value = "更新商品失败: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteProduct(product: ProductEntity) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                repository.deleteProduct(product)
            } catch (e: Exception) {
                _errorMessage.value = "删除商品失败: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
