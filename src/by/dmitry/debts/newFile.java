package by.dmitry.debts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import java.sql.ResultSet;


public class newFile extends Activity implements View.OnClickListener{

    Button btNewFile;
    Button btCancel;
    EditText newName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.new_file);
        btNewFile = (Button) findViewById(R.id.bt_new_file);
        btNewFile.setOnClickListener(this);
        btCancel = (Button) findViewById(R.id.bt_cancel_new);
        btCancel.setOnClickListener(this);
        newName = (EditText) findViewById(R.id.new_name);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bt_new_file:
                if (newName.getText().length()>0) {
                    Intent intent = new Intent();
                    intent.putExtra("name", newName.getText().toString());
                    setResult(RESULT_OK, intent);
                    finish();
                }
                break;
            case R.id.bt_cancel_new:
                setResult(RESULT_CANCELED);
                finish();
                break;
        }
    }
}
