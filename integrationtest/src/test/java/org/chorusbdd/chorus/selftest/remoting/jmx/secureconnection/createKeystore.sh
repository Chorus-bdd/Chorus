#!/bin/sh

# Regenerate the test keystores (PKCS12 format, SAN for localhost, SHA256withRSA).
# Run this script from the directory containing it if you need to renew the certificates.
#
# The keystores use .jks file extensions for historical reasons but are PKCS12 format internally.

KEYTOOL=$(which keytool)

# 1. Generate keystore with self-signed certificate (includes SAN for localhost)
${KEYTOOL} -genkeypair \
  -alias selfsigned \
  -keyalg RSA \
  -keysize 2048 \
  -sigalg SHA256withRSA \
  -validity 7300 \
  -dname "CN=localhost, OU=ChorusBDD, O=ChorusBDD, L=Cambridge, ST=Cambridgeshire, C=UK" \
  -ext "SAN=DNS:localhost,IP:127.0.0.1" \
  -storetype PKCS12 \
  -keystore chorus-test-keystore.jks \
  -storepass chorusIsCool \
  -keypass chorusIsCool

# 2. Export the certificate
${KEYTOOL} -exportcert \
  -alias selfsigned \
  -keystore chorus-test-keystore.jks \
  -storepass chorusIsCool \
  -file selfsigned.cer

# 3. Import certificate into truststore
${KEYTOOL} -importcert \
  -noprompt \
  -alias selfsigned \
  -file selfsigned.cer \
  -storetype PKCS12 \
  -keystore chorus-test-truststore.jks \
  -storepass chorusIsCool

rm -f selfsigned.cer
