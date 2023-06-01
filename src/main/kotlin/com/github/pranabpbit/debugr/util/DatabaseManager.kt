package com.github.pranabpbit.debugr.util

import com.mongodb.client.MongoClients

import com.github.pranabpbit.debugr.listeners.*
import org.bson.types.ObjectId
import java.time.Instant
import com.github.pranabpbit.debugr.starters.UserData

class DatabaseManager(connectionString: String) {
    private val client = MongoClients.create(connectionString)
    private val database = client.getDatabase("test1")

    private val breakpointDataSender = BreakpointDataSender(database)
    private val debuggerActionDataSender = DebuggerActionDataSender(database)
    private val sessionDataSender = SessionDataSender(database)
    private val userDataSender = UserDataSender(database)
    fun sendDataToDatabase(sessionData: SessionData, breakpoints: List<BreakpointData>, debuggerActions: List<DebuggerActionData>) {
        val sessionId = sessionDataSender.sendSessionData(sessionData)
        breakpointDataSender.sendBreakpoints(breakpoints, sessionId)
        debuggerActionDataSender.sendDebuggerActions(debuggerActions, sessionId)
        BreakpointListener.clearBreakpoints()
        DebuggerActionListener.clearDebuggerActions()
    }

    fun addUser(userData: UserData): ObjectId {
        return userDataSender.sendUserData(userData)
    }

    fun updateUserExpEndTime(userId: ObjectId?, endTime: Instant) {
        if (userId != null) {
            val userDataSender = UserDataSender(database)
            userDataSender.updateUserExpEndTime(userId, endTime)
        }
    }

    fun closeConnection() {
        client.close()
    }
}
