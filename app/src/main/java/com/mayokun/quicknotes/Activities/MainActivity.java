package com.mayokun.quicknotes.Activities;

import android.app.LoaderManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.PersistableBundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.view.View;

import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;

import com.mayokun.quicknotes.Adapter.CourseRecyclerViewAdapter;
import com.mayokun.quicknotes.Adapter.NoteRecyclerViewAdapter;
import com.mayokun.quicknotes.BuildConfig;
import com.mayokun.quicknotes.ContentProvider.ProviderContract;
import com.mayokun.quicknotes.ContentProvider.ProviderContract.Notes;
import com.mayokun.quicknotes.Data.DataBaseOpenHelper;
import com.mayokun.quicknotes.Data.DataManager;
import com.mayokun.quicknotes.Data.NoteBackup;
import com.mayokun.quicknotes.Model.CourseInfo;
import com.mayokun.quicknotes.Model.NoteInfo;
import com.mayokun.quicknotes.R;
import com.mayokun.quicknotes.Utils.Constants;
import com.mayokun.quicknotes.Utils.Constants.CourseInfoEntry;
import com.mayokun.quicknotes.Utils.Constants.NoteInfoEntry;
import com.mayokun.quicknotes.Utils.NoteBackUpService;
import com.mayokun.quicknotes.Utils.NoteUploaderJobService;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final int JOB_ID = 1;
    private NoteRecyclerViewAdapter noteRecyclerViewAdapter;
    private CourseRecyclerViewAdapter courseRecyclerViewAdapter;
    private RecyclerView recyclerView;
    private List<NoteInfo> noteInfoList;
    private List<CourseInfo> courseInfoList;
    private LinearLayoutManager linearLayoutManager;
    private GridLayoutManager gridLayoutManager;
    private DataBaseOpenHelper dataBaseOpenHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        enableStrictMode();

        FloatingActionButton fab = findViewById(R.id.fab);
        noteInfoList = DataManager.getInstance().getNotes();
        courseInfoList = DataManager.getInstance().getCourses();
        dataBaseOpenHelper = new DataBaseOpenHelper(this);
        DataManager.loadDataFromDatabase(dataBaseOpenHelper);


        recyclerView = (RecyclerView) findViewById(R.id.list_items);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this);
        gridLayoutManager = new GridLayoutManager(this, getResources().getInteger(R.integer.course_grid_span));

        noteRecyclerViewAdapter = new NoteRecyclerViewAdapter(this, null);
        courseRecyclerViewAdapter = new CourseRecyclerViewAdapter(this, courseInfoList);
        displayNotes();


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, NoteActivity.class));

            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void enableStrictMode() {
        if (BuildConfig.DEBUG) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    private void displayNotes() {
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(noteRecyclerViewAdapter);
        setMenuItemChecked(R.id.nav_notes);
    }

    private void displayCourses() {
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(courseRecyclerViewAdapter);
        setMenuItemChecked(R.id.nav_courses);
    }

    private void setMenuItemChecked(int id) {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        menu.findItem(id).setChecked(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(Constants.LOADER_NOTES, null, this);
        //openDrawer();
    }

    private void openDrawer() {
        Handler handler = new Handler(getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawerLayout.openDrawer(GravityCompat.START);
            }
        }, 1000);

    }

    private void loadNotes() {
        SQLiteDatabase db = dataBaseOpenHelper.getReadableDatabase();
        final String[] noteColumns = {NoteInfoEntry.COLUMN_NOTE_TITLE,
                NoteInfoEntry.COLUMN_COURSE_ID, NoteInfoEntry._ID};
        String noteOrderBy = NoteInfoEntry.COLUMN_COURSE_ID + "," + NoteInfoEntry.COLUMN_NOTE_TITLE;

        final Cursor noteCursor = db.query(NoteInfoEntry.TABLE_NAME, noteColumns,
                null, null, null, null, noteOrderBy);
        noteRecyclerViewAdapter.changeCursor(noteCursor);
    }

    @Override
    protected void onDestroy() {
        dataBaseOpenHelper.close();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            return true;
        }else if (id == R.id.backup_notes){
            backUpNotes();
        }else if (id == R.id.upload_notes){
            scheduleNotesUpload();
        }

        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void scheduleNotesUpload() {
        PersistableBundle extras = new PersistableBundle();
        extras.putString(NoteUploaderJobService.EXTRA_DATA_URI,Notes.CONTENT_URI.toString());

        ComponentName componentName = new ComponentName(this, NoteUploaderJobService.class);
        JobInfo jobInfo = new JobInfo.Builder(JOB_ID,componentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setExtras(extras)
                .build();

        JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(jobInfo);

    }

    private void backUpNotes() {
        Intent intent = new Intent(this, NoteBackUpService.class);
        intent.putExtra(NoteBackUpService.EXTRA_COURSE_ID,NoteBackup.ALL_COURSES);
        startService(intent);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_notes) {
            displayNotes();
        } else if (id == R.id.nav_courses) {
            displayCourses();

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader loader = null;
        if (Constants.LOADER_NOTES == id) {

            final String[] noteColumns = {Notes.COLUMN_NOTE_TITLE,
                    NoteInfoEntry.getQNames(NoteInfoEntry._ID),
                    Notes.COLUMN_COURSE_TITLE};

            String noteOrderBy = Notes.COLUMN_COURSE_TITLE + "," +
                    Notes.COLUMN_NOTE_TITLE;

            loader = new CursorLoader(this, Notes.CONTENT_EXPANDED_URI,
                    noteColumns, null, null, noteOrderBy);
        }
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == Constants.LOADER_NOTES) {
            noteRecyclerViewAdapter.changeCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == Constants.LOADER_NOTES) {
            noteRecyclerViewAdapter.changeCursor(null);
        }
    }
}
