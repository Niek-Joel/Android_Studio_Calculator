package com.example.calculator;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.constraintlayout.widget.Guideline;

import com.example.calculator.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {
    private final int KEYS_HEIGHT = 4;
    private final int KEYS_WIDTH = 5;
    private ActivityMainBinding binding;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        initLayout();
    }

    private void initLayout() {
        // Create an instance of ConstraintLayout
        ConstraintLayout layout = binding.main;

        // String resources as arrays
        String[] btn_texts = getResources().getStringArray(R.array.btn_texts);
        String[] btn_tags = getResources().getStringArray(R.array.btn_tags);

        // Arrays to order buttons
        int[][] horizontals = new int[KEYS_HEIGHT][KEYS_WIDTH];
        int[][] verticals = new int[KEYS_WIDTH][KEYS_HEIGHT];

        // TextView at the top to show calculator output
        int tvId = View.generateViewId();
        TextView tv = new TextView(this);
        tv.setId(tvId);
        tv.setTag("TextView" + tvId);
        tv.setText(getResources().getString(R.string.default_output));
        tv.setTextSize(48);
        tv.setGravity(Gravity.CENTER_VERTICAL | android.view.Gravity.END);
        layout.addView(tv);

        // Creating buttons
        int btnIndex = 0;
        for (int i=0; i<KEYS_HEIGHT; i++) {
            for (int j=0; j<KEYS_WIDTH; j++) {
                int btnID = View.generateViewId();
                Button btn = new Button(this);
                btn.setId(btnID);
                btn.setTag(btn_tags[btnIndex]);
                btn.setText(btn_texts[btnIndex]);
                layout.addView(btn);
                btnIndex++;

                // Adding button IDs to arrays
                horizontals[i][j] = btnID;
                verticals[j][i] = btnID;
            }
        }

//        Log.d("Mainactivity", "horizontals = " + Arrays.deepToString(horizontals));
//        Log.d("Mainactivity", "verticals = " + Arrays.deepToString(verticals));

        //  Create Constraint Set
        ConstraintSet set = new ConstraintSet();
        set.clone(layout);

        /* TextView Constraint (output)
           connect params: startID, startSide, endID, endSide */
        set.connect(tvId, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT);
        set.connect(tvId, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);

        // Horizontal Chains
        for (int i = 0; i < horizontals.length; i++) {
            int[] hIdArray = horizontals[i];
            set.createHorizontalChain(binding.guideWest.getId(), ConstraintSet.LEFT, binding.guideEast.getId(), ConstraintSet.RIGHT, hIdArray, null, ConstraintSet.CHAIN_SPREAD_INSIDE);
        }

        // Vertical Chains

        set.applyTo(layout);
    }
}