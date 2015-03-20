call conf\config_tester_classpath.bat
echo "Sip Tester Class Path configured: " 
echo %TESTER_PATH%
java -classpath %TESTER_PATH% fj_levee.FJLToolsPdfTestPlanGenerator tests/CallFlowList_EstablishIncoming_Call.xml

