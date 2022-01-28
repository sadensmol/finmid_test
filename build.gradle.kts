import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.5.6"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.5.31"
    kotlin("plugin.spring") version "1.5.31"
}

group = "me.sadensmol.finmid"
version = "0.0.1"

val jacksonVersion = "2.12.4"

java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}

val developmentOnlyConfiguration by configurations.creating

configurations {
    runtimeClasspath {
        extendsFrom(developmentOnlyConfiguration)
    }
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}


dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")


    implementation( "com.fasterxml.jackson.core:jackson-databind:${jacksonVersion}")
    implementation( "com.fasterxml.jackson.module:jackson-module-kotlin:${jacksonVersion}")

    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")

    implementation("com.github.ben-manes.caffeine:caffeine:3.0.5")

    developmentOnly("org.springframework.boot:spring-boot-devtools")
    compileOnly("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    implementation(platform("com.google.cloud:libraries-bom:24.2.0"))

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.mockk:mockk:1.11.0")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
