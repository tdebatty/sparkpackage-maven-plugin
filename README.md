# sparkpackage-maven-plugin
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/info.debatty/sparkpackage-maven-plugin/badge.svg)](https://maven-badges.herokuapp.com/maven-central/info.debatty/sparkpackage-maven-plugin)

Maven plugin for publishing on spark-packages.org


## Installation

Add the plugin to the build plugins section of your pom.xml:

```xml
<build>
    <plugins>
        <plugin>
            <groupId>info.debatty</groupId>
            <artifactId>sparkpackage-maven-plugin</artifactId>
            <version>0.1</version>
            <executions>
                <execution>
                    <phase>package</phase>
                    <goals>
                        <goal>zip</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

With the above configuration, type `mvn package` to build the correct zip for spark-packages.org.