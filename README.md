# Mine Little Pony

[![Current Version](https://img.shields.io/github/v/tag/MineLittlePony/MineLittlePony)](https://img.shields.io/github/v/tag/MineLittlePony/MineLittlePony)
[![Build Status](https://github.com/MineLittlePony/MineLittlePony/actions/workflows/gradle-build.yml/badge.svg)](https://github.com/MineLittlePony/MineLittlePony/actions/workflows/gradle-build.yml)
![Downloads](https://img.shields.io/github/downloads/MineLittlePony/MineLittlePony/total.svg?color=yellowgreen)
[![Discord Server](https://img.shields.io/discord/182490536119107584.svg?color=blueviolet)](https://discord.gg/HbJSFyu)
![License](https://img.shields.io/github/license/MineLittlePony/MineLittlePony)
![](https://img.shields.io/badge/api-fabric-orange.svg)

Turns players and mobs into ponies.

https://minelittlepony-mod.com

## Building

1. JDK 17 is required. Install it using https://adoptium.net/?variant=openjdk17&jvmVariant=hotspot

2. Open a terminal window in the same directory as the sources (git clone or extracted from zip). Run the following command (windows).

```
gradlew build
```

3. After some time, the built mod will be in `/build/libs`.

## Installation

Fabric (And FabricAPI) are required. Please refer to their installation instructions <a href="https://fabricmc.net">here</a> and come back once you have Fabric functioning.

Once you have fabric installed, simply download the MineLittlePony-version.jar for your particular version from [the releases page](https://github.com/MineLittlePony/MineLittlePony/releases) and place it into your mods folder next to the fabric-api jar and (optionally) the hdskins jar.

**Remember to use the fabric launcher profile when starting the game!**

## Maven

Stable Releases Channel: `https://repo.minelittlepony-mod.com/maven/release`

Unstable Snapshot Channel: `https://repo.minelittlepony-mod.com/maven/snapshot`

Dependency: `com.minelittlepony:minelittlepony:<version>`
