package com.mayokun.quicknotes.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.mayokun.quicknotes.Data.DataManager;
import com.mayokun.quicknotes.Model.NoteInfo;
import com.mayokun.quicknotes.R;
import com.mayokun.quicknotes.Utils.Constants;

import java.util.List;

public class NoteListActivity extends AppCompatActivity {
    private ListView listView;
    private List<NoteInfo> noteInfoList;
    private ArrayAdapter<NoteInfo> noteInfoArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listView = (ListView) findViewById(R.id.note_list);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NoteListActivity.this, NoteActivity.class));
            }
        });

        initializeDisplayContent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        noteInfoArrayAdapter.notifyDataSetChanged();
    }

    private void initializeDisplayContent() {
       noteInfoList = DataManager.getInstance().getNotes();

       //Create Note List Array Adapter
        noteInfoArrayAdapter = new ArrayAdapter<>(NoteListActivity.this,
                android.R.layout.simple_list_item_1, noteInfoList);
        listView.setAdapter(noteInfoArrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(NoteListActivity.this,NoteActivity.class);
                intent.putExtra(Constants.NOTE_POSITION,position);
                startActivity(intent);
            }
        });
    }

}
