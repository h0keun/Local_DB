package com.grow.db_sql;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    EditText editText2;
    EditText editText3;
    EditText editText4;
    EditText editText5;

    TextView textView;

    SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
        editText2 = findViewById(R.id.editText2);
        editText3 = findViewById(R.id.editText3);
        editText4 = findViewById(R.id.editText4);
        editText5 = findViewById(R.id.editText5);

        textView = findViewById(R.id.textView);

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String databaseName = editText.getText().toString();
                openDatabase(databaseName);
            }
        });

        Button button2 = findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tableName = editText2.getText().toString();
                createTable(tableName);
            }
        });

        Button button3 = findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editText3.getText().toString().trim();
                String ageStr = editText4.getText().toString().trim();
                String mobile = editText5.getText().toString().trim();

                int age = -1;
                try{
                    age = Integer.parseInt(ageStr);
                }catch(Exception e){
                    e.printStackTrace();
                }

                insertData(name, age, mobile);
            }
        });

        Button button4 = findViewById(R.id.button4);
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tableName = editText2.getText().toString();
                selectData(tableName);
            }
        });

    }

    public void openDatabase(String databaseName){
        println("openDatabase() ?????????.");

        /*database = openOrCreateDatabase(databaseName, MODE_PRIVATE, null);
        if(database != null){
            println("?????????????????? ?????????.");
        }*/
        DatabaseHelper helper = new DatabaseHelper(this, databaseName, null, 1);
        database = helper.getWritableDatabase();

    }

    public void createTable(String tableName){
        println("createTable() ?????????.");

        if(database != null){
            String sql = "create table " + tableName + "(_id integer PRIMARY KEY autoincrement, name text, age integer, mobile text)";
            database.execSQL(sql);

            println("????????? ?????????.");
        }else{
            println("?????? ????????????????????? ???????????????.");
        }
    }

    public void insertData(String name, int age, String mobile){
        println("insertData ?????????.");

        if(database != null){
            String sql = "insert into customer(name, age, mobile) values(?, ?, ?)";
            Object[] params = {name, age, mobile};

            database.execSQL(sql, params);

            println("????????? ?????????.");
        }else{
            println("?????? ????????????????????? ???????????????.");
        }
    }

    public void selectData(String tableName){
        println("selectData() ?????????.");

        if(database != null) {
            String sql ="select name, age, mobile from " + tableName;
            Cursor cursor = database.rawQuery(sql, null);
            println("????????? ????????? ?????? : " + cursor.getCount());

            for(int i=0; i<cursor.getCount(); i++){
                cursor.moveToNext();
                String name = cursor.getString(0);
                int age = cursor.getInt(1);
                String mobile = cursor.getString(2);

                println("#" + i + " > " + name + ", " + age + ", " + mobile);
            }
            cursor.close();
        }
    }

    public void println(String data){
        textView.append(data + "\n");
    }
    
        class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            println("onCreate() ?????????.");

            String tableName = "customer";
            String sql = "create table if not exists " + tableName + "(_id integer PRIMARY KEY autoincrement, name text, age integer, mobile text)";
            db.execSQL(sql);

            println("????????? ?????????.");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            println("onUpgrade ????????? : " + oldVersion +", "+ newVersion);

            if(newVersion > 1){
                String tableName = "customer";
                db.execSQL("drop table if exists "+ tableName);

                println("????????? ?????????.");

                String sql = "create table if not exists " + tableName + "(_id integer PRIMARY KEY autoincrement, name text, age integer, mobile text)";
                db.execSQL(sql);

                println("????????? ?????? ?????????.");
            }
        }
    }
}
