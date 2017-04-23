package com.jinwoo.android.httpurlconnection;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;



public class MainActivity extends AppCompatActivity {

    Button btnGet;
    EditText editUrl;
    TextView textResult, textTitle;
    RelativeLayout progressLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnGet = (Button)findViewById(R.id.btnGet);
        editUrl = (EditText)findViewById(R.id.editUrl);
        textResult = (TextView)findViewById(R.id.textResult);
        textTitle = (TextView)findViewById(R.id.textTitle);
        progressLayout = (RelativeLayout)findViewById(R.id.progressLayout);

        btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String urlString = editUrl.getText().toString();
                getUrl(urlString);
            }

        });


    }

    public void getUrl(String urlString){

        if(!urlString.startsWith("http")){                       // http 로 시작이 안되면
            urlString = "http://" + urlString;
        }

        new AsyncTask<String, Void, String>(){

            ProgressDialog dialog = new ProgressDialog(MainActivity.this);

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                // ProgressDialog 세팅
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setMessage("불러오는 중........");
                dialog.show();
            }

            @Override
            protected String doInBackground(String... params) {
                String urlString = params[0];

                try {
                    // 1. String을 url 객체로 변환
                    URL url = new URL(urlString);
                    // 2. url 로 네트워크 연결 시작  (서버랑 연결!)
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    // 3. url 연결에 대한 옵션 설정 (서버랑 연결하는 방식 결정!)
                    connection.setRequestMethod("GET"); // 가. GET : 데이터 요청시 사용하는 방식
                                                        // 나. POST : 데이터 입력시 사용하는 방식
                                                        // 다. PUT : 데이터 수정시 사용하는 방식
                                                        // 라. DELETE : 데이터 삭제시 사용하는 방식
                    // 4. 서버로부터 응답코드 회신
                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) { // 데이터가 정상적으로 들어왔느냐??
                        // 4.1 서버연결로부터 스트림을 얻는다.
                        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        // 4.2 반복문을 돌면서 버퍼의 데이터를 읽어온다.
                        StringBuilder result = new StringBuilder(); // 스트링 빌더는 스트링 연산을 빠르게 해준다.
                        String lineOfData = "";

                        while ( (lineOfData = br.readLine()) != null) {

                            result.append(lineOfData);
                        }

                        return result.toString();

                    } else {
                        Log.e("HTTPConnection", "Error Code =" + responseCode);
                    }
                } catch( Exception e){
                    e.printStackTrace();
                }


                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                // title 태그값 추출하기
                String title = result.substring(result.indexOf("<title>") + 7, result.indexOf("</title>"));
                textTitle.setText(title);

                // 결과값 메인 UI에 세팅팅
               textResult.setText(result);

                // ProgressDialog 종료
                dialog.dismiss();
            }



        }.execute(urlString);

    }



}
