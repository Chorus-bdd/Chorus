#!/bin/sh

KEYTOOL="/Library/Java/JavaVirtualMachines/jdk1.8.0_181.jdk/Contents/Home/bin/keytool"

#${KEYTOOL} -genkey -keyalg RSA -alias selfsigned -keystore chorus-test-keystore.jks -storepass chorusIsCool -validity 7300 -keysize 2048

#${KEYTOOL} -export -alias selfsigned -file selfsigned.cer -keystore chorus-test-keystore.jks

#${KEYTOOL} -import -v -trustcacerts -alias selfsigned -file selfsigned.cer -keystore chorus-test-truststore.jks

