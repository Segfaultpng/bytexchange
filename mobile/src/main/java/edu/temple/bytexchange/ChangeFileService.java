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

public class ChangeFileService extends IntentService {

    final String portfile = "port_file_test3.txt";


    public ChangeFileService() {
        super("ChangeFileService");
         Log.d("cfs","hello from cfs");
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

            while ((line = file.readLine()) != null) {


                if(lincounter == 0){
                    symbol = line;

                    url = new URL("http://dev.markitondemand.com/MODApis/Api/v2/Quote/json/?symbol=" + symbol);

                    BufferedReader buffreader = new BufferedReader(new InputStreamReader(
                            url.openStream()));

                    StringBuilder sb = new StringBuilder();

                    String response;
                    while((response = buffreader.readLine()) != null) {
                        sb.append(response);
                    }

                    InputStream targetStream = new ByteArrayInputStream(sb.toString().getBytes());

                    newStockData = readJsonStream(targetStream);

                }else if(lincounter == 1){
                    name = line;
                }else if(lincounter == 2){
                    oldcurentprice = Double.parseDouble(line);

                    line = Double.toString(newStockData.getCurrentPrice());
                }else if(lincounter == 3){
                    oldopenprice = Double.parseDouble(line);

                    line = Double.toString(newStockData.getOpenPrice());
                }

                if (line.equals("---")){
                    lincounter = -1;
                }
                lincounter++;

                inputBuffer.append(line);
                inputBuffer.append('\n');
            }

            String inputStr = inputBuffer.toString();

            file.close();

            // write the new String with the replaced line OVER the same file
            FileOutputStream fileOut = new FileOutputStream(getFilesDir()+ "/" +portfile);
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
