plugins {
    `java-library`
}

group = "net.cap5lut"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    api("net.cap5lut", "cap5lut-util", "1.0.0-SNAPSHOT")
    testImplementation("org.junit.jupiter", "junit-jupiter-api", "5.7.1")
    testRuntimeOnly("org.junit.jupiter", "junit-jupiter-engine", "5.7.1")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
    modularity.inferModulePath.set(true)
}

tasks.withType<Test> {
    useJUnitPlatform()
}