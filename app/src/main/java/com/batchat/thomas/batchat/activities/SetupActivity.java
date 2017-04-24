package com.batchat.thomas.batchat.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.batch.android.Batch;
import com.batchat.thomas.batchat.R;
import com.batchat.thomas.batchat.database.UserDbHelper;
import com.batchat.thomas.batchat.lib.BatchConfig;
import com.batchat.thomas.batchat.lib.MyApplication;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class SetupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        BatchConfig.start();
        ButterKnife.bind(this);

        UserDbHelper userDbHelper = new UserDbHelper(MyApplication.getAppContext());
        SQLiteDatabase db = userDbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("select * from user",null);
        String pseudo_database = null;
        int count = 0;

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                count = count + 1;
                pseudo_database = cursor.getString(cursor.getColumnIndex(UserDbHelper.UserEntry.COLUMN_NAME_PSEUDO));
                cursor.moveToNext();
            }
        }
        cursor.close();

        if(count > 0){
            TextView pseudo = (TextView) findViewById(R.id.title);
            pseudo.setText(pseudo_database);
        }

    }

    @OnClick(R.id.connexion)
    void connexion(){
        TextView title = (TextView) findViewById(R.id.title);
        TextView value = (TextView) findViewById(R.id.value);
        TextView key = (TextView) findViewById(R.id.key);

        // Find select radio
        RadioGroup radios = (RadioGroup) findViewById(R.id.radioGroup);
        int selectedId = radios.getCheckedRadioButtonId();
        RadioButton radioButton = (RadioButton) findViewById(selectedId);
        String selector = String.valueOf(radioButton.getText());
        String method = null;

        UserDbHelper userDbHelper = new UserDbHelper(MyApplication.getAppContext());
        SQLiteDatabase db = userDbHelper.getReadableDatabase();
        SQLiteDatabase userDB = userDbHelper.getWritableDatabase();

        checkUser(db, String.valueOf(title.getText()), userDB);
        checkChat(db, userDB, String.valueOf(value.getText()));

        Log.d("LE SELECTOR => ", selector);

        if(selector.equals("By tag")){
            method = "tag";
            Batch.User.editor()
                    .addTag(String.valueOf(key.getText()), String.valueOf(value.getText()))
                    .save();
        } else if(selector.equals("By attribute")){
            method = "attribut";
            Batch.User.editor()
                    .removeAttribute(String.valueOf(String.valueOf(key.getText())))
                    .setAttribute(String.valueOf(key.getText()), String.valueOf(value.getText()))
                    .save();
        }

        Batch.User.printDebugInformation();
        Log.e("METHOD", method);

        Intent intent = new Intent(SetupActivity.this, MainActivity.class);
        intent.putExtra("title", String.valueOf(title.getText()));
        intent.putExtra("key", String.valueOf(key.getText()));
        intent.putExtra("method", method);
        intent.putExtra("value", String.valueOf(value.getText()));
        startActivity(intent);
    }

    public void checkChat(SQLiteDatabase db, SQLiteDatabase userDB, String chat){
        Cursor chatCursor = db.rawQuery("select "+UserDbHelper.ChatEntry.COLUMN_NAME_CHAT_ID+" from chat",null);
        String chat_database_id = null;
        boolean IDhere = false;

        if (chatCursor.moveToFirst()) {
            while (!chatCursor.isAfterLast()) {
                chat_database_id = chatCursor.getString(chatCursor.getColumnIndex(UserDbHelper.ChatEntry.COLUMN_NAME_CHAT_ID));
                if(chat_database_id == chat){
                    IDhere = true;
                }
                chatCursor.moveToNext();
            }
        }
        chatCursor.close();

        if(!IDhere){
            // Insert chat ID
            ContentValues chatValues = new ContentValues();
            chatValues.put(UserDbHelper.ChatEntry.COLUMN_NAME_CHAT_ID, chat);
            userDB.insert(UserDbHelper.ChatEntry.TABLE_NAME, null, chatValues);
        }
    }

    public void checkUser(SQLiteDatabase db, String pseudo, SQLiteDatabase userDB){
        Cursor cursor = db.rawQuery("select * from user",null);
        String pseudo_database = null;
        String pseudo_database_id = null;
        int count = 0;

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                count = count + 1;
                pseudo_database_id = cursor.getString(cursor.getColumnIndex(UserDbHelper.UserEntry._ID));
                pseudo_database = cursor.getString(cursor.getColumnIndex(UserDbHelper.UserEntry.COLUMN_NAME_PSEUDO));
                cursor.moveToNext();
            }
        }
        cursor.close();

        if(count == 0){
            // save pseudo
            ContentValues userValues = new ContentValues();
            userValues.put(UserDbHelper.UserEntry.COLUMN_NAME_PSEUDO, pseudo);
            userDB.insert(UserDbHelper.UserEntry.TABLE_NAME, null, userValues);
        }
        else if(pseudo_database != pseudo) {
            ContentValues values = new ContentValues();
            values.put(UserDbHelper.UserEntry.COLUMN_NAME_PSEUDO, pseudo);

            String selection = UserDbHelper.UserEntry._ID + " LIKE ?";
            String[] selectionArgs = { pseudo_database_id };

            db.update(
                    UserDbHelper.UserEntry.TABLE_NAME,
                    values,
                    selection,
                    selectionArgs);
        }
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
