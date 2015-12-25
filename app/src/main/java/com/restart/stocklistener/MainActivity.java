package com.restart.stocklistener;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Button;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;


public class MainActivity extends AppCompatActivity {

    private final String TAG = "com.restart.stocklisten";
    private SharedPreferences sharedPref;
    private String listCompany = "";
    private Context context;
    private Button[] button = new Button[100];
    private int buttons = 0;

    /**
     * Create and assign widgets to ones in the layout.
     * Start the onclicklistener for the action button
     *
     * @param savedInstanceState on create method
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPref = getSharedPreferences(TAG, MODE_PRIVATE);
        listCompany = sharedPref.getString(getString(R.string.listCompany), "");


        context = getApplicationContext();

        final LinearLayout ll = (LinearLayout) findViewById(R.id.Linear_view);
        ll.setOrientation(LinearLayout.VERTICAL);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                        alert.setTitle("New Stock");
                        alert.setIcon(R.drawable.ic_launch);
                        final EditText input = new EditText(context);
                        input.setTextColor(Color.BLACK);
                        input.setPadding(80, 40, 40, 40);
                        input.setHint("ex: aapl, amzn, goog, etc...");
                        input.setHintTextColor(Color.GRAY);
                        alert.setView(input);

                        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                if (input.getText().toString().trim().length() == 0) {
                                    Toast.makeText(context, "Nothing was entered!",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    String value = input.getText().toString();
                                    final int currenti = buttons;
                                    button[currenti] = new Button(context);
                                    final String load = "Loading...";
                                    button[currenti].setText(load);
                                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.WRAP_CONTENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT
                                    );
                                    params.setMargins(0, 0, 0, 20);
                                    params.gravity = Gravity.CENTER;
                                    button[currenti].setLayoutParams(params);
                                    ll.addView(button[currenti]);
                                    parseJSON(value, currenti, false);
                                    ++buttons;
                                }
                            }
                        });

                        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        });
                        alert.show();
                    }
                });
            }
        });

        /**
         * If we have just created our view, but listCompany holds values, then we need to
         * set them up. We will do this process in a new thread.
         */
        if (listCompany.length() != 0) {
            Thread worker = new Thread(new Runnable() {
                @Override
                public void run() {
                    reset(ll);
                }
            });
            worker.start();
        }
    }

    /**
     * Method gets called when user has opened the MainActivity and companyList contains values.
     * We will need to set up user's previous watchlist.
     *
     * @param ll The LinearLayout set up in the onCreate
     */
    private void reset(final LinearLayout ll) {
        String[] companyArray = listCompany.split(",");
        int length = companyArray.length;

        Log.wtf(TAG, "Here is the listCompany = " + listCompany + " and its length = " + length);

        for (int i = 0; i < length; ++i) {
            button[i] = new Button(context);
            final String load = "Loading...";
            button[i].setText(load);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 0, 20);
            params.gravity = Gravity.CENTER;
            button[i].setLayoutParams(params);
            final int fi = i;

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ll.addView(button[fi]);
                }
            });
            parseJSON(companyArray[i], i, true);
        }
    }

    /**
     * Uses API from Yahoo Finance to get stock information such as a companies value and it
     * recent change.
     *
     * @param company     A string given by the user or other methods to add or update the stock
     * @param buttonvalue A int that represents which button in the button array need to be changed
     * @param reset       Are we adding anything to listCompany or just updating the current list?
     */
    private void parseJSON(final String company, final int buttonvalue, final boolean reset) {
        AsyncTask.execute(new Runnable() {
            public void run() {
                String strContent = "";

                try {
                    URL urlHandle = new URL("http://query.yahooapis.com/v1/public/yql?q=select%20%" +
                            "2a%20from%20yahoo.finance.quotes%20where%20symbol%20in%20%28%22" +
                            company + // <-- Company stock goes here
                            "%22%29%0A%09%09&env=http%3A%2F%2Fdatatables.org%2Falltables.env&" +
                            "format=json");
                    URLConnection urlconnectionHandle = urlHandle.openConnection();
                    InputStream inputstreamHandle = urlconnectionHandle.getInputStream();

                    try {
                        int intRead;
                        byte[] byteBuffer = new byte[1024];

                        do {
                            intRead = inputstreamHandle.read(byteBuffer);

                            if (intRead == 0) {
                                break;

                            } else if (intRead == -1) {
                                break;
                            }

                            strContent += new String(byteBuffer, 0, intRead, "UTF-8");
                        } while (true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    inputstreamHandle.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    JSONObject results = new JSONObject(strContent)
                            .getJSONObject("query")
                            .getJSONObject("results")
                            .getJSONObject("quote");

                    DecimalFormat decimalFormat = new DecimalFormat("0.00");
                    final String symbol = results.getString("symbol");
                    String bid = "$" + decimalFormat.format(Float.parseFloat(results.getString("Bid")));
                    String change = results.getString("PercentChange");
                    String finalchange;
                    final Boolean updown;


                    if (change.substring(0, 1).equals("-")) {
                        finalchange = "▼" + change;
                        updown = true;
                    } else {
                        finalchange = "▲" + change;
                        updown = false;
                    }

                    final String result = symbol + "   " + bid + "   " + finalchange;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            button[buttonvalue].setText(result);
                            if (!reset) {
                                listCompany += symbol + ",";
                                sharedPref.edit().putString(getString(R.string.listCompany), listCompany).apply();
                            }

                            if (updown) {
                                button[buttonvalue].setTextColor(getResources().getColor(R.color.red));
                                button[buttonvalue].setBackgroundResource(R.drawable.button_custom_bearish);
                            } else {
                                button[buttonvalue].setTextColor(Color.GREEN);
                                button[buttonvalue].setBackgroundResource(R.drawable.button_custom_bullish);
                            }

                            button[buttonvalue].setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(context, StockDataActivity.class);
                                    intent.putExtra("symbol", symbol);
                                    startActivity(intent);
                                }
                            });
                        }
                    });

                } catch (JSONException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "Spaces? Or are you connected to internet?",
                                    Toast.LENGTH_LONG).show();
                            button[buttonvalue].setVisibility(View.GONE);
                        }
                    });

                } catch (Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "Correct company abbreviation?",
                                    Toast.LENGTH_LONG).show();
                            button[buttonvalue].setVisibility(View.GONE);
                        }
                    });

                }
            }
        });
    }

    /**
     * Creates the settings drop down menu and refresh button
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.refresh:
                refresh();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Creates the hamburger menu which only holds settings.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        getMenuInflater().inflate(R.menu.main, menu);
        inflater.inflate(R.menu.refresh, menu);
        return true;
    }

    /**
     * refresh will get called when the user has clicked the refresh button in the
     * action bar. It will go through the watch list updating its values.
     */
    private void refresh() {
        if (listCompany.length() != 0) {
            Thread worker = new Thread(new Runnable() {
                @Override
                public void run() {
                    String[] companyArray = listCompany.split(",");
                    int length = companyArray.length;

                    for (int i = 0; i < length; ++i) {
                        parseJSON(companyArray[i], i, true);
                    }
                }
            });
            worker.start();
        }
    }

}
