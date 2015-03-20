call conf\config_tester_classpath.bat
echo "Sip Tester Class Path configured: " 
echo %TESTER_PATH%
java -classpath %TESTER_PATH% com.fjl.sip.test.run.SipTester tests/CallFlow_13_EstablishOutgoing_Call.xml

