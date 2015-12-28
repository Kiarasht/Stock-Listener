package com.restart.stocklistener;


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

    /**
     * The main function where the Insertionsort algorithm is defined. Insertion implementation
     * find a number and moves it back until it finds its perfect place
     */
    public void sort() {
        for (int i = 1; i <= buttons - 1; ++i) {
            Button x = button[i];
            int j = i;

            while (j > 0 && button[j - 1].getText().toString().compareTo(x.getText().toString()) > 0) {
                button[j] = button[j - 1];
                j = j - 1;
            }
            button[j] = x;
        }
    }
}
