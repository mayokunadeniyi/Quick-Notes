package com.mayokun.quicknotes.Activities;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import com.mayokun.quicknotes.ContentProvider.ProviderContract;
import com.mayokun.quicknotes.ContentProvider.ProviderContract.Courses;
import com.mayokun.quicknotes.ContentProvider.ProviderContract.Notes;
import com.mayokun.quicknotes.Data.DataBaseOpenHelper;
import com.mayokun.quicknotes.Data.DataManager;
import com.mayokun.quicknotes.Data.DatabaseDataWorker;
import com.mayokun.quicknotes.Model.CourseInfo;
import com.mayokun.quicknotes.Model.NoteInfo;
import com.mayokun.quicknotes.R;
import com.mayokun.quicknotes.Utils.Constants;
import com.mayokun.quicknotes.Utils.Constants.CourseInfoEntry;
import com.mayokun.quicknotes.Utils.Constants.NoteInfoEntry;

import java.net.URI;
import java.util.List;

public class NoteActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private Spinner spinnerCourses;
    private NoteInfo noteInfo = new NoteInfo(DataManager.getInstance().getCourses().get(0), "", "");
    private EditText noteTitle;
    private EditText noteText;
    private CourseInfo courseInfo;
    private boolean mIsNewNote;
    private DataManager dataManager;
    private int notePosition;
    private boolean isCanceling;
    private String originalCourseID;
    private String originalNoteTitle;
    private String originalNoteText;
    private DataBaseOpenHelper dataBaseOpenHelper;
    private Cursor cursor;
    private int courseIdPosition;
    private int noteTitlePosition;
    private int noteTextPosition;
    private int noteID;
    private SimpleCursorAdapter courseInfoArrayAdapter;
    private boolean coursesQueryFinished;
    private boolean notesQueryFinished;
    private Uri noteUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        dataBaseOpenHelper = new DataBaseOpenHelper(this);

        spinnerCourses = (Spinner) findViewById(R.id.spinner_courses);
        noteTitle = (EditText) findViewById(R.id.note_title);
        noteText = (EditText) findViewById(R.id.note_text);
        dataManager = DataManager.getInstance();

        //Create Adapter for dropdown spinner
        courseInfoArrayAdapter = new SimpleCursorAdapter(NoteActivity.this,
                android.R.layout.simple_spinner_item, null,
                new String[]{CourseInfoEntry.COLUMN_COURSE_TITLE},
                new int[]{android.R.id.text1}, 0);
        courseInfoArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCourses.setAdapter(courseInfoArrayAdapter);
        //Using a Loader Manger to query db for courses
        getLoaderManager().initLoader(Constants.LOADER_COURSES, null, this);


        readDisplayStateValues();
        if (savedInstanceState == null) {
            saveOriginalNoteValues();
        } else {
            restoreOriginalStateValues(savedInstanceState);
        }

        if (!mIsNewNote)
            //Using a Loader Manager to query db for notes
            getLoaderManager().initLoader(Constants.LOADER_NOTES, null, this);
    }

    private void restoreOriginalStateValues(Bundle savedInstanceState) {
        originalCourseID = savedInstanceState.getString(Constants.ORIGINAL_NOTE_COURSE_ID);
        originalNoteTitle = savedInstanceState.getString(Constants.ORIGINAL_NOTE_TITLE);
        originalNoteText = savedInstanceState.getString(Constants.ORIGINAL_NOTE_TEXT);
    }

    private void saveOriginalNoteValues() {
        if (mIsNewNote)
            return;
        originalCourseID = noteInfo.getCourse().getCourseId();
        originalNoteTitle = noteInfo.getTitle();
        originalNoteText = noteInfo.getText();

    }

    private void displayNotes() {
        String noteTitleFromDB = cursor.getString(noteTitlePosition);
        String noteTextFromDB = cursor.getString(noteTextPosition);
        String courseFromDB = cursor.getString(courseIdPosition);


        int indexOfCourse = getIndexOfCourse(courseFromDB);
        spinnerCourses.setSelection(indexOfCourse);
        noteTitle.setText(noteTitleFromDB);
        noteText.setText(noteTextFromDB);
    }

    private int getIndexOfCourse(String courseFromDB) {
        Cursor cursor = courseInfoArrayAdapter.getCursor();
        int courseIdPosition = cursor.getColumnIndex(CourseInfoEntry.COLUMN_COURSE_ID);
        int courseRowIndex = 0;

        boolean more = cursor.moveToFirst();
        while (more) {
            String courseId = cursor.getString(courseIdPosition);
            if (courseFromDB.equals(courseId))
                break;

            courseRowIndex++;
            more = cursor.moveToNext();
        }
        return courseRowIndex;
    }

    private void readDisplayStateValues() {
        Intent intent = getIntent();
        noteID = intent.getIntExtra(Constants.NOTE_ID, Constants.NOTE_ID_NOT_SET);
        mIsNewNote = noteID == Constants.NOTE_ID_NOT_SET;
        if (mIsNewNote) {
            createNewNote();
        }

    }

    private void createNewNote() {

        final ContentValues values = new ContentValues();
        values.put(Notes.COLUMN_COURSE_ID, "");
        values.put(Notes.COLUMN_NOTE_TITLE, "");
        values.put(Notes.COLUMN_NOTE_TEXT, "");

        noteUri = getContentResolver().insert(Notes.CONTENT_URI, values);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isCanceling) {
            if (mIsNewNote) {
                deleteNoteFromDataBase();
            } else {
                storePreviousNoteValues();
            }
        } else {
            saveNote();
        }

    }

    private void deleteNoteFromDataBase() {
        final String selection = NoteInfoEntry._ID + " = ?";
        final String[] selectionArgs = {Integer.toString(noteID)};

        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {

                SQLiteDatabase db = dataBaseOpenHelper.getWritableDatabase();
                db.delete(NoteInfoEntry.TABLE_NAME, selection, selectionArgs);
                return null;
            }
        };
        task.execute();
    }

    @Override
    protected void onDestroy() {
        dataBaseOpenHelper.close();
        super.onDestroy();
    }

    private void storePreviousNoteValues() {
        CourseInfo courseInfo = dataManager.getCourse(originalCourseID);
        noteInfo.setCourse(courseInfo);
        noteInfo.setTitle(originalNoteTitle);
        noteInfo.setText(originalNoteText);
    }


    private void saveNote() {
        String courseId = selectedCourseId();
        String title = noteTitle.getText().toString();
        String text = noteText.getText().toString();
        saveToDataBase(courseId, title, text);
    }

    private String selectedCourseId() {
        int spinnerPosition = spinnerCourses.getSelectedItemPosition();
        Cursor cursor = courseInfoArrayAdapter.getCursor();
        cursor.moveToPosition(spinnerPosition);
        int courseIdPos = cursor.getColumnIndex(CourseInfoEntry.COLUMN_COURSE_ID);
        String courseId = cursor.getString(courseIdPos);
        return courseId;
    }

    private void saveToDataBase(String courseId, String noteTitle, String noteText) {
        String selection = NoteInfoEntry._ID + " = ?";
        String[] selectionArgs = {Integer.toString(noteID)};

        ContentValues values = new ContentValues();
        values.put(NoteInfoEntry.COLUMN_COURSE_ID, courseId);
        values.put(NoteInfoEntry.COLUMN_NOTE_TITLE, noteTitle);
        values.put(NoteInfoEntry.COLUMN_NOTE_TEXT, noteText);

        SQLiteDatabase db = dataBaseOpenHelper.getWritableDatabase();
        db.update(NoteInfoEntry.TABLE_NAME, values, selection, selectionArgs);


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Constants.ORIGINAL_NOTE_COURSE_ID, originalCourseID);
        outState.putString(Constants.ORIGINAL_NOTE_TITLE, originalNoteTitle);
        outState.putString(Constants.ORIGINAL_NOTE_TEXT, originalNoteText);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_send_note) {
            sendEmail();
            return true;
        } else if (id == R.id.action_cancel) {
            isCanceling = true;
            finish();
        } else if (id == R.id.action_next) {
            moveNext();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.findItem(R.id.action_next);
        int indexPosition = DataManager.getInstance().getNotes().size() - 1;
        menuItem.setEnabled(notePosition < indexPosition);
        return super.onPrepareOptionsMenu(menu);
    }

    private void moveNext() {
        saveNote();
        ++notePosition;
        noteInfo = DataManager.getInstance().getNotes().get(notePosition);
        saveOriginalNoteValues();
        displayNotes();
        invalidateOptionsMenu();
    }

    private void sendEmail() {
        courseInfo = (CourseInfo) spinnerCourses.getSelectedItem();
        String subject = noteTitle.getText().toString();
        String textBody = "Check out what I learnt in " + noteInfo.getTitle() + "\n"
                + noteText.getText().toString();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc2822");
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, textBody);
        startActivity(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader loader = null;
        if (Constants.LOADER_NOTES == id)
            loader = createLoaderNotes();
        else if (Constants.LOADER_COURSES == id)
            loader = createLoaderCourses();
        return loader;
    }

    private CursorLoader createLoaderCourses() {
        coursesQueryFinished = false;
        Uri uri = Courses.CONTENT_URI;
        String[] courseColumns = {Courses.COLUMN_COURSE_TITLE,
                Courses.COLUMN_COURSE_ID, Courses._ID};
        return new CursorLoader(this, uri, courseColumns, null,
                null, Courses.COLUMN_COURSE_TITLE);
    }

    private CursorLoader createLoaderNotes() {
        notesQueryFinished = false;

        final String[] noteColumns = {NoteInfoEntry.COLUMN_COURSE_ID,
                NoteInfoEntry.COLUMN_NOTE_TITLE, NoteInfoEntry.COLUMN_NOTE_TEXT};

        noteUri = ContentUris.withAppendedId(Notes.CONTENT_URI, noteID);
        return new CursorLoader(this, noteUri, noteColumns, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == Constants.LOADER_NOTES)
            loadFinishedNotes(data);
        else if (loader.getId() == Constants.LOADER_COURSES) {
            courseInfoArrayAdapter.changeCursor(data);
            coursesQueryFinished = true;
            displayNoteWhenQueriesFinish();
        }
    }

    private void loadFinishedNotes(Cursor data) {
        cursor = data;
        courseIdPosition = cursor.getColumnIndex(NoteInfoEntry.COLUMN_COURSE_ID);
        noteTitlePosition = cursor.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TITLE);
        noteTextPosition = cursor.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TEXT);
        cursor.moveToNext();
        notesQueryFinished = true;
        displayNoteWhenQueriesFinish();
    }

    private void displayNoteWhenQueriesFinish() {
        if (notesQueryFinished && coursesQueryFinished) {
            displayNotes();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == Constants.LOADER_NOTES) {
            if (cursor != null)
                cursor.close();
        } else if (loader.getId() == Constants.LOADER_COURSES) {
            courseInfoArrayAdapter.changeCursor(null);
        }
    }
}
