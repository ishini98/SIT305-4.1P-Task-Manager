package com.example.a41ptaskmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreateTask extends AppCompatActivity {

    Button btn_save;
    Button btn_back;
    EditText et_taskName, et_taskDescription, et_taskDueDate;

    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-mm-yyyy", Locale.ENGLISH);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_task);

        btn_save = findViewById(R.id.btn_save);
        btn_back = findViewById(R.id.btn_back);
        et_taskName = findViewById(R.id.et_taskName);
        et_taskDescription = findViewById(R.id.et_taskDescription);
        et_taskDueDate = findViewById(R.id.et_taskDueDate);

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaskModel taskModel;

                // Get all the fields the user has provided
                String taskName = et_taskName.getText().toString();
                String taskDescription = et_taskDescription.getText().toString();
                String taskDueDateString = et_taskDueDate.getText().toString();

                // Validate the name and due date fields
                String dateMatchRegex = "^\\d{4}\\-(0[1-9]|1[012])\\-(0[1-9]|[12][0-9]|3[01])$";
                if (taskName.equals("")) {
                    Toast.makeText(CreateTask.this, "Task name is mandatory", Toast.LENGTH_SHORT).show();
                }
                else if (!taskDueDateString.matches(dateMatchRegex)) {
                    Toast.makeText(CreateTask.this, "Date must be in format yyyy-mm-dd", Toast.LENGTH_SHORT).show();
                }
                else {
                    // If everything was OK, add the task to the database
                    DatabaseHelper dbHelper = new DatabaseHelper(CreateTask.this);
                    taskModel = new TaskModel(taskName, taskDescription, taskDueDateString);
                    boolean success = dbHelper.addTask(taskModel);
                    if (success) {
                        Intent intent = new Intent(CreateTask.this, MainActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateTask.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}