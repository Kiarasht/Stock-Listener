package com.restart.stocklistener;


import android.util.Log;
import android.widget.Button;

public class SortManager {

    private final String TAG = "com.restart.stocklisten";
    private Button[] button;
    private int buttons;

    public SortManager(Button[] button, int buttons) {
        this.button = button;
        this.buttons = buttons;
    }

    public Button[] Sortsymbol() {
        sort();
        return button;
    }

    public Button[] Sortprice() {

        return button;
    }

    public Button[] Sortchange() {

        return button;
    }

    public void sort() {
        final String button12 = button[0].getText().toString();
        final String[] companyArray12 = button12.split("   ");
        final String final1 = companyArray12[0];
        final String button21 = button[1].getText().toString();
        final String[] companyArray21 = button21.split("   ");
        final String final2 = companyArray21[0];
        if (final1.compareTo(final2) < 0) {
            Log.d(TAG, "Set!");
            Button temp = button[0];
            button[0] = button[1];
            button[1] = temp;
        }
    }
}
