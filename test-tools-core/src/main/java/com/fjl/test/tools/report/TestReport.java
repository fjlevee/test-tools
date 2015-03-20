package com.fjl.test.tools.report;

import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fjl.test.tools.ActionReport;
import com.fjl.test.tools.ActionReport.ActionResult;

public class TestReport {
    /**
     * Log4J Logger
     */
    private Logger logger = LoggerFactory
            .getLogger(com.fjl.test.tools.report.TestReport.class);

    /**
     * Final result of the SipClient TEST if true => Test success if false=>
     * Test failed
     */
    private boolean finalResult;

    /**
     * List of all Actions Reports
     */
    private Vector<ActionReport> actionsReports;

    /**
     * Sip Client Test Name.
     */
    private String testName;

    /**
     * 
     * Constructor of TestReport.
     * 
     * @param testName
     */
    public TestReport(String testName) {
        this.testName = testName;
        actionsReports = new Vector<ActionReport>();
        finalResult = true;

    }

    /**
     * Add an Action Report
     * 
     * @param actionsReports
     */
    public void addActionReport(ActionReport actionReport) {
        logger.trace("TestReport.addActionReport: {}", actionReport.getResult());
        actionsReports.add(actionReport);
        if (actionReport.getResult() == ActionResult.ACTION_FAILED) {
            finalResult = false;
        }
    }

    /**
     * Get the Final Result of the Test
     * 
     * @return true => Test success, else false=> Test failed
     */
    public boolean getFinalResult() {
        return finalResult;
    }

    /**
     * get a String representation of this Report
     * 
     * @return a String representation of this Report
     */
    public String getReportAsString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("\n----------- TEST REPORT -----------\n");
        buffer.append("\n\t -> Test Name: " + testName);
        String resultString = "[TEST SUCCESS]";
        if (!finalResult) {
            resultString = "[TEST FAILED]";
        }

        buffer.append("\n\t -> Test Result: " + resultString);
        buffer.append("\n\t -> Report Actions List: ");
        for (int i = 0; i < actionsReports.size(); i++) {
            buffer.append("\n\t\t" + actionsReports.get(i).getActionReportAsString());
        }

        buffer.append("\n-----------------------------------\n");
        return buffer.toString();
    }

    /**
     * Get the Action Report at the given index
     * 
     * @return the Report Event at the given index
     */
    public ActionReport getActionReport(int index) {
        return actionsReports.get(index);
    }

    /**
     * Get the Action Report count
     * 
     * @return the Action Report count
     */
    public int getActionReportsCount() {
        return actionsReports.size();
    }

    /**
     * Get the Sip Client Test Name
     * 
     * @return the Sip Client Test Name
     */
    public String getTestId() {
        return testName;
    }
}
