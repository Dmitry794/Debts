package by.dmitry.debts;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Dmitry on 04.08.16.
 */
public class DBHelper extends SQLiteOpenHelper {

    final static String DB_NAME = "debtDB";
    final static String TABLE_NAME = "debts";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqlDB) {
        Log.d("myLogs", "--- onCreate database ---");
        // создаем таблицу с полями
        sqlDB.execSQL("create table "+TABLE_NAME+" ("
                + "id integer primary key autoincrement,"
                + "name text,"
                + "date text,"
                + "count double,"
                + "coast double,"
                + "cash double"

                + ");");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void deletDB(Context context) {
        context.deleteDatabase(DB_NAME);
        Log.d("myLogs", "DB deleted");
    }

    String getDbName() {
        return DB_NAME;
    }

    public static String getTableName() {
        return TABLE_NAME;
    }
}
