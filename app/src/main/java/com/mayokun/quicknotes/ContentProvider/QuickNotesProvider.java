package com.mayokun.quicknotes.ContentProvider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.mayokun.quicknotes.Data.DataBaseOpenHelper;
import com.mayokun.quicknotes.Utils.Constants;
import com.mayokun.quicknotes.Utils.Constants.CourseInfoEntry;


public class QuickNotesProvider extends ContentProvider {

    private DataBaseOpenHelper dataBaseOpenHelper;

    public static UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(ProviderContract.AUTHORITY, ProviderContract.Courses.PATH,0);
        uriMatcher.addURI(ProviderContract.AUTHORITY, ProviderContract.Notes.PATH,0);
    }

    public QuickNotesProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean onCreate() {
        dataBaseOpenHelper = new DataBaseOpenHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;
        SQLiteDatabase db = dataBaseOpenHelper.getReadableDatabase();
        cursor = db.query(CourseInfoEntry.TABLE_NAME, projection, selection, selectionArgs,
                null,null,sortOrder);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
