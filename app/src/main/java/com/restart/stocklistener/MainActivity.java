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
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Button;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final String TAG = "com.restart.stocklisten";
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private ProgressBar progressBar;
    private String listCompany = "";
    private Context context;
    private Button[] button = new Button[100];
    private int buttons = 0;
    private boolean reset;

    /**
     * Create and assign widgets to ones in the layout
     *
     * @param savedInstanceState on create method
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        sharedPref = getSharedPreferences(TAG, MODE_PRIVATE);
        listCompany = sharedPref.getString(getString(R.string.listCompany), "");
        Log.wtf(TAG, "Here is the listCompany = " + listCompany + " and its length = " + listCompany.length());

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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (listCompany.length() != 0) {
            reset(ll);
        }
    }

    private void reset(LinearLayout ll) {
        String[] companyArray = listCompany.split(",");
        int length = companyArray.length;
        progressBar.setProgress(0);
        progressBar.setVisibility(View.VISIBLE);

        for (int i = 0; i < companyArray.length; ++i) {
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
            ll.addView(button[i]);
            parseJSON(companyArray[i], i, true);
            progressBar.setProgress((i / length) * 100);
        }
        //progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_camera) {

        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

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
                    String change = decimalFormat.format(Float.parseFloat(results.getString("Change")));
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
}
