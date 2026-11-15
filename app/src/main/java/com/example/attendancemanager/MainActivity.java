package com.example.attendancemanager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    EditText etRollNo, etName, etAttendance, etTaskTitle;
    Button btnAdd, btnUpdate, btnDelete, btnSearch, btnViewAll;
    ListView lvStudents;
    TextView tvEmpty;

    DatabaseHelper db;
    ArrayList<StudentModel> students = new ArrayList<>(); // records shown in the list
    ArrayList<String> display = new ArrayList<>();        // text of every row
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etRollNo = findViewById(R.id.etRollNo);
        etName = findViewById(R.id.etName);
        etAttendance = findViewById(R.id.etAttendance);
        etTaskTitle = findViewById(R.id.etTaskTitle);
        btnAdd = findViewById(R.id.btnAdd);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);
        btnSearch = findViewById(R.id.btnSearch);
        btnViewAll = findViewById(R.id.btnViewAll);
        lvStudents = findViewById(R.id.lvStudents);
        tvEmpty = findViewById(R.id.tvEmpty);

        db = new DatabaseHelper(this);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, display);
        lvStudents.setAdapter(adapter);
        lvStudents.setEmptyView(tvEmpty);

        showStudents("");

        // ADD
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String roll = etRollNo.getText().toString().trim().toUpperCase();
                String name = etName.getText().toString().trim();
                String att = etAttendance.getText().toString().trim();
                String task = etTaskTitle.getText().toString().trim();

                if (roll.isEmpty() || name.isEmpty() || att.isEmpty() || task.isEmpty()) {
                    Toast.makeText(MainActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                    return;
                }
                double attendance;
                try {
                    attendance = Double.parseDouble(att);
                } catch (NumberFormatException e) {
                    Toast.makeText(MainActivity.this, "Enter a valid attendance", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (db.insertStudent(roll, name, attendance, task)) {
                    Toast.makeText(MainActivity.this, "Student added", Toast.LENGTH_SHORT).show();
                    clearFields();
                    showStudents("");
                } else {
                    Toast.makeText(MainActivity.this, "Roll number already exists", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // UPDATE
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String roll = etRollNo.getText().toString().trim().toUpperCase();
                String name = etName.getText().toString().trim();
                String att = etAttendance.getText().toString().trim();
                String task = etTaskTitle.getText().toString().trim();

                if (roll.isEmpty() || name.isEmpty() || att.isEmpty() || task.isEmpty()) {
                    Toast.makeText(MainActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                    return;
                }
                double attendance;
                try {
                    attendance = Double.parseDouble(att);
                } catch (NumberFormatException e) {
                    Toast.makeText(MainActivity.this, "Enter a valid attendance", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (db.updateStudent(roll, name, attendance, task)) {
                    Toast.makeText(MainActivity.this, "Student updated", Toast.LENGTH_SHORT).show();
                    clearFields();
                    showStudents("");
                } else {
                    Toast.makeText(MainActivity.this, "No student with this roll number", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // DELETE (asks for confirmation first)
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String roll = etRollNo.getText().toString().trim().toUpperCase();
                if (roll.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Enter the roll number to delete", Toast.LENGTH_SHORT).show();
                    return;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Delete");
                builder.setMessage("Do you want to delete " + roll + "?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (db.deleteStudent(roll)) {
                            Toast.makeText(MainActivity.this, "Student deleted", Toast.LENGTH_SHORT).show();
                            clearFields();
                            showStudents("");
                        } else {
                            Toast.makeText(MainActivity.this, "No student with this roll number", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setNegativeButton("No", null);
                builder.show();
            }
        });

        // SEARCH by roll number or name
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = etRollNo.getText().toString().trim();
                if (key.isEmpty()) {
                    key = etName.getText().toString().trim();
                }
                if (key.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Enter a roll number or name to search", Toast.LENGTH_SHORT).show();
                    return;
                }
                showStudents(key);
            }
        });

        // VIEW ALL
        btnViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStudents("");
            }
        });

        // tap a record to fill the fields
        lvStudents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                StudentModel s = students.get(position);
                etRollNo.setText(s.getRollNo());
                etName.setText(s.getName());
                etAttendance.setText(String.valueOf(s.getAttendance()));
                etTaskTitle.setText(s.getTask());
            }
        });
    }

    // reads the records from the database and refreshes the ListView
    private void showStudents(String search) {
        students = db.getStudents(search);
        display.clear();
        for (StudentModel s : students) {
            display.add(s.getRollNo() + " | " + s.getName() + " | " + s.getAttendance() + "% | " + s.getTask());
        }
        adapter.notifyDataSetChanged();
    }

    private void clearFields() {
        etRollNo.setText("");
        etName.setText("");
        etAttendance.setText("");
        etTaskTitle.setText("");
    }
}
