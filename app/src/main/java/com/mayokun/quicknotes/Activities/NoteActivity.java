package com.mayokun.quicknotes.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.mayokun.quicknotes.Data.DataManager;
import com.mayokun.quicknotes.Model.CourseInfo;
import com.mayokun.quicknotes.Model.NoteInfo;
import com.mayokun.quicknotes.R;
import com.mayokun.quicknotes.Utils.Constants;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        spinnerCourses = (Spinner) findViewById(R.id.spinner_courses);
        noteTitle = (EditText) findViewById(R.id.note_title);
        noteText = (EditText) findViewById(R.id.note_text);
        dataManager = DataManager.getInstance();

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
            displayNotes(spinnerCourses, noteTitle, noteText);


    }

    private void restoreOriginalStateValues(Bundle savedInstanceState) {
        originalCourseID = savedInstanceState.getString(Constants.ORIGINAL_NOTE_COURSE_ID);
        originalNoteTitle = savedInstanceState.getString(Constants.ORIGINAL_NOTE_TITLE);
        originalNoteText = savedInstanceState.getString(Constants.ORIGINAL_NOTE_TEXT);
    }

    private void saveOriginalNoteValues() {
        if (mIsNewNote) {
            return;
        } else {
            originalCourseID = noteInfo.getCourse().getCourseId();
            originalNoteTitle = noteInfo.getTitle();
            originalNoteText = noteInfo.getText();
        }
    }

    private void displayNotes(Spinner spinnerCourses, EditText noteTitle, EditText noteText) {

        List<CourseInfo> courseInfo = DataManager.getInstance().getCourses();
        int indexOfCourse = courseInfo.indexOf(noteInfo.getCourse());

        spinnerCourses.setSelection(indexOfCourse);
        noteTitle.setText(noteInfo.getTitle());
        noteText.setText(noteInfo.getText());
    }

    private void readDisplayStateValues() {
        Intent intent = getIntent();
        int position = intent.getIntExtra(Constants.NOTE_POSITION, Constants.POSITION_NOT_SET);
        mIsNewNote = position == Constants.POSITION_NOT_SET;
        if (mIsNewNote) {
            createNewNote();
        } else {
            noteInfo = DataManager.getInstance().getNotes().get(position);
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
        }

        return super.onOptionsItemSelected(item);
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
