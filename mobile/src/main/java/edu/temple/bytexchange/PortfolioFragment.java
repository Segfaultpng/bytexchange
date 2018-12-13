package edu.temple.bytexchange;


import android.os.Bundle;
import android.os.FileObserver;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class PortfolioFragment extends Fragment {

    final String portfile = "port_file_test3.txt";

    PortAdapter portadapter;

    List<StockShort> portStocks = new ArrayList<StockShort>();


    public PortfolioFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_portfolio, container, false);

        try {
            readFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        portadapter = new PortAdapter(getActivity(), portStocks);

        // Attach the adapter to a ListView
        ListView portlist = (ListView) v.findViewById(R.id.portlist);

        portlist.setAdapter(portadapter);

        //observe file
       FileObserver observer = new FileObserver(portfile) { // set up a file observer to watch this directory on sd card

            @Override
            public void onEvent(int event, String file) {


                try {
                    //read info from file again
                    readFile();

                    Toast.makeText(getActivity().getBaseContext(),  "portfolio updated", Toast.LENGTH_LONG).show();
                    //notify adapter
                    portadapter.notifyDataSetChanged();

                } catch (IOException e) {
                    e.printStackTrace();
                }


                //}
            }
        };
        observer.startWatching(); //START OBSERVING

        return v;
    }


    public void readFile() throws IOException {
        FileInputStream fis = null;

        String symbol = "";
        String name = "";
        double curentprice = 0;
        double openprice = 0;
        int lincounter = 0;
        try {
            fis = getActivity().openFileInput(portfile);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                Log.d("fileline",line);
                if(lincounter == 0){
                    symbol = line;
                }else if(lincounter == 1){
                    name = line;
                }else if(lincounter == 2){
                    curentprice = Double.parseDouble(line);
                }else if(lincounter == 3){
                    openprice = Double.parseDouble(line);
                }

                if (line.equals("---")){
                    portStocks.add(new StockShort(symbol,name,curentprice,openprice));
                    lincounter = -1;
                }
                lincounter++;
                sb.append(line);
            }

            isr.close();
            fis.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

}
