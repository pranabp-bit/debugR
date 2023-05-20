package com.github.pranabpbit.debugr.util

import com.mongodb.client.MongoClients

import com.github.pranabpbit.debugr.listeners.*

class DatabaseManager(connectionString: String) {
    private val client = MongoClients.create(connectionString)
    private val database = client.getDatabase("test")

    private val breakpointDataSender = BreakpointDataSender(database)
    private val debuggerActionDataSender = DebuggerActionDataSender(database)
    private val sessionDataSender = SessionDataSender(database)

    fun sendDataToDatabase(sessionData: SessionData, breakpoints: List<BreakpointData>, debuggerActions: List<DebuggerActionData>) {
        val sessionId = sessionDataSender.sendSessionData(sessionData)
        breakpointDataSender.sendBreakpoints(breakpoints, sessionId)
        debuggerActionDataSender.sendDebuggerActions(debuggerActions, sessionId)
        BreakpointListener.clearBreakpoints()
        DebuggerActionListener.clearDebuggerActions()
    }

    fun closeConnection() {
        client.close()
    }
}
