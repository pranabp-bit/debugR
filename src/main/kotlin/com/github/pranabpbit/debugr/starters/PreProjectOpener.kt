package com.github.pranabpbit.debugr.starters

import com.intellij.openapi.application.ApplicationActivationListener
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.wm.IdeFrame

val projectLocation: String = System.getenv("PROJECT_LOCATION") ?: "/default/project/location"

class PreProjectOpener : ApplicationActivationListener {
    private var projectTriggered: Boolean = false

    override fun applicationActivated(ideFrame: IdeFrame) {
        if (!projectTriggered) {
            val projectDir = LocalFileSystem.getInstance().findFileByPath(projectLocation)
            if (projectDir != null) {
                ProjectManager.getInstance().loadAndOpenProject(projectDir.path)
                projectTriggered = true
            }
        }
    }
}