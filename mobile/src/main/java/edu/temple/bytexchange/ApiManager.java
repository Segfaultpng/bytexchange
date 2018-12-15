package edu.temple.bytexchange;

import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;


// Not used
public class ApiManager {



    InputStream targetStream;


    public InputStream postData(final String symbol) {


        new Thread(){
            @Override
            public void run() {
                URL url = null;
                try {
                    url = new URL("http://dev.markitondemand.com/MODApis/Api/v2/Lookup/json?input=" + symbol);

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

                    targetStream = new ByteArrayInputStream(sb.toString().getBytes());


                    //return targetStream;

                    Log.d("response",sb.toString());

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();


    return targetStream;

    }
}
