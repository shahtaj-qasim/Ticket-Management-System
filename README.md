

## General Information

The following template is structured as a multi-project build using gradle as a build tool.
By the way, gradle is the only tool supporting multi-project builds.
The main project _idistrsys_ specifies all included subprojects in the _settings.gradle_ file.
_client_ and _server_ are dependent on the _shared_ project since all the model classes are included in this project.

## How to get the server and client running?

### Generating classes via protobuf plugin

1. Within the _idistrsys_ folder start a CMD.
2. Build the project via _gradle clean build --console 'verbose'_: the verbose option enables printing of all executed tasks.
3. Refresh your gradle project within your IDE (e.g. Eclipse)
4. Implement client and server interfaces

### Starting the server

1. Within the _idistrsys_ folder start a CMD.
2. Run the server via **java -jar server\build\libs\server-1.0.jar**

The _shared_ project is automatically build before the server is started.
Look at the dependencies block in the server/build.gradle.

### Starting the client

1. Within the _idistrsys_ folder start a CMD.
2. Run the server via **gradle client:run**

The _shared_ project is automatically included in the dependencies of the client and server.
If you are interested inspect the `idistrsys/build.gradle` to see all the relevant settings, configurations and tasks.

### Technologies:
1. Java EE
2. Gradle
3. UDP 
4. RabbitMQ

### Note:

1. For running UDP on client and server, go to Run -> Edit Configurations, pass argument "udp localhost 5672" for client. And pass argument "localhost 5672 udp" for server
2. For running RabbitMQ on client and server, go to Run -> Edit Configurations, pass argument "rmq localhost 5672" for client. And pass argument "localhost 5672 rmq" for server
