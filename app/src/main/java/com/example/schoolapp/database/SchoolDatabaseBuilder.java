package com.example.schoolapp.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.schoolapp.dao.AssessmentDAO;
import com.example.schoolapp.dao.CourseDAO;
import com.example.schoolapp.dao.TermDAO;
import com.example.schoolapp.entities.Assessment;
import com.example.schoolapp.entities.Course;
import com.example.schoolapp.entities.Term;

@Database(entities = {Term.class, Course.class, Assessment.class}, version = 8, exportSchema = false)
public abstract class SchoolDatabaseBuilder extends RoomDatabase {
    public abstract TermDAO termDAO();
    public abstract CourseDAO courseDAO();
    public abstract AssessmentDAO assessmentDAO();
    private static volatile SchoolDatabaseBuilder INSTANCE;

    static SchoolDatabaseBuilder getDatabase(final Context context){
        if (INSTANCE==null){
            synchronized (SchoolDatabaseBuilder.class){
                if(INSTANCE==null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), SchoolDatabaseBuilder.class, "MySchoolDatabase.db")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
