plugins {
    `java-library`
    jacoco
}

group = "net.cap5lut"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
}

sourceSets.create("test-integration") {
    java {
        srcDir("src/test-integration/java")
        compileClasspath += sourceSets["main"].output
        runtimeClasspath += sourceSets["main"].output
    }
    resources.srcDir("src/test-integration/resources")
}
fun DependencyHandler.testIntegrationImplementation(
    group: String,
    name: String,
    version: String? = null,
    configuration: String? = null,
    classifier: String? = null,
    ext: String? = null,
    dependencyConfiguration: Action<ExternalModuleDependency>? = null
): ExternalModuleDependency = org.gradle.kotlin.dsl.accessors.runtime.addExternalModuleDependencyTo(
    this, "testIntegrationImplementation", group, name, version, configuration, classifier, ext, dependencyConfiguration
)
fun DependencyHandler.testIntegrationRuntimeOnly(
    group: String,
    name: String,
    version: String? = null,
    configuration: String? = null,
    classifier: String? = null,
    ext: String? = null,
    dependencyConfiguration: Action<ExternalModuleDependency>? = null
): ExternalModuleDependency = org.gradle.kotlin.dsl.accessors.runtime.addExternalModuleDependencyTo(
    this, "testIntegrationRuntimeOnly", group, name, version, configuration, classifier, ext, dependencyConfiguration
)

dependencies {
    compileOnly("com.zaxxer", "HikariCP", "4.0.3")

    testImplementation("com.zaxxer", "HikariCP", "4.0.3")
    testImplementation("org.junit.jupiter", "junit-jupiter-api", "5.7.1")
    testImplementation("org.mockito", "mockito-core", "3.11.2")
    testRuntimeOnly("org.junit.jupiter", "junit-jupiter-engine", "5.7.1")
    testRuntimeOnly("org.slf4j", "slf4j-nop", "1.7.31")

    testIntegrationImplementation("com.zaxxer", "HikariCP", "4.0.3")
    testIntegrationImplementation("io.zonky.test", "embedded-postgres", "1.3.0")
    testIntegrationImplementation("org.junit.jupiter", "junit-jupiter-api", "5.7.1")
    testIntegrationImplementation("org.mockito", "mockito-core", "3.11.2")
    testIntegrationRuntimeOnly("org.junit.jupiter", "junit-jupiter-engine", "5.7.1")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
    modularity.inferModulePath.set(true)
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
    finalizedBy(tasks.getByName("jacocoTestReport"))
}

tasks.getByName<JacocoReport>("jacocoTestReport") {
    dependsOn(tasks.getByName("test"))
    reports {
        csv.required.set(true)
        html.required.set(true)
        xml.required.set(true)
    }
}

tasks.create<Test>("testIntegration") {
    group = "verification"
    testClassesDirs = sourceSets["test-integration"].output.classesDirs
    classpath = sourceSets["test-integration"].runtimeClasspath
    outputs.upToDateWhen { false }
    mustRunAfter(tasks.getByName("test"))
    tasks.getByName("check").dependsOn(this)

    useJUnitPlatform()
}