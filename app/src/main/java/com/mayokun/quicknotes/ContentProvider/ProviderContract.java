package com.mayokun.quicknotes.ContentProvider;

import android.net.Uri;
import android.provider.BaseColumns;

public final class ProviderContract {
    private ProviderContract() {
    }

    public static final String AUTHORITY = "com.mayokun.quicknotes.provider";
    public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);

    protected interface CourseIdColumns {
        public static final String COLUMN_COURSE_ID = "course_id";
    }

    protected interface CourseColumns {
        public static final String COLUMN_COURSE_TITLE = "course_title";
    }

    protected interface NoteColumns {
        public static final String COLUMN_NOTE_TITLE = "note_title";
        public static final String COLUMN_NOTE_TEXT = "note_text";
    }

    public static final class Courses implements CourseColumns, BaseColumns, CourseIdColumns {
        public static final String PATH = "courses";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, PATH);
    }

    public static final class Notes implements NoteColumns, BaseColumns, CourseIdColumns {
        public static final String PATH = "notes";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, PATH);
    }
}
