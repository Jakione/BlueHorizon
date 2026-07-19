```text
 ____  _              _    _            _                
|  _ \| |            | |  | |          (_)               
| |_) | |_   _  ___  | |__| | ___  _ __ _ _______  _ __  
|  _ <| | | | |/ _ \ |  __  |/ _ \| '__| |_  / _ \| '_ \
| |_) | | |_| |  __/ | |  | | (_) | |  | |/ / (_) | | | |
|____/|_|\__,_|\___| |_|  |_|\___/|_|  |_/___\___/|_| |_|
```


**Blue Horizon** is a 2D top-down pixel art video game focused on maritime exploration and fishing.

Project developed by Jacques Bozzoli e Jacopo Correggi as a university exam for the **Computer Engineering** at the **University of Modena and Reggio Emilia (UNIMORE)**.

## Core Mechanics

* **Stochastic Fishing System**: Catching fish is based on dynamically calculated probabilities. Stats are influenced by multiple factors:
    * *Weather*: Variable atmospheric conditions that alter the available fauna.
    * *Map Zones*: Specific coordinates offer different fish types and rarities.
* **Data Persistence**: Native Save and Load system implemented with MySQL to securely keep progress, collection, and inventory across different game sessions.
* **Dynamic Weather & Day/Night Cycle**: Random atmospheric conditions and continuous time progression actively alter the available fauna and the game environment.
* **Autotiling System**: The map is visually rendered through an automatic tile-matching system.

## Technology Stack

* **Core Language**: Java
* **User Interface**: JavaFX (layouts defined via `.fxml` files)
* **Build Automation**: Maven
* **Documentation**: Detailed JavaDoc for logic classes and architectural decisions.


## Internal Structure

```text
blue-horizon/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── it/unimore/bluehorizon/
│   │   │       ├── model/               # Core business logic
│   │   │       │   ├── entities/        # Player, Fish, Boat classes
│   │   │       │   ├── fishing/         # Fishing system, probabilities (Strategy Pattern)
│   │   │       │   ├── items/           # Equipment, Consumables (Decorator Pattern)
│   │   │       │   └── map/             # Tile management, Weather logic
│   │   │       ├── controller/          # Input handling and game loop
│   │   │       ├── persistence/         # Save/Load system implementations
│   │   │       └── view/                # View interfaces and Observer implementations
│   │   └── resources/
│   │       ├── fxml/                    # JavaFX UI layouts
│   │       ├── css/                     # UI styling
│   │       ├── assets/
│   │       │   ├── player/              # Player sprites
│   │       │   ├── tile/                # Map tiles (Water, Sand, Rock)
│   │       │   └── props/               # Environment objects
│   │       └── config/                  # Initial game configuration files
│   └── test/
│       └── java/
│           └── it/unimore/bluehorizon/
│               ├── model/               # Unit tests for business logic
│               └── fishing/             # Tests for stochastic algorithms
├── pom.xml                              # Maven configuration and dependencies
└── README.md                            # Project documentation
```
