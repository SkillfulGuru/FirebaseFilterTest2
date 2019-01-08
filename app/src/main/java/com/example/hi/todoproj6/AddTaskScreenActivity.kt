package com.example.hi.todoproj6

import android.app.DatePickerDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.new_task.*
import java.util.*
import java.text.SimpleDateFormat

open class AddTaskScreenActivity : AppCompatActivity() {
    lateinit var _db: DatabaseReference
    var cal = Calendar.getInstance()
    var buttonPickDate: Button? = null
    var dateViewText: TextView?= null
    var textboxSummary: String? = null
    var textboxDetails: String? = null
    var dateToMain: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_task)


        _db = FirebaseDatabase.getInstance().reference

        dateViewText = this.buDateTextbox
        buttonPickDate = this.buPickDate

        dateViewText!!.text = ""

        val dateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int,
                                   dayOfMonth: Int) {
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            }
        }


        buPickDate!!.setOnClickListener{
                DatePickerDialog(this@AddTaskScreenActivity,
                    dateSetListener,
                    // set DatePickerDialog to point to today's date when it loads up
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show()

        }

        buAdd.setOnClickListener {
            val myIntent = Intent(this, MainActivity::class.java)
            if (taskSummary.text.toString() == "" ) {
                Toast.makeText(this, "Please fill in task summary", Toast.LENGTH_SHORT).show()
            }
            else {
                addTask()
                startActivity(myIntent)
            }
        }

        buCancel.setOnClickListener {
            super.finish()
        }



    }

    fun addTask(){

            //Declare and Initialise the Task
            val task = Task.create()

            //Set Task Description and isDone Status
            task.taskDesc = taskSummary.text.toString()
            if (taskSummary.text.toString() != "") {
                task.taskDetails = taskDetails.text.toString()
            }
            else {
                task.taskDetails = ""
            }

            if (dateToMain != null) {
                task.taskDueDate = dateToMain
            }
            else {
                task.taskDueDate = ""
            }
            task.done = false

            //Get the object id for the new task from the Firebase Database
            val newTask = _db.child(Statics.FIREBASE_TASK).push()
            task.objectId = newTask.key

            //Set the values for new task in the firebase using the footer form
            newTask.setValue(task)

            Toast.makeText(this, "New Task added to the List successfully" + task.objectId, Toast.LENGTH_SHORT).show()
        }

    private fun updateDateInView() {

        val myFormat = "MM/dd/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        buDateTextbox!!.text = sdf.format(cal.getTime())
        dateToMain = sdf.format(cal.getTime()).toString()
    }


}
