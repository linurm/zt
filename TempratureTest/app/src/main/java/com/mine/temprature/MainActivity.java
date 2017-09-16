package com.mine.temprature;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.greenrobot.greendao.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {
    TextView temprature;
    List<Map<String, String>> mSearchListData = null;
    SimpleAdapter mSearchAdapter = null;
    ListView mListSearch;
    private NoteDao tempDao;
    private Query<Note> tempsQuery;

    public void setCPUTemprature(String temp) {
        temprature.setText(temp);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get the note DAO
        DaoSession daoSession = ((App) getApplication()).getDaoSession();
        tempDao = daoSession.getNoteDao();
        // query all notes, sorted a-z by their text
        tempsQuery = tempDao.queryBuilder().orderAsc(NoteDao.Properties.Date).build();
        setUpViews();
        updateNotes();
        new TempThread((App) getApplication()).start();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);

        Intent intent = new Intent(this, TempService.class);
        startService(intent);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    protected void setUpViews() {
        temprature = findViewById(R.id.id_temprature);
        mListSearch = (ListView) findViewById(R.id.id_listView);
    }

    private void updateNotes() {
        List<Note> notes = tempsQuery.list();

        mSearchListData = new ArrayList<Map<String, String>>();
        mSearchAdapter = new SimpleAdapter(this, mSearchListData,
                android.R.layout.simple_list_item_2, new String[]{"name",
                "mac"}, new int[]{android.R.id.text1,
                android.R.id.text2});
        mListSearch.setAdapter(mSearchAdapter);
//        notesAdapter.setNotes(notes);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {

        String lasttemp = null;
        if (lasttemp != event.temp) {
            temprature.setText(event.temp);
            lasttemp = event.temp;
        }
    }
}
