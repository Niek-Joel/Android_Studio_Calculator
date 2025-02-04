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
        int id = View.generateViewId();
        TextView tv = new TextView(this);
        tv.setId(id);
        tv.setTag("TextView" + id);
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
                btn.setTag(btn_tags[btnIndex]);        // NOTE: String Arrays btn_tags & btn_texts have to be in the same order
                btn.setText(btn_texts[btnIndex]);
                layout.addView(btn);
                btnIndex++;

                horizontals[i][j] = btnID;
                verticals[j][i] = btnID;
            }
        }

//        Log.d("Mainactivity", "horizontals = " + Arrays.deepToString(horizontals));
//        Log.d("Mainactivity", "verticals = " + Arrays.deepToString(verticals));

        //  Create Constraint Set
        ConstraintSet set = new ConstraintSet();
        set.clone(layout);

        // Constraints
        // TextView Constraint (output)
        set.connect(tv.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT);
//        set.connect(tv.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT);
//        set.connect(tv.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);

        // Horizontal Chains
//        for (int i = 0; i < horizontals.length; i++) {
//            int[] hIdArray = horizontals[i];
//            for (int j = 0; j < hIdArray.length; j++) {
//                int hId = hIdArray[j];
//                 set.connect(id, ConstraintSet.LEFT,);
//            }
//        }
        // Vertical Chains

    }
}