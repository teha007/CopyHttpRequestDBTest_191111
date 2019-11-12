package com.jocom.copyhttprequestdbtest_191111;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class TalkActivity extends AppCompatActivity {

    ListView listView;
    TalkAdapter adapter;

    ArrayList<TalkItem> talkItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talk);

        loadDB();

        listView = findViewById(R.id.listview);
        adapter = new TalkAdapter(getLayoutInflater(), talkItems);
        listView.setAdapter(adapter);
    }

    void loadDB() {

        new Thread() {
            @Override
            public void run() {

                String serverUrl = "http://chosangho1234.dothome.co.kr/Android/loadDB.php";

                try {
                    URL url = new URL(serverUrl);

                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setDoInput(true);
                    connection.setUseCaches(false);

                    InputStream is = connection.getInputStream();
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader reader = new BufferedReader(isr);

                    StringBuffer buffer = new StringBuffer();
                    String line = reader.readLine();
                    while (line != null) {
                        buffer.append(line + "\n");
                        line = reader.readLine();
                    }

                    talkItems.clear();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });

                    String[] rows = buffer.toString().split(";");

                    for(String row : rows){
                        String[] datas = row.split("&");
                        if(datas.length!=5) continue;

                        int no = Integer.parseInt(datas[0]);
                        String name = datas[1];
                        String msg = datas[2];
                        String imgPath = "http://chosangho1234.dothome.co.kr/Android/"+datas[3];
                        String date = datas[4];

                        talkItems.add(new TalkItem(no, name, msg, imgPath, date));

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
