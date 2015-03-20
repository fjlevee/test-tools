package com.fjl.test.tools;

import java.io.Serializable;

public abstract class TestParam implements Serializable {

    /**
     * UID
     */
    protected static final long serialVersionUID = -3275761201537878011L;

    protected String name;

    protected Serializable value;

    public abstract void replaceParamNameWithValue(String paramName,
            Serializable paramValue);

    /**
     * TestParam Constructor
     * 
     * @param name
     * @param value
     */
    protected TestParam(String name, Serializable value) {
        this.name = name;
        this.value = value;
    }

    public String getAsString() {

        return this.name + "=" + this.value.toString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Serializable getValue() {
        return value;
    }

    public void setValue(Serializable value) {
        this.value = value;
    }

}
