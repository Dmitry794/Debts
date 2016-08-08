package by.dmitry.debts;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;

import java.io.File;
import java.io.FileOutputStream;
import java.util.*;


public class windowActivity extends Activity implements OnClickListener {

    TextView loader;
    ViewPager f;
    Button btAdd, btOption;
    ListView list, listK;
    RelativeLayout relativeLayoutV, relativeLayoutK;
    TextView txResult, resultV, resultK, pSumV, pSumK;
    private static final String TAG = "myLogs";


    int selectedItemList;
    int currentPage;

    ArrayList<Map<String, Object>> dataList;
    ArrayList<Map<String, Object>> dataListK;
    SimpleAdapter adapter, adapterK;
    DBHelper dbHelper;
    SQLiteDatabase db;
    String filename;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_window);
        txResult = (TextView) findViewById(R.id.txResult);

        loader = (TextView) findViewById(R.id.loader);

        btAdd = (Button) findViewById(R.id.bt_add);
        btAdd.setOnClickListener(this);
        btOption = (Button) findViewById(R.id.bt_option);
        btOption.setOnClickListener(this);

        f = (ViewPager) findViewById(R.id.pager);
        List<View> pages = new ArrayList<View>();

        LayoutInflater inflater1 = LayoutInflater.from(this);
        View page = inflater1.inflate(R.layout.page, null);
        list = (ListView) page.findViewById(R.id.list1);
//        list.setVisibility(View.VISIBLE);
        relativeLayoutV = (RelativeLayout) page.findViewById(R.id.rLayout);
        relativeLayoutV.setBackgroundColor(getResources().getColor(R.color.backColorMain));
        pSumV = (TextView) page.findViewById(R.id.p_sum);
        pSumV.setTextColor(getResources().getColor(R.color.textColor));
        resultV = (TextView) page.findViewById(R.id.tx_res);
        resultV.setText("0.00");
        resultV.setTextColor(getResources().getColor(R.color.textColor));
        pages.add(page);


        LayoutInflater inflater2 = LayoutInflater.from(this);
        View pageK = inflater2.inflate(R.layout.page, null);
        listK = (ListView) pageK.findViewById(R.id.list1);
//        listK.setVisibility(View.VISIBLE);
        relativeLayoutK = (RelativeLayout) pageK.findViewById(R.id.rLayout);
        relativeLayoutK.setBackgroundColor(getResources().getColor(R.color.backColorMainR));
        resultK = (TextView) pageK.findViewById(R.id.tx_res);
        pSumK = (TextView) pageK.findViewById(R.id.p_sum);
        pSumK.setTextColor(getResources().getColor(R.color.textColorR));
        resultK.setText("0.00");
        resultK.setTextColor(getResources().getColor(R.color.textColorR));

        pages.add(pageK);


        SimplePagerAdapter pagerAdapter = new SimplePagerAdapter(pages);
        f.setAdapter(pagerAdapter);
        f.setCurrentItem(0);
        currentPage = 0;

        f.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                currentPage = i;
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });


//        list = (ListView) findViewById(R.id.list);
        dataList = new ArrayList<>();
        dataListK = new ArrayList<>();

        String[] from = {"Hash", "Date", "Name", "Coast", "Count", "Cash", "Person"};
        int[] to = {R.id.itHashCode, R.id.itDate, R.id.itName, R.id.itCoast, R.id.itCount, R.id.itCash, R.id.itPerson};
        adapter = new SimpleAdapter(this, dataList, R.layout.item, from, to);
        adapterK = new SimpleAdapter(this, dataListK, R.layout.item_k, from, to);

        list.setAdapter(adapter);
        registerForContextMenu(list);

        listK.setAdapter(adapterK);
        registerForContextMenu(listK);

        allCashUpdate();
        dbHelper = new DBHelper(this);

        Intent intent = getIntent();
        filename = intent.getStringExtra("filename").toString();

        dbQueryTask mt = new dbQueryTask(dbQueryTask.DB_READ,filename);
        mt.execute();


    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.list1) {
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) menuInfo;
            selectedItemList = acmi.position;
            menu.add(0, 1, 0, "Редактировать");
            menu.add(0, 2, 0, "Удалить");
        }

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1: //Edit
                Map<String, Object> m;
                if (currentPage == 0)
                    m = dataList.get(selectedItemList);
                else m = dataListK.get(selectedItemList);

                Intent intent = new Intent(this, editActivity.class);

                intent.putExtra("Name", m.get("Name").toString());
                intent.putExtra("Count", m.get("Count").toString());
                intent.putExtra("Coast", m.get("Coast").toString());
                intent.putExtra("Person", currentPage);
                startActivityForResult(intent, 2);

                break;

            case 2: //Delete
                Map<String, Object> m_del;
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                switch (currentPage) {
                    case 0:
                        m_del = dataList.get(selectedItemList);
                        if (db.delete(filename, "hash=" + m_del.get("Hash").toString(), null) > 0) {

                            dataList.remove(selectedItemList);
                            adapter.notifyDataSetChanged();

                        }
                        break;
                    case 1:
                        m_del = dataListK.get(selectedItemList);
                        if (db.delete(filename, "hash=" + m_del.get("Hash").toString(), null) > 0) {

                            dataListK.remove(selectedItemList);
                            adapterK.notifyDataSetChanged();

                        }
                        break;

                }
                allCashUpdate();
                db.close();
                break;

        }
        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.opt_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mAdd:
                addItem();
                break;
            case R.id.mClear:
                clearList();
                db = dbHelper.getWritableDatabase();
                db.delete(filename, "", null);
                db.close();
                break;
            case R.id.mUpdate:
//                dbHelper.deletDB(this);
                break;
            case R.id.mSendImg:
                sendByViber(list);
                break;
            case R.id.mSetting:

                break;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_add:
                addItem();
                break;
            case R.id.bt_option:
                openOptionsMenu();
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
                        m.put("Hash", data.getIntExtra("Hash", 0));
                        m.put("Name", data.getStringExtra("Name"));
                        m.put("Date", data.getStringExtra("Date"));
                        m.put("Count", String.format(Locale.ENGLISH, "%.1f", data.getDoubleExtra("Count", 0)));

                        m.put("Coast", String.format(Locale.ENGLISH, "%.2f", data.getDoubleExtra("Coast", 0)));
                        m.put("Cash", String.format(Locale.ENGLISH, "%.2f", data.getDoubleExtra("Cash", 0)));
                        m.put("Person", String.format("%d", data.getIntExtra("Person", 0)));

                        if (data.getIntExtra("Person", 0) == 0) dataList.add(m);
                        else dataListK.add(m);

                        db = dbHelper.getWritableDatabase();
                        ContentValues cv = new ContentValues();
                        cv.put("hash", data.getIntExtra("Hash", 0));
                        cv.put("name", data.getStringExtra("Name"));
                        cv.put("date", data.getStringExtra("Date"));
                        cv.put("count", String.format(Locale.ENGLISH, "%.1f", data.getDoubleExtra("Count", 0)));

                        cv.put("coast", String.format(Locale.ENGLISH, "%.2f", data.getDoubleExtra("Coast", 0)));
                        cv.put("cash", String.format(Locale.ENGLISH, "%.2f", data.getDoubleExtra("Cash", 0)));
                        cv.put("person", data.getIntExtra("Person", 0));

                        db.insert(filename, null, cv);
                        db.close();
                        allCashUpdate();
                        f.setCurrentItem(data.getIntExtra("Person", 0));
                        break;

                    case RESULT_CANCELED:

                        Log.d(TAG, "canceled");
                        break;
                }
                break;
            case 2: //Edit
                switch (resultCode) {
                    case RESULT_OK:
                        Map<String, Object> m;

                        if (data.getIntExtra("Person", 0) == 0)
                            m = dataList.get(selectedItemList);
                        else
                            m = dataListK.get(selectedItemList);


                        m.put("Name", data.getStringExtra("Name"));
                        m.put("Count", String.format(Locale.ENGLISH, "%.1f", data.getDoubleExtra("Count", 0)));
                        m.put("Coast", String.format(Locale.ENGLISH, "%.2f", data.getDoubleExtra("Coast", 0)));
                        m.put("Cash", String.format(Locale.ENGLISH, "%.2f", data.getDoubleExtra("Cash", 0)));

                        db = dbHelper.getWritableDatabase();
                        ContentValues cv = new ContentValues();
                        cv.put("name", data.getStringExtra("Name"));
                        cv.put("count", String.format(Locale.ENGLISH, "%.1f", data.getDoubleExtra("Count", 0)));
                        cv.put("coast", String.format(Locale.ENGLISH, "%.2f", data.getDoubleExtra("Coast", 0)));
                        cv.put("cash", String.format(Locale.ENGLISH, "%.2f", data.getDoubleExtra("Cash", 0)));

                        db.update(filename, cv, "hash=" + m.get("Hash"), null);
                        adapter.notifyDataSetChanged();
                        adapterK.notifyDataSetChanged();
                        allCashUpdate();
                        break;

                    case RESULT_CANCELED:

                        Log.d(TAG, "canceled");
                        break;
                }

                break;
        }
    }

    void allCashUpdate() {
        Double sumV = new Double(0);
        Double sumK = new Double(0);

        for (Map<String, Object> m : dataList) {
            sumV += Double.valueOf((String) m.get("Cash"));
        }

        for (Map<String, Object> m : dataListK) {
            sumK += Double.valueOf((String) m.get("Cash"));
        }

        resultV.setText(String.format(Locale.ENGLISH, "%.2f", sumV));
        resultK.setText(String.format(Locale.ENGLISH, "%.2f", sumK));
        txResult.setText(String.format(Locale.ENGLISH, "%.2f", sumV - sumK));

    }

    void addItem() {
        Intent intent = new Intent(this, addActivity.class);
        intent.putExtra("Person", currentPage);
        startActivityForResult(intent, 1);
    }

    void clearList() {
        dataList.clear();
        dataListK.clear();
        adapter.notifyDataSetChanged();
        adapterK.notifyDataSetChanged();
        allCashUpdate();
    }

    String getStringPoints(String in, int count){
        StringBuffer out= new StringBuffer("...................");


        Log.e(TAG, String.valueOf(in.length()));

        out = out.replace(0, in.length()-1,in);

        return out.toString();
    }

    void sendByViber(View view) {

        Paint p = new Paint();
        int pixForItem = 42;
        int w = 610;
        int h = 70+(dataList.size() + dataListK.size()) * pixForItem;

        Log.d(TAG, w + "x" + h);

        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bmp);

        canvas.drawColor(getResources().getColor(R.color.backColor));

        p.setTextAlign(Paint.Align.LEFT);
        p.setAntiAlias(true);
        p.setFlags(Paint.ANTI_ALIAS_FLAG);

        int x = 40;
        int xName=20,xCash=w-25;
        int sizeN = 35, sizeR = 50;
        int y = 35, dy = 37;

        p.setTextSize(sizeN);
        p.setTypeface(Typeface.MONOSPACE);

        p.setColor(getResources().getColor(R.color.textColor));
        for (Map<String,Object> m: dataList) {
            p.setTextAlign(Paint.Align.LEFT);
            canvas.drawText(getStringPoints(m.get("Name").toString(), 33), xName, y, p);
            p.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText(m.get("Cash").toString(), xCash, y, p);
            y+=dy;
        }
        p.setColor(getResources().getColor(R.color.textColorR));
        for (Map<String,Object> m: dataListK) {
            p.setTextAlign(Paint.Align.LEFT);
            canvas.drawText(getStringPoints(m.get("Name").toString(), 28), xName, y, p);
            p.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText("-"+m.get("Cash").toString(), xCash, y, p);
            y+=dy;
        }
        y-=20;
        p.setColor(getResources().getColor(R.color.textColor));
        canvas.drawLine(xName, y, xCash, y, p);
        y+=1.5*dy;
        p.setFakeBoldText(true);
        p.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("ИТОГО:", xName, y, p);
        p.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(txResult.getText().toString(), xCash, y, p);

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
        private String name;

        public dbQueryTask(int action,String name) {

            super();
            this.action = action;
            this.name = name;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loader.setVisibility(View.VISIBLE);

        }

        @Override
        protected Void doInBackground(Void... params) {
            switch (action) {
                case DB_READ:
                    readDB(name);
                    break;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            loader.setVisibility(View.INVISIBLE);
            allCashUpdate();

        }

        void readDB(String dbName) {
            db = dbHelper.getWritableDatabase();
            Cursor c = db.query(dbName, null, null, null, null, null, null);

            if (c.moveToFirst()) {
                int hashColIndex = c.getColumnIndex("hash");
                int nameColIndex = c.getColumnIndex("name");
                int dateColIndex = c.getColumnIndex("date");
                int countColIndex = c.getColumnIndex("count");
                int coastColIndex = c.getColumnIndex("coast");
                int cashColIndex = c.getColumnIndex("cash");
                int personColIndex = c.getColumnIndex("person");

                do {
                    // получаем значения по номерам столбцов и пишем все в лог
                    Map<String, Object> m = new HashMap<String, Object>();
                    m.put("Hash", String.valueOf(c.getInt(hashColIndex)));
                    m.put("Name", c.getString(nameColIndex));
                    m.put("Date", c.getString(dateColIndex));
                    m.put("Count", String.format(Locale.ENGLISH, "%.1f", c.getDouble(countColIndex)));
                    m.put("Coast", String.format(Locale.ENGLISH, "%.2f", c.getDouble(coastColIndex)));
                    m.put("Cash", String.format(Locale.ENGLISH, "%.2f", c.getDouble(cashColIndex)));
                    m.put("Person", String.format("%d", c.getInt(personColIndex)));

                    if (c.getInt(personColIndex) == 0) dataList.add(m);
                    else dataListK.add(m);


                } while (c.moveToNext());

            } else
                Log.d(TAG, "0 rows");
            c.close();
        }
    }

}


