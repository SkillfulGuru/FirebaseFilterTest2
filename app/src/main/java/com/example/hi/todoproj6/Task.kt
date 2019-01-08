package com.example.hi.todoproj6

class Task {
    companion object Factory {
        fun create(): Task = Task()
    }

    var objectId: String? = null
    var taskDesc: String? = null
    var done: Boolean? = false
    var taskDetails: String? = null
    var taskDueDate: String? = null
}