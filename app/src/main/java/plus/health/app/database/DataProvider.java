package plus.health.app.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

/**
 * Created by Arif on 16-Jul-16.
 */

public class DataProvider extends ContentProvider {

    private DatabaseHelper mHelper;
    private static final int USER = 1;
    private static final int USER_LIST = 2;
    private static final int REPORT_LIST = 3;
    private static final int REPORT = 4;
    private static final int PRESCRIPTION_LIST = 5;
    private static final int PRESCRIPTION = 6;
    private static final int DOCTOR = 7;
    private static final int DOCTOR_LIST = 8;
    private static final int MEDICATION_LIST = 9;
    private static final int MEDICATION = 10;
    //private static final int REPORT_VIEW_LIST = 11;
    //private static final int PRESC_VIEW_LIST = 12;
    private static final int DOCTOR_VIEW_LIST = 11;
    private static final UriMatcher URI_MATCHER;

    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(DataContract.AUTHORITY, "users", USER_LIST);
        URI_MATCHER.addURI(DataContract.AUTHORITY,"users/#",USER);
        URI_MATCHER.addURI(DataContract.AUTHORITY,"reports",REPORT_LIST);
        URI_MATCHER.addURI(DataContract.AUTHORITY,"reports/medication/#",REPORT);
        URI_MATCHER.addURI(DataContract.AUTHORITY,"prescriptions",PRESCRIPTION_LIST);
        URI_MATCHER.addURI(DataContract.AUTHORITY,"prescriptions/medication/#",PRESCRIPTION);
        URI_MATCHER.addURI(DataContract.AUTHORITY,"doctors",DOCTOR_LIST);
        URI_MATCHER.addURI(DataContract.AUTHORITY,"doctors/medication/#",DOCTOR);
        URI_MATCHER.addURI(DataContract.AUTHORITY,"medications",MEDICATION_LIST);
        URI_MATCHER.addURI(DataContract.AUTHORITY,"medications/#",MEDICATION);
        //URI_MATCHER.addURI(DataContract.AUTHORITY,"report_views",REPORT_VIEW_LIST);
        //URI_MATCHER.addURI(DataContract.AUTHORITY,"presc_views",PRESC_VIEW_LIST);
        URI_MATCHER.addURI(DataContract.AUTHORITY,"doctor_views",DOCTOR_VIEW_LIST);
    }

    @Override
    public boolean onCreate() {
        mHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        boolean useAuthorityUri = false;
        switch (URI_MATCHER.match(uri)) {
            case USER_LIST:
                builder.setTables(DataContract.Users.TABLE_USER);
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = DataContract.Users.SORT_ORDER_DEFAULT;
                }
                break;
            case USER:
                builder.setTables(DataContract.Users.TABLE_USER);
                builder.appendWhere(DataContract.Users.USER_ID + " = " + uri.getLastPathSegment());
                break;
            case REPORT_LIST:
                builder.setTables(DataContract.Reports.TABLE_REPORTS);
                break;
            case REPORT:
                builder.setTables(DataContract.Reports.TABLE_REPORTS);
                builder.appendWhere(DataContract.Reports.MEDICATION_ID + " = " + uri.getLastPathSegment());
                break;
            case PRESCRIPTION_LIST:
                builder.setTables(DataContract.Prescriptions.TABLE_PRESCRIPTIONS);
                break;
            case PRESCRIPTION:
                builder.setTables(DataContract.Prescriptions.TABLE_PRESCRIPTIONS);
                builder.appendWhere(DataContract.Prescriptions.MEDICATION_ID + " = " + uri.getLastPathSegment());
                break;
            case DOCTOR_LIST:
                builder.setTables(DataContract.Doctors.TABLE_DOCTORS);
                break;
            case DOCTOR:
                builder.setTables(DataContract.Doctors.TABLE_DOCTORS);
                builder.appendWhere(DataContract.Doctors.MEDICATION_ID + " = " + uri.getLastPathSegment());
                break;
            case MEDICATION_LIST:
                builder.setTables(DataContract.Medications.TABLE_MEDICATIONS);
                break;
            case DOCTOR_VIEW_LIST:
                builder.setTables(DataContract.DoctorView.VIEW_DOCTOR);
                break;
            /*case REPORT_VIEW_LIST:
                builder.setTables(DataContract.ReportView.VIEW_REPORT);
                break;
            case PRESC_VIEW_LIST:
                builder.setTables(DataContract.PrescView.VIEW_PRESC);
                break;*/
            default:
                break;
        }
        Cursor cursor = builder.query(db,projection,selection,selectionArgs,null,null,sortOrder);
        // if we want to be notified of any changes:
        if (useAuthorityUri) {
            cursor.setNotificationUri(getContext().getContentResolver(),DataContract.CONTENT_URI);
        }
        else {
            cursor.setNotificationUri(getContext().getContentResolver(),uri);
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case USER:
                return DataContract.Users.CONTENT_ITEM_TYPE;
            case USER_LIST:
                return DataContract.Users.CONTENT_TYPE;
            case REPORT:
                return DataContract.Reports.CONTENT_ITEM_TYPE;
            case REPORT_LIST:
                return DataContract.Reports.CONTENT_TYPE;
            case PRESCRIPTION:
                return DataContract.Prescriptions.CONTENT_ITEM_TYPE;
            case PRESCRIPTION_LIST:
                return DataContract.Prescriptions.CONTENT_TYPE;
            case DOCTOR:
                return DataContract.Doctors.CONTENT_ITEM_TYPE;
            case DOCTOR_LIST:
                return DataContract.Doctors.CONTENT_TYPE;
            case MEDICATION_LIST:
                return DataContract.Medications.CONTENT_TYPE;
            case DOCTOR_VIEW_LIST:
                return DataContract.DoctorView.CONTENT_TYPE;
            /*case REPORT_VIEW_LIST:
                return DataContract.ReportView.CONTENT_TYPE;
            case PRESC_VIEW_LIST:
                return DataContract.PrescView.CONTENT_TYPE;*/
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        if (URI_MATCHER.match(uri) == USER) {
            long id = db.insert(DataContract.Users.TABLE_USER, null, contentValues);
            return getUriForId(id, uri);
        }else if (URI_MATCHER.match(uri) == PRESCRIPTION_LIST) {
            long id = db.insert(DataContract.Prescriptions.TABLE_PRESCRIPTIONS, null, contentValues);
            return getUriForId(id, uri);
        }else if (URI_MATCHER.match(uri) == REPORT_LIST) {
            long id = db.insert(DataContract.Reports.TABLE_REPORTS, null, contentValues);
            return getUriForId(id, uri);
        }else if (URI_MATCHER.match(uri) == DOCTOR_LIST) {
            long id = db.insert(DataContract.Doctors.TABLE_DOCTORS, null, contentValues);
            return getUriForId(id, uri);
        }else if (URI_MATCHER.match(uri) == MEDICATION_LIST) {
            long id = db.insert(DataContract.Medications.TABLE_MEDICATIONS, null, contentValues);
            return getUriForId(id, uri);
        }

        return uri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        int delCount = 0;
        String idStr;
        String where;
        switch (URI_MATCHER.match(uri)) {
            case USER_LIST:
                delCount = db.delete(DataContract.Users.TABLE_USER,selection,selectionArgs);
                break;
            case USER:
                idStr = uri.getLastPathSegment();
                where = DataContract.Users.USER_ID + " = " + idStr;
                if (!TextUtils.isEmpty(selection)) {
                    where += " AND " + selection;
                }
                delCount = db.delete(DataContract.Users.TABLE_USER,where,selectionArgs);
                break;
            case REPORT_LIST:
                delCount = db.delete(DataContract.Reports.TABLE_REPORTS,selection,selectionArgs);
                break;
            case REPORT:
                idStr = uri.getLastPathSegment();
                where = DataContract.Reports.ID + " = " + idStr;
                if (!TextUtils.isEmpty(selection)) {
                    where += " AND " + selection;
                }
                delCount = db.delete(DataContract.Reports.TABLE_REPORTS,where,selectionArgs);
                break;
            case PRESCRIPTION_LIST:
                delCount = db.delete(DataContract.Prescriptions.TABLE_PRESCRIPTIONS,selection,selectionArgs);
                break;
            case PRESCRIPTION:
                idStr = uri.getLastPathSegment();
                where = DataContract.Prescriptions.ID + " = " + idStr;
                if (!TextUtils.isEmpty(selection)) {
                    where += " AND " + selection;
                }
                delCount = db.delete(DataContract.Prescriptions.TABLE_PRESCRIPTIONS,where,selectionArgs);
                break;
            case DOCTOR_LIST:
                delCount = db.delete(DataContract.Doctors.TABLE_DOCTORS,selection,selectionArgs);
                break;
            case DOCTOR:
                idStr = uri.getLastPathSegment();
                where = DataContract.Doctors.ID + " = " + idStr;
                if (!TextUtils.isEmpty(selection)) {
                    where += " AND " + selection;
                }
                delCount = db.delete(DataContract.Doctors.TABLE_DOCTORS,where,selectionArgs);
                break;
            case MEDICATION_LIST:
                delCount = db.delete(DataContract.Medications.TABLE_MEDICATIONS,selection,selectionArgs);
                break;
            default:
                // no support for deleting photos or entities –
                // photos are deleted by a trigger when the item is deleted
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        // notify all listeners of changes:
        if (delCount > 0 ) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return delCount;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        int updateCount = 0;
        String idStr,where;
        switch (URI_MATCHER.match(uri)) {
            case USER_LIST:
                updateCount = db.update(DataContract.Users.TABLE_USER,contentValues,selection,selectionArgs);
                break;
            case USER:
                idStr = uri.getLastPathSegment();
                where = DataContract.Users.USER_ID + " = " + idStr;
                if (!TextUtils.isEmpty(selection)) {
                    where += " AND " + selection;
                }
                updateCount = db.update(DataContract.Users.TABLE_USER,contentValues,where,selectionArgs);
                break;
            case REPORT_LIST:
                updateCount = db.update(DataContract.Reports.TABLE_REPORTS,contentValues,selection,selectionArgs);
                break;
            case REPORT:
                idStr = uri.getLastPathSegment();
                where = DataContract.Reports.ID + " = " + idStr;
                if (!TextUtils.isEmpty(selection)) {
                    where += " AND " + selection;
                }
                updateCount = db.update(DataContract.Reports.TABLE_REPORTS,contentValues,where,selectionArgs);
                break;
            case PRESCRIPTION_LIST:
                updateCount = db.update(DataContract.Prescriptions.TABLE_PRESCRIPTIONS,contentValues,selection,selectionArgs);
                break;
            case PRESCRIPTION:
                idStr = uri.getLastPathSegment();
                where = DataContract.Prescriptions.ID + " = " + idStr;
                if (!TextUtils.isEmpty(selection)) {
                    where += " AND " + selection;
                }
                updateCount = db.update(DataContract.Prescriptions.TABLE_PRESCRIPTIONS,contentValues,where,selectionArgs);
                break;
            case DOCTOR_LIST:
                updateCount = db.update(DataContract.Doctors.TABLE_DOCTORS,contentValues,selection,selectionArgs);
                break;
            case DOCTOR:
                idStr = uri.getLastPathSegment();
                where = DataContract.Doctors.ID + " = " + idStr;
                if (!TextUtils.isEmpty(selection)) {
                    where += " AND " + selection;
                }
                updateCount = db.update(DataContract.Doctors.TABLE_DOCTORS,contentValues,where,selectionArgs);
                break;
            case MEDICATION_LIST:
                updateCount = db.update(DataContract.Medications.TABLE_MEDICATIONS,contentValues,selection,selectionArgs);
                break;
            default:
                // no support for updating photos or entities!
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        // notify all listeners of changes:
        if (updateCount > 0 ) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return updateCount;
    }

    private Uri getUriForId(long id, Uri uri) {
        if (id > 0) {
            Uri itemUri = ContentUris.withAppendedId(uri, id);
            getContext().getContentResolver().notifyChange(itemUri, null);
            return itemUri;
        }
        return null;
    }
}