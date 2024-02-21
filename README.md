# JavaFX Render Engine
JavaFX Render Engine, file structure is for Eclipse IDE for Java.

Starts in windowed mode.

```
Shared Keys:
ALT-ENTER             -- toggles between windowed and full screen mode
F5                    -- Draw App
F6                    -- CAD App
F7                    -- Model App
F8                    -- Editor App
F9                    -- Game App
F12                   -- save a screenshot image file

Draw App Keys:
-- none --            -- placeholder key binding

CAD App Keys:
-- none --            -- placeholder key binding

Model App Keys:
-- none --            -- placeholder key binding

Editor App Keys:
-- none --            -- placeholder key binding

Game App Keys:
-- none --            -- placeholder key binding
```

# Installing and running
Install Eclipse IDE for Java Developers 2023‑09 (or later) and load the repository as a java project into the IDE:
https://www.eclipse.org/downloads/packages/release/2023-09/r/eclipse-ide-java-developers

Install JAVA JDK 21 or later at, and double click on the downloaded release javarenderengine.jar to run it directly:
https://www.oracle.com/java/technologies/downloads/#java21

To run the older JavaRenderEngine application interface, use console command:
java -cp javafxrenderengine.jar fi.jkauppa.javarenderengine.JavaRenderEngine

Download JavaFX version 21.0.2 LTS or later for your platform at https://openjfx.io/.
Make an user library from the JavaFX library .jar files, include them in modulepath in project build path properties,
include module path files in run configurations -> dependencies -> add modules from --- to ALL-MODULE-PATH.

To make an executable .jar file, export runnable .jar from eclipse with package required libraries into generated JAR.
After generating application .jar, add platform specific javafx jars and binaries to the root of the .jar zip file.
