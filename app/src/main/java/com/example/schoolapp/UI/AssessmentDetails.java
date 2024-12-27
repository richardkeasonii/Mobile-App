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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.schoolapp.R;
import com.example.schoolapp.database.Repository;
import com.example.schoolapp.entities.Assessment;
import com.example.schoolapp.entities.Course;
import com.example.schoolapp.entities.Term;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AssessmentDetails extends AppCompatActivity {

    String name;
    int assessmentID;
    int courseID;
    Assessment currentAssessment;
    char type;
    String startString;
    String endString;
    EditText editName;
    EditText editNote;
    TextView editStart;
    TextView editEnd;
    RadioGroup editType;
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
        setContentView(R.layout.activity_assessment_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        repository = new Repository(getApplication());

        editName = findViewById(R.id.assessmentname);
        editStart = findViewById(R.id.assessmentstart);
        editEnd = findViewById(R.id.assessmentend);
        editType = findViewById(R.id.assessmentradio);

        name = getIntent().getStringExtra("name");
        assessmentID = getIntent().getIntExtra("id", -1);
        courseID = getIntent().getIntExtra("courseID", -2);
        startString = getIntent().getStringExtra("start");
        endString = getIntent().getStringExtra("end");
        type = getIntent().getCharExtra("type", 'N');

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

        switch (type) {
            case 'N':
//                Toast.makeText(getApplicationContext(), "No type found.", Toast.LENGTH_LONG).show();
//                break;
            case 'O':
                editType.check(R.id.objective);
                break;
            case 'P':
                editType.check(R.id.performance);
                break;
            default:
                Toast.makeText(getApplicationContext(), "This message should not appear.", Toast.LENGTH_LONG).show();
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
                    info = "04/16/24";
                }

                try {
                    myCalendar.setTime(sdf.parse(info));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                new DatePickerDialog(AssessmentDetails.this, startDate, myCalendar.get(Calendar.YEAR),
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

                new DatePickerDialog(AssessmentDetails.this, endDate, myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

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
        getMenuInflater().inflate(R.menu.menu_assessment_details, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.assessmentsave) {
            Assessment assessment;
            char type;
            int buttonId;
            buttonId = editType.getCheckedRadioButtonId();
            type = (buttonId == R.id.objective) ? 'O' : ((buttonId == R.id.performance) ? 'P' : 'N');
            if (assessmentID == -1) {
                if (repository.getmAllAssessments().isEmpty()) {
                    assessmentID = 1;
                } else {
                    assessmentID = repository.getmAllAssessments().get(repository.getmAllAssessments().size() - 1).getAssessmentID() + 1;
                }
                assessment = new Assessment(assessmentID, editName.getText().toString(), courseID, type, editStart.getText().toString(), editEnd.getText().toString());
                repository.insert(assessment);
                this.finish();
            } else {
                assessment = new Assessment(assessmentID, editName.getText().toString(), courseID, type, editStart.getText().toString(), editEnd.getText().toString());
                repository.update(assessment);
                this.finish();
            }
            return true;
        }

        if(item.getItemId() == R.id.assessmentdelete) {
            for (Assessment assessment : repository.getmAllAssessments()){
                if(assessment.getAssessmentID() == assessmentID){
                    currentAssessment = assessment;
                }
            }
            repository.delete(currentAssessment);
            Toast.makeText(AssessmentDetails.this, currentAssessment.getAssessmentName() + " was deleted.", Toast.LENGTH_LONG).show();
            this.finish();
        }

        if (item.getItemId() == R.id.share) {
            Intent sentIntent = new Intent();
            sentIntent.setAction(Intent.ACTION_SEND);
            sentIntent.putExtra(Intent.EXTRA_TEXT, editNote.getText().toString());
            sentIntent.putExtra(Intent.EXTRA_TITLE, "Note for " + editName.getText().toString() + ".");
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
                Intent intent = new Intent(AssessmentDetails.this, MyReceiver.class);
                //THIS WILL BE REPLACED WITH A NOTIFICATION OF A SPECIFIC COURSE OR ASSESSMENT STARTING/ENDING
                intent.putExtra("key", "Assessment " + editName.getText().toString() + " has started.");

                PendingIntent sender = PendingIntent.getBroadcast(AssessmentDetails.this, ++MainActivity.numAlert, intent, PendingIntent.FLAG_IMMUTABLE);
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
                Intent intent = new Intent(AssessmentDetails.this, MyReceiver.class);
                //THIS WILL BE REPLACED WITH A NOTIFICATION OF A SPECIFIC COURSE OR ASSESSMENT STARTING/ENDING
                intent.putExtra("key", "Assessment " + editName.getText().toString() + " has ended.");

                PendingIntent sender = PendingIntent.getBroadcast(AssessmentDetails.this, ++MainActivity.numAlert, intent, PendingIntent.FLAG_IMMUTABLE);
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


}
