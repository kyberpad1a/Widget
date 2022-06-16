package com.example.widgetandroid;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WidgetProvider extends android.appwidget.AppWidgetProvider {
    //по кнопке в приложение или окно
    /*public static String WIDGET_BUTTON1 = "MY_PACKAGE_NAME.WIDGET_BUTTON";
    String futureJokeString = "";
    String JokeString = "";
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds){
        for(int appWidgetId: appWidgetIds){
            RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.widget);
            Intent intent = new Intent(context,MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            views.SetOnClickPendingIntent(R.id.widget_button,pendingIntent);
            appWidgetManager.updateAppWidget(appWidgetId,views);
        }
    }*/
    static String futureJokeString = "";
    static String JokeString = "";
    private static final String SYNC_CLICKED    = "automaticWidgetSyncButtonClick";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        RemoteViews remoteViews;
        ComponentName watchWidget;

        remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
        watchWidget = new ComponentName(context, WidgetProvider.class);

        remoteViews.setOnClickPendingIntent(R.id.widget_button, getPendingSelfIntent(context, SYNC_CLICKED));
        appWidgetManager.updateAppWidget(watchWidget, remoteViews);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        super.onReceive(context, intent);

        if (SYNC_CLICKED.equals(intent.getAction())) {

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

            RemoteViews remoteViews;
            ComponentName watchWidget;

            remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
            watchWidget = new ComponentName(context, WidgetProvider.class);
            new JokeLoader().execute();
            System.out.println(JokeString);
            remoteViews.setTextViewText(R.id.txtJoke, JokeString);

            appWidgetManager.updateAppWidget(watchWidget, remoteViews);

        }
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
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
