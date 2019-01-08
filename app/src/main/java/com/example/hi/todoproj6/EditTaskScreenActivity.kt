package com.example.hi.todoproj6

import android.app.DatePickerDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.DatePicker
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_edit_task_screen.*
import kotlinx.android.synthetic.main.new_task.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class EditTaskScreenActivity : AppCompatActivity() {
    var taskList: MutableList<readJsonTaskList>?= null
    val gson = GsonBuilder().setPrettyPrinting().create()
    var cal = Calendar.getInstance()
    var newDate:String ?= null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_task_screen)

        readJSONFile()

        var taskPosition = intent.getStringExtra("position").toInt()
        var taskObjectID = intent.getStringExtra("objectID")
        val taskChild = FirebaseDatabase.getInstance().getReference().child("task").child(taskObjectID)

        editTaskSummary.setText(taskList!!.get(taskPosition).taskDesc)
        editBuDateTextbox.text = taskList!!.get(taskPosition).taskDueDate

        val dateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int,
                                   dayOfMonth: Int) {
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            }
        }


        editBuPickDate!!.setOnClickListener{
            DatePickerDialog(this@EditTaskScreenActivity,
                dateSetListener,
                // set DatePickerDialog to point to today's date when it loads up
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show()

        }

        editBuEdit.setOnClickListener {
            val oldDate = taskList!!.get(taskPosition).taskDueDate
            val oldDetails = taskList!!.get(taskPosition).taskDetails
            if (newDate == null){newDate = ""}
            if (editTaskSummary.text.toString() != "") {
                taskChild.child("taskDesc").setValue(editTaskSummary.text.toString())
                if (editBuDateTextbox.text.toString() != oldDate) {
                    if (editBuDateTextbox.text.toString() != "") {
                        taskChild.child("taskDueDate").setValue(newDate)
                    }
                    else {
                        taskChild.child("taskDueDate").setValue("")
                    }
                }
                if (editTaskDetails.text.toString() != oldDetails) {
                    if (editTaskDetails.text.toString() != "") {
                        taskChild.child("taskDetails").setValue(editTaskDetails.text.toString())
                    }
                    else {
                        taskChild.child("taskDetails").setValue("")
                    }
                }

            }
            else {
                Toast.makeText(this, "Please fill in the task summary", Toast.LENGTH_SHORT).show()
            }
            val myIntent = Intent(this, MainActivity::class.java)
            startActivity(myIntent)
        }


        editBuCancel.setOnClickListener {
            super.finish()
        }

    }


    fun readJSONFile() {
        //val index = numbers.add("1".toInt() - "0".toInt())
        val inputStream = File("/data/user/0/com.example.hi.todoproj6/files/test.json") .inputStream().readBytes().toString(Charsets.UTF_8)

        taskList = gson.fromJson(inputStream, object: TypeToken<List<readJsonTaskList>>() {}.type)
        //val testindex = inputStream.task
        //Log.d("MainActivity", taskList!!.get(0).objectId)
        //Log.d("MainActivity",taskList.get(1).objectId)
    }

    private fun updateDateInView() {

        val myFormat = "MM/dd/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        editBuDateTextbox.text = sdf.format(cal.getTime())
        newDate = sdf.format(cal.getTime()).toString()

    }
}
