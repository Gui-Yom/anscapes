plugins {
    `java-library`
    application
    `maven-publish`
    id("com.github.ben-manes.versions") version "0.29.0"
}

group = "com.github.Gui-Yom"
version = "0.10.0"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            pom {
                name.set("anscapes")
                description.set("Color codes and images for your terminal")
                url.set("https://github.com/Gui-Yom/anscapes")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://github.com/Gui-Yom/anscapes/blob/master/LICENSE")
                    }
                }
                developers {
                    developer {
                        id.set("Gui-Yom")
                        name.set("Guillaume Anthouard")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/Gui-Yom/anscapes.git")
                    developerConnection.set("scm:git:ssh://github.com/Gui-Yom/anscapes.git")
                    url.set("https://github.com/Gui-Yom/anscapes/")
                }
            }
        }
    }
}

repositories {
    mavenLocal()
    mavenCentral()
    jcenter()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.6.2")
}

application {
    mainClass.set("tech.guiyom.anscapes.Main")
}

tasks {
    withType(JavaCompile::class).configureEach {
        options.encoding = "UTF-8"
    }

    test {
        useJUnitPlatform()
    }
    jar {
        manifest {
            attributes(
                    "Automatic-Module-Name" to "tech.guiyom.anscapes",
                    "Main-Class" to application.mainClass.get()
            )
        }
    }

    clean {
        delete("temp")
        delete("out")
    }

    dependencyUpdates {
        resolutionStrategy {
            componentSelection {
                all {
                    if (isNonStable(candidate.version) && !isNonStable(currentVersion)) {
                        reject("Release candidate")
                    }
                }
            }
        }
        checkConstraints = true
        gradleReleaseChannel = "current"
    }
}

fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.toUpperCase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return isStable.not()
}
