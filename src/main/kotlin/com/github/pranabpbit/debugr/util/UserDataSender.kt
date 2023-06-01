package com.github.pranabpbit.debugr.util

import com.github.pranabpbit.debugr.starters.UserData
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import org.bson.Document
import org.bson.types.ObjectId
import java.time.Instant

class UserDataSender(database: MongoDatabase) {

    private val collection: MongoCollection<Document> = database.getCollection("Users")

    fun sendUserData(userData: UserData): ObjectId {
        return try {
            val userDocument = createUserDocument(userData)
            collection.insertOne(userDocument)
            println("User data uploaded successfully.")
            userDocument.getObjectId("_id")
        } catch (e: Exception) {
            println("Error while uploading user data:")
            e.printStackTrace()
            ObjectId("000000000000000000000000") //Identify error in database
        }
    }

    fun updateUserExpEndTime(userId: ObjectId, endTime: Instant) {
        try {
            val filter = Document("_id", userId)
            val update = Document("\$set", Document("endTime", endTime))
            collection.updateOne(filter, update)
            println("User's experiment end time updated successfully. UserID: $userId")
        } catch (e: Exception) {
            println("Error while updating user's experiment end time:")
            e.printStackTrace()
        }
    }

    private fun createUserDocument(userData: UserData): Document {
        val document = Document()
        document.append("_id", ObjectId()) // Generate a unique ObjectId for the _id field
        document.append("username", userData.username)
        document.append("subject", userData.subject)
        document.append("debuggerType", userData.debuggerType)
        document.append("projectLocation", userData.projectLocation)
        document.append("startTime", userData.startTime)
        return document
    }
}
