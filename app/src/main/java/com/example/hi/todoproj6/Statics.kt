package com.example.hi.todoproj6

object Statics {
    var objectId: String?= null
    @JvmStatic val FIREBASE_TASK: String = "task"
    fun getTaskDeepDetailsPopup(position: Int, _taskList:MutableList<Task>) {
        objectId = _taskList.get(position).objectId as String
        val itemText = _taskList.get(position).taskDesc as String
        val done = _taskList.get(position).done as Boolean
        val taskDetails = _taskList.get(position).taskDetails as String
        val duedateIn = _taskList.get(position).taskDueDate as String




    }
}