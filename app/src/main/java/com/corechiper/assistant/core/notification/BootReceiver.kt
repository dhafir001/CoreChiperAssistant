package com.corechiper.assistant.core.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.corechiper.assistant.data.local.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || intent.action == Intent.ACTION_MY_PACKAGE_REPLACED) {
            CoroutineScope(Dispatchers.IO).launch {
                val dao = AppDatabase.getInstance(context).coreDao()
                val activeReminders = dao.getActiveRemindersSnapshot()
                val now = System.currentTimeMillis()
                activeReminders.filter { it.triggerAtMillis > now }.forEach { reminder ->
                    NotificationHelper.scheduleReminderWork(
                        context = context,
                        reminderId = reminder.id,
                        title = reminder.title,
                        moduleType = reminder.moduleType,
                        triggerAtMillis = reminder.triggerAtMillis
                    )
                }
            }
        }
    }
}
