package com.expirytracker

import android.app.Application
import com.expirytracker.data.database.AppDatabase
import com.expirytracker.data.repository.ProductRepository

class ExpiryTrackerApp : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { ProductRepository(database.productDao(), this) }
}
