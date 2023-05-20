package com.github.pranabpbit.debugr.listeners

import com.intellij.xdebugger.breakpoints.XBreakpoint
import com.intellij.xdebugger.breakpoints.XBreakpointListener
import java.time.Instant

object BreakpointListener : XBreakpointListener<XBreakpoint<*>> {
    private val breakpoints = mutableListOf<BreakpointData>()

    override fun breakpointAdded(breakpoint: XBreakpoint<*>) {
        addBreakpointData("Added", breakpoint)
    }

    override fun breakpointRemoved(breakpoint: XBreakpoint<*>) {
        addBreakpointData("Removed", breakpoint)
    }

    override fun breakpointChanged(breakpoint: XBreakpoint<*>) {
        addBreakpointData("Changed", breakpoint)
    }

    private fun addBreakpointData(action: String, breakpoint: XBreakpoint<*>) {
        val type = breakpoint.javaClass.simpleName
        val lineNumber = breakpoint.sourcePosition?.line?.plus(1) ?: -1 // plus 1 is done in line number to adjust for 1-based indexing of IDE
        val filePath = breakpoint.sourcePosition?.file?.path ?: ""
        val timestamp = Instant.now()

        val breakpointData = BreakpointData(action, type, lineNumber, filePath, timestamp)
        breakpoints.add(breakpointData)

        println("$timestamp - Breakpoint $action $type at line $lineNumber in file $filePath")
    }

    fun getBreakpoints(): List<BreakpointData> {
        return breakpoints.toList()
    }

    fun clearBreakpoints() {
        breakpoints.clear()
    }
}

data class BreakpointData(val action: String, val type: String, val lineNumber: Int, val filePath: String, val timestamp: Instant)
