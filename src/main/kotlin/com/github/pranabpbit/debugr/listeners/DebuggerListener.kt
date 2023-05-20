package com.github.pranabpbit.debugr.listeners

import com.intellij.execution.process.ProcessEvent
import com.intellij.execution.process.ProcessListener
import com.intellij.xdebugger.XDebugProcess

import com.intellij.xdebugger.XDebuggerManagerListener
import com.intellij.openapi.util.Key
import com.intellij.openapi.diagnostic.Logger
import com.github.pranabpbit.debugr.util.*

import java.time.Instant

class DebuggerListener : XDebuggerManagerListener {
    companion object {
        private val LOG = Logger.getInstance(DebuggerListener::class.java)
    }
    override fun processStarted(debugProcess: XDebugProcess) {
        var sessionStartTime: Instant? = null

//        val session: XDebugSession = debugProcess.session
//        //Update Debugger++ tabs when paused
//        session.addSessionListener(object : XDebugSessionListener {
//            override fun sessionStopped() {
//                ApplicationManager.getApplication().invokeAndWait {
//                    updateDependenciesTabs(session, debugProcess.slice, dataDepPanel, controlDepPanel, graphPanel)
//                }
//            }
//        })

        // Listen to process events to enable/disable line greying
        debugProcess.processHandler.addProcessListener(object : ProcessListener {
            override fun startNotified(processEvent: ProcessEvent) {
                sessionStartTime = Instant.now()
            }

            override fun processTerminated(processEvent: ProcessEvent) {
                //Get the endTime
                val sessionEndTime = Instant.now()
                val sessionData = SessionData(sessionStartTime!!, sessionEndTime)

                // Get the collected breakpoints, DebuggerActions
                val breakpoints = BreakpointListener.getBreakpoints()
                val debuggerActions = DebuggerActionListener.getDebuggerActions()

                //To-do hide this in ENV
                val connectionString = "mongodb+srv://anyuser:anyuser@cluster0.w95f5mz.mongodb.net/?retryWrites=true&w=majority"

                // Create a MongoClient and connect to the database
                val databaseManager = DatabaseManager(connectionString)

                // Send data to the database
                databaseManager.sendDataToDatabase(sessionData, breakpoints, debuggerActions)

                // Close the database connection
                databaseManager.closeConnection()
            }
            override fun onTextAvailable(event: ProcessEvent, outputType: Key<*>) {}
        })
    }
}
data class SessionData(val startTime: Instant, val endTime: Instant)