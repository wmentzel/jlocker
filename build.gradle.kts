plugins {
    application
    kotlin("jvm") version "1.7.10"
}

application {
    mainClass.set("com.randomlychosenbytes.jlocker.MainKt")
}

version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {

    implementation("com.google.code.gson:gson:2.9.0")
    implementation("com.google.guava:guava:31.1-jre")

    implementation("org.jgrapht:jgrapht-ext:1.0.0") // don't bump, breaking changes
    implementation("org.jgrapht:jgrapht-core:1.0.0") // don't bump, breaking changes

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")

    testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.25")
    testImplementation("org.mockito:mockito-core:4.7.0")
    testImplementation("org.mockito:mockito-inline:4.7.0")
    testImplementation("org.mockito:mockito-junit-jupiter:4.7.0")
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")
}

tasks {

    test {
        useJUnitPlatform()
    }

    register<Jar>("fatJar") {
        manifest {
            attributes["Main-Class"] = "com.randomlychosenbytes.jlocker.MainKt"
        }
        archiveBaseName.set(project.name)
        from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
        with(jar.get())

        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }

    "wrapper"(Wrapper::class) {
        gradleVersion = "7.5.1"
    }
}
