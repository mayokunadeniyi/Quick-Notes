package com.mayokun.quicknotes.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.mayokun.quicknotes.Utils.Constants;

public class DataBaseOpenHelper extends SQLiteOpenHelper {

    private Context context;

    public DataBaseOpenHelper(@Nullable Context context) {
        super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);

        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_COURSE_TABLE = "CREATE TABLE " + Constants.CourseInfoEntry.TABLE_NAME + "("
                + Constants.CourseInfoEntry._ID + " INTEGER PRIMARY KEY," +
                Constants.CourseInfoEntry.COLUMN_COURSE_ID + " TEXT UNIQUE NOT NULL," +
                Constants.CourseInfoEntry.COLUMN_COURSE_TITLE + " TEXT NOT NULL);";

        db.execSQL(CREATE_COURSE_TABLE);


        String CREATE_NOTE_TABLE = "CREATE TABLE " + Constants.NoteInfoEntry.TABLE_NAME + "("
                + Constants.NoteInfoEntry._ID + " INTEGER PRIMARY KEY," +
                Constants.NoteInfoEntry.COLUMN_NOTE_TITLE + " TEXT NOT NULL," +
                Constants.NoteInfoEntry.COLUMN_NOTE_TEXT + " TEXT," +
                Constants.NoteInfoEntry.COLUMN_COURSE_ID + " TEXT UNIQUE NOT NULL);";

        db.execSQL(CREATE_NOTE_TABLE);

        DatabaseDataWorker databaseDataWorker = new DatabaseDataWorker(db);
        databaseDataWorker.insertCourses();
        databaseDataWorker.insertSampleNotes();

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
       db.execSQL("DROP TABLE IF EXISTS " + Constants.CourseInfoEntry.TABLE_NAME);
       db.execSQL("DROP TABLE IF EXISTS " + Constants.NoteInfoEntry.TABLE_NAME);

       onCreate(db);
    }
}
