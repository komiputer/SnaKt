plugins {
    kotlin("jvm")
    id("maven-publish")
    `java-library`
    `java-test-fixtures`
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(kotlin("compiler"))
    api(kotlin("compiler-internal-test-framework"))

    testFixturesApi(kotlin("test-junit5"))
}

sourceSets {
    main {
        java.srcDirs("src")
        resources.srcDir("resources")
    }
    test {
        java.setSrcDirs(emptyList<String>())
        resources.setSrcDirs(emptyList<String>())
    }
    testFixtures {
        java.setSrcDirs(listOf("test-fixtures"))
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}
