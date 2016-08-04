package by.dmitry.debts;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.Environment;
import android.util.Log;
import android.view.*;
import android.view.View.*;
import android.widget.*;

import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import android.graphics.*;

import static android.content.Intent.ACTION_VIEW;

public class mainActivity extends Activity implements OnClickListener {

    Button btn_add;
    ListView list;
    TextView text, txResult;
    private static final String TAG = "myLogs";

    int selectedItemList;

    ArrayList<Map<String, Object>> dataList;
    SimpleAdapter adapter;
    DBHelper dbHelper;
    SQLiteDatabase db;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        btn_add = (Button) findViewById(R.id.bt_add);
        btn_add.setOnClickListener(this);
        text = (TextView) findViewById(R.id.text);
        txResult = (TextView) findViewById(R.id.txResult);
        registerForContextMenu(text);

        list = (ListView) findViewById(R.id.list);
        dataList = new ArrayList<>();

        String[] from = {"Hash", "Date", "Name", "Coast", "Count", "Cash"};
        int[] to = {R.id.itHashCode, R.id.itDate, R.id.itName, R.id.itCoast, R.id.itCount, R.id.itCash};
        adapter = new SimpleAdapter(this, dataList, R.layout.item, from, to);

        list.setAdapter(adapter);
        registerForContextMenu(list);

        allCashUpdate();
        dbHelper = new DBHelper(this);

        dbQueryTask mt = new dbQueryTask(dbQueryTask.DB_READ);
        mt.execute();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.list) {

            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) menuInfo;
            selectedItemList = acmi.position;

            menu.add(0, 1, 0, "Редактировать");
            menu.add(0, 2, 0, "Удалить");
            text.setText(String.valueOf(selectedItemList));
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1: //Edit
                Intent intent = new Intent(this, editActivity.class);
                Map<String, Object> m = dataList.get(selectedItemList);
                intent.putExtra("Name", m.get("Name").toString());
                intent.putExtra("Count", m.get("Count").toString());
                intent.putExtra("Coast", m.get("Coast").toString());

                startActivityForResult(intent, 2);

                break;

            case 2: //Delete
                Map<String, Object> m_del = dataList.get(selectedItemList);

                SQLiteDatabase db = dbHelper.getWritableDatabase();

                if (db.delete(DBHelper.TABLE_NAME, "hash=" + m_del.get("Hash").toString(), null)>0){
                dataList.remove(selectedItemList);
                adapter.notifyDataSetChanged();
                allCashUpdate();
                }
                db.close();
                break;

        }
        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*menu.add(0, 0, 0, "Добавить запись");
        menu.add(0, 1, 0, "Очистить список");
        menu.add(0, 2, 2, "Настройки");
        menu.add(0, 3, 3, "Обновить");
        menu.add(0, 4, 4, "Сформировать и отправить чек");*/
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.opt_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        StringBuilder sb = new StringBuilder();

        // Выведем в TextView информацию о нажатом пункте меню
        sb.append("Item Menu");
        sb.append("\r\n groupId: " + String.valueOf(item.getGroupId()));
        sb.append("\r\n itemId: " + String.valueOf(item.getItemId()));
        sb.append("\r\n order: " + String.valueOf(item.getOrder()));
        sb.append("\r\n title: " + item.getTitle());
        text.setText(sb.toString());
        switch (item.getItemId()) {
            case R.id.mAdd:
                addItem();
                break;
            case R.id.mClear:
                clearList();
                db = dbHelper.getWritableDatabase();
                db.delete(DBHelper.TABLE_NAME, "", null);
                db.close();
                break;
            case R.id.mUpdate:
                dbHelper.deletDB(this);
                break;
            case R.id.mSendImg:
                sendByViber(list);
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_add:
                db = dbHelper.getWritableDatabase();
                ContentValues cv = new ContentValues();
                cv.put("hash", 12345678);
                cv.put("name", "Творожок");
                cv.put("date", "04.08.2016");
                cv.put("count", 4);
                cv.put("coast", 0.69);
                cv.put("cash", 2.76);
                long rowID = db.insert("debts", null, cv);
                db.close();
                text.setText(String.valueOf(rowID));
                break;


        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1: //Add
                switch (resultCode) {
                    case RESULT_OK:

                        Map<String, Object> m = new HashMap<String, Object>();
                        m.put("Hash", data.getIntExtra("Hash",0));
                        m.put("Name", data.getStringExtra("Name"));
                        m.put("Date", data.getStringExtra("Date"));
                        m.put("Count", String.format(Locale.ENGLISH, "%.2f", data.getDoubleExtra("Count", 0)));
                        m.put("Coast", String.format(Locale.ENGLISH, "%.2f", data.getDoubleExtra("Coast", 0)));
                        m.put("Cash", String.format(Locale.ENGLISH, "%.2f", data.getDoubleExtra("Cash", 0)));
                        dataList.add(m);

                        db = dbHelper.getWritableDatabase();
                        ContentValues cv = new ContentValues();
                        cv.put("hash", data.getIntExtra("Hash",0));
                        cv.put("name", data.getStringExtra("Name"));
                        cv.put("date", data.getStringExtra("Date"));
                        cv.put("count", String.format(Locale.ENGLISH, "%.2f", data.getDoubleExtra("Count", 0)));
                        cv.put("coast", String.format(Locale.ENGLISH, "%.2f", data.getDoubleExtra("Coast", 0)));
                        cv.put("cash", String.format(Locale.ENGLISH, "%.2f", data.getDoubleExtra("Cash", 0)));
                        db.insert("debts", null, cv);
                        db.close();
                        allCashUpdate();
                        break;

                    case RESULT_CANCELED:
                        text.setText("RESULT_CANCELED");
                        Log.d(TAG, "canceled");
                        break;
                }
                break;
            case 2: //Edit
                switch (resultCode) {
                    case RESULT_OK:

                        Map<String, Object> m = dataList.get(selectedItemList);
                        m.put("Name", data.getStringExtra("Name"));
                        m.put("Count", String.format(Locale.ENGLISH, "%.2f", data.getDoubleExtra("Count", 0)));
                        m.put("Coast", String.format(Locale.ENGLISH, "%.2f", data.getDoubleExtra("Coast", 0)));
                        m.put("Cash", String.format(Locale.ENGLISH, "%.2f", data.getDoubleExtra("Cash", 0)));

                        db = dbHelper.getWritableDatabase();
                        ContentValues cv = new ContentValues();
                        cv.put("name", data.getStringExtra("Name"));
                        cv.put("count", String.format(Locale.ENGLISH, "%.2f", data.getDoubleExtra("Count", 0)));
                        cv.put("coast", String.format(Locale.ENGLISH, "%.2f", data.getDoubleExtra("Coast", 0)));
                        cv.put("cash", String.format(Locale.ENGLISH, "%.2f", data.getDoubleExtra("Cash", 0)));

                        db.update(DBHelper.TABLE_NAME, cv, "hash="+m.get("Hash"), null);








                        adapter.notifyDataSetChanged();
                        allCashUpdate();
                        break;

                    case RESULT_CANCELED:
                        text.setText("RESULT_CANCELED");
                        Log.d(TAG, "canceled");
                        break;
                }

                break;
        }
    }

    void allCashUpdate() {
        Double sum = new Double(0);

        for (Map<String, Object> m : dataList) {
            sum += Double.valueOf((String) m.get("Cash"));
        }
        txResult.setText(String.format(Locale.ENGLISH, "%.2f", sum));
    }

    void addItem() {
        Intent intent = new Intent(this, addActivity.class);
        startActivityForResult(intent, 1);
    }

    void clearList() {
        dataList.clear();
        adapter.notifyDataSetChanged();
        allCashUpdate();
    }

    void sendByViber(View view) {
        Bitmap bmp = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.RGB_565);
        Canvas cnv = new Canvas(bmp);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null) {
            //draw background drawable
            bgDrawable.draw(cnv);
        } else {
            //draw white background
            cnv.drawColor(Color.WHITE);
        }
        view.draw(cnv);

        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "imgDebt.png");
        Log.d(TAG, file.toString());
        try {
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file);
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            } finally {
                if (fos != null) fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/gif");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        intent.setPackage("com.viber.voip");
        startActivity(intent);
    }

    class dbQueryTask extends AsyncTask<Void, Void, Void> {
        int action;
        public final static int DB_READ = 1;

        public dbQueryTask(int action) {

            super();
            this.action = action;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params) {
            switch (action)
            {
                case DB_READ: readDB();break;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            allCashUpdate();

        }

        void readDB() {
            db = dbHelper.getWritableDatabase();
            Cursor c = db.query(dbHelper.getTableName(), null, null, null, null, null, null);

            if (c.moveToFirst()) {
                int hashColIndex = c.getColumnIndex("hash");
                int nameColIndex = c.getColumnIndex("name");
                int dateColIndex = c.getColumnIndex("date");
                int countColIndex = c.getColumnIndex("count");
                int coastColIndex = c.getColumnIndex("coast");
                int cashColIndex = c.getColumnIndex("cash");


                do {
                    // получаем значения по номерам столбцов и пишем все в лог
                    Map<String, Object> m = new HashMap<String, Object>();
                    m.put("Hash", String.valueOf(c.getInt(hashColIndex)));
                    m.put("Name", c.getString(nameColIndex));
                    m.put("Date", c.getString(dateColIndex));
                    m.put("Count", String.format(Locale.ENGLISH, "%.2f", c.getDouble(countColIndex)));
                    m.put("Coast", String.format(Locale.ENGLISH, "%.2f", c.getDouble(coastColIndex)));
                    m.put("Cash", String.format(Locale.ENGLISH, "%.2f", c.getDouble(cashColIndex)));
                    dataList.add(m);

                 /*   try {
                        TimeUnit.MILLISECONDS.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }*/

                } while (c.moveToNext());

            } else
                Log.d(TAG, "0 rows");
            c.close();
        }
    }
}
