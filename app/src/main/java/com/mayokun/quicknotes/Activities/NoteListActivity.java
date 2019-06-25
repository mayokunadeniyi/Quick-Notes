package com.mayokun.quicknotes.Activities;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

import com.mayokun.quicknotes.Adapter.NoteRecyclerViewAdapter;
import com.mayokun.quicknotes.Data.DataManager;
import com.mayokun.quicknotes.Model.NoteInfo;
import com.mayokun.quicknotes.R;

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

        noteRecyclerViewAdapter = new NoteRecyclerViewAdapter(this, null);
        recyclerView.setAdapter(noteRecyclerViewAdapter);
        noteRecyclerViewAdapter.notifyDataSetChanged();


    }


    @Override
    protected void onResume() {
        super.onResume();
        noteRecyclerViewAdapter.notifyDataSetChanged();
    }


}
