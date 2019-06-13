package com.mayokun.quicknotes.Activities;

import android.content.Intent;
import android.os.Bundle;
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
    private  NoteInfo noteInfo;
    private EditText noteTitle;
    private EditText noteText;
    private boolean mIsNewNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        spinnerCourses = (Spinner) findViewById(R.id.spinner_courses);
        noteTitle = (EditText) findViewById(R.id.note_title);
        noteText = (EditText) findViewById(R.id.note_text);

        //Create Adapter for dropdown spinner
        List<CourseInfo> courseInfoList = DataManager.getInstance().getCourses();
        ArrayAdapter<CourseInfo> courseInfoArrayAdapter =
                new ArrayAdapter<>(NoteActivity.this,android.R.layout.simple_spinner_item,courseInfoList);
        courseInfoArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerCourses.setAdapter(courseInfoArrayAdapter);

        readDisplayStateValues();

        if (!mIsNewNote)
            displayNotes(spinnerCourses,noteTitle,noteText);



    }

    private void displayNotes(Spinner spinnerCourses, EditText noteTitle, EditText noteText) {

        List<CourseInfo> courseInfos = DataManager.getInstance().getCourses();
        int indexOfCourse = courseInfos.indexOf(noteInfo.getCourse());

        spinnerCourses.setSelection(indexOfCourse);
        noteTitle.setText(noteInfo.getTitle());
        noteText.setText(noteInfo.getText());
    }

    private void readDisplayStateValues() {
        Intent intent = getIntent();
        noteInfo = intent.getParcelableExtra(Constants.NOTE_INFO);
        mIsNewNote = noteInfo == null;
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
