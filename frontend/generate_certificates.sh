#! /bin/bash
openssl genrsa -aes256 -out server.key 1024
openssl req -x509 -sha256 -new -key server.key -out server.csr
openssl x509 -sha256 -days 3652 -in server.csr -signkey server.key -out selfsigned.crt
openssl pkcs12 -export -name localhost -in selfsigned.crt -inkey server.key -out keystore.p12
keytool -importkeystore -destkeystore mykeystore.jks -srckeystore keystore.p12 -srcstoretype pkcs12 -alias localhost
openssl rsa -in server.key -out decserver.key
cat selfsigned.crt decserver.key > server.pem
