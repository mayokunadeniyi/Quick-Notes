package com.mayokun.quicknotes.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.mayokun.quicknotes.Data.DataBaseOpenHelper;
import com.mayokun.quicknotes.Data.DataManager;
import com.mayokun.quicknotes.Model.CourseInfo;
import com.mayokun.quicknotes.Model.NoteInfo;
import com.mayokun.quicknotes.R;
import com.mayokun.quicknotes.Utils.Constants;
import com.mayokun.quicknotes.Utils.Constants.NoteInfoEntry;

import java.util.List;

public class NoteActivity extends AppCompatActivity {
    private Spinner spinnerCourses;
    private NoteInfo noteInfo;
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
        noteInfo = DataManager.getInstance().getNotes().get(noteID);

        //Create Adapter for dropdown spinner
        List<CourseInfo> courseInfoList = DataManager.getInstance().getCourses();
        ArrayAdapter<CourseInfo> courseInfoArrayAdapter =
                new ArrayAdapter<>(NoteActivity.this, android.R.layout.simple_spinner_item, courseInfoList);
        courseInfoArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerCourses.setAdapter(courseInfoArrayAdapter);

        readDisplayStateValues();

        if (savedInstanceState == null) {
            saveOriginalNoteValues();
        } else {
            restoreOriginalStateValues(savedInstanceState);
        }

        if (!mIsNewNote)
            loadNoteData();
    }

    private void loadNoteData() {
        SQLiteDatabase db = dataBaseOpenHelper.getReadableDatabase();

        String selection = NoteInfoEntry._ID + " = ?";

        String[] selectionArgs = {Integer.toString(noteID)};

        final String[] noteColumns = {NoteInfoEntry.COLUMN_COURSE_ID,
                NoteInfoEntry.COLUMN_NOTE_TITLE, NoteInfoEntry.COLUMN_NOTE_TEXT};

        cursor = db.query(NoteInfoEntry.TABLE_NAME, noteColumns, selection, selectionArgs,
                null, null, null);
        courseIdPosition = cursor.getColumnIndex(NoteInfoEntry.COLUMN_COURSE_ID);
        noteTitlePosition = cursor.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TITLE);
        noteTextPosition = cursor.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TEXT);

        cursor.moveToNext();
        displayNotes();

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

        List<CourseInfo> courseInfo = DataManager.getInstance().getCourses();
        CourseInfo courseInfoDB = DataManager.getInstance().getCourse(courseFromDB);
        int indexOfCourse = courseInfo.indexOf(courseInfoDB);

        spinnerCourses.setSelection(indexOfCourse);
        noteTitle.setText(noteTitleFromDB);
        noteText.setText(noteTextFromDB);
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

        dataManager = DataManager.getInstance();
        notePosition = dataManager.createNewNote();
        noteInfo = dataManager.getNotes().get(notePosition);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isCanceling) {
            if (mIsNewNote) {
                DataManager.getInstance().removeNote(notePosition);
            } else {
                storePreviousNoteValues();
            }
        } else {
            saveNote();
        }

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
        noteInfo.setCourse((CourseInfo) spinnerCourses.getSelectedItem());
        noteInfo.setTitle(noteTitle.getText().toString());
        noteInfo.setText(noteText.getText().toString());
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
}
