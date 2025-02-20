package com.example.calculator;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.example.calculator.databinding.ActivityMainBinding;


public class CalcActivityView extends AppCompatActivity {
    private static final int KEYS_HEIGHT = 4;
    private static final int KEYS_WIDTH = 5;
    private TextView outputTextView;
    private CalculatorClickHandler click;
    private ActivityMainBinding binding;
    private CalcPresenter calcPresenter;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        click = new CalculatorClickHandler();
        initLayout();

        // Create presenter and Model
        calcPresenter = new CalcPresenter();
        CalcModel calcModel = new CalcModel();

        // Register Activity View and Model with Presenter
        calcPresenter.setView(this);
        calcPresenter.setModel(calcModel);

        // Initialize Model to default values
        calcModel.initDefault();
    }

    public void setOutput(String value) {
        outputTextView.setText(value);
    }

    class CalculatorClickHandler implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            String tag = view.getTag().toString();
            calcPresenter.onTagEntered(tag);
        }
    }

    private void initLayout() {
        // Dip padding 8
        int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
        int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());

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
        outputTextView = new TextView(this);
        outputTextView.setId(tvId);
        outputTextView.setTag("TextView" + tvId);
        outputTextView.setText(getResources().getString(R.string.default_output));
        outputTextView.setTextSize(48);
        outputTextView.setGravity(Gravity.CENTER_VERTICAL | android.view.Gravity.END);
        layout.addView(outputTextView);

        // Creating buttons
        int btnIndex = 0;
        for (int i=0; i<KEYS_HEIGHT; i++) {
            for (int j=0; j<KEYS_WIDTH; j++) {
                int btnID = View.generateViewId();
                Button btn = new Button(this);
                btn.setId(btnID);
                btn.setTag(btn_tags[btnIndex]);
                btn.setText(btn_texts[btnIndex]);
                btn.setTextSize(24);
                btn.setOnClickListener(click);      // Set click listener for each button
                layout.addView(btn);
                btnIndex++;

                // Adding button IDs to arrays
                horizontals[i][j] = btnID;
                verticals[j][i] = btnID;
            }
        }

        //  Create Constraint Set
        ConstraintSet set = new ConstraintSet();
        set.clone(layout);

        // TextView Constraint (output)
        set.connect(tvId, ConstraintSet.RIGHT, binding.guideEast.getId(), ConstraintSet.RIGHT);
        set.connect(tvId, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        set.setMargin(tvId, ConstraintSet.BOTTOM, margin);

        // Button margins
        for (int[] horizontal : horizontals) {
            for (int j = 0; j < verticals.length; j++) {
                int btnId = horizontal[j];
                findViewById(btnId).setPadding(padding, padding, padding, padding);
                set.constrainWidth(btnId, 0);
                set.constrainHeight(btnId, 0);
                set.setMargin(btnId, ConstraintSet.TOP, margin);
                set.setMargin(btnId, ConstraintSet.BOTTOM, margin);
                set.setMargin(btnId, ConstraintSet.RIGHT, margin);
                set.setMargin(btnId, ConstraintSet.LEFT, margin);
            }
        }

        // Horizontal Chains constraint
        for (int[] hIdArray : horizontals) {
            set.createHorizontalChain(binding.guideWest.getId(), ConstraintSet.RIGHT,
                    binding.guideEast.getId(), ConstraintSet.LEFT, hIdArray, null, ConstraintSet.CHAIN_SPREAD_INSIDE);
        }
        // Vertical Chains constraint
        for (int[] vIdArray : verticals) {
            set.createVerticalChain(binding.guideNorth.getId(), ConstraintSet.TOP,
                    binding.guideSouth.getId(), ConstraintSet.BOTTOM, vIdArray, null, ConstraintSet.CHAIN_SPREAD_INSIDE);
        }

        set.applyTo(layout);
    }
}