package am.amitm29.com.home.Database;

public class DbHelper {
    private static AppDatabase mDb;

    public static AppDatabase getDbInstance()
    {
        return mDb;
    }

    public static void setmDb(AppDatabase mDb) {
        DbHelper.mDb = mDb;
    }
}
