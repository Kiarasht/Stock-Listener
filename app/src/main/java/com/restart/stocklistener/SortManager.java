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

    /**
     * The main function where the Insertionsort algorithm is defined. Insertion implementation
     * find a number and moves it back until it finds its perfect place
     */
    public void sort_symbol_A() {
        for (int i = 1; i <= buttons - 1; ++i) {
            Button x = button[i];
            int j = i;

            while (j > 0 && button[j - 1].getText().toString()
                    .compareTo(x.getText().toString()) > 0) {
                button[j] = button[j - 1];
                j = j - 1;
            }
            button[j] = x;
        }
    }

    public void sort_price_A() {
        for (int i = 1; i <= buttons - 1; ++i) {
            Button x = button[i];
            int j = i;

            while (j > 0 && (
                    Float.valueOf(button[j - 1].getText().toString().split("   ")[1].substring(1)) <
                            Float.valueOf(x.getText().toString().split("   ")[1].substring(1)))) {
                button[j] = button[j - 1];
                j = j - 1;
            }
            button[j] = x;
        }
    }

    public void sort_change_A() {
        for (int i = 1; i <= buttons - 1; ++i) {
            Button x = button[i];
            int j = i;

            while (j > 0 && (
                    Float.valueOf(button[j - 1].getText().toString().split("   ")[2].substring(2).replaceAll("[\\D.]", "")) <
                            Float.valueOf(x.getText().toString().split("   ")[2].substring(2).replaceAll("[\\D.]", "")))) {
                button[j] = button[j - 1];
                j = j - 1;
            }
            button[j] = x;
        }
    }

    public void sort_symbol_D() {
        for (int i = 1; i <= buttons - 1; ++i) {
            Button x = button[i];
            int j = i;

            while (j > 0 && button[j - 1].getText().toString()
                    .compareTo(x.getText().toString()) < 0) {
                button[j] = button[j - 1];
                j = j - 1;
            }
            button[j] = x;
        }
    }

    public void sort_price_D() {
        for (int i = 1; i <= buttons - 1; ++i) {
            Button x = button[i];
            int j = i;

            while (j > 0 && (
                    Float.valueOf(button[j - 1].getText().toString().split("   ")[1].substring(1)) >
                            Float.valueOf(x.getText().toString().split("   ")[1].substring(1)))) {
                button[j] = button[j - 1];
                j = j - 1;
            }
            button[j] = x;
        }
    }

    public void sort_change_D() {
        for (int i = 1; i <= buttons - 1; ++i) {
            Button x = button[i];
            int j = i;

            while (j > 0 && (
                    Float.valueOf(button[j - 1].getText().toString().split("   ")[2].substring(2).replaceAll("[\\D]", "")) >
                            Float.valueOf(x.getText().toString().split("   ")[2].substring(2).replaceAll("[\\D]", "")))) {
                button[j] = button[j - 1];
                j = j - 1;
            }
            button[j] = x;
        }
    }
}
