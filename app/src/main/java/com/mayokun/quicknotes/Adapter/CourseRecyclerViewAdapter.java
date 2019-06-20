package com.mayokun.quicknotes.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mayokun.quicknotes.Activities.NoteActivity;
import com.mayokun.quicknotes.Model.CourseInfo;
import com.mayokun.quicknotes.Model.NoteInfo;
import com.mayokun.quicknotes.R;
import com.mayokun.quicknotes.Utils.Constants;

import java.util.List;

public class CourseRecyclerViewAdapter extends RecyclerView.Adapter<CourseRecyclerViewAdapter.ViewHolder> {
    private Context context;
    private List<CourseInfo> courseInfoList;

    public CourseRecyclerViewAdapter(Context context, List<CourseInfo> courseInfoList) {
        this.context = context;
        this.courseInfoList = courseInfoList;
    }

    @NonNull
    @Override
    public CourseRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_course_list, viewGroup, false);
        return new ViewHolder(view,context);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseRecyclerViewAdapter.ViewHolder viewHolder, int i) {
        CourseInfo courseInfo = courseInfoList.get(i);
        viewHolder.textCourse.setText(courseInfo.getTitle());
        viewHolder.currentPosition = viewHolder.getAdapterPosition();
    }

    @Override
    public int getItemCount() {
        return courseInfoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView textCourse;
        public int currentPosition;

        public ViewHolder(@NonNull View itemView, final Context context) {
            super(itemView);

            textCourse = (TextView) itemView.findViewById(R.id.text_course);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        @Override
        public void onClick(View v) {

        }
    }
}
