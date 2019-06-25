package com.mayokun.quicknotes.Utils;

import android.provider.BaseColumns;

import com.mayokun.quicknotes.Activities.NoteActivity;

public class Constants {
    public static final String NOTE_ID = "com.mayokun.quicknotes.NOTE_ID";
    public static final int NOTE_ID_NOT_SET = -1;

    public static final String ORIGINAL_NOTE_COURSE_ID = "com.mayokun.quicknotes.ORIGINAL_NOTE_COURSE_ID";
    public static final String ORIGINAL_NOTE_TITLE = "com.mayokun.quicknotes.NOTE_TITLE";
    public static final String ORIGINAL_NOTE_TEXT = "com.mayokun.quicknotes.NOTE_TEXT";


    //Database properties
    public static final String DATABASE_NAME = "QuickNotes.db";
    public static final int DATABASE_VERSION = 1;

    //CourseInfoEntry
    public static final class CourseInfoEntry implements BaseColumns {
        public static final String TABLE_NAME = "course_info";
        public static final String COLUMN_COURSE_ID = "course_id";
        public static final String COLUMN_COURSE_TITLE = "course_title";

    }

    //NoteInfoEntry
    public static final class NoteInfoEntry implements BaseColumns {
        public static final String TABLE_NAME = "note_info";
        public static final String COLUMN_NOTE_TITLE = "note_title";
        public static final String COLUMN_NOTE_TEXT = "note_text";
        public static final String COLUMN_COURSE_ID = "course_id";
    }

    //Loader Properties
    public static final int LOADER_NOTES = 0;
    public static final int LOADER_COURSES = 1;


}
