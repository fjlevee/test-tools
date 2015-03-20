package com.fjl.test.tools.run;

import java.lang.reflect.Field;
import java.util.Vector;

import com.fjl.test.tools.Test;
import com.fjl.test.tools.parser.TestParser;


/**
 * Test Tools Main CLass
 */
public class TestToolsMain {
    
public static void main(String[] args) {
        
        

    try {
       
        //ClassPathScanner scanner = new ClassPathScanner("com.fjl.test");
        TestParser parser = new TestParser();
        Test test1 = parser.parseFile("Test01.tst");
        /*
        TestParser parser = new TestParser();
        
       
        
        
        
        Test test2 = parser.parseFile("Test02.tst");
        */
        
        
    } catch (Exception e) {
        System.err.println("Error in Test Parser: " + e.getMessage());
    }
    }
    
}
