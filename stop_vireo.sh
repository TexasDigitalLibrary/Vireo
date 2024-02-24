#!/bin/sh
# 06-05-2020 Joe Troy create script to stop vireo4
# 02-20-2024 Seth Robbins edit script for new version of vireo4

# Find the process to kill using a command that does the following
# list processes with PID in first column (ps -ax)
# grep the results with the string used to start vireo4 (grep "java -jar ./target/vireo-4.0.0.war")
# do a grep -v so we do not get the first grep process in the results (grep -v "grep")
# remove leading spaces with sed (sed 's/^[[:space:]]*//')
# using space as the delimiter, get the first column valye (cut -d' ' -f1)
PROCESS_TO_KILL=`ps -ax | grep "java -jar .*/target/vireo-4.2.6.war" | grep -v "grep" | sed 's/^[[:space:]]*//' | cut -d' ' -f1`

# Kill the process, if the process is not found diplay a message
if ps -p $PROCESS_TO_KILL > /dev/null
then
   kill $PROCESS_TO_KILL
else
   echo “Process = ‘“$PROCESS_TO_KILL”’ not found”
fi
