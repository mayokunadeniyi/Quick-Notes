package com.mayokun.quicknotes.Adapter;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mayokun.quicknotes.Activities.NoteActivity;
import com.mayokun.quicknotes.Model.NoteInfo;
import com.mayokun.quicknotes.R;
import com.mayokun.quicknotes.Utils.Constants;
import com.mayokun.quicknotes.Utils.Constants.NoteInfoEntry;

import java.util.List;

public class NoteRecyclerViewAdapter extends RecyclerView.Adapter<NoteRecyclerViewAdapter.ViewHolder> {
    private Context context;
    private Cursor mCursor;
    private int coursePos;
    private int noteTitlePos;
    private int noteId;


    public NoteRecyclerViewAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.mCursor = cursor;
        populateColumnPositions();
    }

    private void populateColumnPositions() {
        if (mCursor == null)
            return;
        //Get column index from mCursor

        coursePos = mCursor.getColumnIndex(NoteInfoEntry.COLUMN_COURSE_ID);
        noteTitlePos = mCursor.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TITLE);
        noteId = mCursor.getColumnIndex(NoteInfoEntry._ID);
    }

    public void changeCursor(Cursor cursor){
        if (mCursor != null)
            mCursor.close();
        mCursor = cursor;
        populateColumnPositions();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NoteRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_note_list, viewGroup, false);
        return new ViewHolder(view,context);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteRecyclerViewAdapter.ViewHolder viewHolder, int i) {
        mCursor.moveToPosition(i);
        String courseTitle = mCursor.getString(coursePos);
        String noteTitle = mCursor.getString(noteTitlePos);
        int id = mCursor.getInt(noteId);

        viewHolder.textCourse.setText(courseTitle);
        viewHolder.textTitle.setText(noteTitle);
        viewHolder.noteID = id;
    }

    @Override
    public int getItemCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView textCourse;
        public TextView textTitle;
        public int noteID;

        public ViewHolder(@NonNull View itemView, final Context context) {
            super(itemView);

            textCourse = (TextView) itemView.findViewById(R.id.text_course);
            textTitle = (TextView) itemView.findViewById(R.id.text_title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(),NoteActivity.class);
                    intent.putExtra(Constants.NOTE_ID, noteID);
                    context.startActivity(intent);
                }
            });
        }

        @Override
        public void onClick(View v) {

        }
    }
}
