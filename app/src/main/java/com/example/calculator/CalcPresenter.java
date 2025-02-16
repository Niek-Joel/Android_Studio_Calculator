package com.example.calculator;

import android.util.Log;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;

public class CalcPresenter implements PropertyChangeListener {
    private CalcModel calcModel;
    private CalcActivityView calcView;
    public static final String TAG = "Presenter";
    private static final Map<String/*Button Tag*/, OperatorEnum/*EnumEquivalent*/> tagToEnum = Map.ofEntries(
            Map.entry("btnPlus", OperatorEnum.PLUS),
            Map.entry("btnMinus", OperatorEnum.MINUS),
            Map.entry("btnMultiply", OperatorEnum.MULTIPLY),
            Map.entry("btnDivide", OperatorEnum.DIVIDE),
            Map.entry("btnSqrt", OperatorEnum.SQRT),
            Map.entry("btnPercent", OperatorEnum.PERCENT),
            Map.entry("btnChangeSign", OperatorEnum.CHANGE_SIGN),
            Map.entry("btnClear", OperatorEnum.CLEAR),
            Map.entry("btnEquals", OperatorEnum.EQUALS));

    public static final String ELEMENT_OUTPUT_PROPERTY = "output";

    @Override public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(ELEMENT_OUTPUT_PROPERTY)) {
            String value = evt.getNewValue().toString();
            calcView.setOutput(value);
        }
    }

    public void onTagEntered(String tag) {
       // Parse tag for operator/digit/etc.

        // Is operator
        if (tagToEnum.containsKey(tag)) {
            OperatorEnum operator = tagToEnum.get(tag);
            Log.i(TAG, "presenterObj.onTagEntered called with btnPlus.");
            calcModel.setOperator(operator);
        }
        else {  //  decimal
            if (tag.equals("btnDecimal")) {
                calcModel.addDecimal();
            }
            else { // digit
                int digit = Integer.parseInt(tag);
                calcModel.addDigit(digit);
            }
        }
    }

    public void setView(CalcActivityView view) {
        calcView = view;
    }

    public void setModel(CalcModel calcModel) {
        this.calcModel = calcModel;
        calcModel.addPropertyChangeListener(this);
    }

    public void changeOutput(CalcModel calcModel, String newOutput) {
        calcModel.setOutput(newOutput);
    }

    public void changeLHS(CalcModel calcModel, String newLeftOperand) {
        calcModel.setLeftOperand(newLeftOperand);
    }

    public void changeRHS(CalcModel calcModel, String newRightOperand) {
        calcModel.setRightOperand(newRightOperand);
    }
}
