plugins {
    id("java")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "quirkle.Main"
    }

    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
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

    implementation(files("libs/ResourceLair-0.2.3-SNAPSHOT.jar"))
}

tasks.test {
    useJUnitPlatform()
}