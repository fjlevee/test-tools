package com.fjl.test.tools.parser;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.swing.text.StyledEditorKit.ItalicAction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fjl.test.tools.Actor;
import com.fjl.test.tools.ActorFactory;
import com.fjl.test.tools.GenericActorFactory;
import com.fjl.test.tools.StringParam;
import com.fjl.test.tools.Test;
import com.fjl.test.tools.TestException;
import com.fjl.test.tools.run.ClassPathScanner;

public class TestParser {

    public static final int UNDEFINED = 0;
    public static final int COMMENT_LINE = 1;
    public static final int CONFIG_START_LINE = 2;
    public static final int CONFIG_LINE = 3;
    public static final int CONFIG_END_LINE = 4;
    public static final int ACTORS_START_LINE = 5;
    public static final int ACTORS_LINE = 6;
    public static final int ACTORS_END_LINE = 7;
    public static final int TEST_START_LINE = 8;
    public static final int TEST_LINE = 9;
    public static final int TEST_END_LINE = 10;

    public static final String[] LINE_TYPES = new String[] { "undefined",
            "Comment", ">> Configuration", "Configuration", "<< Configuration",
            ">> Actors", "Actors", "<< Actors", ">> Test", "Test", "<< Test" };

    public static final String[] LINE_PATTERNS = new String[] { "no_pattern",
            "\\s{0,}#.*", "\\s{0,}>> Configuration\\s{0,}", "no_pattern",
            "\\s{0,}<< Configuration\\s{0,}", "\\s{0,}>> Actors\\s{0,}",
            "no_pattern", "\\s{0,}<< Actors\\s{0,}", "\\s{0,}>> Test\\s{0,}",
            "no_pattern", "\\s{0,}<< Test\\s{0,}" };

    /**
     * SLF4J logger
     */
    protected Logger logger = LoggerFactory
            .getLogger(com.fjl.test.tools.parser.TestParser.class);

    protected static final String START_BLOC_PATTERN = ".*>>.*";
    protected static final String END_BLOC_PATTERN = ".*<<.*";

    protected static final String COMMENT_LINE_PATTERN = "\\s{0,}#.*";
    protected static final String CONFIG_START_LINE_PATTERN = "\\s{0,}>> Configuration\\s{0,}";
    protected static final String CONFIG_END_LINE_PATTERN = "\\s{0,}<< Configuration\\s{0,}";
    protected static final String ACTORS_START_LINE_PATTERN = "\\s{0,}>> Actors\\s{0,}";
    protected static final String ACTORS_END_LINE_PATTERN = "\\s{0,}<< Actors\\s{0,}";
    protected static final String TEST_START_LINE_PATTERN = "\\s{0,}>> Test\\s{0,}";
    protected static final String TEST_END_LINE_PATTERN = "\\s{0,}<< Test\\s{0,}";

    protected ActorFactory actorFactory;
    protected ClassPathScanner scanner;

    /**
     * TestFileParser constructor
     * 
     * @param testfilename
     */
    public TestParser() {
        scanner = new ClassPathScanner("com.fjl.test");
        actorFactory = new GenericActorFactory(scanner);
    }

    public Test parseFile(String testfilename) throws TestParserException {
        File file = new File(testfilename);

        if (!file.exists()) {
            logger.error("Error can not find: " + testfilename);
            throw new TestParserException("Error can not find: " + testfilename);
        }
        return parseFile(file);
    }

    public Test parseFile(File file) throws TestParserException {
        int requiredType = UNDEFINED;
        Vector<String> testLines = new Vector<String>();
        Vector<String> configLines = new Vector<String>();
        Vector<String> actorLines = new Vector<String>();

        try {
            List<String> lines = Files
                    .readAllLines(Paths.get(file.getAbsolutePath()),
                            Charset.forName("UTF-8"));
            int lineNumber = 1;

            for (String line : lines) {
                int currentLineType = UNDEFINED;

                String pattern = null;
                for (int i = 0; i < LINE_PATTERNS.length; i++) {
                    pattern = LINE_PATTERNS[i];
                    // check that pattern is not any .*

                    if (Pattern.matches(pattern, line)) {

                        currentLineType = i;
                        if (Pattern.matches(START_BLOC_PATTERN, line)) {

                            // This is a Start Bloc (contains >>)
                            if (requiredType > UNDEFINED) {
                                throw new TestParserException(
                                        " Error in file: " + file.getName()
                                                + " at line: " + lineNumber
                                                + ", \""
                                                + LINE_TYPES[requiredType]
                                                + "\" required instead of: "
                                                + LINE_TYPES[currentLineType]);
                            } else {
                                requiredType = currentLineType + 2;
                            }

                        } else if (Pattern.matches(END_BLOC_PATTERN, line)) {

                            // This is a End Bloc (contains <<)
                            if (requiredType == UNDEFINED) {
                                throw new TestParserException(
                                        " Error in file: "
                                                + file.getName()
                                                + " at line: "
                                                + lineNumber
                                                + ", \""
                                                + LINE_TYPES[currentLineType - 2]
                                                + "\" required instead of: "
                                                + LINE_TYPES[currentLineType]);
                            } else if (requiredType != currentLineType) {
                                throw new TestParserException(
                                        " Error in file: " + file.getName()
                                                + " at line: " + lineNumber
                                                + ", \""
                                                + LINE_TYPES[requiredType]
                                                + "\" required instead of: "
                                                + LINE_TYPES[currentLineType]);
                            }
                            requiredType = UNDEFINED;
                        }

                        break;
                    }

                }

                if (currentLineType == UNDEFINED) {

                    if (requiredType == CONFIG_END_LINE) {
                        currentLineType = CONFIG_LINE;

                        configLines.add(line);

                    } else if (requiredType == ACTORS_END_LINE) {
                        currentLineType = ACTORS_LINE;
                        actorLines.add(line);
                    } else if (requiredType == TEST_END_LINE) {
                        currentLineType = TEST_LINE;
                        testLines.add(line);
                    }
                }

                logger.debug("Line {} is a {} line.", lineNumber,
                        LINE_TYPES[currentLineType]);

                lineNumber++;
            }
        } catch (IOException e) {
            throw new TestParserException("IOException reading file: "
                    + file.getName(), e);
        }

        Test test = new Test(file.getAbsolutePath());

        parseConfigLines(test, configLines);
        parseActorLines(test, actorLines);

        return test;

    }

    private void parseConfigLines(Test test, Vector<String> configLines) {

        String key = null;
        String value = null;

        for (String line : configLines) {
            int equalIndex = line.indexOf("=");

            if (equalIndex != -1) {
                key = line.substring(0, equalIndex);
                key = key.trim();
                value = line.substring(equalIndex + 1);
                value = value.trim();
                logger.trace("Actors param: >{}={}<", key, value);
                test.addTestParam(new StringParam(key, value));
            }

        }
        test.replaceParamsNamesWithValues();

        test.dumpTestsParams(logger);
    }

    private void parseActorLines(Test test, Vector<String> actorLines)
            throws TestParserException {

        String key = null;
        String value = null;
        String actorId = null;
        String paramName = null;
        HashMap<String, HashMap<String, Serializable>> actorsparams = new HashMap<String, HashMap<String, Serializable>>();
        HashMap<String, Serializable> actorParams;
        for (String line : actorLines) {
            int equalIndex = line.indexOf("=");

            if (equalIndex != -1) {
                key = line.substring(0, equalIndex);
                key = key.trim();
                value = line.substring(equalIndex + 1);
                value = value.trim();
                logger.trace("Actor param: >{}={}<", key, value);

                int pointIndex = key.indexOf(".");
                if (pointIndex != -1) {
                    actorId = key.substring(0, pointIndex);
                    actorId = actorId.trim();
                    paramName = key.substring(pointIndex + 1);
                    paramName = paramName.trim();
                    logger.trace("Actor Id: >{}< param: >{}={}<", actorId,
                            paramName, value);
                    actorParams = actorsparams.get(actorId);
                    if (actorParams == null) {
                        actorParams = new HashMap<String, Serializable>();
                        actorsparams.put(actorId, actorParams);
                    }
                    actorParams.put(paramName, value);
                }
            }

        }
        // No create the actors
        Iterator<String> actorIds = actorsparams.keySet().iterator();
        while (actorIds.hasNext()) {
            actorId = actorIds.next();
            try {
                Actor actor = actorFactory.createActor(actorId,
                        actorsparams.get(actorId));

            } catch (TestException e) {
                logger.error("Error creating actor: ", e);
                throw new TestParserException("Error creating actor:", e);
            }
        }

    }

}
