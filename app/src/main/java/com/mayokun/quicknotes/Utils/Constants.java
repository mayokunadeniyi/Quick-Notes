package com.mayokun.quicknotes.Utils;

import android.provider.BaseColumns;


public class Constants {
    public static final String NOTE_ID = "com.mayokun.quicknotes.NOTE_ID";
    public static final int NOTE_ID_NOT_SET = -1;

    public static final String ORIGINAL_NOTE_COURSE_ID = "com.mayokun.quicknotes.ORIGINAL_NOTE_COURSE_ID";
    public static final String ORIGINAL_NOTE_TITLE = "com.mayokun.quicknotes.NOTE_TITLE";
    public static final String ORIGINAL_NOTE_TEXT = "com.mayokun.quicknotes.NOTE_TEXT";


    //Database properties
    public static final String DATABASE_NAME = "QuickNotes.db";
    public static final int DATABASE_VERSION = 2;

    //CourseInfoEntry
    public static final class CourseInfoEntry implements BaseColumns {
        public static final String TABLE_NAME = "course_info";
        public static final String COLUMN_COURSE_ID = "course_id";
        public static final String COLUMN_COURSE_TITLE = "course_title";

        //CREATE INDEX course_info_index1 ON course_info (course_title)
        public static final String INDEX1 = TABLE_NAME + "_index1";
        public static final String SQL_CREATE_INDEX1 = "CREATE INDEX "
                + INDEX1 + " ON " + TABLE_NAME + "(" + COLUMN_COURSE_TITLE +
                ")";

        public static final String getQNames(String columnName) {
            return TABLE_NAME + "." + columnName;
        }
    }

    //NoteInfoEntry
    public static final class NoteInfoEntry implements BaseColumns {
        public static final String TABLE_NAME = "note_info";
        public static final String COLUMN_NOTE_TITLE = "note_title";
        public static final String COLUMN_NOTE_TEXT = "note_text";
        public static final String COLUMN_COURSE_ID = "course_id";

        public static final String INDEX1 = TABLE_NAME + "_index1";
        public static final String SQL_CREATE_INDEX1 = "CREATE INDEX "
                + INDEX1 + " ON " + TABLE_NAME + "(" + COLUMN_NOTE_TITLE +
                ")";


        public static final String getQNames(String columnName) {
            return TABLE_NAME + "." + columnName;
        }
    }

    //Loader Properties
    public static final int LOADER_NOTES = 0;
    public static final int LOADER_COURSES = 1;

    //Content Provider
    public static final int COURSES_CODE = 0;
    public static final int NOTES_CODE = 1;
    public static final int NOTES_EXPANDED_CODE = 2;
    public static final int NOTES_ROW = 3;


}
