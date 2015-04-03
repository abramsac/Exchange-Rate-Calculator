package com.example.alexabrams.exchangerate;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.view.View;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Pattern;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Activity1 extends ActionBarActivity implements AdapterView.OnItemSelectedListener {

    Button btn;
    EditText txt;
    Spinner from;
    Spinner to;
    String url;

    private Pattern tPattern = null;
    private String tRE = "\\s=\\s(\\d+\\.?\\d*)\\s\\w";

    String[] items = { "USD", "EUR", "CVE", "LAK", "AFA", "DZD"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity1);

        txt = (EditText) findViewById(R.id.editText);
        btn = (Button) findViewById(R.id.button);
        from = (Spinner) findViewById(R.id.spinner);
        to = (Spinner) findViewById(R.id.spinner2);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>( this,
                android.R.layout.simple_spinner_dropdown_item, items);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>( this,
                android.R.layout.simple_spinner_dropdown_item, items);

        from.setAdapter(adapter);
        to.setAdapter(adapter2);

        from.setOnItemSelectedListener(this);
        to.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
        //txt.setText(from.getSelectedItem().toString());
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO do nothing – needed by the interface
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity1, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void startTask(View v) {
        url = "http://www.gocurrency.com/v2/dorate.php?inV=1&from="+from.getSelectedItem().toString()+"&to="+to.getSelectedItem().toString()+"&Calculate=Convert";
        doScrape scrapeTask = new doScrape();
        scrapeTask.execute(from.getSelectedItem().toString(), to.getSelectedItem().toString(), url);  //this kicks off background task
    }

    public void displayAnswer(String res) {
        txt.setText(res);
    }

    private class doScrape extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... arguments) {
            //String fromcurr = arguments[0];
            //String tocurr = arguments[1];
            String urlIn = arguments[2];

            String s="";

            BufferedReader in = null;
            if (tPattern == null)
                tPattern = Pattern.compile(tRE);
            try {
                URL url = new URL(urlIn);
                in = new BufferedReader(
                        new InputStreamReader(
                                url.openStream()));
                String inputLine;
                System.out.println("Here");
                while ((inputLine = in.readLine()) != null) {
                    if (inputLine.contains(">Currency Converter Results<")) {
                        inputLine = in.readLine();

                        Matcher m = tPattern.matcher(inputLine);
                        if (m.find()) {
                            s = m.group(1);
                        }
                        return s;
                    }
                }
            } catch (IOException e) {
                Log.e("ScrapeTemperatures", "Unable to open url: " + url);
                return "Thrown IOException";
            } catch (Exception e) {
                Log.e("ScrapeTemperatures", e.toString());
                return "Thrown Exception " + e;
            } finally {
                if (in != null)
                    try {
                        in.close();
                    } catch (IOException e) {
                        //nothing
                    }
            }
            return "Error";
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            //do nothing
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            displayAnswer(result);
        }
    }
}
