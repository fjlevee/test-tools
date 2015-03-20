rem first set directories
set TESTER_PATH=.

rem FJL SipClient Tester library
set TESTER_PATH=%TESTER_PATH%;%TESTER_ABSOLUTE_PATH%/lib/FJL_SipClient_Tester_G0R1C0.jar

rem FJL SipTesterTools library
set TESTER_PATH=%TESTER_PATH%;%TESTER_ABSOLUTE_PATH%/lib/FJL_SipTesterTools_G0R1C0.jar

rem FJL SipStack library
set TESTER_PATH=%TESTER_PATH%;lib/FJL_SipStack_G0R1C0.jar

rem FJL SipTesterGui library
set TESTER_PATH=%TESTER_PATH%;%TESTER_ABSOLUTE_PATH%/lib/FJL_SipTesterGui_G0R1C0.jar

rem Jain Sip library
set TESTER_PATH=%TESTER_PATH%;lib/jain-sip-api-1.2.jar;lib/jain-sip-ri-1.2.108.jar

rem Jain Sip library
rem set TESTER_PATH=%TESTER_PATH%;lib/jain-sip-api-1.2.jar;lib/jain-sdp-1.0.102.jar;lib/jain-sip-ri-1.2.108.jar;lib/jain-sip-sdp-1.2.108.jar;lib/jain-sip-tck-1.2.108.jar

rem logger library
set TESTER_PATH=%TESTER_PATH%;lib/log4j-1.2.13.jar

rem Set common libraries
set TESTER_PATH=%TESTER_PATH%;lib/commons-collections-3.2.jar;lib/commons-logging-1.1.1.jar;lib/commons-configuration-1.3.jar;lib/commons-lang-2.3.jar;lib/commons-codec-1.4.jar

rem Set XML parser libraries
set TESTER_PATH=%TESTER_PATH%;lib/dom4j-1.3.jar

rem Set Http Client libraries Keep the old version of Http Client (problem with | character in urls)
set TESTER_PATH=%TESTER_PATH%;lib/commons-httpclient-3.1.jar

rem Set Pdf Libraries iText-2.1.5.jar
set TESTER_PATH=%TESTER_PATH%;lib/iText-2.1.5.jar

rem Set the JAVA_OPTIONS
set JAVA_OPTS=%JAVA_OPTS% -Xms512m -Xmx1024m -XX:MaxPermSize=512m


