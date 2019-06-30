package com.mayokun.quicknotes.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.mayokun.quicknotes.Utils.Constants;
import com.mayokun.quicknotes.Utils.Constants.CourseInfoEntry;
import com.mayokun.quicknotes.Utils.Constants.NoteInfoEntry;

public class DataBaseOpenHelper extends SQLiteOpenHelper {

    private Context context;

    public DataBaseOpenHelper(@Nullable Context context) {
        super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);

        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_COURSE_TABLE = "CREATE TABLE " + CourseInfoEntry.TABLE_NAME + "("
                + CourseInfoEntry._ID + " INTEGER PRIMARY KEY," +
                CourseInfoEntry.COLUMN_COURSE_ID + " TEXT UNIQUE NOT NULL," +
                CourseInfoEntry.COLUMN_COURSE_TITLE + " TEXT NOT NULL);";

        db.execSQL(CREATE_COURSE_TABLE);


        String CREATE_NOTE_TABLE = "CREATE TABLE " + NoteInfoEntry.TABLE_NAME + "("
                + NoteInfoEntry._ID + " INTEGER PRIMARY KEY," +
                NoteInfoEntry.COLUMN_NOTE_TITLE + " TEXT NOT NULL," +
                NoteInfoEntry.COLUMN_NOTE_TEXT + " TEXT," +
                NoteInfoEntry.COLUMN_COURSE_ID + " TEXT NOT NULL);";

        db.execSQL(CREATE_NOTE_TABLE);

        DatabaseDataWorker databaseDataWorker = new DatabaseDataWorker(db);
        databaseDataWorker.insertCourses();
        databaseDataWorker.insertSampleNotes();

        db.execSQL(CourseInfoEntry.SQL_CREATE_INDEX1);
        db.execSQL(NoteInfoEntry.SQL_CREATE_INDEX1);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
       db.execSQL("DROP TABLE IF EXISTS " + CourseInfoEntry.TABLE_NAME);
       db.execSQL("DROP TABLE IF EXISTS " + NoteInfoEntry.TABLE_NAME);

       onCreate(db);
    }
}
