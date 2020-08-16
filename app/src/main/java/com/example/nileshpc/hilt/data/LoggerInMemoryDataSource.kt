package com.example.nileshpc.hilt.data

import com.example.android.hilt.data.Log
import dagger.hilt.android.scopes.ActivityScoped
import java.util.LinkedList
import javax.inject.Inject
@ActivityScoped
class LoggerInMemoryDataSource @Inject constructor() : LoggerDataSource {

    private val logs = LinkedList<Log>()

    override fun addLog(msg: String) {
        logs.addFirst(Log(msg, System.currentTimeMillis()))
    }

    override fun getAllLogs(callback: (List<Log>) -> Unit) {
        callback(logs)
    }

    override fun removeLogs() {
        logs.clear()
    }
}