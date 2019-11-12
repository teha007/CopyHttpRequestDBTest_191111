package com.jocom.copyhttprequestdbtest_191111;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.SimpleMultiPartRequest;
import com.android.volley.toolbox.Volley;
import com.muddzdev.styleabletoast.StyleableToast;

public class MainActivity extends AppCompatActivity {

    EditText etName, etMsg;
    ImageView iv;

    String imgPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etName = findViewById(R.id.et_name);
        etMsg = findViewById(R.id.et_msg);
        iv = findViewById(R.id.iv);

        new StyleableToast
                .Builder(this)
                .text("안녕 상호야!!")
                .textColor(Color.BLACK)
                .backgroundColor(Color.YELLOW)
                .length(Toast.LENGTH_LONG)
                .show();
    }

    public void clickBtn(View view) {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 10);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    if (uri != null) {
                        iv.setImageURI(uri);

                        imgPath = getRealPathFromUri(uri);

                        new AlertDialog.Builder(this).setMessage(uri.toString() + "\n" + imgPath).create().show();
                    }
                } else {
                    Toast.makeText(this, "이미지 선택을 하지 않았습니다.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    String getRealPathFromUri(Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(this, uri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;

    }


    public void clickUpload(View view) {

        String name = etName.getText().toString();
        String msg = etMsg.getText().toString();

        String serverUrl = "http://chosangho1234.dothome.co.kr/Android/insertDB.php";

        SimpleMultiPartRequest smpr = new SimpleMultiPartRequest(Request.Method.POST, serverUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                new AlertDialog.Builder(MainActivity.this).setMessage("응답: " + response).create().show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "에러", Toast.LENGTH_SHORT).show();
            }
        });

        smpr.addStringParam("name", name);
        smpr.addStringParam("msg", msg);

        smpr.addFile("img", imgPath);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(smpr);

    }

    public void clickLoad(View view) {

        Intent intent = new Intent(this, TalkActivity.class);
        startActivity(intent);

    }
}
