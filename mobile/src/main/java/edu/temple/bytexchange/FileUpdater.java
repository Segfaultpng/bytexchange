package edu.temple.bytexchange;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.FileObserver;
import android.os.IBinder;
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

public class FileUpdater extends IntentService {

    String portfile = "";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public FileUpdater(String name) {
        super(name);
    }

    public FileUpdater(){
        super("FIleupdater");
    }

    @Override
    public void onCreate() {

        portfile = getResources().getString(R.string.currentFile);
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        //observe file
        FileObserver observer = new FileObserver(portfile) { // set up a file observer to watch this directory on sd card

            @Override
            public void onEvent(int event, String file) {

                //((PortSymbolInterface) parent).portSymbolSelected(portadapter.getItem(position).getSymbol());

                Log.d("File","file updated");

                Toast.makeText(getBaseContext(),  "portfolio updated", Toast.LENGTH_LONG).show();


            }
        };
        observer.startWatching(); //START OBSERVING


    }
}
