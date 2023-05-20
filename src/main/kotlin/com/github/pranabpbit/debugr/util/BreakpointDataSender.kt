package com.github.pranabpbit.debugr.util

import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import org.bson.Document
import org.bson.types.ObjectId
import com.github.pranabpbit.debugr.listeners.BreakpointData

class BreakpointDataSender(database: MongoDatabase) {
    private val collection: MongoCollection<Document> = database.getCollection("Breakpoints")

    fun sendBreakpoints(breakpoints: List<BreakpointData>, sessionId: ObjectId) {
        try {
            // To-do: get value of session id at session start and pass it to createDocument
            val documents = breakpoints.map { createDocument(sessionId, it) }
            collection.insertMany(documents)
            println("Breakpoint data uploaded successfully.")
        } catch (e: Exception) {
            println("Error while uploading breakpoint data:")
            e.printStackTrace()
        }
    }

    private fun createDocument(sessionId: ObjectId, breakpointData: BreakpointData): Document {
        val document = Document()
        document.append("_id", ObjectId()) // Generate a unique ObjectId for the _id field
        document.append("sessionId", sessionId)
        document.append("action", breakpointData.action)
        document.append("type", breakpointData.type)
        document.append("lineNumber", breakpointData.lineNumber)
        document.append("filePath", breakpointData.filePath)
        document.append("timestamp", breakpointData.timestamp)
        return document
    }
}
