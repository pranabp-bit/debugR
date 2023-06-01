package com.github.pranabpbit.debugr.starters

import com.github.pranabpbit.debugr.util.DatabaseManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.project.ProjectManagerListener
import com.intellij.openapi.project.VetoableProjectManagerListener
import com.intellij.openapi.startup.StartupActivity
import org.bson.types.ObjectId
import java.time.Instant

var connectionString: String = System.getenv("CONNECTION_STRING") ?: "mongodb+srv://anyuser:anyuser@cluster0.w95f5mz.mongodb.net/?retryWrites=true&w=majority"
var userId: ObjectId = ObjectId("000000000000000000000000") //Detect error in session data
var desiredProject: Project? = null
class ProjectOpener : StartupActivity {
    private var expStartTime: Instant? = null

    override fun runActivity(project: Project) {
        if (project.basePath != projectLocation || desiredProject!=null) {
            ProjectManager.getInstance().closeAndDispose(project)
            return
        }
        desiredProject = project
        expStartTime = Instant.now()

        val userData = UserData(
            System.getenv("USERNAME"),
            System.getenv("SUBJECT"),
            System.getenv("DEBUGGER_TYPE"),
            projectLocation,
            expStartTime,
            null
        )
        // Add user details to database
        val databaseManager = DatabaseManager(connectionString)
        userId = databaseManager.addUser(userData)

        // Close the database connection
        databaseManager.closeConnection()
    }

    init {
        ProjectManager.getInstance().addProjectManagerListener(ProjectCloseListener())
    }

    private inner class ProjectCloseListener : ProjectManagerListener, VetoableProjectManagerListener {
        override fun projectClosing(project: Project) {
            if (desiredProject == project) {
                // Calculate the experiment end time
                val expEndTime = Instant.now()

                // Update the user data with the experiment end time
                val databaseManager = DatabaseManager(connectionString)
                databaseManager.updateUserExpEndTime(userId, expEndTime)

                // Close the database connection
                databaseManager.closeConnection()
            }
        }

        override fun canClose(project: Project): Boolean {
            return true
        }
    }
}

data class UserData(
    val username: String,
    val subject: String,
    val debuggerType: String,
    val projectLocation: String,
    val startTime: Instant?,
    val endTime: Instant?
)