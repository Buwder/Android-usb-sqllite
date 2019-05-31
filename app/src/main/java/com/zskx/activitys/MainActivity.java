package com.zskx.activitys;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.example.hrv.startpack.R;
import com.zskx.util.Config;
import com.zskx.util.MySqliteHelper;
import com.zskx.util.PersonModel;
import com.zskx.util.RecyclerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Random mRandom;
    private Context context;
    private MySqliteHelper mySqliteHelper;
    private int currentVersion;
    private List<PersonModel> mList;
    private RecyclerAdapter mAdapter;
    private RecyclerView recyclerview;
    private Button add,exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        viewInit();
        dataInit();
        eventInit();
    }
    @Override
    protected void onResume() {
        super.onResume();
        mList.clear();
        mList.addAll(mySqliteHelper.queryAllPersonData());
        mAdapter.notifyDataSetChanged();
    }
    private void viewInit() {
        recyclerview = (RecyclerView) findViewById(R.id.recyclerview);
        add = (Button) findViewById(R.id.add);
        exit = (Button) findViewById(R.id.exit);
    }
    private void dataInit() {
        mRandom = new Random();
        mySqliteHelper = new MySqliteHelper(context, Config.DB_NAME, null, Config.DB_VERSION);
        currentVersion = Config.DB_VERSION;
        mList = new ArrayList<>();
        mAdapter = new RecyclerAdapter(context, mList,mySqliteHelper);
        recyclerview.setAdapter(mAdapter);
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        recyclerview.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
    }
    private void eventInit() {
        add.setOnClickListener(this);
        exit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add:
                Intent intent = new Intent(MainActivity.this, Insert.class);
                MainActivity.this.startActivityForResult(intent, Config.REQUEST_CODE);
            break;
            case R.id.exit:
                Intent intents = new Intent(MainActivity.this, LoginActivity.class);
                MainActivity.this.startActivityForResult(intents, Config.REQUEST_CODE);
            break;
        }
    }
}
