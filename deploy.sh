#!/bin/bash

INSTALLDIR="/opt/vireo"
CWD="`pwd`"
cd ${INSTALLDIR}
rm -rf ./*
unzip $CWD/target/Vireo-4.0.x-SNAPSHOT-install.zip
mkdir -p public/configuration/theme
chown -R tomcat7:tomcat7 public
