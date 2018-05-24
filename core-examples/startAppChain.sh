#/bin/bash

vertx3='/Volumes/EXP2/ei-playpit/vertx2-examples/vertx3/bin/vertx'

echo > /tmp/verticle1.log /tmp/verticle2.log /tmp/verticle3.log /tmp/verticle4.log /tmp/verticle5.log
echo Starting Hybris-AD
$(
	export JAVA_OPTS="-javaagent:/Users/sandeepsingh/Downloads/appagent/javaagent.jar -Dappdynamics.agent.nodeName=App1 -Dappdynamics.agent.tierName=Hybris-AD"
	$vertx3 run ./src/main/java/io/vertx/example/core/http/proxy/HybrisAD.java > /tmp/verticle1.log 
)&

echo Starting Hybris-ACP
$(
	export JAVA_OPTS="-javaagent:/Users/sandeepsingh/Downloads/appagent/javaagent.jar -Dappdynamics.agent.nodeName=App2 -Dappdynamics.agent.tierName=Hybris-ACP"
	$vertx3 run ./src/main/java/io/vertx/example/core/http/proxy/HybrisACP.java > /tmp/verticle2.log 
)&

echo Starting EI-ESB
$(
	export JAVA_OPTS="-javaagent:/Users/sandeepsingh/Downloads/appagent/javaagent.jar -Dappdynamics.agent.nodeName=App3 -Dappdynamics.agent.tierName=EI-ESB"
	$vertx3 run ./src/main/java/io/vertx/example/core/http/proxy/AppEI.java > /tmp/verticle3.log 
)&

echo Starting EI-AMQ
$(
	export JAVA_OPTS="-javaagent:/Users/sandeepsingh/Downloads/appagent/javaagent.jar -Dappdynamics.agent.nodeName=App4 -Dappdynamics.agent.tierName=EI-AMQ"
	$vertx3 run ./src/main/java/io/vertx/example/core/http/proxy/AppEIAMQ.java > /tmp/verticle4.log 
)&

echo Starting AL
$(
	export JAVA_OPTS="-javaagent:/Users/sandeepsingh/Downloads/appagent/javaagent.jar -Dappdynamics.agent.nodeName=App5 -Dappdynamics.agent.tierName=EJ.AbstractionLayer"
	$vertx3 run ./src/main/java/io/vertx/example/core/http/proxy/AppAL.java > /tmp/verticle5.log  
)&

tail -f /tmp/verticle1.log /tmp/verticle2.log /tmp/verticle3.log /tmp/verticle4.log /tmp/verticle5.log


