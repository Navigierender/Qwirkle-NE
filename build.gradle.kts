plugins {
    id("java")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "quirkle.Main"
    }
}

group = "Qwirkle-NE"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}