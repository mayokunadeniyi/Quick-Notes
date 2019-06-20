package com.mayokun.quicknotes.Adapter;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mayokun.quicknotes.Activities.NoteActivity;
import com.mayokun.quicknotes.Model.NoteInfo;
import com.mayokun.quicknotes.R;
import com.mayokun.quicknotes.Utils.Constants;

import java.util.List;

public class NoteRecyclerViewAdapter extends RecyclerView.Adapter<NoteRecyclerViewAdapter.ViewHolder> {
    private Context context;
    private List<NoteInfo> noteInfoList;

    public NoteRecyclerViewAdapter(Context context, List<NoteInfo> noteInfoList) {
        this.context = context;
        this.noteInfoList = noteInfoList;
    }

    @NonNull
    @Override
    public NoteRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_note_list, viewGroup, false);
        return new ViewHolder(view,context);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteRecyclerViewAdapter.ViewHolder viewHolder, int i) {
        NoteInfo noteInfo = noteInfoList.get(i);
        viewHolder.textCourse.setText(noteInfo.getCourse().getTitle());
        viewHolder.textTitle.setText(noteInfo.getTitle());
        viewHolder.currentPosition = viewHolder.getAdapterPosition();
    }

    @Override
    public int getItemCount() {
        return noteInfoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView textCourse;
        public TextView textTitle;
        public int currentPosition;

        public ViewHolder(@NonNull View itemView, final Context context) {
            super(itemView);

            textCourse = (TextView) itemView.findViewById(R.id.text_course);
            textTitle = (TextView) itemView.findViewById(R.id.text_title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(),NoteActivity.class);
                    intent.putExtra(Constants.NOTE_POSITION,currentPosition);
                    context.startActivity(intent);
                }
            });
        }

        @Override
        public void onClick(View v) {

        }
    }
}
