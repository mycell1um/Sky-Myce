import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
	id("net.fabricmc.fabric-loom")
	`maven-publish`
	id("org.jetbrains.kotlin.jvm") version "2.4.0"
}

version = providers.gradleProperty("mod_version").get()
group = providers.gradleProperty("maven_group").get()

repositories {
	// Add repositories to retrieve artifacts from in here.
	// You should only use this when depending on other mods because
	// Loom adds the essential maven repositories to download Minecraft and libraries from automatically.
	// See https://docs.gradle.org/current/userguide/declaring_repositories.html
	// for more information about repositories.

	// Hypixel API
	maven("https://repo.hypixel.net/repository/Hypixel/")
	maven("https://api.modrinth.com/maven")

	// Resourceful Config
	maven("https://maven.teamresourceful.com/repository/maven-public/")

	// Devauth
	maven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1")

	// owo lib
	maven("https://maven.wispforest.io")
	maven("https://jitpack.io")
}

dependencies {
	// To change the versions see the gradle.properties file
	minecraft("com.mojang:minecraft:${providers.gradleProperty("minecraft_version").get()}")
	implementation("net.fabricmc:fabric-loader:${providers.gradleProperty("loader_version").get()}")

	// Fabric API. This is technically optional, but you probably want it anyway.
	implementation("net.fabricmc.fabric-api:fabric-api:${providers.gradleProperty("fabric_api_version").get()}")
    implementation("net.fabricmc:fabric-language-kotlin:${providers.gradleProperty("fabric_kotlin_version").get()}")

	// Dev Auth
	runtimeOnly("me.djtheredstoner:DevAuth-fabric:${providers.gradleProperty("devauth_version").get()}")

	// Hypixel API
	implementation("net.hypixel:mod-api:${providers.gradleProperty("hypixel_mod_api_version").get()}")
	include("net.hypixel:mod-api:${providers.gradleProperty("hypixel_mod_api_version").get()}")

	// Skyblock API
	api("tech.thatgravyboat:skyblock-api:${providers.gradleProperty("skyblock_api_version").get()}") {
		capabilities { requireCapability("tech.thatgravyboat:skyblock-api-26.1") }
	}
	include("tech.thatgravyboat:skyblock-api:${providers.gradleProperty("skyblock_api_version").get()}") {
		capabilities { requireCapability("tech.thatgravyboat:skyblock-api-26.1") }
	}

	// Resourceful Config
	implementation("com.teamresourceful.resourcefulconfig:resourcefulconfig-fabric-26.1:${providers.gradleProperty("resourceful_config_version").get()}")
	include("com.teamresourceful.resourcefulconfig:resourcefulconfig-fabric-26.1:${providers.gradleProperty("resourceful_config_version").get()}")

	implementation("com.teamresourceful.resourcefulconfigkt:resourcefulconfigkt-26.1-rc-1:${providers.gradleProperty("resourceful_config_kt_version").get()}")
	include("com.teamresourceful.resourcefulconfigkt:resourcefulconfigkt-26.1-rc-1:${providers.gradleProperty("resourceful_config_kt_version").get()}")

	// owo lib
	implementation("io.wispforest:owo-lib:${providers.gradleProperty("owo_version").get()}")
	include("io.wispforest:owo-sentinel:${providers.gradleProperty("owo_version").get()}")
}

tasks.processResources {
	val version = version
	inputs.property("version", version)

	filesMatching("fabric.mod.json") {
		expand("version" to version)
	}
}

tasks.withType<JavaCompile>().configureEach {
	options.release = 25
}

kotlin {
	compilerOptions {
		jvmTarget = JvmTarget.JVM_25
	}
}

java {
	// Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
	// if it is present.
	// If you remove this line, sources will not be generated.
	withSourcesJar()

	sourceCompatibility = JavaVersion.VERSION_25
	targetCompatibility = JavaVersion.VERSION_25
}

tasks.jar {
	val projectName = project.name
	inputs.property("projectName", projectName)

	from("LICENSE") {
		rename { "${it}_$projectName" }
	}
}

// configure the maven publication
publishing {
	publications {
		register<MavenPublication>("mavenJava") {
			from(components["java"])
		}
	}

	// See https://docs.gradle.org/current/userguide/publishing_maven.html for information on how to set up publishing.
	repositories {
		// Add repositories to publish to here.
		// Notice: This block does NOT have the same function as the block in the top level.
		// The repositories here will be used for publishing your artifact, not for
		// retrieving dependencies.
	}
}
