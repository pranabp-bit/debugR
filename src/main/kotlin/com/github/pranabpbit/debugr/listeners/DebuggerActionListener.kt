package com.github.pranabpbit.debugr.listeners

import com.intellij.debugger.actions.DebuggerAction
import com.intellij.openapi.actionSystem.AnActionResult
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.ex.AnActionListener
import com.intellij.xdebugger.XDebuggerManager
import com.intellij.xdebugger.XSourcePosition
import com.intellij.xdebugger.impl.XDebugSessionImpl
import java.time.Instant

object DebuggerActionListener : AnActionListener {
    private val debuggerActions = mutableListOf<DebuggerActionData>()

    override fun beforeActionPerformed(action: AnAction, event: AnActionEvent) {
        // This method is called before the action is performed

        // Check if the action is a debugger action
        if (isDebuggerAction(action)) {
            // Get the current debug session
            val debugSession = getCurrentDebugSession(event)
            if (debugSession != null) {
                // Get the current location
                val location = getCurrentLocation(debugSession)

                if (location != null) {
                    // Get the timestamp
                    val timestamp = Instant.now()

                    // Get the action ID or class name
                    val actionId = action.javaClass.simpleName
                    // plus 1 is done in line number to adjust for 1-based indexing of IDE
                    val debuggerActionData = DebuggerActionData(actionId, location.line + 1, location.file.path, timestamp)
                    debuggerActions.add(debuggerActionData)

                    // Print the timestamp, line number, and action ID when a debugger action is executed
                    println("Debugger action executed at $timestamp at line ${location.line + 1} in file ${location.file.path}, action: $actionId")
                }
            }
        }
    }

    override fun afterActionPerformed(action: AnAction, event: AnActionEvent, result: AnActionResult) {
        // This method is called after the action is performed
    }

    private fun isDebuggerAction(action: AnAction): Boolean {
        // Identify the debugger actions based on their class or ID
        val actionPackage = action.javaClass.`package`
        return (actionPackage != null && actionPackage.name.startsWith("com.intellij.xdebugger.impl.actions")) ||
                action is DebuggerAction
    }

    private fun getCurrentDebugSession(event: AnActionEvent): XDebugSessionImpl? {
        // Get the project from the event
        val project = event.project
        if (project != null) {
            // Get the active debug session
            val activeSession = XDebuggerManager.getInstance(project).currentSession
            if (activeSession is XDebugSessionImpl) {
                return activeSession
            }
        }
        return null
    }

    private fun getCurrentLocation(debugSession: XDebugSessionImpl): XSourcePosition? {
        val currentFrame = debugSession.currentStackFrame
        if (currentFrame != null) {
            return currentFrame.sourcePosition
        }
        return null
    }

    fun getDebuggerActions(): List<DebuggerActionData> {
        return debuggerActions.toList()
    }

    fun clearDebuggerActions() {
        debuggerActions.clear()
    }
}

data class DebuggerActionData(val action: String, val lineNumber: Int, val filePath: String, val timestamp: Instant)
