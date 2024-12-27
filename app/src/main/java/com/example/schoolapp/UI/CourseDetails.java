package com.example.schoolapp.UI;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
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
import com.example.schoolapp.entities.Assessment;
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

public class CourseDetails extends AppCompatActivity {

    String name;
    int courseID;
    int termID;
    Course currentCourse;
    String note;
    String startString;
    String endString;
    String instructor;
    String phoneNum;
    String email;
    char status;
    EditText editName;
    EditText editNote;
    TextView editStart;
    TextView editEnd;
    EditText editInstructor;
    EditText editPhone;
    EditText editEmail;
    RadioGroup editStatus;
    Repository repository;
    final Calendar myCalendar = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener startDate;
    DatePickerDialog.OnDateSetListener endDate;
    String myFormat = "MM/dd/yy";
    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_course_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        repository = new Repository(getApplication());
        editName = findViewById(R.id.coursename);
        editNote = findViewById(R.id.note);
        editStart = findViewById(R.id.coursestart);
        editEnd = findViewById(R.id.courseend);
        editInstructor = findViewById(R.id.instructor);
        editPhone = findViewById(R.id.phone);
        editEmail = findViewById(R.id.email);
        editStatus = findViewById(R.id.courseradio);

        FloatingActionButton fab = findViewById(R.id.floatingActionButton3);


        name = getIntent().getStringExtra("name");
        courseID = getIntent().getIntExtra("id", -1);
        termID = getIntent().getIntExtra("termID", -1);
        note = getIntent().getStringExtra("note");
        startString = getIntent().getStringExtra("start");
        endString = getIntent().getStringExtra("end");
        instructor = getIntent().getStringExtra("instructor");
        phoneNum = getIntent().getStringExtra("phone");
        email = getIntent().getStringExtra("email");
        status = getIntent().getCharExtra("status", 'N');

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
        editNote.setText(note);
        editStart.setText(startString);
        editEnd.setText(endString);
        editInstructor.setText(instructor);
        editPhone.setText(phoneNum);
        editEmail.setText(email);

        switch (status) {
            case 'N':
            case 'I':
                editStatus.check(R.id.progress);
                break;
            case 'C':
                editStatus.check(R.id.complete);
                break;
            case 'D':
                editStatus.check(R.id.dropped);
                break;
            case 'P':
                editStatus.check(R.id.planned);
                break;
            default:
                Toast.makeText(getApplicationContext(), "You shouldn't see this message. Status error.", Toast.LENGTH_LONG).show();
                break;
        }





        //Date Picker
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
                    info = "05/06/24";
                }

                try {
                    myCalendar.setTime(sdf.parse(info));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                new DatePickerDialog(CourseDetails.this, startDate, myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        editEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date;
                String info = editEnd.getText().toString();
                if (info.isEmpty()) {
                    info = "05/07/24";
                }
                try {
                    myCalendar.setTime(sdf.parse(info));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                new DatePickerDialog(CourseDetails.this, endDate, myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(CourseDetails.this, AssessmentDetails.class);
                intent.putExtra("courseID", courseID);
                startActivity(intent);
            }
        });


//        Spinner spinner = findViewById(R.id.spinner);
//        ArrayList<Term> termArrayList = new ArrayList<>();
//        termArrayList.addAll(repository.getmAllTerms());
//        ArrayAdapter<Term> termAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, termArrayList);
//        spinner.setAdapter(termAdapter);
//        spinner.setSelection(0);


        RecyclerView recyclerView = findViewById(R.id.assessmentRecyclerView);
        repository = new Repository(getApplication());
        final AssessmentAdapter assessmentAdapter = new AssessmentAdapter(this);
        recyclerView.setAdapter(assessmentAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<Assessment> filteredAssessments = new ArrayList<>();
        for (Assessment p : repository.getmAllAssessments()) {
            if (p.getCourseID() == courseID) {
                filteredAssessments.add(p);
            }
        }
        assessmentAdapter.setAssessments(filteredAssessments);

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

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_course_details, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.coursesave) {
            //Get char to store in database to represent RadioGroup selection
            Course course;
            char type;
            int checkedButtonId = editStatus.getCheckedRadioButtonId();
            if (checkedButtonId == R.id.progress) {
                type = 'I';
            } else if (checkedButtonId == R.id.complete) {
                type = 'C';
            } else if (checkedButtonId == R.id.dropped) {
                type = 'D';
            } else if (checkedButtonId == R.id.planned) {
                type = 'P';
            } else {
                // Handle the case where none of the RadioButtons is selected
                Toast.makeText(getApplicationContext(), "You shouldn't see this message. Save error for type.", Toast.LENGTH_LONG).show();
                type = 'N';
            }
            try {
                Date startDate = sdf.parse(editStart.getText().toString());
                Date endDate = sdf.parse(editEnd.getText().toString());

                assert endDate != null;
                if (endDate.before(startDate)) {
                    Toast.makeText(getApplicationContext(), "The End Date is before the Start Date.", Toast.LENGTH_LONG).show();
                } else if (!(endDate.after(startDate))) {
                    Toast.makeText(getApplicationContext(), "The End Date is the same as the Start Date.", Toast.LENGTH_LONG).show();
                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            if (courseID == -1) {
                if (repository.getmAllCourses().isEmpty()) {
                    courseID = 1;
                } else {
                    courseID = repository.getmAllCourses().get(repository.getmAllCourses().size() - 1).getCourseID() + 1;
                }
                course = new Course(courseID, editName.getText().toString(), termID, editInstructor.getText().toString(), editPhone.getText().toString(), editEmail.getText().toString(), type, editStart.getText().toString(),
                        editEnd.getText().toString(), editNote.getText().toString());
                repository.insert(course);
                this.finish();
            } else {
                course = new Course(courseID, editName.getText().toString(), termID, editInstructor.getText().toString(), editPhone.getText().toString(), editEmail.getText().toString(), type, editStart.getText().toString(),
                        editEnd.getText().toString(), editNote.getText().toString());
                repository.update(course);
                this.finish();
            }
            return true;
        }

        if(item.getItemId() == R.id.coursedelete) {
            for (Course course : repository.getmAllCourses()){
                if(course.getCourseID() == courseID){
                    currentCourse = course;
                }
            }
            repository.delete(currentCourse);
            Toast.makeText(CourseDetails.this, currentCourse.getCourseName() + " was deleted.", Toast.LENGTH_LONG).show();
            this.finish();
        }

        if (item.getItemId() == R.id.share) {
            Intent sentIntent = new Intent();
            sentIntent.setAction(Intent.ACTION_SEND);
            sentIntent.putExtra(Intent.EXTRA_TEXT, editNote.getText().toString());
            sentIntent.putExtra(Intent.EXTRA_TITLE, "Note for: " + editName.getText().toString() + ".");
            sentIntent.setType("text/plain");
            Intent shareIntent = Intent.createChooser(sentIntent, null);
            startActivity(shareIntent);
            return true;
        }
        if (item.getItemId() == R.id.notifystart) {
            String startDateFromScreen = editStart.getText().toString();
            String myFormat = "MM/dd/yy";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            Date myDate = null;
            try {
                myDate = sdf.parse(startDateFromScreen);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            try {
                Long trigger = myDate.getTime();
                Intent intent = new Intent(CourseDetails.this, MyReceiver.class);
                intent.putExtra("key", "The Course: " + editName.getText().toString() + " is starting.");

                PendingIntent sender = PendingIntent.getBroadcast(CourseDetails.this, ++MainActivity.numAlert, intent, PendingIntent.FLAG_IMMUTABLE);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, trigger, sender);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return true;
        }

        if (item.getItemId() == R.id.notifyend) {
            String endDateFromScreen = editEnd.getText().toString();
            String myFormat = "MM/dd/yy";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            Date myDate = null;
            try {
                myDate = sdf.parse(endDateFromScreen);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            try {
                Long trigger = myDate.getTime();
                Intent intent = new Intent(CourseDetails.this, MyReceiver.class);
                intent.putExtra("key", "The Course: " + editName.getText().toString() + " is ending.");

                PendingIntent sender = PendingIntent.getBroadcast(CourseDetails.this, ++MainActivity.numAlert, intent, PendingIntent.FLAG_IMMUTABLE);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, trigger, sender);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return true;
        }
        this.finish();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();


        RecyclerView recyclerView = findViewById(R.id.assessmentRecyclerView);
        repository = new Repository(getApplication());
        final AssessmentAdapter assessmentAdapter = new AssessmentAdapter(this);
        recyclerView.setAdapter(assessmentAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<Assessment> filteredAssessments = new ArrayList<>();
        for (Assessment p : repository.getmAllAssessments()) {
            if (p.getCourseID() == courseID) {
                filteredAssessments.add(p);
            }
        }
        assessmentAdapter.setAssessments(filteredAssessments);


    }


}