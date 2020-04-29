# Mine Little Pony

[![Build Status](https://travis-ci.org/MineLittlePony/MineLittlePony.svg?branch=master)](https://travis-ci.org/MineLittlePony/MineLittlePony)
![Downloads](https://img.shields.io/github/downloads/MineLittlePony/MineLittlePony/total.svg?color=yellowgreen)
[![Discord Server](https://img.shields.io/discord/182490536119107584.svg?color=blueviolet)](https://discord.gg/HbJSFyu)
![](https://img.shields.io/badge/api-fabric-orange.svg)

Turns players and mobs into ponies.

https://minelittlepony-mod.com

## Building

1. JDK 8 is required. Install it using https://adoptopenjdk.net/

2. Open a terminal window in the same directory as the sources (git clone or extracted from zip). Run the following command (windows).

```
gradlew build
```

3. After some time, the built mod will be in `/build/libs`.

## Installation (Users):

Starting in 1.13 Mine Little Pony uses _Fabric_ for it's modding APIs. Refer to their installation instructions <a href="https://fabricmc.net">here</a>

Once you have fabric installed, simply download the MineLittlePony-version.jar from releases and place it in your mods folder. 
**Remember to use the fabric launcher profile when starting the game!**


## Installation (Modders):

Yer probably lookin' fer a meven, am I right? Well ye have two choices. Pick yer poison.


Stable Releases Channel: `https://repo.minelittlepony-mod.com/maven/release`

Unstable Snapshot Channel: `https://repo.minelittlepony-mod.com/maven/snapshot`

Dependency: `com.minelittlepony:MineLittlePony:<version>-1.15.2`

Check [releases](https://github.com/MineLittlePony/MineLittlePony/releases) for the most recent release version or the `gradle.properties` for most recent snapshot version.
