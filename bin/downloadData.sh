#!/bin/sh
export MAVEN_OPTS="-Xmx200G -Dlog4j.configuration=file:data/log4j.properties"

nohup mvn exec:java  -Dexec.mainClass="org.aksw.leopard.pipe.DownloadData" > run.log &