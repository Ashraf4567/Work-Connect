package com.example.workconnect.data.model

data class Project(
    val id: String? = null,
    val title: String? = null,
    val background: Int? = null,
    val description: String? = null,
    val tasks : List<Task?>? = null
){
    companion object{
        val PROJECTS_COLLECTION_NAME = "projects"
    }
}
