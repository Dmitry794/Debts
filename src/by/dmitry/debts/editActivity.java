package by.dmitry.debts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.*;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;


public class editActivity extends Activity implements OnClickListener {
    EditText txedName, txedCount, txedCoast;
    int person;
    Button btEdit, btCanseled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.edit);

        txedName = (EditText) findViewById(R.id.txedName);
        txedCount = (EditText) findViewById(R.id.txedCount);
        txedCoast = (EditText) findViewById(R.id.txedCoast);
        btEdit = (Button) findViewById(R.id.bt_edit);
        btEdit.setOnClickListener(this);
        btCanseled = (Button) findViewById(R.id.bt_cancel_ed);
        btCanseled.setOnClickListener(this);


        Intent intent = getIntent();

        txedName.setText(intent.getStringExtra("Name"));
        txedCount.setText(intent.getStringExtra("Count"));
        txedCoast.setText(intent.getStringExtra("Coast"));
        person = intent.getIntExtra("Person", 0);

        //  txedName.setSelectAllOnFocus(true);

        txedName.setOnClickListener(this);
        txedCoast.setOnClickListener(this);
        txedCount.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_cancel_ed:
                setResult(RESULT_CANCELED, null);
                finish();
                break;
            case R.id.bt_edit:
                try {
                    Intent intent = new Intent();
                    intent.putExtra("Name", txedName.getText().toString());
                    intent.putExtra("Count", Double.valueOf(txedCount.getText().toString()));
                    intent.putExtra("Coast", Double.valueOf(txedCoast.getText().toString()));
                    intent.putExtra("Cash", Double.valueOf(txedCount.getText().toString()) * Double.valueOf(txedCoast.getText().toString()));
                    intent.putExtra("Person", person);
                    setResult(RESULT_OK, intent);
                    finish();
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.txedName:
            case R.id.txedCoast:
            case R.id.txedCount:
                ((EditText) view).setSelection(0, ((EditText) view).getText().length());
                break;
        }
    }
}
