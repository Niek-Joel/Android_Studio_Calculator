package com.example.calculator;

import android.view.View;

public class Presenter {
    public static final String ELEMENT_OUTPUT_PROPERTY = "output";
    public static final String ELEMENT_LHS_PROPERTY = "LHS";
    public static final String ELEMENT_RHS_PROPERTY = "RHS";
    public static final String ELEMENT_RESULT_PROPERTY = "result";
    public static final String  ELEMENT_OPERATOR_PROPERTY = "operator";
    public static final String  ELEMENT_STATE_PROPERTY = "state";

    private Model presenterModel;
    private View presenterView;

    public void changeElementOutput(String propertyName, String newOutput) {
    }
}
