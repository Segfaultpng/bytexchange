package edu.temple.bytexchange;

import android.content.Intent;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    Button searchButton;
    EditText searchtxt;
    List<StockShort> searchedStocks = new ArrayList<StockShort>();
    ShortStockAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchButton = findViewById(R.id.searchbutton);

        searchtxt = findViewById(R.id.searchtxt);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width * .9), (int)(height * .9));


         adapter = new ShortStockAdapter(this, searchedStocks);

        // Attach the adapter to a ListView
        ListView searchedlist = (ListView) findViewById(R.id.searchlist);

        searchedlist.setAdapter(adapter);


        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                postData();





            }
        });


        searchedlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                StockShort selectedStock = adapter.getItem(position);

                Intent intent = new Intent();
                intent.putExtra("selectedStock", selectedStock.getSymbol());
                setResult(RESULT_OK, intent);
                finish();
            }
        });






    }

    public List<StockShort> readJsonStream(InputStream in) throws IOException {


        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            return readStockSearchArray(reader);
        } finally {
            reader.close();
        }
    }



    public List<StockShort> readStockSearchArray(JsonReader reader) throws IOException {
        List<StockShort> searchedStocks = new ArrayList<StockShort>();

        reader.beginArray();
        while (reader.hasNext()) {
            searchedStocks.add(readShortStock(reader));
        }
        reader.endArray();
        return searchedStocks;
    }

    public StockShort readShortStock(JsonReader reader) throws IOException {
        String symbol = "x";
        String stockname = "x";
        String exchange = "";


        reader.beginObject();
        while (reader.hasNext()) {

            String name = reader.nextName();

            if (name.equals("Symbol")) {

                symbol = reader.nextString();

            } else if (name.equals("Name")) {

                stockname = reader.nextString();

            }else if (name.equals("Exchange")) {

                exchange = reader.nextString();
            }
            else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return new StockShort(symbol,stockname,exchange);
    }

    public void postData() {

        new Thread(){
            @Override
            public void run() {
                URL url = null;
                try {
                    url = new URL("http://dev.markitondemand.com/MODApis/Api/v2/Lookup/json?input=" + searchtxt.getText().toString());

                    BufferedReader reader = new BufferedReader(new InputStreamReader(
                            url.openStream()));

                    StringBuilder sb = new StringBuilder();

                    String response;
                    while((response = reader.readLine()) != null) {
                        sb.append(response);
                    }

                    Message msg = Message.obtain();
                    msg.obj = sb.toString();
                    //responseHandler.sendMessage(msg);

                    InputStream targetStream = new ByteArrayInputStream(sb.toString().getBytes());

                    searchedStocks.addAll(readJsonStream(targetStream));

                    //update ui not on main thread
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            adapter.addAll(searchedStocks);

                        }
                    });




                    Log.d("response",sb.toString());

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    interface GetSymbolInterface{
        public void symbolSelected(String symbol);
    }
}
