package by.dmitry.debts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import java.text.SimpleDateFormat;
import java.util.Date;

public class addActivity extends Activity implements View.OnClickListener{
    EditText txName,txCount,txCoast;
    RadioButton rbKsu,rbVi;
    Button btSend,btCansel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.add);

        txName = (EditText) findViewById(R.id.txName);
        txCount = (EditText) findViewById(R.id.txCount);
        txCoast = (EditText) findViewById(R.id.txCoast);
        btSend = (Button) findViewById(R.id.bt_send);
        btSend.setOnClickListener(this);
        btCansel = (Button) findViewById(R.id.bt_cancel);
        btCansel.setOnClickListener(this);

        rbKsu = (RadioButton) findViewById(R.id.rbKsu);
        rbVi = (RadioButton) findViewById(R.id.rbVi);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bt_send:
                Intent intent = new Intent();
                intent.putExtra("Name", txName.getText().toString());
                intent.putExtra("Count", Double.valueOf(txCount.getText().toString()));
                intent.putExtra("Coast",  Double.valueOf(txCoast.getText().toString()));
                intent.putExtra("Who", rbKsu.isChecked());
                intent.putExtra("Cash", Double.valueOf(txCount.getText().toString()) * Double.valueOf(txCoast.getText().toString()));
             
                SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy   hh:mm:ss");
                String date = df.format(new Date(System.currentTimeMillis()));
                intent.putExtra("Date", date);

                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.bt_cancel:
                setResult(RESULT_CANCELED, null);
                finish();
                break;

        }
    }
}
