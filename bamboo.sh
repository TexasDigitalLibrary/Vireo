#!/bin/bash -v

rm -rf /tmp/play/
rm -f /tmp/play.zip
curl -s http://download.playframework.org/releases/play-1.2.5-RC3.zip -o /tmp/play.zip
unzip -qq -o /tmp/play.zip -d /tmp/
export PATH=/tmp/play-1.2.5rc3/:$PATH
sed -i.orig s/Go\ to\ /Listening\ for\ HTTP/ /tmp/play-1.2.5rc3/framework/pym/play/commands/base.py
rm -rf ./vireo/
rm -f ./vireo.zip
curl -s -L -k -u TDLVireo:Vireo20 https://github.com/TexasDigitalLibrary/Vireo/zipball/master -o ./vireo.zip
unzip -qq -o ./vireo.zip -d ./
mv ./TexasDigitalLibrary-Vireo-* ./vireo
cd ./vireo
/tmp/play-1.2.5rc3/play dependencies
/tmp/play-1.2.5rc3/play auto-test
/tmp/play-1.2.5rc3/play war -o /tmp/vireo --zip