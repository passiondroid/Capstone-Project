package plus.health.app.database;

import android.content.ContentResolver;
import android.net.Uri;

/**
 * Created by Arif on 16-Jul-16.
 */

public class DataContract {

    public static final String AUTHORITY = "plus.health.app";
    /**
     * The content URI for the top-level
     */
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final class Users{

        public static final Uri CONTENT_URI = Uri.withAppendedPath(DataContract.CONTENT_URI,"users");

        // The mime type of a directory of users.
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.plus.health.app.user";
        //The mime type of a single user.
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.plus.health.app.user";
        public static String TABLE_USER = "USERS";
        public static String USER_ID = "USER_ID";
        public static String NAME = "NAME";
        public static String EMAIL = "EMAIL";
        public static String PHOTO_URL = "PHOTO_URL";

        // A projection of all columns in the items table.
        public static final String[] PROJECTION_ALL = {USER_ID, NAME, EMAIL,PHOTO_URL};

        //The default sort order for queries containing NAME fields.
        public static final String SORT_ORDER_DEFAULT = NAME + " ASC";
    }

    public static final class Prescriptions{

        public static final Uri CONTENT_URI = Uri.withAppendedPath(DataContract.CONTENT_URI,"prescriptions");

        // The mime type of a directory of presc.
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.plus.health.app.presc";
        //The mime type of a single presc.
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.plus.health.app.presc";
        public static String TABLE_PRESCRIPTIONS = "PRESCRIPTIONS";
        public static String ID = "ID";
        public static String MEDICATION_ID = "MEDICATION_ID";
        public static String NAME = "NAME";
        public static String PATH = "PATH";
        public static String TYPE = "TYPE";
        public static String STATUS = "STATUS";
        public static String DATE_MODIFIED = "DATE_MODIFIED";

        // A projection of all columns in the items table.
        public static final String[] PROJECTION_ALL = {ID, NAME, PATH,TYPE,STATUS, DATE_MODIFIED};

    }

    public static final class Reports{

        public static final Uri CONTENT_URI = Uri.withAppendedPath(DataContract.CONTENT_URI,"reports");

        // The mime type of a directory of presc.
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.plus.health.app.report";
        //The mime type of a single presc.
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.plus.health.app.report";
        public static String TABLE_REPORTS = "REPORTS";
        public static String ID = "ID";
        public static String MEDICATION_ID = "MEDICATION_ID";
        public static String NAME = "NAME";
        public static String PATH = "PATH";
        public static String TYPE = "TYPE";
        public static String STATUS = "STATUS";
        public static String DATE_MODIFIED = "DATE_MODIFIED";

        // A projection of all columns in the items table.
        public static final String[] PROJECTION_ALL = {ID, NAME, PATH,TYPE,STATUS, DATE_MODIFIED};

    }

    public static final class Doctors{

        public static final Uri CONTENT_URI = Uri.withAppendedPath(DataContract.CONTENT_URI,"doctors");

        // The mime type of a directory of presc.
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.plus.health.app.doctor";
        //The mime type of a single presc.
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.plus.health.app.doctor";
        public static String TABLE_DOCTORS = "DOCTORS";
        public static String ID = "ID";
        public static String MEDICATION_ID = "MEDICATION_ID";
        public static String NAME = "NAME";
        public static String PHONE_NUMBER = "PHONE_NUMBER";
        public static String EMAIL = "EMAIL";
        public static String ADDRESS = "ADDRESS";

        // A projection of all columns in the items table.
        public static final String[] PROJECTION_ALL = {ID, NAME, PHONE_NUMBER,EMAIL,ADDRESS};

    }

    public static final class Medications{

        public static final Uri CONTENT_URI = Uri.withAppendedPath(DataContract.CONTENT_URI,"medications");

        // The mime type of a directory of presc.
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.plus.health.app.medication";
        //The mime type of a single presc.
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.plus.health.app.medication";
        public static String TABLE_MEDICATIONS = "MEDICATIONS";
        public static String ID = "ID";
        public static String PROBLEM = "PROBLEM";
        public static String DATE_MODIFIED = "DATE_MODIFIED";
        public static String STATUS = "STATUS";

        public static final String[] PROJECTION_ALL = {ID, PROBLEM, DATE_MODIFIED, STATUS};

    }

    public static final class DoctorView{

        public static final Uri CONTENT_URI = Uri.withAppendedPath(DataContract.CONTENT_URI,"doctor_views");

        // The mime type of a directory of presc.
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.plus.health.app.doctor_view";
        //The mime type of a single presc.
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.plus.health.app.doctor_view";
        public static String VIEW_DOCTOR = "DOCTOR_VIEW";
        public static String ID = "ID";
        public static String DOCTOR_MEDICATION_ID = "DID";
        public static String PROBLEM = "PROBLEM";
        public static String DATE_MODIFIED = "DATE_MODIFIED";
        public static String NAME = "NAME";
        public static String PHONE_NUMBER = "PHONE_NUMBER";
        public static String EMAIL = "EMAIL";
        public static String ADDRESS = "ADDRESS";

    }


}
