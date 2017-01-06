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
./RunServer start --https
```
Before you can use WSS with a self-signed certificate, you need to browse to https://localhost:4444 and add it to the certificate exceptions of your browser.

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

A token will be valid only for a limited amount of time. By default this is 20 minutes.

# User accounts and authentication

Imperial College LDAP authentication can be used to log in. This is enabled only when the server is started with `--https`.

If no `--https` is used, the client will not send any password back, and the server will only simulate user accounts. Logging in with any username that starts with A will authenticate an Admin, any username that starts with P a Professor, anything else becomes a Student.
