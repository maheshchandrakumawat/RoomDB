package com.example.maheshkumawat

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.LEFT
import androidx.recyclerview.widget.ItemTouchHelper.RIGHT
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.roomdb.R
import com.example.roomdb.databinding.ActivityMainBinding
import com.huawei.todolist.NoteAdapter

const val ADD_NOTE_REQUEST = 1
const val EDIT_NOTE_REQUEST = 2

class MainActivity : AppCompatActivity() {

    private lateinit var vm: NoteViewModel
    private lateinit var adapter: NoteAdapter
    private lateinit var mainActivityMainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        mainActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setUpRecyclerView()

        setUpListeners()

        vm = ViewModelProviders.of(this)[NoteViewModel::class.java]

        vm.getAllNotes().observe(this, Observer {
            Log.i("Notes observed", "$it")

            adapter.submitList(it)
        })

    }

    private fun setUpListeners() {
        mainActivityMainBinding.buttonAddNote.setOnClickListener {
            val intent = Intent(this, AddEditNoteActivity::class.java)
            startActivityForResult(intent, ADD_NOTE_REQUEST)
        }

        // swipe listener
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, LEFT or RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val note = adapter.getNoteAt(viewHolder.adapterPosition)
                vm.delete(note)
            }

        }).attachToRecyclerView(mainActivityMainBinding.recyclerView)
    }

    private fun setUpRecyclerView() {
        mainActivityMainBinding.recyclerView.layoutManager = LinearLayoutManager(this)
        mainActivityMainBinding.recyclerView.setHasFixedSize(true)

        adapter = NoteAdapter {
            val intent = Intent(this, AddEditNoteActivity::class.java)
            intent.putExtra(EXTRA_ID, it.id)
            intent.putExtra(EXTRA_TITLE, it.title)
            intent.putExtra(EXTRA_DESCRIPTION, it.description)
            intent.putExtra(EXTRA_PRIORITY, it.priority)
            startActivityForResult(intent, EDIT_NOTE_REQUEST)
        }
        mainActivityMainBinding.recyclerView.adapter = adapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(data != null && requestCode == ADD_NOTE_REQUEST && resultCode == Activity.RESULT_OK) {
            val title: String = data.getStringExtra(EXTRA_TITLE).toString()
            val description: String =
                data.getStringExtra(EXTRA_DESCRIPTION).toString()
            val priority: Int = data.getIntExtra(EXTRA_PRIORITY, -1)
            vm.insert(Note(title, description, priority))
            Toast.makeText(this, "Note inserted!", Toast.LENGTH_SHORT).show()

        } else if(data != null && requestCode == EDIT_NOTE_REQUEST && resultCode == Activity.RESULT_OK) {
            val id = data.getIntExtra(EXTRA_ID, -1)
            if(id == -1) {
                Toast.makeText(this, "Note couldn't be updated!", Toast.LENGTH_SHORT).show()
                return
            }
            val title: String = data.getStringExtra(EXTRA_TITLE).toString()
            val description: String =
                data.getStringExtra(EXTRA_DESCRIPTION).toString()
            val priority: Int = data.getIntExtra(EXTRA_PRIORITY, -1)
            vm.update(Note(title, description, priority, id))
            Toast.makeText(this, "Note updated!", Toast.LENGTH_SHORT).show()

        } else {
            Toast.makeText(this, "Note not saved!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.delete_all_notes -> {
                vm.deleteAllNotes()
                Toast.makeText(this, "All notes deleted!", Toast.LENGTH_SHORT).show()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
