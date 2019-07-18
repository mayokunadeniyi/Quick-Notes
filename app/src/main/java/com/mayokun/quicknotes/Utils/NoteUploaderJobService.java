package com.mayokun.quicknotes.Utils;

import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class NoteUploaderJobService extends JobService {
    public static final String EXTRA_DATA_URI = "com.mayokun.quicknotes.extras.DATA_URI";
    private NoteUploader noteUploader;

    public NoteUploaderJobService() {
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        AsyncTask<JobParameters,Void,Void> task = new AsyncTask<JobParameters, Void, Void>() {
            @Override
            protected Void doInBackground(JobParameters... jobParameters) {
                JobParameters jobParams = jobParameters[0];
                String stringDataUri = jobParams.getExtras().getString(EXTRA_DATA_URI);
                Uri data = Uri.parse(stringDataUri);
                noteUploader.doUpload(data);

                if (!noteUploader.isCanceled())
                    jobFinished(jobParams,true);

                return null;
            }
        };

        noteUploader = new NoteUploader(this);
        task.execute(params);

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        noteUploader.cancel();
        return true;
    }


}
