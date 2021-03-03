# SimpleHTTPServer

It's a simple HTTP web server, written in Java, that provides static files and allows to browse through a directory and its subdirectories.

The directory `SimpleHTTPServer` contains the actual project. The directory `Test` provides a simple file structure that is used for testing the server.

# Running
To start the Simple HTTP Web Server, compile the project and run the following command:

```
$ java -jar SimpleHTTPServer-1.0-SNAPSHOT.jar -port <PORT_NUMBER> -directory <ROOT_DIRECTORY>
```

Both, the PORT\_NUMBER and <ROOT_DIRECTORY> parameters are optional. The default PORT\_NUMBER is 8080 and the default root directory is the project's root directory.

# Compile
The project is managed using [Apache Maven](https://maven.apache.org).

In order to download and compile this project, perform the following operations:
```
$ git clone git@github.com:Boffmann/SimpleHTTPServer.git
$ cd ./SimpleHTTPServer
$ mvn clean package
```
This will create a directory called `target` with a file called `SimpleHTTPServer-1.0-SNAPSHOT.jar`, which can be run as described above.

# Use Docker

The server can also be run inside a docker container. In order to create a docker image containing the server program, run the following commands from the project's root directory:

```
1 $ mvn -f ./SimpleHTTPServer compile package
2 $ docker build -t simplehttpserver .
3 $ docker run -d -p 127.0.0.1:<EXPOSED_PORT>:8080 simplehttpserver
```

With (1), it is ensured that the latest server version is used.
With (2), a new docker image is created based on the `Dockerfile`.
With (3), the freshly created docker image is run and the service is exposed to port <EXPOSED_PORT> on localhost. The server starts serving files from the docker image's root directory.

