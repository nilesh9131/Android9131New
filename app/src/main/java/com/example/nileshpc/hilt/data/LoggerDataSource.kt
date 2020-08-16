package com.example.nileshpc.hilt.data

import com.example.android.hilt.data.Log

// Common interface for Logger data sources.
interface LoggerDataSource {
    fun addLog(msg: String)
    fun getAllLogs(callback: (List<Log>) -> Unit)
    fun removeLogs()
}