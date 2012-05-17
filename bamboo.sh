#!/bin/bash

rm -rf /tmp/play-1.2.5rc3/
rm /tmp/play-1.2.5-RC3.zip
curl http://download.playframework.org/releases/play-1.2.5-RC3.zip -o /tmp/play-1.2.5-RC3.zip
unzip /tmp/play-1.2.5-RC3.zip -d /tmp
#export PLAY_HOME=/tmp/play-1.2.5rc3
#export PATH=$PATH:$PLAY_HOME
rm -rf ./vireo/
rm ./vireo.zip
curl -L -k -u TDLVireo:Vireo20 https://github.com/TexasDigitalLibrary/Vireo/zipball/master -o ./vireo.zip
unzip ./vireo.zip
mv ./TexasDigitalLibrary-Vireo-* ./vireo
cd ./vireo
/tmp/play-1.2.5rc3/play dependencies
/tmp/play-1.2.5rc3/play auto-test
/tmp/play-1.2.5rc3/play war