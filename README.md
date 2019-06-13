# Mine Little Pony

[![Build Status](https://travis-ci.org/MineLittlePony/MineLittlePony.svg?branch=master)](https://travis-ci.org/MineLittlePony/MineLittlePony)
![Downloads](https://img.shields.io/github/downloads/MineLittlePony/MineLittlePony/total.svg?color=yellowgreen)
[![Discord Server](https://img.shields.io/discord/182490536119107584.svg?color=blueviolet)](https://discord.gg/HbJSFyu)
![](https://img.shields.io/badge/api-fabric-orange.svg)

Turns players and mobs into ponies.

http://minelittlepony-mod.com

## Building

1. JDK 8 is required. Install it using https://adoptopenjdk.net/

2. Open a terminal window in the same directory as the sources (git clone or extracted from zip). Run the following command (windows).

```
gradlew build
```

3. After some time, the built mod will be in `/build/libs`.

## Installation (Users):

Starting in 1.13 Mine Little Pony uses _Fabric_ for it's modding APIs. Refer to their installation instructions <a href="https://fabricmc.net">here</a>

Once you have fabric installed, simply download the -version.jar below and place it in your mods folder. 
**Remember to use the fabric launcher profile when starting the game!**


## Installation (Modders):

Maven: `http://repo.minelittlepony-mod.com/maven/snapshot`
Dependency: `com.minelittlepony:MineLittlePony:1.14.2-3.+`
