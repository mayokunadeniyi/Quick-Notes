package com.mayokun.quicknotes.Data;

import com.mayokun.quicknotes.Model.CourseInfo;
import com.mayokun.quicknotes.Model.NoteInfo;

import org.junit.Test;

import static org.junit.Assert.*;

public class DataManagerTest {

    @Test
    public void createNewNote() {
        final DataManager dataManager = DataManager.getInstance();
        final CourseInfo courseInfo = dataManager.getCourse("android_async");
        final String noteTitle = "Test note title";
        final String noteText = "Test note text";

        int noteIndex = dataManager.createNewNote();
        NoteInfo newNote = dataManager.getNotes().get(noteIndex);
        newNote.setCourse(courseInfo);
        newNote.setTitle(noteTitle);
        newNote.setText(noteText);

        NoteInfo testNote = dataManager.getNotes().get(noteIndex);

        assertEquals(testNote.getCourse(),courseInfo);
        assertEquals(testNote.getTitle(),noteTitle);
        assertEquals(testNote.getText(),noteText);
    }
}