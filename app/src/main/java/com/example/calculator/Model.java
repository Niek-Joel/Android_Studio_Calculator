package com.example.calculator;

import android.util.Log;
import android.util.Property;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.math.BigDecimal;

public class Model {
    public static final String TAG = "Model";
    // "output" is the string format to show in the View. "result" is the BigDecimal equivalent to the String "output"
    private String output;
    private BigDecimal leftOperand, rightOperand, result;
    private String operator;
    private CalculatorState state;
    protected PropertyChangeSupport propertyChangeSupport;

    public Model() {
        propertyChangeSupport = new PropertyChangeSupport(this);
    }

    public void initDefault() {
        output = "0";
        state = CalculatorState.CLEAR;
    }


    // Setters
    public void setLeftOperand(BigDecimal newLeftOperand) {
        BigDecimal oldLeftOperand = this.leftOperand;
        this.leftOperand = newLeftOperand;

        propertyChangeSupport.firePropertyChange(Presenter.ELEMENT_LHS_PROPERTY, oldLeftOperand, newLeftOperand);
    }
    public void setOutput(String newOutput) {
        String oldOutput = this.output;
        this.output = newOutput;
        Log.i(TAG, "Output changed: From " + oldOutput + " to " + newOutput);

        propertyChangeSupport.firePropertyChange(Presenter.ELEMENT_OUTPUT_PROPERTY, oldOutput, newOutput);
    }
    public void setRightOperand(BigDecimal newRightOperand) {
        BigDecimal oldRightOperand = this.rightOperand;
        this.rightOperand = newRightOperand;
        Log.i(TAG, "Right Operand changed: From " + oldRightOperand + " to " + newRightOperand);

        propertyChangeSupport.firePropertyChange(Presenter.ELEMENT_RHS_PROPERTY, oldRightOperand, newRightOperand);
    }
    public void setResult(BigDecimal newResult) {
        BigDecimal oldResult = this.result;
        this.result = newResult;
        Log.i(TAG, "Result changed: From " + oldResult + " to " + newResult);

        propertyChangeSupport.firePropertyChange(Presenter.ELEMENT_RESULT_PROPERTY, oldResult, newResult);
    }
    public void setOperator(String newOperator) {
        String oldOperator = this.operator;
        this.operator = newOperator;
        Log.i(TAG, "Operator changed: From " + oldOperator + " to " + newOperator);

        propertyChangeSupport.firePropertyChange(Presenter.ELEMENT_OPERATOR_PROPERTY, oldOperator, newOperator);
    }
    public void setState(CalculatorState newState) {
        CalculatorState oldState = this.state;
        this.state = newState;
        Log.i(TAG, "State changed: From " + oldState + " to " + newState);

        propertyChangeSupport.firePropertyChange(Presenter.ELEMENT_STATE_PROPERTY, oldState, newState);
    }

    // Getters
    public BigDecimal getLeftOperand() {return leftOperand;}
    public String getOutput() {return output;}
    public BigDecimal getRightOperand() {return rightOperand;}
    public BigDecimal getResult() {return result;}
    public String getOperator() {return operator;}
    public CalculatorState getState() {return state;}
}
