package by.dmitry.debts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.*;
import android.view.View.*;
import android.widget.*;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class mainActivity extends Activity implements OnClickListener {

    Button btn_add;
    ListView list;
    TextView text, txResult;
    private static final String TAG = "myLogs";

    int selectedItemList;

    ArrayList<Map<String, Object>> dataList;
    SimpleAdapter adapter;


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

        String[] from = {"Date", "Name", "Coast", "Count", "Cash"};
        int[] to = {R.id.itDate, R.id.itName, R.id.itCoast, R.id.itCount, R.id.itCash};
        adapter = new SimpleAdapter(this, dataList, R.layout.item, from, to);

        list.setAdapter(adapter);
        registerForContextMenu(list);

        allCashUpdate();


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
                dataList.remove(selectedItemList);
                adapter.notifyDataSetChanged();
                allCashUpdate();
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
        mi.inflate(R.menu.opt_menu,menu);

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
            case 0:
                Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT);
         //       text.setText(item.getTitle());
                break;
            case 1:
//                Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT);

            //    text.setText(item.getTitle());
                break;
            case 2:
                Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT);
           //     text.setText(item.getTitle());

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_add:
                Intent intent = new Intent(this, addActivity.class);
                startActivityForResult(intent, 1);
                break;


        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                switch (resultCode) {
                    case RESULT_OK:

                        Map<String, Object> m = new HashMap<String, Object>();
                        m.put("Name", data.getStringExtra("Name"));
                        m.put("Date", data.getStringExtra("Date"));
                        m.put("Count", String.format(Locale.ENGLISH, "%.2f", data.getDoubleExtra("Count", 0)));
                        m.put("Coast", String.format(Locale.ENGLISH, "%.2f", data.getDoubleExtra("Coast", 0)));
                        m.put("Cash", String.format(Locale.ENGLISH, "%.2f", data.getDoubleExtra("Cash", 0)));
                        dataList.add(m);

                        text.setText(String.valueOf(m.hashCode()));
                        allCashUpdate();
                        break;

                    case RESULT_CANCELED:
                        text.setText("RESULT_CANCELED");
                        Log.d(TAG, "canceled");
                        break;
                }
                break;
            case 2:
                switch (resultCode) {
                    case RESULT_OK:

                        Map<String, Object> m = dataList.get(selectedItemList);
                        m.put("Name", data.getStringExtra("Name"));
                        m.put("Count", String.format(Locale.ENGLISH, "%.2f", data.getDoubleExtra("Count", 0)));
                        m.put("Coast", String.format(Locale.ENGLISH, "%.2f", data.getDoubleExtra("Coast", 0)));
                        m.put("Cash", String.format(Locale.ENGLISH, "%.2f", data.getDoubleExtra("Cash", 0)));
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
}
