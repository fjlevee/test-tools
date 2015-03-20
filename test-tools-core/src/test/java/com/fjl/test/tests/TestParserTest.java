package com.fjl.test.tests;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Ignore;
import org.junit.Test;

import com.fjl.test.tools.parser.TestParser;

@Ignore
public class TestParserTest {
    
    @Test
    public void testParserConstructor() throws Exception{
        
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("Test01.tst").getFile());
        
        TestParser parser = new TestParser();
        assertNotNull(parser);
    }
    
    @Test
    public void testParser01() throws Exception{
        
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("Test01.tst").getFile());
        
        TestParser parser = new TestParser();
        com.fjl.test.tools.Test test1 = parser.parseFile("src/test/resources/Test01.tst");
        //fail("TOTO");
    }
}
