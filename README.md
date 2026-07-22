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
├── .gitignore
├── bluehorizon_save.db
├── pom.xml
├── README.md
└── src/
    └── main/
        ├── java/
        │   └── com/
        │       └── jakione/
        │           └── bluehorizon/
        │               ├── App.java
        │               ├── AppLauncher.java
        │               ├── controller/
        │               │   └── GameEngine.java
        │               ├── model/
        │               │   ├── GameModel.java
        │               │   ├── GameObserver.java
        │               │   ├── Props.java
        │               │   ├── TileType.java
        │               │   ├── TimeOfDay.java
        │               │   ├── Weather.java
        │               │   ├── fish/
        │               │   │   ├── AbstractFish.java
        │               │   │   ├── CatchRecord.java
        │               │   │   ├── CatchRegistry.java
        │               │   │   ├── Fish.java
        │               │   │   ├── FishSpecies.java
        │               │   │   └── StandardFish.java
        │               │   ├── fishing/
        │               │   │   ├── BasicRod.java
        │               │   │   ├── FishingGear.java
        │               │   │   ├── FishingManager.java
        │               │   │   └── GearDecorator.java
        │               │   ├── inventory/
        │               │   │   ├── Inventory.java
        │               │   │   ├── RodType.java
        │               │   │   └── UsableItem.java
        │               │   └── player/
        │               │       ├── Direction.java
        │               │       └── Player.java
        │               ├── persistence/
        │               │   ├── GameDAO.java
        │               │   ├── MapReader.java
        │               │   └── SQLiteGameDAO.java
        │               └── view/
        │                   ├── FishingResultView.java
        │                   ├── GameHUD.java
        │                   ├── GameRenderer.java
        │                   ├── InventoryView.java
        │                   └── RegistryView.java
        └── resources/
            ├── map.txt
            ├── props.txt
            ├── Fisherman/
            │   ├── east.png
            │   ├── north.png
            │   ├── south.png
            │   └── west.png
            ├── Fishes/
            │   ├── Eclissi_di_corallo.png
            │   ├── Guardiano_del_leviatano.png
            │   └── Re_di_ghiaccio.png
            ├── InventoryBackground/
            │   └── vertical.png
            ├── Player/
            │   ├── P2_fishing_rod.png
            │   ├── P2down.png
            │   ├── P2left.png
            │   ├── P2right.png
            │   └── P2up.png
            ├── Props/
            │   ├── palm1.png
            │   └── palm2.png
            └── Tile/
                ├── Rock/
                │   ├── rock1.png
                │   ├── rock2.png
                │   └── rock_nowater.png
                ├── Sand/
                │   └── sand.png
                ├── Sand_Water/
                │   ├── sand-water1.png
                │   ├── sand-water2.png
                │   ├── Sand_Water_CornersS/
                │   │   ├── northest/
                │   │   │   ├── 1.png
                │   │   │   └── 2.png
                │   │   ├── northwest/
                │   │   │   ├── 1.png
                │   │   │   └── 2.png
                │   │   ├── southest/
                │   │   │   ├── 1.png
                │   │   │   └── 2.png
                │   │   └── southwest/
                │   │       ├── 1.png
                │   │       └── 2.png
                │   ├── Sand_Water_CornersW/
                │   │   ├── northest/
                │   │   │   ├── 1.png
                │   │   │   └── 2.png
                │   │   ├── northwest/
                │   │   │   ├── 1.png
                │   │   │   └── 2.png
                │   │   ├── southest/
                │   │   │   ├── 1.png
                │   │   │   └── 2.png
                │   │   └── southwest/
                │   │       ├── 1.png
                │   │       └── 2.png
                │   ├── Sand_Water_Est/
                │   │   ├── 1.png
                │   │   └── 2.png
                │   ├── Sand_Water_North/
                │   │   ├── 1.png
                │   │   └── 2.png
                │   ├── Sand_Water_South/
                │   │   ├── 1.png
                │   │   └── 2.png
                │   └── Sand_Water_West/
                │       ├── 1.png
                │       └── 2.png
                └── Water/
                    ├── deep_water1.png
                    ├── deep_water2.png
                    ├── water_tile.png
                    ├── water_tile_2.png
                    ├── watertile1.png
                    └── watertile2.png
```
