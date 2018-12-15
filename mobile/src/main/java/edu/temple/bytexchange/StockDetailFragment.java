package edu.temple.bytexchange;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static android.content.Context.MODE_APPEND;


/**
 * A simple {@link Fragment} subclass.
 */
public class StockDetailFragment extends Fragment {

    String stockSymbol;

    FloatingActionButton addtoport;

    StockShort selectedStock;

     String portfile = "";

    TextView companyname,currentprice,openprice;
    WebView stockview;


    public static final String SYMBOL_KEY = "symbol_name";


    public StockDetailFragment() {
        // Required empty public constructor
    }

    public static StockDetailFragment newInstance(String stockSymbol){

        StockDetailFragment sd = new StockDetailFragment();
        Bundle bundle = new Bundle();

        bundle.putString(SYMBOL_KEY,stockSymbol);

        sd.setArguments(bundle);

        return  sd;
    }

    @Override
    public void onAttach(Context context) {

        portfile = getResources().getString(R.string.currentFile);

        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null) {
            stockSymbol = getArguments().getString(SYMBOL_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_stock_detail, container, false);

        companyname = v.findViewById(R.id.companyname);
        currentprice = v.findViewById(R.id.currentprice);
        openprice = v.findViewById(R.id.openprice);

        stockview = v.findViewById(R.id.stockview);

        addtoport = v.findViewById(R.id.addtoport);


        postData();

        addtoport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToFile();
            }
        });




        return  v;
    }


    public void saveToFile(){
        //File file = new File(getActivity().getFilesDir(), portfile);


        try {

            FileOutputStream fOut = getActivity().openFileOutput(portfile,  MODE_APPEND);
            PrintWriter pw = new PrintWriter(fOut);

            pw.println(selectedStock.getSymbol());
            pw.println(selectedStock.getName());
            pw.println(selectedStock.getCurrentPrice());
            pw.println(selectedStock.getOpenPrice());
            pw.println("---");
            pw.close();

            fOut.close();




        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readFile() throws IOException {
        FileInputStream fis = null;
        try {
            fis = getActivity().openFileInput(portfile);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                Log.d("fileline",line);
                sb.append(line);
            }

            isr.close();
            fis.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }



    public StockShort readJsonStream(InputStream in) throws IOException {


        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            return readShortStock(reader);
        } finally {
            reader.close();
        }
    }

    public StockShort readShortStock(JsonReader reader) throws IOException {
        String symbol = "x";
        String stockname = "x";
        double currentPrice = 0;
        double openPrice = 0;


        reader.beginObject();
        while (reader.hasNext()) {

            String name = reader.nextName();

            if (name.equals("Symbol")) {

                symbol = reader.nextString();

            } else if (name.equals("Name")) {

                stockname = reader.nextString();

            }else if (name.equals("LastPrice")) {

                currentPrice = Double.parseDouble(reader.nextString());
            }else if (name.equals("Open")) {

                openPrice = Double.parseDouble(reader.nextString());
            }
            else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return new StockShort(symbol,stockname,currentPrice,openPrice);
    }

    Handler responseHandler = new Handler(new Handler.Callback() {



        @Override
        public boolean handleMessage(Message msg) {

            StockShort fullStock = (StockShort) msg.obj;

            companyname.setText(fullStock.getName());
            currentprice.setText(Double.toString(fullStock.getCurrentPrice()));
            openprice.setText(Double.toString(fullStock.getOpenPrice()));


            /*
            //update ui not on main thread
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            adapter.addAll(searchedStocks);

                        }
                    });

             */


            //webView.loadData((String) msg.obj,"text/html; charset=UTF-8", null);
            // ((TextView) findViewById(R.id.displayTextView)).setText((String) msg.obj);
            return false;
        }
    });





//Will set our selectedStock after json is returned and parsed
    public void postData() {

        new Thread(){
            @Override
            public void run() {
                URL url = null;
                try {
                    url = new URL("http://dev.markitondemand.com/MODApis/Api/v2/Quote/json/?symbol=" + stockSymbol);

                    BufferedReader reader = new BufferedReader(new InputStreamReader(
                            url.openStream()));

                    StringBuilder sb = new StringBuilder();

                    String response;
                    while((response = reader.readLine()) != null) {
                        sb.append(response);
                    }



                    InputStream targetStream = new ByteArrayInputStream(sb.toString().getBytes());

                   StockShort fullStock = readJsonStream(targetStream);

                   selectedStock = fullStock;

                    Message msg = Message.obtain();
                    msg.obj = fullStock;

                    responseHandler.sendMessage(msg);

                    stockview.post(new Runnable() {
                        @Override
                        public void run() {
                            stockview.getSettings().setJavaScriptEnabled(true);
                            stockview.loadUrl("https://macc.io/lab/cis3515/?symbol=" + stockSymbol);
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


}
