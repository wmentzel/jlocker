plugins {
    application
}

application {
    mainClassName = "main.MainFrame"
}

version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jgrapht:jgrapht-ext:0.9.0") // don't bump, breaking changes
    implementation("org.jgrapht:jgrapht-core:0.9.0") // don't bump, breaking changes
}

tasks {

    "wrapper"(Wrapper::class) {
        gradleVersion = "7.5.1"
    }

    compileJava {
        sourceCompatibility = "1.7"
        targetCompatibility = "1.7"
    }

    register<Jar>("fatJar") {
        manifest {
            attributes["Main-Class"] = "main.MainFrame"
        }
        archiveBaseName.set(project.name)
        from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
        with(jar.get())

        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }
}
