package com.batchat.thomas.batchat.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.batch.android.Batch;
import com.batch.android.Config;
import com.batchat.thomas.batchat.R;
import com.batchat.thomas.batchat.database.UserDbHelper;
import com.batchat.thomas.batchat.lib.BatchConfig;
import com.batchat.thomas.batchat.lib.MyApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BatchConfig.start();
        ButterKnife.bind(this);

        UserDbHelper userDbHelper = new UserDbHelper(MyApplication.getAppContext());
        SQLiteDatabase db = userDbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("select * from message",null);

        scrollDown();

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String title = cursor.getString(cursor.getColumnIndex(UserDbHelper.MessageEntry.COLUMN_NAME_PSEUDO));
                String message = cursor.getString(cursor.getColumnIndex(UserDbHelper.MessageEntry.COLUMN_NAME_MESSAGE));
                String value = cursor.getString(cursor.getColumnIndex(UserDbHelper.MessageEntry.COLUMN_NAME_CHAT));
                String key = cursor.getString(cursor.getColumnIndex(UserDbHelper.MessageEntry.COLUMN_NAME_ATTRIBUTE));
                String type = cursor.getString(cursor.getColumnIndex(UserDbHelper.MessageEntry.COLUMN_NAME_TYPE));
                appendText(message, title, key, value, type);
                cursor.moveToNext();
            }
        }
        cursor.close();
    }

    @OnClick(R.id.send)
    void send (){

        TextView textview = (TextView) findViewById(R.id.message);
        String message = String.valueOf(textview.getText());

        Intent intent = getIntent();
        String key = intent.getStringExtra("key");
        String value = intent.getStringExtra("value");
        String title = intent.getStringExtra("title");
        String method = intent.getStringExtra("method");

        sendMessage(message, title, key, value, method);

    }

    public void sendMessage(final String message, final String title, final String key, final String value, final String method){

        JSONObject queryObject = new JSONObject();
        JSONObject tagObject = new JSONObject();
        JSONObject operationObject = new JSONObject();
        JSONArray tags = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        JSONObject messageObject = new JSONObject();

        if(method.equals("tag")){
            tags.put(value);

            try {
                operationObject.put("$contains", tags);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                tagObject.put("t."+key, operationObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                queryObject.put("query", tagObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                messageObject.put("language", "fr");
                messageObject.put("title", title);
                messageObject.put("body", message);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JSONArray arr = new JSONArray();
            arr.put(messageObject);

            try {
                jsonObject.put("name", "Batchat push");
                jsonObject.put("push_time", "now");
                jsonObject.put("live", true);
                jsonObject.put("messages", arr);
                jsonObject.put("targeting", queryObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            try {
                tagObject.put("c."+key, value);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                queryObject.put("query", tagObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                messageObject.put("language", "fr");
                messageObject.put("title", title);
                messageObject.put("body", message);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JSONArray arr = new JSONArray();
            arr.put(messageObject);

            try {
                jsonObject.put("name", "Batchat push");
                jsonObject.put("push_time", "now");
                jsonObject.put("live", true);
                jsonObject.put("messages", arr);
                jsonObject.put("targeting", queryObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Log.e("JSON => ", jsonObject.toString());

        AndroidNetworking.post("https://api.batch.com/1.1/DEV58E226714D81BD27506A210C551/campaigns/create")
                .addHeaders("X-Authorization", "ac0eed73a27bcaa8ecb7779ca6bfb6b2")
                .addHeaders("Content-Type", "application/json")
                .addJSONObjectBody(jsonObject)
                .setTag("test")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(final JSONObject response) {
                        Log.e("BATCH RESPONSE => ", response.toString());
                        appendText(message, title, key, value, method);
                        saveMessage(message, title, key, value, method);
                        scrollDown();
                    }
                    @Override
                    public void onError(ANError error) {
                        Log.d("ERROR => ", error.toString());
                        Toast.makeText(getApplicationContext(), "Erreur reseaux", Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void saveMessage(String message, String title, String key, String value, String type){
        UserDbHelper userDbHelper = new UserDbHelper(MainActivity.this);
        SQLiteDatabase userDB = userDbHelper.getWritableDatabase();

        ContentValues messageValues = new ContentValues();
        messageValues.put(UserDbHelper.MessageEntry.COLUMN_NAME_MESSAGE, message);
        messageValues.put(UserDbHelper.MessageEntry.COLUMN_NAME_CHAT, value);
        messageValues.put(UserDbHelper.MessageEntry.COLUMN_NAME_PSEUDO, title);
        messageValues.put(UserDbHelper.MessageEntry.COLUMN_NAME_ATTRIBUTE, key);
        messageValues.put(UserDbHelper.MessageEntry.COLUMN_NAME_TYPE, type);
        userDB.insert(UserDbHelper.MessageEntry.TABLE_NAME, null, messageValues);

    }

    public void appendText(String message, String title, String key, String value, String type){

        View mainView = getLayoutInflater().inflate(R.layout.template_layout, null);
        LinearLayout containerLayout = (LinearLayout) mainView.findViewById(R.id.message_layout);
        LinearLayout mainLayout = (LinearLayout) mainView.findViewById(R.id.main);

        TextView messageView = (TextView) mainView.findViewById(R.id.messagetextview);
        containerLayout.removeView(messageView);
        TextView titleView = (TextView) mainView.findViewById(R.id.titletextview);
        containerLayout.removeView(titleView);
        TextView attibuteView = (TextView) mainView.findViewById(R.id.attributetextview);
        containerLayout.removeView(attibuteView);

        messageView.setText(message);
        titleView.setText(title);
        attibuteView.setText("Envoyer au utilisateur avec "+type+" "+key+ ": " + value);

        containerLayout.addView(titleView);
        containerLayout.addView(messageView);
        containerLayout.addView(attibuteView);

        mainLayout.removeView(containerLayout);

        LinearLayout messagesLayout = (LinearLayout) findViewById(R.id.messages);
        messagesLayout.addView(containerLayout);

    }

    public void scrollDown(){
        final ScrollView scroll = (ScrollView) findViewById(R.id.scroll);
        scroll.post(new Runnable() {

            @Override
            public void run() {
                scroll.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        Batch.onStart(this);
    }

    @Override
    protected void onStop()
    {
        Batch.onStop(this);

        super.onStop();
    }

    @Override
    protected void onDestroy()
    {
        Batch.onDestroy(this);
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        Batch.onNewIntent(this, intent);

        super.onNewIntent(intent);
    }

}
