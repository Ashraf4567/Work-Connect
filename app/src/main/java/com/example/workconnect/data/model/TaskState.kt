package com.example.workconnect.data.model

enum class TaskCompletionState(val state: String) {
    NEW("New"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed")

}


enum class TaskAcceptanceStatus(val state: String) {
    PENDING("Pending"),
    ACCEPTED("Accepted"),
    REJECTED("Rejected")
}