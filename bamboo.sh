#!/bin/bash -v

# Change to test git trigger of bamboo build
rm -rf /tmp/play/
rm -f /tmp/play.zip
curl -s http://download.playframework.org/releases/play-1.2.5-RC4.zip -o /tmp/play.zip
unzip -qq -o /tmp/play.zip -d /tmp/
export PATH=/tmp/play-1.2.5rc4/:$PATH
sed -i.orig s/Go\ to\ /Listening\ for\ HTTP/ /tmp/play-1.2.5rc4/framework/pym/play/commands/base.py
rm -rf ./vireo/
rm -f ./vireo.zip
curl -s -L -k https://github.com/TexasDigitalLibrary/Vireo/zipball/master -o ./vireo.zip
unzip -qq -o ./vireo.zip -d ./
mv ./TexasDigitalLibrary-Vireo-* ./vireo
cd ./vireo
/tmp/play-1.2.5rc4/play deps --@test --sync
/tmp/play-1.2.5rc4/play auto-test
rm -f /tmp/vireo.war
/tmp/play-1.2.5rc4/play war -o /tmp/vireo --zip
mv /tmp/vireo.war ../