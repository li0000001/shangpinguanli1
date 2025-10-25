package com.expirytracker.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
    private val displayDateFormat = SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA)
    
    fun formatDate(timestamp: Long): String {
        return displayDateFormat.format(Date(timestamp))
    }
    
    fun formatDateShort(timestamp: Long): String {
        return dateFormat.format(Date(timestamp))
    }
    
    fun parseDate(dateString: String): Long? {
        return try {
            dateFormat.parse(dateString)?.time
        } catch (e: Exception) {
            null
        }
    }
    
    fun calculateExpiryDate(productionDate: Long, shelfLifeDays: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = productionDate
        calendar.add(Calendar.DAY_OF_YEAR, shelfLifeDays)
        return calendar.timeInMillis
    }
    
    fun getDaysUntilExpiry(expiryDate: Long): Int {
        val now = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        
        val expiry = Calendar.getInstance().apply {
            timeInMillis = expiryDate
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        
        val diffInMillis = expiry.timeInMillis - now.timeInMillis
        return (diffInMillis / (1000 * 60 * 60 * 24)).toInt()
    }
    
    fun getExpiryStatus(expiryDate: Long): ExpiryStatus {
        val daysUntil = getDaysUntilExpiry(expiryDate)
        return when {
            daysUntil < 0 -> ExpiryStatus.EXPIRED
            daysUntil == 0 -> ExpiryStatus.EXPIRING_TODAY
            daysUntil <= 3 -> ExpiryStatus.EXPIRING_SOON
            daysUntil <= 7 -> ExpiryStatus.WARNING
            else -> ExpiryStatus.FRESH
        }
    }
}

enum class ExpiryStatus {
    EXPIRED,
    EXPIRING_TODAY,
    EXPIRING_SOON,
    WARNING,
    FRESH
}
