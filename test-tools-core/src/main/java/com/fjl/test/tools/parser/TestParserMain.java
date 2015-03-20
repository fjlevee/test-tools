package com.fjl.test.tools.parser;

import com.fjl.test.tools.Test;

public class TestParserMain {

    public static void main(String[] args) {
        
        
        try {
            
            TestParser parser = new TestParser();
            Test test1 = parser.parseFile("Test01.tst");
            Test test2 = parser.parseFile("Test02.tst");
            
            
        } catch (Exception e) {
            System.err.println("Error in Test Parser: " + e.getMessage());
        }
    }

}
