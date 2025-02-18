package com.example.calculator;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class CalcPresenter implements PropertyChangeListener {
    private CalcModel calcModel;
    private CalcActivityView calcView;
    public static final String TAG = "Presenter";

    public static final String ELEMENT_OUTPUT_PROPERTY = "output";

    @Override public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(ELEMENT_OUTPUT_PROPERTY)) {
            String value = evt.getNewValue().toString();
            calcView.setOutput(value);
        }
    }

    public void onTagEntered(String tag) {
        calcModel.stateMachine(tag);
    }

    public void setView(CalcActivityView view) {
        calcView = view;
    }

    public void setModel(CalcModel calcModel) {
        this.calcModel = calcModel;
        calcModel.addPropertyChangeListener(this);
    }

    public void changeOutput(CalcModel calcModel, String newOutput) {
        calcModel.setDisplay(newOutput);
    }

    public void changeLHS(CalcModel calcModel, String newLeftOperand) {
        calcModel.setLeftOperand(newLeftOperand);
    }

    public void changeRHS(CalcModel calcModel, String newRightOperand) {
        calcModel.setRightOperand(newRightOperand);
    }
}
