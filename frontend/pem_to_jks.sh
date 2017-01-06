openssl pkcs12 -export -out eneCert.pkcs12 -in server.pem
keytool -genkey -keyalg RSA -alias endeca -keystore mykeystore.jks
keytool -delete -alias endeca -keystore mykeystore.jks
keytool -v -importkeystore -srckeystore eneCert.pkcs12 -srcstoretype PKCS12 -destkeystore mykeystore.jks -deststoretype JKS
rm eneCert.pkcs12
openssl x509 -in server.pem -outform pem -out selfsigned.crt
openssl pkey -in server.pem -out decserver.key
