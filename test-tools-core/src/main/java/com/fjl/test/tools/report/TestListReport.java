package com.fjl.test.tools.report;

import java.util.Vector;

/**
 * 
 * This class SipClientTestListReport contains information about the list of
 * test executed
 *
 * @author FJ. LEVEE (ofli8276)
 *
 * @version G0R1C0
 * @since G0R1C0
 */
public class TestListReport {
    /**
     * List of Sip Client Test reportsS
     */
    private Vector<TestReport> testReports;

    /**
     * List of Sip Client Test Lists reports
     */
    private Vector<TestListReport> testListReports;

    /**
     * 
     * Constructor of SipClientTestListReport.
     *
     */
    public TestListReport() {
        testReports = new Vector<TestReport>();
        testListReports = new Vector<TestListReport>();
    }

    /**
     * Get the testReports count
     * 
     * @return the testReports count
     */
    public int testReportsCount() {
        return testReports.size();
    }

    /**
     * Add a SipClientTestReport in the testReports
     * 
     * @param report
     *            Specifies the SipClientTestReport to add
     */
    public void addSipClientTestReport(TestReport report) {
        testReports.add(report);

    }

    /**
     * Add a SipClientTestReportList in the testReportList
     * 
     * @param reportList
     *            Specifies the SipClientTestReportList to add
     */
    public void addSipClientTestListReport(TestListReport reportList) {
        testListReports.add(reportList);

    }

    /**
     * Get the List of Sip Client Test Reports
     * 
     * @return the List of Sip Client Test Reports
     */
    public Vector<TestReport> getSipClientTestReports() {
        Vector<TestReport> reports = new Vector<TestReport>();
        for (int i = 0; i < testListReports.size(); i++) {
            Vector<TestReport> listReports = testListReports.get(i)
                    .getSipClientTestReports();
            /*
             * for(int j = 0;j<listReports.size();j++){
             * reports.add(listReports.get(j)); }
             */
            reports.addAll(listReports);
        }
        reports.addAll(testReports);
        return reports;

    }
}
