package com.example.bel_touchless;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.example.bel_touchless.db.AppDatabase;
import com.example.bel_touchless.db.Data;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.lang.ref.WeakReference;
import java.util.List;

public class Logs extends AppCompatActivity {

    private DataListAdapter dataListAdapter;
    private AppDatabase data;
    BottomNavigationView bottomNavigationView;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logs);

        //Inisialisasi RecyclerView
        recyclerView = findViewById(R.id.recyclerView);

        //Inisialisasi Variabel Bottom Navbar
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        //Set Home (selected)
        bottomNavigationView.setSelectedItemId(R.id.logs);

        //RecyclerView
//        initRecyclerView();
//        loadDataList();
        data = AppDatabase.getDbInstance(Logs.this);
        new RetrieveTask(this).execute();

        //Perform ItemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){

            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.logs:
                        return true;
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(), Home.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.about:
                        startActivity(new Intent(getApplicationContext(), About.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });
    }

    private static class RetrieveTask extends AsyncTask<Void, Void, List<Data>> {

        private WeakReference<Logs> logsReference;

        RetrieveTask(Logs context){
            logsReference = new WeakReference<>(context);
        }

        @Override
        protected List<Data> doInBackground(Void... voids) {
            // Jika Logs sudah terbuat atau Logs bukan Null
            if (logsReference.get() != null)
                return logsReference.get().data.dataDao().getLastData();
            else
                return null;
        }

        @Override
        protected void onPostExecute(List<Data> dataList){
            //Jika data bukan null dan ukuran dari list data lebih dari 0 (terdapat data)
            if (dataList != null && dataList.size() > 0){
                Logs logsActivityReference = logsReference.get();

                logsActivityReference.dataListAdapter = new DataListAdapter(dataList);
                logsActivityReference.recyclerView.setLayoutManager(new LinearLayoutManager(logsReference.get()));
                logsActivityReference.recyclerView.setAdapter(logsReference.get().dataListAdapter);

                logsActivityReference.dataListAdapter.setOnItemClickListener(new DataListAdapter.OnItemClickListener() {
                    @Override
                    public void deleteData(Data data) {
                        logsActivityReference.data.dataDao().delete(data);
                        logsActivityReference.finish();
                        logsActivityReference.startActivity(logsActivityReference.getIntent());
                        Toast.makeText(logsActivityReference, "Berhasil dihapus!", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        }
    }
}