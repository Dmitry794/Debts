package by.dmitry.debts;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;

import java.util.ArrayList;


public class mainActivity extends Activity implements OnClickListener {


    DBHelper dbHelper;
    SQLiteDatabase db;
    TableLayout mainDesc;
    Button btAdd;
    // FileIco[] files;
    // ArrayList<String> fileList;
    ArrayList<FileIco> f;
    int currentFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        btAdd = (Button) findViewById(R.id.bt_add_file);
        btAdd.setOnClickListener(this);

        mainDesc = (TableLayout) findViewById(R.id.main_desc);

        dbHelper = new DBHelper(this);


        f = new ArrayList<FileIco>();
        refreshDesktop();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        for (FileIco file : f) {
            if (v.getId() == file.getId()) {
                currentFile = f.indexOf(file);
                menu.add(0, 1, 0, "Переименовать");
                menu.add(0, 0, 0, "Удалить");
                Log.d("myLogs", String.valueOf(currentFile));
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0: //delete file
                mainDesc.removeView(f.get(currentFile));
                db = dbHelper.getWritableDatabase();
                db.execSQL("DROP TABLE IF EXISTS " + f.get(currentFile).getName().replace(' ', '_') + ";");
                f.remove(currentFile);
                refreshDesktop();
                break;
            case 1: //rename file

                Intent intent = new Intent(this, renameFile.class);
                intent.putExtra("name", f.get(currentFile).getName().toString());
                startActivityForResult(intent, 3);
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {  //new File(table)
                    boolean isExist = false;
                    for (FileIco file : f) {
                        if (file.getName().toString().equals(data.getStringExtra("name").toString())) {

                            isExist = true;
                            break;
                        }
                    }

                    if (!isExist) {


                        db = dbHelper.getWritableDatabase();

                        db.execSQL("create table " + data.getStringExtra("name").toString().replace(' ', '_') + " ("
                                + "id integer primary key autoincrement,"
                                + "hash integer,"
                                + "name text,"
                                + "date text,"
                                + "count double,"
                                + "coast double,"
                                + "cash double, "
                                + "person int"

                                + ");");
                        Log.d("myLogs", "add new file - OK");
                        db.close();
                        refreshDesktop();
                    } else Toast.makeText(this, "Файл с таким именем уже существует", Toast.LENGTH_LONG);
                }
                break;
            case 3: //rename file
                if (resultCode == RESULT_OK) {

                    boolean isExist = false;
                    for (FileIco file : f) {
                        if (file.getName().toString().equals(data.getStringExtra("name").toString())) {

                            isExist = true;
                            break;
                        }
                    }

                    if (!isExist) {

                        db = dbHelper.getWritableDatabase();
                        db.execSQL("ALTER TABLE " + f.get(currentFile).getName().replace(' ', '_') + " RENAME TO " + data.getStringExtra("name").replace(' ', '_') + ";");

                        f.get(currentFile).setName(data.getStringExtra("name").toString());

                        refreshDesktop();
                    } else Toast.makeText(this, "Файл с таким именем уже существует", Toast.LENGTH_LONG);
                }
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_add_file:
                Intent intent = new Intent(this, newFile.class);
                startActivityForResult(intent, 0);
                break;
        }

        for (FileIco file : f) {
            if (view.getId() == file.getId()) {
                Intent intent = new Intent(getApplicationContext(), windowActivity.class);
                intent.putExtra("filename", file.getName().replace(' ', '_'));
                startActivity(intent);
            }
        }


    }

    void refreshDesktop() {

        int picSize = 100;
        WindowManager w = getWindowManager();
        Display d = w.getDefaultDisplay();
        int n = d.getWidth() / (picSize * 2);
        Log.d("myLogs", String.valueOf(d.getWidth()));
        Log.d("myLogs", String.valueOf(n));


        mainDesc.removeAllViews();
        f.clear();

        db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                if (!((c.getString(0).toString().equals("android_metadata")) || (c.getString(0).equals("sqlite_sequence")))) {

                    f.add(new FileIco(this, c.getString(0).toString().replace('_', ' ')));
                    Log.d("myLogs", c.getString(0).toString());
                }
                c.moveToNext();
            }
        }

        TableRow tr = new TableRow(this);
        tr.setGravity(Gravity.CENTER_HORIZONTAL);

        for (int i = 0; i < f.size(); i++) {


            f.get(i).setPadding(15, 15, 15, 15);
            f.get(i).setId(f.get(i).hashCode());
            f.get(i).setOnClickListener(this);
            registerForContextMenu(f.get(i));
            tr.addView(f.get(i));

            if (((i + 1) % n) == 0) {
                mainDesc.addView(tr);
                tr = new TableRow(this);
                tr.setGravity(Gravity.CENTER_HORIZONTAL);
            }

            if (i == (f.size() - 1)) mainDesc.addView(tr);
        }

        db.close();

    }

    class FileIco extends LinearLayout {
        String name;
        TextView tx;

        public FileIco(Context context, String name) {
            super(context);
            this.name = name;
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.pic_file, this);
            tx = (TextView) findViewById(R.id.title);
            tx.setText(name);

        }

        public String getName() {
            return name;
        }

        public void setName(String new_name) {
            name = new_name;
            tx.setText(name);
        }
    }
}


