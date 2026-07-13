import org.gradle.api.plugins.JavaPluginExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    kotlin("jvm") version "2.4.0" apply false
    kotlin("plugin.spring") version "2.4.0" apply false
    id("org.springframework.boot") version "4.1.0" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
    id("org.jlleitschuh.gradle.ktlint") version "14.2.0" apply false
}

allprojects {
    group = "com.keyloop"
    version = "0.1.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    extensions.configure<JavaPluginExtension> {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(25))
        }
    }

    extensions.configure<KotlinJvmProjectExtension> {
        jvmToolchain(25)
    }

    tasks.withType<KotlinJvmCompile>().configureEach {
        compilerOptions {
            freeCompilerArgs.addAll(
                "-Xjsr305=strict",
            )
        }
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
    }
}
