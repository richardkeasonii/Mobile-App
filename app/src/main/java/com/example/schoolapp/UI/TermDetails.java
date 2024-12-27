package com.example.schoolapp.UI;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schoolapp.R;
import com.example.schoolapp.database.Repository;
import com.example.schoolapp.entities.Course;
import com.example.schoolapp.entities.Term;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TermDetails extends AppCompatActivity {
    String name;
    int numCourses;
    int termID;
    EditText editName;
    TextView editStart;
    TextView editEnd;
    Term currentTerm;
    final Calendar myCalendar = Calendar.getInstance();
    String startString;
    String endString;
    DatePickerDialog.OnDateSetListener startDate;
    DatePickerDialog.OnDateSetListener endDate;
    String myFormat = "MM/dd/yy";
    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
    Repository repository;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_term_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        FloatingActionButton fab = findViewById(R.id.floatingActionButton2);


        editName = findViewById(R.id.titletext);
        editStart = findViewById(R.id.termstart);
        editEnd = findViewById(R.id.termend);
        termID = getIntent().getIntExtra("id", -1);
        name = getIntent().getStringExtra("name");
        startString = getIntent().getStringExtra("start");
        endString = getIntent().getStringExtra("end");

        if (startString == null) {
            startString = "Tap Here to Select Date";
        } else if (startString.isEmpty()) {
            startString = "Tap Here to Select Date";
        }

        if (endString == null) {
            endString = "Tap Here to Select Date";
        } else if (endString.isEmpty()) {
            endString = "Tap Here to Select Date";
        }


        editName.setText(name);
        editStart.setText(startString);
        editEnd.setText(endString);

        //Sets date to current day

        startDate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateStartLabel();
            }

        };

        endDate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateEndLabel();
            }
        };





        editStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date;
                String info = editStart.getText().toString();
                if (info.isEmpty()) {
                    info = "04/16/24";
                }

                try {
                    myCalendar.setTime(sdf.parse(info));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                new DatePickerDialog(TermDetails.this, startDate, myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        editEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date;
                String info = editEnd.getText().toString();
                if (info.isEmpty()) {
                    info = "04/16/24";
                }

                try {
                    myCalendar.setTime(sdf.parse(info));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                new DatePickerDialog(TermDetails.this, endDate, myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(TermDetails.this, CourseDetails.class);
                intent.putExtra("termID", termID);
                startActivity(intent);
            }
        });
        RecyclerView recyclerView = findViewById(R.id.courseRecyclerView);
        repository = new Repository(getApplication());
        final CourseAdapter courseAdapter = new CourseAdapter(this);
        recyclerView.setAdapter(courseAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<Course> filteredCourses = new ArrayList<>();
        for (Course p : repository.getmAllCourses()) {
            if (p.getTermID() == termID) {
                filteredCourses.add(p);
            }
        }
        courseAdapter.setCourses(filteredCourses);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_term_details, menu);
        return true;
    }

    private void updateStartLabel() {
        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        editStart.setText(sdf.format(myCalendar.getTime()));
    }

    private void updateEndLabel() {
        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        editEnd.setText(sdf.format(myCalendar.getTime()));
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.termsave){
            Term term;
            if (termID == -1){
                if (repository.getmAllTerms().isEmpty()) {
                    termID = 1;
                } else {
                    termID = repository.getmAllTerms().get(repository.getmAllTerms().size() - 1).getTermID() + 1;
                }
                term = new Term(termID, editName.getText().toString(), editStart.getText().toString(), editEnd.getText().toString());
                repository.insert(term);
                this.finish();
            } else {
                term = new Term(termID, editName.getText().toString(), editStart.getText().toString(), editEnd.getText().toString());
                repository.update(term);
                this.finish();
            }
        }
        if(item.getItemId() == R.id.termdelete) {
            for (Term term : repository.getmAllTerms()){
                if(term.getTermID() == termID){
                    currentTerm = term;
                }
            }
            numCourses = 0;
            for (Course course : repository.getmAllCourses()) {
                if (course.getTermID() == termID) {
                    ++numCourses;
                }
            }
            if (numCourses == 0) {
                repository.delete(currentTerm);
                Toast.makeText(TermDetails.this, currentTerm.getTermName() + " was deleted.", Toast.LENGTH_LONG).show();
                this.finish();
            } else {
                Toast.makeText(TermDetails.this, "Can't delete a term with courses.", Toast.LENGTH_LONG).show();
            }
        }
        this.finish();
        return true;
    }


    @Override
    protected void onResume() {
        super.onResume();
//        List<Course> allCourses = repository.getmAllCourses();
//        RecyclerView recyclerView = findViewById(R.id.courseRecyclerView);
//        final CourseAdapter courseAdapter = new CourseAdapter(this);
//        recyclerView.setAdapter(courseAdapter);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        courseAdapter.setCourses(allCourses);
        RecyclerView recyclerView = findViewById(R.id.courseRecyclerView);
        repository = new Repository(getApplication());
        final CourseAdapter courseAdapter = new CourseAdapter(this);
        recyclerView.setAdapter(courseAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<Course> filteredCourses = new ArrayList<>();
        for (Course p : repository.getmAllCourses()) {
            if (p.getTermID() == termID) {
                filteredCourses.add(p);
            }
        }
        courseAdapter.setCourses(filteredCourses);
    }

}