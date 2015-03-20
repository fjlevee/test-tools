package com.fjl.test.tests;

import org.junit.Test;

import com.fjl.test.tools.run.ClassPathScanner;

public class ClasspathScannerTest {
    
    @Test
    public void classpathScannerConstructor(){
        ClassPathScanner scanner = new ClassPathScanner("com.fjl.test");
    }

}
