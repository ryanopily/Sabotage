# Sabotage

## What is sabotage?

Sabotage is a classic PVP gamemode created by ThaRedstoner, resembling Trouble in Terrorist Town.  
**This is NOT a release**  

## Version

This plugin is made for Spigot 1.19  

## Dependencies

- [Citizens2](https://ci.citizensnpcs.co/view/Citizens/job/Citizens2/)
- [CorpseImmortal](https://github.com/ryanopily/corpseimmortal)
- [Config](https://github.com/ryanopily/config)

## Instructions

Dependencies should be installed in the local maven repository.  
Use the 'mvn install' in the pom.xml root directory if compiling from source.  

### Note
Using the Citizens2 API as a dependency is not sufficient.   
You must install the actual plugin jar file into the local maven repository using the following command:  
mvn install:install-file -Dfile=(path to dependency jar) -DgroupId=net.citizensnpcs -DartifactId=citizens -Dversion=(version in pom) -Dpackaging=jar  
