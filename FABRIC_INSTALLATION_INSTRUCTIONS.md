# Installation

Follow these instructions to install *MineLittlePony-MOD* on MC 1.13 or later versions. 

## Environment Required

- Any OS which supports **Java** and **Minecraft**. (e.g. Microsoft Windows, Apple macOS, GNU/Linux, **but not MS-DOS**!)
- Oracle JDK 1.8 or OpenJDK (**not recommended**).
- A **Minecraft Launcher**, already installed a Minecraft version. Highly suggest you use the official launcher. Fabric **might not support** HMCL or other third-party launchers.

## How To Set Up

1. Download:

   - Fabric Installer

     - Download Page: https://jenkins.modmuss50.me/job/FabricMC/job/fabric-installer/job/master/
     - Direct Link:https://jenkins.modmuss50.me/job/FabricMC/job/fabric-installer/job/master/lastSuccessfulBuild/artifact/build/libs/fabric-installer-0.5.2.40.jar

   - Fabric API

     - Download Page: https://www.curseforge.com/minecraft/mc-mods/fabric-api/files/all

     At this page, choose a Fabric API which **SUPPORTS YOUR MC VERSION**. **DO NOT** just click *Download* if you aren't the latest MC version!!!

2. Prepare your `.minecraft` folder:

   - Find your `.minecraft` folder, it should be at:
     - Microsoft Windows:
       - Official Launcher: `C:\Users\<Your Name>\AppData\Roaming\.minecraft`
       - Third-Party Launcher: `<The Directory Contains The Launcher>\.minecraft`
     - Apple macOS:
       - Official Launcher: `/home/Library/Application Support/minecraft`
       - Third-Party Launcher: Usually As Windows
     - GNU/Linux:
       - Official Launcher: `/home/.minecraft` (Might be hidden)
       - Third-Party Launcher: Usually As Windows
   - Copy the exact path of it.

3. Install Fabric:

   - Run `fabric-installer-<version>.jar`. (Double Click)

   - **If failed**, open your terminal and run:

     ```
     cd <The Directory Where Fabric Was Saved>
     java -jar fabric-installer-<version>.jar
     ```

     And see what happened.

   - In the open window, select your MC version and **Select Install Location**, point it directly to your `.minecraft` folder.

     (For Official Launcher, this step can be skipped.)

   - Check **Create profile**.
   - Click **Install**.
   - Wait for a while, then **Done** will appear.
   - Quit Installer.

4. Install Fabric API:

   - Copy `fabric-api-<version>+build.<build version>-<MC version>.jar` to `./minecraft/mods/`. If it doesn't exist, create it.

5. Install Mods:

   - Copy your mods to `./minecraft/mods/`.
   - Remove **ANY MOD using other mod loader**. Considering they might cause unexpected errors.

6. Install Mod Menu (Optional):

   - Download from https://www.curseforge.com/minecraft/mc-mods/modmenu/files/all, remember to **CHOOSE THE RIGHT VERSION**.
   - Copy it to `./minecraft/mods/`

7. Start MC:

   - In your launcher, switch the game version to `fabric-loader-<version>+build.<build version>-<MC version>`.
   - Launch the game.
   - See if your Mods has loaded correctly. For *MineLittlePony*, a *Ponify Settings* button will be shown on the main menu.

All done. Have fun!

## What about Other ML?

- Forge: Nope... Sadly, you cannot load both **Fabric Mods and Forge Mods** now. Maybe this will change soon.
- OptiFine: Eeyup, install OptiFabric from https://www.curseforge.com/minecraft/mc-mods/optifabric/files/all, then download OptiFine, put them together in `.minecraft/mods`.

Have a good time!
