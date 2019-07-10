package com.mayokun.quicknotes.ContentProvider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.ContactsContract;

import com.mayokun.quicknotes.ContentProvider.ProviderContract.CourseIdColumns;
import com.mayokun.quicknotes.ContentProvider.ProviderContract.Courses;
import com.mayokun.quicknotes.ContentProvider.ProviderContract.Notes;
import com.mayokun.quicknotes.Data.DataBaseOpenHelper;
import com.mayokun.quicknotes.Utils.Constants;
import com.mayokun.quicknotes.Utils.Constants.CourseInfoEntry;
import com.mayokun.quicknotes.Utils.Constants.NoteInfoEntry;


public class QuickNotesProvider extends ContentProvider {

    private DataBaseOpenHelper dataBaseOpenHelper;

    public static UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(ProviderContract.AUTHORITY, Courses.PATH, Constants.COURSES_CODE);
        uriMatcher.addURI(ProviderContract.AUTHORITY, Notes.PATH, Constants.NOTES_CODE);
        uriMatcher.addURI(ProviderContract.AUTHORITY, Notes.EXPANDED_PATH, Constants.NOTES_EXPANDED_CODE);
        uriMatcher.addURI(ProviderContract.AUTHORITY, Notes.PATH + "/#", Constants.NOTES_ROW);
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
        String mimeType = null;
        int uriMatch = uriMatcher.match(uri);
        switch (uriMatch) {
            case Constants.COURSES_CODE:
                mimeType = ContentResolver.CURSOR_DIR_BASE_TYPE + "/"
                        + "vnd." + ProviderContract.AUTHORITY + "." + Courses.PATH;
                break;

            case Constants.NOTES_CODE:
                mimeType = ContentResolver.CURSOR_DIR_BASE_TYPE + "/"
                        + "vnd." + ProviderContract.AUTHORITY + "." + Notes.PATH;
                break;

            case Constants.NOTES_EXPANDED_CODE:
                mimeType = ContentResolver.CURSOR_DIR_BASE_TYPE + "/"
                        + ".vnd" + ProviderContract.AUTHORITY + "." + Notes.EXPANDED_PATH;
                break;
            case Constants.NOTES_ROW:
                mimeType = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/"
                        + ".vnd" + ProviderContract.AUTHORITY + "." + Notes.PATH;
                break;
        }

        return mimeType;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dataBaseOpenHelper.getWritableDatabase();
        long rowId = -1;
        Uri rowUri = null;
        int uriMatch = uriMatcher.match(uri);

        switch (uriMatch) {
            case Constants.NOTES_CODE:
                rowId = db.insert(NoteInfoEntry.TABLE_NAME, null, values);
                rowUri = ContentUris.withAppendedId(Notes.CONTENT_URI, rowId);
                break;

            case Constants.COURSES_CODE:
                rowId = db.insert(CourseInfoEntry.TABLE_NAME, null, values);
                rowUri = ContentUris.withAppendedId(Courses.CONTENT_URI, rowId);
                break;
            case Constants.NOTES_EXPANDED_CODE:
                //throw exception
                break;
        }

        return rowUri;
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

        int uriMatch = uriMatcher.match(uri);
        switch (uriMatch) {
            case Constants.COURSES_CODE:
                cursor = db.query(CourseInfoEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case Constants.NOTES_CODE:
                cursor = db.query(NoteInfoEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case Constants.NOTES_EXPANDED_CODE:
                cursor = noteExpandedQuery(db, projection, selection, selectionArgs, sortOrder);
                break;
            case Constants.NOTES_ROW:
                long rowId = ContentUris.parseId(uri);
                String rowSelection = NoteInfoEntry._ID + " = ?";
                String[] rowSelectionArgs = new String[]{Long.toString(rowId)};
                cursor = db.query(NoteInfoEntry.TABLE_NAME, projection, rowSelection, rowSelectionArgs,
                        null, null, null);
                break;
        }

        return cursor;
    }

    private Cursor noteExpandedQuery(SQLiteDatabase db, String[] projection, String selection,
                                     String[] selectionArgs, String sortOrder) {

        String[] columns = new String[projection.length];
        for (int idx = 0; idx < projection.length; idx++) {
            columns[idx] = projection[idx].equals(BaseColumns._ID) ||
                    projection[idx].equals(CourseIdColumns.COLUMN_COURSE_ID) ?
                    NoteInfoEntry.getQNames(projection[idx]) : projection[idx];
        }

        String tablesWithJoin = NoteInfoEntry.TABLE_NAME + " JOIN " +
                CourseInfoEntry.TABLE_NAME + " ON " +
                NoteInfoEntry.getQNames(NoteInfoEntry.COLUMN_COURSE_ID) + " = " +
                CourseInfoEntry.getQNames(CourseInfoEntry.COLUMN_COURSE_ID);

        return db.query(tablesWithJoin, columns, selection, selectionArgs, null, null, sortOrder);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
