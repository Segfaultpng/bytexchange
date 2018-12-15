package edu.temple.bytexchange;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.JsonReader;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

/*
* Service to run in background every minute
* */
public class ChangeFileService extends IntentService {

    private String portfile = "";


    public ChangeFileService() {
        super("ChangeFileService");
         Log.d("cfs","hello from cfs");
    }

    @Override
    public void onCreate() {

        portfile = getResources().getString(R.string.currentFile);
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        try {
            readFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d("cfs", "handle intent");

    }

    //read file gets information for each stock in portfolio
    public void readFile() throws IOException {

        URL url = null;
        StockShort newStockData = null;

        String symbol = "";
        String name = "";
        double oldcurentprice = 0;
        double oldopenprice = 0;
        int lincounter = 0;

        FileInputStream fis = null;
        try {


            // input the file content to the StringBuffer "input"
            BufferedReader file = new BufferedReader(new FileReader(getFilesDir()+ "/"+portfile));
            String line;
            StringBuffer inputBuffer = new StringBuffer();

            //read each line
            while ((line = file.readLine()) != null) {


                if(lincounter == 0){
                    symbol = line;

                    //get ready to get details for individual stock
                    url = new URL("http://dev.markitondemand.com/MODApis/Api/v2/Quote/json/?symbol=" + symbol);

                    BufferedReader buffreader = new BufferedReader(new InputStreamReader(
                            url.openStream()));

                    StringBuilder sb = new StringBuilder();

                    String response;
                    while((response = buffreader.readLine()) != null) {
                        sb.append(response);
                    }

                    InputStream targetStream = new ByteArrayInputStream(sb.toString().getBytes());

                    //save the new stock information we will rewrite it to the file
                    newStockData = readJsonStream(targetStream);

                }else if(lincounter == 1){
                    name = line;
                }else if(lincounter == 2){
                    oldcurentprice = Double.parseDouble(line);

                    // when price changes overwrite the file
                    line = Double.toString(newStockData.getCurrentPrice());
                }else if(lincounter == 3){
                    oldopenprice = Double.parseDouble(line);

                    //when price changes overwrite the file/line
                    line = Double.toString(newStockData.getOpenPrice());
                }

                //stock seperator
                /**
                 * file looks like
                 * Symbol
                 * name
                 * current price
                 * open price
                 * ---
                 */

                if (line.equals("---")){
                    lincounter = -1;
                }
                lincounter++;

                //append this new line to the temple string we are making
                inputBuffer.append(line);
                inputBuffer.append('\n');
            }

            String inputStr = inputBuffer.toString();

            file.close();

            // write the new String with the replaced line OVER the same file
            FileOutputStream fileOut = new FileOutputStream(getFilesDir()+ "/" +portfile);

            //write the string to the file in which the receiver will fire
            fileOut.write(inputStr.getBytes());
            fileOut.close();




        } catch (FileNotFoundException e) {
            Toast.makeText(getBaseContext(), " add to your portfolio", Toast.LENGTH_LONG).show();
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
}
