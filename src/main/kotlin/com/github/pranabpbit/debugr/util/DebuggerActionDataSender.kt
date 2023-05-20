package com.github.pranabpbit.debugr.util

import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import org.bson.Document
import org.bson.types.ObjectId
import com.github.pranabpbit.debugr.listeners.DebuggerActionData

class DebuggerActionDataSender(database: MongoDatabase) {
    private val collection: MongoCollection<Document> = database.getCollection("DebuggerActions")

    fun sendDebuggerActions(debuggerActions: List<DebuggerActionData>, sessionId: ObjectId) {
        try {
            // To-do: get value of session id at session start and pass it to createDocument
            val documents = debuggerActions.map { createDocument(sessionId, it) }
            collection.insertMany(documents)
            println("DebuggerAction data uploaded successfully.")
        } catch (e: Exception) {
            println("Error while uploading DebuggerAction data:")
            e.printStackTrace()
        }
    }

    private fun createDocument(sessionId: ObjectId, debuggerActionData: DebuggerActionData): Document {
        val document = Document()
        document.append("_id", ObjectId()) // Generate a unique ObjectId for the _id field
        document.append("sessionId", sessionId)
        document.append("action", debuggerActionData.action)
        document.append("lineNumber", debuggerActionData.lineNumber)
        document.append("filePath", debuggerActionData.filePath)
        document.append("timestamp", debuggerActionData.timestamp)
        return document
    }
}
