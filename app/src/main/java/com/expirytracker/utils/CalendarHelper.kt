package com.expirytracker.utils

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.provider.CalendarContract
import android.util.Log
import java.util.*

object CalendarHelper {
    private const val TAG = "CalendarHelper"
    
    fun addEventToCalendar(
        context: Context,
        title: String,
        expiryDate: Long,
        reminderDaysBefore: Int,
        reminderMethod: String = "ALARM"
    ): Long? {
        return try {
            val contentResolver: ContentResolver = context.contentResolver
            
            val calendarId = getCalendarId(contentResolver) ?: run {
                Log.e(TAG, "无法获取日历ID，请确保已授予日历权限")
                return null
            }
            
            val reminderDate = expiryDate - (reminderDaysBefore * 24 * 60 * 60 * 1000L)
            val daysText = if (reminderDaysBefore == 0) "今天" else "${reminderDaysBefore}天后"
            
            val values = ContentValues().apply {
                put(CalendarContract.Events.DTSTART, reminderDate)
                put(CalendarContract.Events.DTEND, reminderDate + 3600000)
                put(CalendarContract.Events.TITLE, "⏰ $title 将在${daysText}过期")
                put(CalendarContract.Events.DESCRIPTION, "商品「$title」将在${daysText}到期，请及时处理")
                put(CalendarContract.Events.CALENDAR_ID, calendarId)
                put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
                put(CalendarContract.Events.HAS_ALARM, 1)
            }
            
            val uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
            val eventId = uri?.lastPathSegment?.toLongOrNull()
            
            if (eventId != null) {
                Log.d(TAG, "日历事件创建成功，事件ID: $eventId")
                val reminderAdded = addReminderToEvent(
                    contentResolver, 
                    eventId, 
                    reminderMethod
                )
                if (!reminderAdded) {
                    Log.e(TAG, "添加提醒失败，但事件已创建")
                }
            } else {
                Log.e(TAG, "创建日历事件失败")
            }
            
            eventId
        } catch (e: SecurityException) {
            Log.e(TAG, "没有日历权限: ${e.message}", e)
            null
        } catch (e: Exception) {
            Log.e(TAG, "创建日历事件时出错: ${e.message}", e)
            null
        }
    }
    
    private fun addReminderToEvent(
        contentResolver: ContentResolver,
        eventId: Long,
        reminderMethod: String = "ALARM"
    ): Boolean {
        return try {
            val method = when (reminderMethod.uppercase()) {
                "ALARM" -> CalendarContract.Reminders.METHOD_ALARM
                "NOTIFICATION" -> CalendarContract.Reminders.METHOD_ALERT
                "ALERT" -> CalendarContract.Reminders.METHOD_ALERT
                else -> {
                    Log.w(TAG, "未知的提醒方式: $reminderMethod, 使用默认的闹钟提醒")
                    CalendarContract.Reminders.METHOD_ALARM
                }
            }
            
            Log.d(TAG, "添加提醒 - 事件ID: $eventId, 提醒方式: $reminderMethod (方法值: $method), 提前时间: 60分钟")
            
            val reminderValues = ContentValues().apply {
                put(CalendarContract.Reminders.EVENT_ID, eventId)
                put(CalendarContract.Reminders.MINUTES, 60)
                put(CalendarContract.Reminders.METHOD, method)
            }
            
            val uri = contentResolver.insert(CalendarContract.Reminders.CONTENT_URI, reminderValues)
            val success = uri != null
            
            if (success) {
                Log.d(TAG, "提醒添加成功，使用方式: ${if (method == CalendarContract.Reminders.METHOD_ALARM) "闹钟" else "通知"}")
            } else {
                Log.e(TAG, "提醒添加失败，insert返回null")
            }
            
            success
        } catch (e: Exception) {
            Log.e(TAG, "添加提醒时出错: ${e.message}", e)
            false
        }
    }
    
    fun deleteEventFromCalendar(context: Context, eventId: Long): Boolean {
        return try {
            val contentResolver: ContentResolver = context.contentResolver
            val uri = CalendarContract.Events.CONTENT_URI
            val deleteUri = android.content.ContentUris.withAppendedId(uri, eventId)
            val deletedRows = contentResolver.delete(deleteUri, null, null)
            
            if (deletedRows > 0) {
                Log.d(TAG, "成功删除日历事件，事件ID: $eventId")
                true
            } else {
                Log.w(TAG, "未找到要删除的日历事件，事件ID: $eventId")
                false
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "删除日历事件时没有权限: ${e.message}", e)
            false
        } catch (e: Exception) {
            Log.e(TAG, "删除日历事件时出错: ${e.message}", e)
            false
        }
    }
    
    private fun getCalendarId(contentResolver: ContentResolver): Long? {
        val projection = arrayOf(
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME
        )
        
        val cursor = contentResolver.query(
            CalendarContract.Calendars.CONTENT_URI,
            projection,
            null,
            null,
            null
        )
        
        cursor?.use {
            if (it.moveToFirst()) {
                val idIndex = it.getColumnIndex(CalendarContract.Calendars._ID)
                if (idIndex >= 0) {
                    return it.getLong(idIndex)
                }
            }
        }
        
        return null
    }
}
