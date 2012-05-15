#!/bin/bash

cd /tmp
curl http://download.playframework.org/releases/play-1.2.5-RC3.zip -o /tmp/play-1.2.5-RC3.zip
unzip /tmp/play-1.2.5-RC3.zip
export PLAY_HOME=/tmp/play-1.2.5-RC3
export PATH=$PATH:$PLAY_HOME
git clone https://TDLVireo:Vireo20@github.com/TexasDigitalLibrary/Vireo.git Vireo
cd Vireo
play dependencies
play auto-test
play war