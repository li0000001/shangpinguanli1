package com.expirytracker.utils

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.provider.CalendarContract
import java.util.*

object CalendarHelper {
    
    fun addEventToCalendar(
        context: Context,
        title: String,
        expiryDate: Long,
        reminderMinutesBefore: Int
    ): Long? {
        return try {
            val contentResolver: ContentResolver = context.contentResolver
            
            val calendarId = getCalendarId(contentResolver) ?: return null
            
            val values = ContentValues().apply {
                put(CalendarContract.Events.DTSTART, expiryDate)
                put(CalendarContract.Events.DTEND, expiryDate + 3600000)
                put(CalendarContract.Events.TITLE, "⏰ $title 即将过期")
                put(CalendarContract.Events.DESCRIPTION, "商品「$title」即将到期，请及时处理")
                put(CalendarContract.Events.CALENDAR_ID, calendarId)
                put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
                put(CalendarContract.Events.HAS_ALARM, 1)
            }
            
            val uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
            val eventId = uri?.lastPathSegment?.toLongOrNull()
            
            eventId?.let { id ->
                addReminderToEvent(contentResolver, id, reminderMinutesBefore)
            }
            
            eventId
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    private fun addReminderToEvent(
        contentResolver: ContentResolver,
        eventId: Long,
        minutesBefore: Int
    ) {
        val reminderValues = ContentValues().apply {
            put(CalendarContract.Reminders.EVENT_ID, eventId)
            put(CalendarContract.Reminders.MINUTES, minutesBefore)
            put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT)
        }
        contentResolver.insert(CalendarContract.Reminders.CONTENT_URI, reminderValues)
    }
    
    fun deleteEventFromCalendar(context: Context, eventId: Long): Boolean {
        return try {
            val contentResolver: ContentResolver = context.contentResolver
            val uri = CalendarContract.Events.CONTENT_URI
            val deleteUri = android.content.ContentUris.withAppendedId(uri, eventId)
            contentResolver.delete(deleteUri, null, null)
            true
        } catch (e: Exception) {
            e.printStackTrace()
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
