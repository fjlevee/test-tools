call conf\config_tester_classpath.bat
echo "Sip Tester Class Path configured: " 
echo %TESTER_PATH%
java -classpath %TESTER_PATH% com.fjl.sip.test.run.SipTester tests/CallFlow_16_Bye_Received_On_ReInvite_Call_Flow_Cancelled.xml

