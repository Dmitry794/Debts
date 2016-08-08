package by.dmitry.debts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;


public class renameFile extends Activity implements View.OnClickListener{

    Button btRenFile;
    Button btRenCancel;
    EditText reName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.rename_file);
        btRenFile = (Button) findViewById(R.id.bt_rename_file);
        btRenFile.setOnClickListener(this);
        btRenCancel = (Button) findViewById(R.id.bt_cancel_ren);
        btRenCancel.setOnClickListener(this);
        reName = (EditText) findViewById(R.id.re_name);
        reName.setOnClickListener(this);
        Intent intent = getIntent();
        reName.setText(intent.getStringExtra("name"));

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bt_rename_file:
               if (reName.getText().length()>0){
                Intent intent = new Intent();
                intent.putExtra("name", reName.getText().toString());
                setResult(RESULT_OK, intent);
                finish();}
                break;
            case R.id.bt_cancel_ren:
                setResult(RESULT_CANCELED);
                finish();
                break;
            case R.id.re_name:
                ((EditText) view).setSelection(0,((EditText)view).getText().length());
        }
    }
}
