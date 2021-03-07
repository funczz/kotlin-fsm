kotlin-fsm
==========
Finite State Machines in Kotlin.

Using Gradle
------------
In your build.gradle.kts.

kotlin-fsm-reentrant:
```kotlin
repositories {
    maven { setUrl("https://funczz.github.io/kotlin-fsm") }
}
dependencies {
    implementation("com.github.funczz:kotlin-fsm-reentrant:<VERSION>")
}
```
kotlin-fsm-simple:
```kotlin
repositories {
    maven { setUrl("https://funczz.github.io/kotlin-fsm") }
}
dependencies {
    implementation("com.github.funczz:kotlin-fsm-simple:<VERSION>")
}
```

How to use
----------
see "kotlin-fsm-demo" sub project.

Demo
----
```shell script
./gradlew run
```

