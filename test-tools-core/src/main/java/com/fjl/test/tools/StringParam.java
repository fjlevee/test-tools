package com.fjl.test.tools;

import java.io.Serializable;

public class StringParam extends TestParam {

    
    
    /**
     * 
     */
    private static final long serialVersionUID = -7968411286529647989L;

    public StringParam(String name,Serializable value){
        super(name, value);
    }
        // TODO Auto-generated constructor
    
    @Override
    public void replaceParamNameWithValue(String paramName,Serializable paramValue){
        String newParamValue = this.value.toString();
        newParamValue = newParamValue.replace(paramName, paramValue.toString());
        this.value = newParamValue;
    }
   
}
