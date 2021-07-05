plugins {
    `java-library`
    jacoco
}

group = "net.cap5lut"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("com.h2database", "h2", "1.4.200")
    testImplementation("io.zonky.test", "embedded-postgres", "1.3.0")
    testImplementation("org.junit.jupiter", "junit-jupiter-api", "5.7.1")
    testImplementation("org.mockito", "mockito-core", "3.11.2")
    testRuntimeOnly("org.junit.jupiter", "junit-jupiter-engine", "5.7.1")
    testRuntimeOnly("org.slf4j", "slf4j-nop", "1.7.31")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
    modularity.inferModulePath.set(true)
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
}