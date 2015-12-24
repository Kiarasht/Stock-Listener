package com.restart.stocklistener;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class StockDataActivity extends ActionBarActivity {
    private String company;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_data);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            company = bundle.getString("symbol");
        }

        final WebView webView = (WebView) findViewById(R.id.webView);
        webView.setVisibility(View.INVISIBLE);
        //rotateLoading.
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewController() {
            @Override
            public void onPageFinished(WebView view, String url)
            {
                /* Removes div elements from the webView
                *  1. News article at the bottom
                *  2. A search bar at the top
                *  3. The top drawer. (LEAVES A WHITE SPACE)
                *  */
                webView.loadUrl("javascript:document.getElementById(\"td-applet-mw-quote-news\").setAttribute(\"style\",\"display:none;\");");
                webView.loadUrl("javascript:document.getElementById(\"mediaquotessearchgs_2_container\").setAttribute(\"style\",\"display:none;\");");
                webView.loadUrl("javascript:document.getElementsByClassName('ct-box-hd yui-sv-hd')[0].setAttribute(\"style\",\"display:none;\");");
                webView.setVisibility(View.VISIBLE);
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

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
