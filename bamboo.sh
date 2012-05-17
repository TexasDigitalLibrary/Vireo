#!/bin/bash

rm -rf /tmp/play/
rm /tmp/play.zip
#curl http://download.playframework.org/releases/play-1.2.5-RC3.zip -o /tmp/play.zip
curl http://download.playframework.org/releases/play-1.2.4.zip -o /tmp/play.zip
unzip /tmp/play.zip -d /tmp/
mv /tmp/play* /tmp/play
export PATH=$PATH:/tmp/play
rm -rf ./vireo/
rm ./vireo.zip
curl -L -k -u TDLVireo:Vireo20 https://github.com/TexasDigitalLibrary/Vireo/zipball/master -o ./vireo.zip
unzip ./vireo.zip
mv ./TexasDigitalLibrary-Vireo-* ./vireo
cd ./vireo
/tmp/play/play dependencies
/tmp/play/play auto-test