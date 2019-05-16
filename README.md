# Clerk
<img align="right" src="https://vignette.wikia.nocookie.net/muppet/images/f/fc/Clerkfraggle.jpg/revision/latest?cb=20060727223537">

Clerk wraps the Kuberntes/OpenShift API and allows an application to perform a limited set of operations in its own namespace.

The component is named after Clerk from the TV-show Fraggle Rock (https://muppet.fandom.com/wiki/Clerk_Fraggle).

## Setup

Clerk requires java 11

In order to use this project you must set repositories in your `~/.gradle/init.gradle` file

    allprojects {
        ext.repos= {
            mavenCentral()
            jcenter()
        }
        repositories repos
        buildscript {
         repositories repos
        }
    }

We use a local repository for distributionUrl in our gradle-wrapper.properties, you need to change it to a public repo in order to use the gradlew command. `../gradle/wrapper/gradle-wrapper.properties`

    <...>
    distributionUrl=https\://services.gradle.org/distributions/gradle-<version>-bin.zip
    <...>
