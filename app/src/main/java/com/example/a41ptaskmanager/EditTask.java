package com.example.a41ptaskmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class EditTask extends AppCompatActivity {

    Button btn_save, btn_back, btn_delete;
    EditText et_taskName, et_taskDescription, et_taskDueDate;
    Switch sw_isComplete;
    int taskId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_task);

        et_taskName = findViewById(R.id.et_taskName);
        et_taskDescription = findViewById(R.id.et_taskDescription);
        et_taskDueDate = findViewById(R.id.et_taskDueDate);
        sw_isComplete = findViewById(R.id.sw_isComplete);
        btn_save = findViewById(R.id.btn_save);
        btn_back = findViewById(R.id.btn_back);
        btn_delete = findViewById(R.id.btn_delete);

        // Pull the task from the database
        Intent intent = getIntent();
        taskId = intent.getIntExtra("ID", 1);
        DatabaseHelper dbHelper = new DatabaseHelper(EditTask.this);
        TaskModel taskModel = dbHelper.getTaskById(taskId);

        // And populate our fields with details from this task
        et_taskName.setText(taskModel.getName());
        et_taskDescription.setText(taskModel.getDescription());
        et_taskDueDate.setText(taskModel.getDueDate());
        sw_isComplete.setChecked(taskModel.isComplete());

        // Set functionality for our buttons
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditTask.this, MainActivity.class);
                startActivity(intent);
            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper.deleteTaskById(taskId);
                Intent intent = new Intent(EditTask.this, MainActivity.class);
                startActivity(intent);
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Get all the fields the user has provided
                String updatedName = et_taskName.getText().toString();
                String updatedDescription = et_taskDescription.getText().toString();
                String updatedDueDateString = et_taskDueDate.getText().toString();
                boolean updatedIsComplete = sw_isComplete.isChecked();

                // Validate the name and due date fields
                String dateMatchRegex = "^\\d{4}\\-(0[1-9]|1[012])\\-(0[1-9]|[12][0-9]|3[01])$";
                if (updatedName.equals("")) {
                    Toast.makeText(EditTask.this, "Task name is mandatory", Toast.LENGTH_SHORT).show();
                }
                else if (!updatedDueDateString.matches(dateMatchRegex)) {
                    Toast.makeText(EditTask.this, "Date must be in format yyyy-mm-dd", Toast.LENGTH_SHORT).show();
                }
                else {
                    // If everything was OK, update the task in the database.
                    TaskModel updatedTask = new TaskModel(taskId, updatedName, updatedDescription, updatedDueDateString, updatedIsComplete);
                    boolean success = dbHelper.updateTask(updatedTask);
                    if (success) {
                        Intent intent = new Intent(EditTask.this, MainActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });

    }
}