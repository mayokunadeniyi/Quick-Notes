package com.mayokun.quicknotes.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.mayokun.quicknotes.Adapter.NoteRecyclerViewAdapter;
import com.mayokun.quicknotes.Data.DataManager;
import com.mayokun.quicknotes.Model.NoteInfo;
import com.mayokun.quicknotes.R;
import com.mayokun.quicknotes.Utils.Constants;

import java.util.List;

public class NoteListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private NoteRecyclerViewAdapter noteRecyclerViewAdapter;
    private List<NoteInfo> noteInfoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        noteInfoList = DataManager.getInstance().getNotes();

        recyclerView = (RecyclerView) findViewById(R.id.noteListRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NoteListActivity.this, NoteActivity.class));
            }
        });

        noteRecyclerViewAdapter = new NoteRecyclerViewAdapter(this, noteInfoList);
        recyclerView.setAdapter(noteRecyclerViewAdapter);
        noteRecyclerViewAdapter.notifyDataSetChanged();


    }


    @Override
    protected void onResume() {
        super.onResume();
        noteRecyclerViewAdapter.notifyDataSetChanged();
    }


}
