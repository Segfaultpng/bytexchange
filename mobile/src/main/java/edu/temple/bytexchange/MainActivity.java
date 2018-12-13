package edu.temple.bytexchange;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements SearchActivity.GetSymbolInterface{

    FloatingActionButton searchfab;

    FragmentManager fm;

    Boolean singlePane;
    Boolean symbolSelected = false;

    static final int SEARCH_STOCK_REQUEST = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        singlePane = findViewById(R.id.container_2) == null;



        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        Intent myIntent = new Intent(this, ChangeFileService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this,  0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 6000, 60000, pendingIntent);

        //RTC_WAKEUP

        searchfab = findViewById(R.id.searchFab);

        fm = getSupportFragmentManager();

        fm.beginTransaction().replace(R.id.container_1, new PortfolioFragment()).commit();

        if(!singlePane && symbolSelected){
            fm.beginTransaction().replace(R.id.container_2, new StockDetailFragment()).commit();
        }


        final Intent intent = new Intent(this, SearchActivity.class);

        searchfab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivityForResult(intent,SEARCH_STOCK_REQUEST);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // Check which request we're responding to
        if (requestCode == SEARCH_STOCK_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                String selectedStock = data.getStringExtra("selectedStock");
                Log.d("fromsearch",selectedStock);

                symbolSelected(selectedStock);
            }
        }
    }

    @Override
    public void symbolSelected(String symbol) {

        symbolSelected = true;

    StockDetailFragment sd = StockDetailFragment.newInstance(symbol);

        if(!singlePane) {
            fm.beginTransaction().replace(R.id.container_2, sd).addToBackStack(null).commit();
        }else {
            fm.beginTransaction().replace(R.id.container_1, sd).addToBackStack(null).commit();
        }

    }
}
