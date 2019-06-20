package com.mayokun.quicknotes.Activities;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.mayokun.quicknotes.Data.DataManager;
import com.mayokun.quicknotes.Model.NoteInfo;
import com.mayokun.quicknotes.R;

import static org.junit.Assert.*;
import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static org.hamcrest.Matchers.*;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.assertion.ViewAssertions.*;
import static android.support.test.espresso.Espresso.onView;

import java.util.ArrayList;
import java.util.List;

public class NextThroughNotesTest {

    @Rule
    public ActivityTestRule<MainActivity> mainActivityActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);
    @Test
    public void nextThroughNotes(){
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_notes));

        onView(withId(R.id.list_items)).perform(RecyclerViewActions.actionOnItemAtPosition(0,click()));

        List<NoteInfo> noteInfoList = DataManager.getInstance().getNotes();

        for (int index = 0; index<noteInfoList.size(); index++){

            NoteInfo noteInfo = noteInfoList.get(index);

            onView(withId(R.id.spinner_courses)).check(
                    matches(withSpinnerText(noteInfo.getCourse().getTitle())));
            onView(withId(R.id.note_title)).check(matches(withText(noteInfo.getTitle())));
            onView(withId(R.id.note_text)).check(matches(withText(noteInfo.getText())));

            if (index < noteInfoList.size() -1)
                onView(allOf(withId(R.id.action_next),isEnabled())).perform(click());
        }
        onView(withId(R.id.action_next)).check(matches(not(isEnabled())));
        pressBack();

    }

}