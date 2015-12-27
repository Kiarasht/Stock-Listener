package com.restart.stocklistener;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class StockDataActivity extends AppCompatActivity {

    private final String TAG = "com.restart.stocklisten";
    private SwipeRefreshLayout swipeLayout;
    private String company;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_data);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            company = bundle.getString("symbol");
        }

        final WebView webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                webView.loadUrl("https://finance.yahoo.com/echarts?s=" +
                        company +
                        "#{\"range\":\"1d\",\"" +
                        "didDisablePrePost\":true,\"allowChartStacking\":true}");
            }
        });

        webView.setWebViewClient(new WebViewController() {
            @Override
            public void onPageFinished(WebView view, String url)
            {
                if(swipeLayout.isRefreshing()){
                    swipeLayout.setRefreshing(false);
                }
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
                /* Removes div elements from the webView
                *  1. News article at the bottom
                *  2. A search bar at the top
                *  3. The top drawer (LEAVES A WHITE SPACE) idk why yet ¯\_(ツ)_/¯
                *  */
                webView.loadUrl("javascript:document.getElementById(\"td-applet-mw-quote-news\").setAttribute(\"style\",\"display:none;\");");
                webView.loadUrl("javascript:document.getElementById(\"mediaquotessearchgs_2_container\").setAttribute(\"style\",\"display:none;\");");
                webView.loadUrl("javascript:document.getElementsByClassName('ct-box-hd yui-sv-hd')[0].setAttribute(\"style\",\"display:none;\");");
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        webView.loadUrl("https://finance.yahoo.com/echarts?s=" +
                company +
                "#{\"range\":\"1d\",\"" +
                "didDisablePrePost\":true,\"allowChartStacking\":true}");
    }

    public class WebViewController extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_stock_data, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
