plugins {
    java
    id("org.jetbrains.kotlin.jvm") version "2.4.21"
    id("org.jetbrains.intellij.platform") version "2.19.0"
}

group = "com.winscript"
version = "0.2.0"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        // 2025.3 起 IC 与 Ultimate 合并为统一免费版，开发依赖使用 intellijIdea
        intellijIdea(providers.gradleProperty("platformVersion").get())
    }
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    // Gradle 测试 worker 启动需要 JUnit4 的 TestRule（org/junit/rules/TestRule）
    testRuntimeOnly("junit:junit:4.13.2")
}

kotlin {
    jvmToolchain(21)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.compilerArgs.add("-Xlint:deprecation")
    }
    test {
        useJUnitPlatform()
    }
}

intellijPlatform {
    pluginConfiguration {
        version = project.version.toString()
        ideaVersion {
            sinceBuild = provider { "253" }
        }
    }
    publishing {
        // token 放 ~/.gradle/gradle.properties 的 publishToken（不入仓库）
        token = providers.gradleProperty("publishToken")
    }
    pluginVerification {
        ides {
            // 复用构建时已解析的 IntelliJ Platform（版本由 gradle.properties 的 platformVersion 决定），
            // 避免硬编码本机缓存路径，让其他开发者/构建机也能直接跑 verifyPlugin
            current()
        }
    }
    buildSearchableOptions = false
}
