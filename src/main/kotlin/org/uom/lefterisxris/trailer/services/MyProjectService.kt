package org.uom.lefterisxris.trailer.services

import com.intellij.openapi.project.Project
import org.uom.lefterisxris.trailer.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
