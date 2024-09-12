package com.example.maheshkumawat

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.roomdb.R
import com.example.roomdb.databinding.ActivityAddEditNoteBinding

const val EXTRA_ID = " com.huawei.todolist.EXTRA_ID"
const val EXTRA_TITLE = " com.huawei.todolist.EXTRA_TITLE"
const val EXTRA_DESCRIPTION = " com.huawei.todolist.EXTRA_DESCRIPTION"
const val EXTRA_PRIORITY = " com.huawei.todolist.EXTRA_PRIORITY"

class AddEditNoteActivity : AppCompatActivity() {

    private lateinit var mode : Mode

    private var noteId: Int = -1
    lateinit var binding: ActivityAddEditNoteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_edit_note)
        binding.numberPickerPriority.minValue = 1
        binding.numberPickerPriority.maxValue = 10
        setSupportActionBar(binding.myToolbar);
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)

        noteId = intent.getIntExtra(EXTRA_ID, -1)
        mode = if(noteId == -1) Mode.AddNote
        else Mode.EditNote

        when(mode) {
            Mode.AddNote -> title = "Add Note"
            Mode.EditNote -> {
                title = "Edit Note"
                binding.etTitle.setText(intent.getStringExtra(EXTRA_TITLE))
                binding.etDesc.setText(intent.getStringExtra(EXTRA_DESCRIPTION))
                binding.numberPickerPriority.value = intent.getIntExtra(EXTRA_PRIORITY, -1)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.add_note_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save_note -> {
                saveNote()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveNote() {
        val title = binding.etTitle.text.toString()
        val desc = binding.etDesc.text.toString()
        val priority = binding.numberPickerPriority.value

        if(title.isEmpty() || desc.isEmpty()) {
            Toast.makeText(this, "please insert title and description", Toast.LENGTH_SHORT).show()
            return
        }

        val data = Intent()
        // only if note ID was provided i.e. we are editing
        if(noteId != -1)
            data.putExtra(EXTRA_ID, noteId)
        data.putExtra(EXTRA_TITLE, title)
        data.putExtra(EXTRA_DESCRIPTION, desc)
        data.putExtra(EXTRA_PRIORITY, priority)

        setResult(Activity.RESULT_OK, data)
        finish()
    }

    private sealed class Mode {
        object AddNote : Mode()
        object EditNote : Mode()

    }
}
