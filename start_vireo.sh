#!/bin/bash -l

# This script should is run by systemd

nohup /home/vireo/java/jdk-11.0.1/bin/java -jar /home/vireo/vireo4/current/target/vireo-4.2.6.war >> ~/logs/custom_uiuc_log_w_stderr.log 2>&1 &
