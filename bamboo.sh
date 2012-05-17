#!/bin/bash -v

rm -rf /tmp/play*
curl http://download.playframework.org/releases/play-1.2.5-RC3.zip -o /tmp/play.zip
unzip -qq -o /tmp/play.zip -d /tmp/
mv /tmp/play* /tmp/play
export PATH=$PATH:/tmp/play
rm -rf ./vireo*
curl -L -k -u TDLVireo:Vireo20 https://github.com/TexasDigitalLibrary/Vireo/zipball/master -o ./vireo.zip
unzip -qq -o ./vireo.zip -d ./
mv ./TexasDigitalLibrary-Vireo-* ./vireo
cd ./vireo
/tmp/play/play dependencies
/tmp/play/play auto-test