# Prova Finale Ingegneria del Software 2021


- ###  Raffaele Berzoini  ([@RaffaeleBerzoini](https://github.com/RaffaeleBerzoini)) <br> raffaele.berzoini@mail.polimi.it
- ###  Elia Fantini ([@EliaFantini](https://github.com/EliaFantini)) <br> elia.fantini@mail.polimi.it
- ###  Elisabetta Fedele ([@elisabettafedele](https://github.com/elisabettafedele)) <br> elisabetta.fedele@mail.polimi.it

| Functionality | State |
|:-----------------------|:------------------------------------:|
| Basic rules | ![#c5f015](https://via.placeholder.com/15/008000/000000?text=+) |
| Complete rules | ![#c5f015](https://via.placeholder.com/15/008000/000000?text=+) |
| Socket | ![#c5f015](https://via.placeholder.com/15/008000/000000?text=+) |
| GUI | ![#c5f015](https://via.placeholder.com/15/008000/000000?text=+) |
| CLI | ![#c5f015](https://via.placeholder.com/15/008000/000000?text=+)|
| Multiple games | ![#c5f015](https://via.placeholder.com/15/008000/000000?text=+) |
| Persistence | ![#c5f015](https://via.placeholder.com/15/008000/000000?text=+) |
| Disconnections | ![#c5f015](https://via.placeholder.com/15/008000/000000?text=+) |

<!--
[![RED](https://placehold.it/15/f03c15/f03c15)](#)
[![YELLOW](https://placehold.it/15/ffdd00/ffdd00)](#)
[![GREEN](https://placehold.it/15/44bb44/44bb44)](#)
-->

# Master Of Reinassance



## Setup

- In the [Deliverables](Deliverables) folder there are two multi-platform jar files, one to set the Server up, and the other one to start the Client.
- The Server can be run with the following command, as default it runs on port 1234:
    ```shell
    > java -jar MasterOfReinassanceServer.jar
    ```
  This command can be followed by these arguments:
  - **-port**: followed by the desired port number between MIN_PORT and MAX_PORT as argument;
  - **-log**: to save the log in a file;
  - **-help**: to get help.

- The Client can be run with the following command:
    ```shell
    > java -jar MasterOfReinassanceClient.jar
    ```
    - This command sets the Client on Graphical User Interface (GUI) mode, but it can be followed by **-cli** if the Command Line Interface (CLI) is preferred.
    - The Server's IP and port to connect to can be specified during the execution.
    

## Tools

* [draw.io](http://draw.io) - UML Diagram
* [Maven](https://maven.apache.org/) - Dependency Management
* [IntelliJ](https://www.jetbrains.com/idea/) - IDE
* [JavaFX](https://openjfx.io) - Graphical Framework

## License

This project is developed in collaboration with [Politecnico di Milano](https://www.polimi.it) and [Cranio Creations](http://www.craniocreations.it).
 