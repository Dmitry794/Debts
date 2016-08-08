package by.dmitry.debts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.*;

import java.text.SimpleDateFormat;
import java.util.Date;

public class addActivity extends Activity implements View.OnClickListener {
    EditText txName, txCount, txCoast;
    Button btSend, btCansel;
    Spinner chooseBuyer;
    Intent recievedIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.add);

        txName = (EditText) findViewById(R.id.txName);

        txCount = (EditText) findViewById(R.id.txCount);
        txCount.setOnClickListener(this);
        txCoast = (EditText) findViewById(R.id.txCoast);
        btSend = (Button) findViewById(R.id.bt_send);
        btSend.setOnClickListener(this);
        btCansel = (Button) findViewById(R.id.bt_cancel);
        btCansel.setOnClickListener(this);

        String[] from = {"Викуля", "Ксюша"};
        chooseBuyer = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.my_spinner_item, from);
        adapter.setDropDownViewResource(R.layout.my_spinner_dropdown_item);
        chooseBuyer.setAdapter(adapter);
        recievedIntent = getIntent();



    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_send:
                try {
                    Intent intent = new Intent();
                    intent.putExtra("Name", txName.getText().toString());
                    intent.putExtra("Count", Double.valueOf(txCount.getText().toString()));
                    intent.putExtra("Coast", Double.valueOf(txCoast.getText().toString()));
                    intent.putExtra("Cash", Double.valueOf(txCount.getText().toString()) * Double.valueOf(txCoast.getText().toString()));

                    SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy            hh:mm:ss");
                    String date = df.format(new Date(System.currentTimeMillis()));
                    intent.putExtra("Date", date);
                    intent.putExtra("Hash", intent.hashCode());
                    intent.putExtra("Person", recievedIntent.getIntExtra("Person",0));

                    setResult(RESULT_OK, intent);
                    finish();
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.bt_cancel:
                setResult(RESULT_CANCELED, null);
                finish();
                break;
            case R.id.txCount:
                ((EditText) view).setSelection(0, ((EditText) view).getText().length());
                break;
        }
    }
}
