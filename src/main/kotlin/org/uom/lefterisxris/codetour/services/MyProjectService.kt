package org.uom.lefterisxris.codetour.services

import com.intellij.openapi.project.Project
import org.uom.lefterisxris.codetour.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
