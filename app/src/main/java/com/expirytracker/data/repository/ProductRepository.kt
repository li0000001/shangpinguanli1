package com.expirytracker.data.repository

import android.content.Context
import com.expirytracker.data.database.ProductDao
import com.expirytracker.data.database.ProductEntity
import com.expirytracker.utils.CalendarHelper
import kotlinx.coroutines.flow.Flow

class ProductRepository(
    private val productDao: ProductDao,
    private val context: Context
) {
    fun getAllProducts(): Flow<List<ProductEntity>> = productDao.getAllProducts()

    suspend fun getProductById(id: Long): ProductEntity? = productDao.getProductById(id)

    suspend fun addProduct(product: ProductEntity): Long {
        val productId = productDao.insertProduct(product)
        
        val eventId = CalendarHelper.addEventToCalendar(
            context,
            product.name,
            product.expiryDate,
            product.reminderDaysBefore,
            product.reminderMethod
        )
        
        if (eventId != null) {
            val updatedProduct = product.copy(id = productId, calendarEventId = eventId)
            productDao.updateProduct(updatedProduct)
        }
        
        return productId
    }

    suspend fun updateProduct(product: ProductEntity) {
        product.calendarEventId?.let { eventId ->
            CalendarHelper.deleteEventFromCalendar(context, eventId)
        }
        
        val newEventId = CalendarHelper.addEventToCalendar(
            context,
            product.name,
            product.expiryDate,
            product.reminderDaysBefore,
            product.reminderMethod
        )
        
        val updatedProduct = product.copy(calendarEventId = newEventId)
        productDao.updateProduct(updatedProduct)
    }

    suspend fun deleteProduct(product: ProductEntity) {
        product.calendarEventId?.let { eventId ->
            CalendarHelper.deleteEventFromCalendar(context, eventId)
        }
        productDao.deleteProduct(product)
    }

    suspend fun deleteProductById(id: Long) {
        val product = productDao.getProductById(id)
        product?.let { deleteProduct(it) }
    }
}
