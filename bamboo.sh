#!/bin/bash

curl http://download.playframework.org/releases/play-1.2.5-RC3.zip -o /tmp/play-1.2.5-RC3.zip
unzip /tmp/play-1.2.5-RC3.zip -d /tmp
export PLAY_HOME=/tmp/play-1.2.5-RC3
export PATH=$PATH:$PLAY_HOME
curl -L -k -u TDLVireo:Vireo20 https://github.com/TexasDigitalLibrary/Vireo/zipball/master -o vireo.zip
unzip vireo.zip
mv TexasDigitalLibrary-Vireo-* vireo
cd vireo
play dependencies
play auto-test
play war