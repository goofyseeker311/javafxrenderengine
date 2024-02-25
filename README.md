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
ENTER                 -- toggles between alpha/src composite pencil draw mode
BACKSPACE             -- erases the whole window canvas to transparent
DRAG-LMB              -- black/hsba-color drag paint
SHIFT-LMB             -- rgba-color picker at cursor
ALT-DRAG-LMB          -- black/hsba-color line drag paint
DRAG-CMB              -- drag image contents on image canvas
MWHEEL                -- pencil width change (minmax)
SHIFT-MWHEEL          -- pencil brush image rotation angle change (looping)
CTRL-MWHEEL           -- hue positive change (looping)
CTRL-ALT-MWHEEL       -- saturation positive change (minmax)
ALT-MWHEEL            -- brightness positive change (minmax)
DRAG-RMB              -- transparent drag paint eraser
ALT-DRAG-RMB          -- transparent line drag paint eraser
INSERT                -- hue positive change (looping)
DELETE                -- hue negative change (looping)
HOME                  -- saturation positive change (minmax)
END                   -- saturation negative change (minmax)
PGUP                  -- brightness positive change (minmax)
PGDOWN                -- brightness negative change (minmax)
NUMPAD+               -- pencil width larger (minmax)
NUMPAD-               -- pencil width smaller (minmax)
NUMPAD*               -- pencil type change next (looping)
NUMPAD/               -- pencil type change previous (looping)
NUMPAD9               -- pencil transparency positive (minmax)
NUMPAD8               -- pencil transparency negative (minmax)
NUMPAD6               -- pencil brush image rotation angle change positive (looping)
NUMPAD5               -- pencil brush image rotation angle change negative (looping)
F2                    -- save image file dialog
F3                    -- load image file dialog
SHIFT-F3              -- load image file as pencil brush dialog
CTRL-C                -- copy image to clipboard
CTRL-V                -- paste image from clipboard

CAD App Keys:
ENTER                 -- changes between polygon flat/textured/none fill modes (looping)
SHIFT-ENTER           -- changes between unlit and lit render modes (looping)
CTRL-ENTER            -- re-calculates all entity triangle surface light maps
WASD                  -- camera location change up/left/down/right (minmax)
C-SPACE               -- camera location change backward/forward (minmax)
QE                    -- camera tilt change left/right (looping)
BACKSPACE             -- removes all vector lines
SHIFT-BACKSPACE       -- reset camera to starting location
CTRL-BACKSPACE        -- reset all entity triangle surface light maps to zero
DRAG-LMB              -- material drag triangle paint and place entity (minmax)
SHIFT-LMB             -- material picker at cursor
CTRL-DRAG-LMB         -- move line vertex
ALT-DRAG-LMB          -- vector line drag draw (in vector line mode)
PERIOD-DRAG-LMB       -- remove line vertex or triangle surface
PERIOD-DRAG-RMB       -- remove entity
DRAG-RMB              -- move/ground entity (minmax)
CTRL-DRAG-RMB         -- rotate entity (looping)
ALT-DRAG-RMB          -- scale entity (minmax)
SHIFT                 -- toggle snap to grid/vertex, drag multiple vertex, and speed movement
DRAG-CMB              -- camera location view position sideways pan (minmax)
CTRL-DRAG-CMB         -- change forward looking movement direction (looping)
MWHEEL                -- draw forward position change (minmax)
ARROW-KEYS            -- change forward looking movement direction (looping)
TAB-DRAG-CMB          -- triangle texture coordinates pan (minmax)
TAB-MWHEEL            -- triangle texture coordinates zoom
TAB-SHIFT-MWHEEL      -- triangle texture coordinates rotate
TAB-CTRL-SHIFT-MWHEEL -- triangle texture coordinates scale
TAB-CTRL-MWHEEL       -- triangle texture coordinates shear
INSERT                -- hue positive change (looping)
DELETE                -- hue negative change (looping)
HOME                  -- saturation positive change (minmax)
END                   -- saturation negative change (minmax)
PGUP                  -- brightness positive change (minmax)
PGDOWN                -- brightness negative change (minmax)
NUMPAD+               -- camera location change forward (minmax)
NUMPAD-               -- camera location change backward (minmax)
NUMPAD*               -- material emissivity positive (minmax)
NUMPAD/               -- material emissivity negative (minmax)
NUMPAD9               -- pencil transparency positive (minmax)
NUMPAD8               -- pencil transparency negative (minmax)
NUMPAD7               -- triangle single sided normal invert (looping)
NUMPAD6               -- material roughness positive (minmax)
NUMPAD5               -- material roughness negative (minmax)
NUMPAD4               -- triangle double sided zero normal (set)
NUMPAD3               -- material metallic positive (minmax)
NUMPAD2               -- material metallic negative (minmax)
NUMPAD1               -- triangle texture coordinate reset, mirror and rotate (looping)
NUMPAD0               -- run entity list updater
F2                    -- save model file dialog (all primitives)
SHIFT-F2              -- save model file dialog (surface only)
F3                    -- load model file dialog
CTRL-F3               -- load insert model file dialog
SHIFT-F3              -- load texture image file dialog
F4                    -- render and save projected view 3840x2160 image with black opaque background
CTRL-F4               -- render and save cube map view 6144x4096 image with transparent background
SHIFT-F4              -- render and save sphere map 7680x2160 image with transparent background
CTRL-SHIFT-F4         -- render and save projected view 4K image with transparent background

Model App Keys:
BACKSPACE             -- remove loaded model and reset camera location
MOUSE-MOVE            -- change forward looking movement direction
WASD                  -- camera location change forward/left/backward/right (minmax)
C-SPACE               -- camera height change down/up (minmax)
QE                    -- camera tilt change left/right (looping)
F3                    -- load model file dialog
ENTER                 -- changes between polygon-projection/polygon-cubemap/
                          plane-projection/plane-spheremap/plane-cubemap/
                          ray-projection/ray-spheremap/ray-cubemap renderers (looping)
SHIFT-ENTER           -- changes between unlit and lit render modes (looping)

Editor App Keys:
-- none --            -- placeholder key binding

Game App Keys:
-- none --            -- placeholder key binding
```

# Installing and running

Install JAVA JDK 21 or later at, and double click on the downloaded release javarenderengine.jar to run it directly:
https://www.oracle.com/java/technologies/downloads/#java21

Alternative way of running the program is to open a console window on the javarenderengine.jar location and type command
"java -jar javarenderengine.jar", which will also show debug output text on the console window. Otherwise console debug output
can be activated in the Java Control Panel or Configure Java application -> Advanced -> Java console -> Show console and
Miscellaneous -> Place Java icon in system tray if the java icon is not already visible on your operating system tray.

# Development and distribution

Install Eclipse IDE for Java Developers 2023‑12 (or later) and load the repository as a java project into the IDE:
https://www.eclipse.org/downloads/packages/release/2023-12/r/eclipse-ide-java-developers

Download JavaFX version 21.0.2 LTS or later for your platform at https://openjfx.io/.
Make an user library from the JavaFX library .jar files, include them in modulepath in project build path properties,
include module path files in run configurations -> dependencies -> add modules from --- to ALL-MODULE-PATH.

To make an executable .jar file, export runnable .jar from eclipse with package required libraries into generated JAR.
After generating application .jar, add platform specific javafx jars and binaries to the root of the .jar zip file.

# Licence (FSNLR -- Free Software No License Required)
This is free software which does not require any license agreement under government enforcement to limit it's freedom of usage for any purpose.

Example renders:
![render2](https://github.com/goofyseeker311/javafxrenderengine/assets/19920254/3f70b282-b0c0-4c44-94d1-eff5b81afcf5)
![render3](https://github.com/goofyseeker311/javafxrenderengine/assets/19920254/733d8953-3d23-495f-a6c4-bc279eaeac52)
![render4](https://github.com/goofyseeker311/javafxrenderengine/assets/19920254/6f3d2aff-da9e-44db-896a-7a4ef5b53e32)
