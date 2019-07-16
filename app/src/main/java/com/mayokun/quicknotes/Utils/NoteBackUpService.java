package com.mayokun.quicknotes.Utils;

import android.app.IntentService;
import android.content.Intent;

import com.mayokun.quicknotes.Data.NoteBackup;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class NoteBackUpService extends IntentService {
    public static final String EXTRA_COURSE_ID = "com.mayokun.quicknotes.Utils.extra.COURSE_ID";

    public NoteBackUpService() {
        super("NoteBackUpService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String backUpCourseId = intent.getStringExtra(EXTRA_COURSE_ID);
            NoteBackup.doBackup(this,backUpCourseId);
        }
    }

}
