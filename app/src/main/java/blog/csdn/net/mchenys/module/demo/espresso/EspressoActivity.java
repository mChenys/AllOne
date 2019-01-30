package blog.csdn.net.mchenys.module.demo.espresso;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import blog.csdn.net.mchenys.R;

/**
 * Created by mChenys on 2019/1/29.
 */

public class EspressoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_espresso);

    }


    public void calResult(View view) {
        TextView tv = findViewById(R.id.textView);
        EditText e1 = findViewById(R.id.editText);
        EditText e2 = findViewById(R.id.editText2);
        int num1 = Integer.parseInt(e1.getText().toString().trim());
        int num2 = Integer.parseInt(e2.getText().toString().trim());
        tv.setText("计算结果：" + (num1 + num2));
    }

    public void toRecycleViewActivity(View view) {
        startActivity(new Intent(this, RecycleViewActivity.class));
    }

    public void callphone(View view) {
        /*Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:" + 123456789);
        intent.setData(data);
        startActivity(intent);*/

        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse(String.valueOf(123456789));
        intent.setData(data);
        intent.putExtra("input", "Test");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CALL_PHONE},100);
            return;
        }
        startActivity(intent);
    }

    @SuppressLint("MissingPermission")
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults){
        if(requestCode==100 &&grantResults.length==1&& grantResults[0]==PackageManager.PERMISSION_GRANTED){
            Intent intent = new Intent(Intent.ACTION_CALL);
            Uri data = Uri.parse(String.valueOf(123456789));
            intent.putExtra("input", "Test");
            intent.setData(data);
            startActivity(intent);
        }
    }
}
