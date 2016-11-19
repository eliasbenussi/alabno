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


# File downloads

The flask server allows to use special tokens for file downloads.

Example: to create the token for file alabno/tmp/test.txt
run script alabno/frontend/CreateDownloadToken with argument
alabno/tmp/test.txt
CreateDownloadToken will print to stdout the token that can be used to retrieve the file.

It's also possible to write the token to file with
```
./CreateDownloadToken alabno/tmp/test.txt --out /tmp/token.txt
```

To give users access to the file, use the link
```
http://localhost:8000/result/token
```
or
```
http://tc.jstudios.ovh:8000/result/token
```
replacing token with the token, and replacing https and ports with the correct combination.