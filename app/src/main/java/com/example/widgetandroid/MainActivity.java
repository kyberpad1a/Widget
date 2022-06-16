package com.example.widgetandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    String futureJokeString = "";
    String JokeString = "";
    TextView txt;
    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new JokeLoader().execute();
        txt = findViewById(R.id.txtProba);
        btn = findViewById(R.id.button);
        txt.setText(JokeString);
        btn.setOnClickListener(view -> {
            new JokeLoader().execute();
        });

    }

    private class JokeLoader extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            String jsonString = getJson("https://api.chucknorris.io/jokes/random");

            try{
                JSONObject jsonObject = new JSONObject(jsonString);
                futureJokeString = jsonObject.getString("value");
            }
            catch(JSONException e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            futureJokeString = "";
        }

        @Override
        protected void onPostExecute(Void aVoid){
            super.onPostExecute(aVoid);
            if(!futureJokeString.equals("")){
                JokeString=futureJokeString;
                Toast.makeText(getApplicationContext(), JokeString, Toast.LENGTH_LONG).show();
                //txt.setText(futureJokeString);
            }
        }
    }

    private String getJson(String link){
        String data = "";
        try{
            URL url = new URL(link);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            if(urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
                //BufferedReader - класс, читает текст из потока ввода символов,
                //буфферизируя прочитанные символы,чтобы обеспечить эффективное считывание символов,
                //массивов строк
                BufferedReader r = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),"utf-8"));
                data = r.readLine();
                urlConnection.disconnect();
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        return data;
    }
}