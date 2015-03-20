# Configuration
>> Configuration
server.ip=127.0.0.1
server.port=5080
local.address=127.0.0.1

<< Configuration

# List of Actors
>> Actors
Actor1.name=Caller
Actor1.type=SIP

Actor2.name=Callee
Actor2.type=SIP


<< Actors

# Call flowid:FLOW

>> Test

# Usage: [ACTION_ID] [ACTOR_ID] [SND/RCV/WAIT] [ACTION_TYPE] [PARAM_NAME:PARAM_VALUE]*
01 Actor1 SIP SND method:INVITE type:REQ to:Actor2 flowid:FLOW1
02 Actor2 SIP RCV method:INVITE type:REQ from:Actor1 flowid:FLOW2
03 Actor2 SIP SND method:INVITE type:RESP code:180 to:Actor1 flowid:FLOW2
04 Actor1 SIP RCV method:INVITE type:RESP code:180 from:Actor2 flowid:FLOW1
05 Actor2 SIP SND method:INVITE type:RESP code:200 to:Actor1 flowid:FLOW2
06 Actor1 SIP RCV method:INVITE type:RESP code:200 from:Actor2 flowid:FLOW1
07 Actor1 SIP SND method:ACK type:REQ to:Actor2 flowid:FLOW1
08 Actor2 SIP RCV method:ACK type:REQ from:Actor1 flowid:FLOW2
10 Actor1 WAT time:10000
09 Actor1 SIP SND method:BYE type:REQ to:Actor2 flowid:FLOW1
11 Actor2 SIP RCV method:BYE type:REQ from:Actor1 flowid:FLOW2
12 Actor2 SIP SND method:BYE type:RESP code:200 to:Actor1 flowid:FLOW2
13 Actor1 SIP RCV method:BYE type:RESP code:200 from:Actor2 flowid:FLOW1

<< Test
