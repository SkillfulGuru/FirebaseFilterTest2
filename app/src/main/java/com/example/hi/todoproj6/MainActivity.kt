package com.example.hi.todoproj6

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import com.google.firebase.database.*
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.io.*


class MainActivity : AppCompatActivity(), TaskRowListener {
    lateinit var _db: DatabaseReference
    var _taskList: MutableList<Task>? = null
    lateinit var _adapter: TaskAdapter
    var newTaskDesc: String? = null
    val gson = GsonBuilder().setPrettyPrinting().create()
    var taskList: MutableList<readJsonTaskList>?= null
    var positionToOtherActivity: String?= null

    //var list = findViewById<ListView>(R.id.listviewTask)



    var _taskListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            loadTaskList(dataSnapshot)
            readJSONFile()
        }


        override fun onCancelled(databaseError: DatabaseError) {
            // Getting Item failed, log a message
            Log.w("MainActivity", "loadItem:onCancelled", databaseError.toException())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        _db = FirebaseDatabase.getInstance().reference
        _taskList = mutableListOf<Task>()

        _adapter = TaskAdapter(this, _taskList!!)

        listviewTask!!.setAdapter(_adapter)


        listviewTask.setOnItemClickListener { parent, view, position, id ->
            positionToOtherActivity = position.toString()
            val alert = AlertDialog.Builder(this)
            alert.setTitle("Task")
            val taskDescInfo = taskList!!.get(position).taskDesc
            var taskDueDateInfo = taskList!!.get(position).taskDueDate
            var taskDetailsInfo = taskList!!.get(position).taskDetails
            if (taskDueDateInfo == "") {taskDueDateInfo = "none"}
            if (taskDetailsInfo == "") {taskDetailsInfo = "none"}
            alert.setCancelable(false)
            var message = "Task Summary: " + taskDescInfo + "\n \nTask Due Date: " + taskDueDateInfo + "\n \nTask Details: " + taskDetailsInfo
            alert.setMessage(message)
            alert.setPositiveButton("Edit") { dialog, positiveButton ->
                val myIntent = Intent(this, EditTaskScreenActivity::class.java)
                myIntent.putExtra("position", position.toString())
                myIntent.putExtra("objectID", taskList!!.get(position).objectId)
                startActivity(myIntent)
            }
            alert.setNegativeButton("Back") { dialog, negativeButton ->
                dialog.cancel()
            }
            alert.setNeutralButton("Delete") {_,_->
                onTaskDelete(taskList!!.get(position).objectId)
            }
            alert.show()

        }

        //mAdminDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Admins")
        fab.setOnClickListener {
            onAddClick()
        }

        _db.orderByKey().addValueEventListener(_taskListener)
    }

    fun readJSONFile() {
        //val index = numbers.add("1".toInt() - "0".toInt())
        val inputStream = File("/data/user/0/com.example.hi.todoproj6/files/test.json") .inputStream().readBytes().toString(Charsets.UTF_8)

       taskList = gson.fromJson(inputStream, object: TypeToken<List<readJsonTaskList>>() {}.type)
    }


    fun onAddClick() {
        val myIntent = Intent(this, AddTaskScreenActivity::class.java)
        startActivity(myIntent)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun loadTaskList(dataSnapshot: DataSnapshot) {
        Log.d("MainActivity", "loadTaskList")

        val tasks = dataSnapshot.children.iterator()


        //Check if current database contains any collection
        if (tasks.hasNext()) {

            _taskList!!.clear()


            val listIndex = tasks.next()
            val itemsIterator = listIndex.children.iterator()

            //check if the collection has any task or not
            while (itemsIterator.hasNext()) {

                //get current task
                val currentItem = itemsIterator.next()
                val task = Task.create()

                //get current data in a map
                val map = currentItem.getValue() as HashMap<String, Any>

                //key will return the Firebase ID
                task.objectId = currentItem.key
                task.done = map.get("done") as Boolean?
                task.taskDesc = map.get("taskDesc") as String?
                task.taskDetails = map.get("taskDetails") as String?
                task.taskDueDate = map.get("taskDueDate") as String?
                _taskList!!.add(task)
            }
        }
        writeTaskListToFile()
        //val w1 = _taskList!!.get(0)
        //Log.d("MainActivity1", _taskList!!.get(index = 1)..toString())

        _adapter.notifyDataSetChanged()



    }

    fun writeTaskListToFile() {
        val dir = filesDir.toString()
        //Log.d("MainActivity", dir)
        val taskListString: String = gson.toJson(_taskList)
        File(dir + "/test.json").writeText(taskListString)
    }

    override fun onTaskChange(objectId: String, isDone: Boolean) {
        val task = _db.child(Statics.FIREBASE_TASK).child(objectId)
        task.child("done").setValue(isDone)
        writeTaskListToFile()
        _adapter.notifyDataSetChanged()
        listviewTask!!.setAdapter(_adapter)

    }


    override fun onTaskDelete(objectId: String) {
        val task = _db.child(Statics.FIREBASE_TASK).child(objectId)
        task.removeValue()
        writeTaskListToFile()
        _adapter.notifyDataSetChanged()
        listviewTask!!.setAdapter(_adapter)
    }

    override fun editItemDialog(objectId: String, taskDesc: String) {
        val alert = AlertDialog.Builder(this)
        val itemEditText = EditText(this)

        val taskChild = FirebaseDatabase.getInstance().getReference().child("task").child(objectId)

        alert.setMessage("Enter new task")
        alert.setTitle("Edit task")
        alert.setView(itemEditText)


        alert.setPositiveButton("Change") { dialog, positiveButton ->
            newTaskDesc = itemEditText.text.toString()
            if (newTaskDesc != "") {
                taskChild.child("taskDesc").setValue(newTaskDesc)
                _adapter.notifyDataSetChanged()
            }


        }
        alert.show()


    }
}


