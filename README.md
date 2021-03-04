# SimpleHTTPServer

It's a simple HTTP web server, written in Java, that provides static files and allows to browse through a directory and its subdirectories. It also provides a simple "Wall" application that is served at `/Wally`.

The directory `SimpleHTTPServer` contains the actual project. The directory `Test` provides a simple file structure that is used for testing the server.

# Running
To start the Simple HTTP Web Server, compile the project and run the following command:

```
$ java -jar SimpleHTTPServer-1.0-SNAPSHOT.jar -port <PORT_NUMBER> -directory <ROOT_DIRECTORY>
```

Both, the PORT\_NUMBER and <ROOT_DIRECTORY> parameters are optional. The default PORT\_NUMBER is 8080 and the default root directory is the project's root directory.

# Compile
The project is managed using [Apache Maven](https://maven.apache.org).

I developed this project using the following dependencies:

```
OpenJDK 11.0.10
Apache Maven 3.6.0
MongoDB 3.6.3
```

In order to download and compile this project, perform the following operations:
```
$ git clone git@github.com:Boffmann/SimpleHTTPServer.git
$ cd ./SimpleHTTPServer
$ mvn clean package
```
This will create a directory called `target` with a file called `SimpleHTTPServer-1.0-SNAPSHOT.jar`, which can be run as described above.
This also generates the javadoc for this project which can afterwards be found in `target/apidocs`.

# Use Docker

The server can also be run inside a docker container. In order to create a docker image containing the server program, run the following commands from the project's root directory:

```
1 $ mvn -f ./SimpleHTTPServer compile package
2 $ docker build -t simplehttpserver .
3 $ docker run -d -p 127.0.0.1:<EXPOSED_PORT>:8080 simplehttpserver
```

With (1), it is ensured that the latest server version is used.
With (2), a new docker image is created from openjdk:11.
With (3), the freshly created docker image is run and the service is exposed to port <EXPOSED\_PORT> on localhost. The server starts serving files from the docker image's root directory. The root directory can be changed by adding the corresponding parameter to the java command in `Dockerfile`.
