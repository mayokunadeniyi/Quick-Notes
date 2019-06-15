package com.mayokun.quicknotes.Activities;

import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.mayokun.quicknotes.Data.DataManager;
import com.mayokun.quicknotes.Model.CourseInfo;
import com.mayokun.quicknotes.Model.NoteInfo;
import com.mayokun.quicknotes.R;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;
import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static org.hamcrest.Matchers.*;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.assertion.ViewAssertions.*;

@RunWith(AndroidJUnit4.class)
public class NoteCreationTest {
    static DataManager dataManager;

    @BeforeClass
    public static void classSetUp() throws Exception {
        dataManager = DataManager.getInstance();
    }

    @Rule
    public ActivityTestRule<NoteListActivity> noteListActivityActivityTestRule =
            new ActivityTestRule<>(NoteListActivity.class);

    @Test
    public void createNewNote() {
        final CourseInfo courseInfo = dataManager.getCourse("java_lang");
        final String noteTitle = "Test note Title";
        final String noteText = "This is the body of our test";

//        ViewInteraction fabNewNote = onView(withId(R.id.fab));
//        fabNewNote.perform(click());
        onView(withId(R.id.fab)).perform(click());

        onView(withId(R.id.spinner_courses)).perform(click());
        onData(allOf(instanceOf(CourseInfo.class), equalTo(courseInfo))).perform(click());

        onView(withId(R.id.spinner_courses)).check(matches(withSpinnerText(
                containsString(courseInfo.getTitle()))
        ));


        onView(withId(R.id.note_title)).perform(typeText(noteTitle));
        onView(withId(R.id.note_text)).perform(typeText(noteText),
                closeSoftKeyboard());

        pressBack();

        int indexOfNote = dataManager.getNotes().size() - 1;
        NoteInfo noteInfo = dataManager.getNotes().get(indexOfNote);
        assertEquals(courseInfo,noteInfo.getCourse());
        assertEquals(noteTitle,noteInfo.getTitle());
        assertEquals(noteText,noteInfo.getText());

    }

}