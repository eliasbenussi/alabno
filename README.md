# ALABNO


External libraries used:

JSON.simple https://mvnrepository.com/artifact/com.googlecode.json-simple/json-simple

Java WebSockets https://github.com/TooTallNate/Java-WebSocket

Bootstrap

AngularJS

jQuery

Play JSON

Stanford Classifier


# How to run the server

First compile

```
make
```

then start the server

```
./RunServer start
```

to show the server's console

```
./RunServer show
```

to start the server in secure mode (both HTTPS and WSS)

```
./RunServer start https
```
For local testing, use 'albano' as passphrase. Before you can use WSS with a self-signed certificate, you need to browse to https://localhost:4444 and add it to the certificate exceptions of your browser.

Now you can navigate to localhost:8000 (https://localhost:4443 if https)
and submit a job as professor

