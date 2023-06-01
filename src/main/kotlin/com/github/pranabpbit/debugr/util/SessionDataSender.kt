package com.github.pranabpbit.debugr.util

import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import org.bson.Document
import org.bson.types.ObjectId
import com.github.pranabpbit.debugr.listeners.SessionData
import com.github.pranabpbit.debugr.starters.userId

class SessionDataSender(database: MongoDatabase) {
    private val collection: MongoCollection<Document> = database.getCollection("Sessions")

    fun sendSessionData(sessionData: SessionData): ObjectId {
        return try {
            val sessionDocument = createSessionDocument(sessionData)
            collection.insertOne(sessionDocument)
            println("Session data uploaded successfully.")
            sessionDocument.getObjectId("_id")
        } catch (e: Exception) {
            println("Error while uploading session data:")
            e.printStackTrace()
            ObjectId("000000000000000000000000") //Identify error in database
        }
    }

    private fun createSessionDocument(sessionData: SessionData): Document {
        val document = Document()
        document.append("_id", ObjectId()) // Generate a unique ObjectId for the _id field
        document.append("user_id", userId) // Dummy value for user ID
        document.append("start_time", sessionData.startTime)
        document.append("end_time", sessionData.endTime)
        return document
    }
}
