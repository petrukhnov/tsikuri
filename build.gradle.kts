plugins {
    id("java")
    kotlin("jvm")
    id("java-library")
    id("maven-publish")
    id("signing")
}

group = "com.petrukhnov.tsikuri"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.4")
    //image detection
    implementation("org.openpnp:opencv:4.9.0-0") //fixme should be also implemented in the project
    //key hooks
    implementation("com.1stleg:jnativehook:2.1.0")
    //text recognition
    implementation("net.sourceforge.tess4j:tess4j:5.19.0")

    testImplementation(platform("org.junit:junit-bom:6.0.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.slf4j:slf4j-api:2.0.17")
    testImplementation("ch.qos.logback:logback-classic:1.5.18")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
    failOnNoDiscoveredTests = false
}
kotlin {
    jvmToolchain(21)
}

java {
    withSourcesJar()
    withJavadocJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}
