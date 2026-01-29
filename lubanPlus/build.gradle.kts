import java.util.Properties
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.PublishingExtension
import org.gradle.plugins.signing.SigningExtension

plugins {
    id("com.android.library")
    id("maven-publish")
    id("signing")
}

android {
    namespace = "cn.xdf.lubanplus.lubanPlus"
    compileSdk = 34

    defaultConfig {
        minSdk = 21
        targetSdk = 34
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
}

afterEvaluate {
    // --- Publishing Info ---
    project.extra.set("PUBLISH_GROUP_ID", "io.github.FDoubleman")
    project.extra.set("PUBLISH_ARTIFACT_ID", "LubanPlus")
    project.extra.set("PUBLISH_VERSION", "1.0.2")

    // --- Signing Info ---
    project.extra.set("ossrhUsername", "")
    project.extra.set("ossrhPassword", "")
    project.extra.set("signing.keyId", "")
    project.extra.set("signing.password", "")
    project.extra.set("signing.secretKeyRingFile", "")

    val secretPropsFile = project.rootProject.file("local.properties")
    if (secretPropsFile.exists()) {
        val p = Properties()
        p.load(secretPropsFile.inputStream())
        p.forEach { name, value -> project.extra.set(name.toString(), value.toString()) }
    }

    val PUBLISH_GROUP_ID = project.extra.get("PUBLISH_GROUP_ID") as String
    val PUBLISH_ARTIFACT_ID = project.extra.get("PUBLISH_ARTIFACT_ID") as String
    val PUBLISH_VERSION = project.extra.get("PUBLISH_VERSION") as String
    val ossrhUsername = project.extra.get("ossrhUsername") as String
    val ossrhPassword = project.extra.get("ossrhPassword") as String
    val signingKeyId = project.extra.get("signing.keyId") as String
    val signingPassword = project.extra.get("signing.password") as String

    configure<PublishingExtension> {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])

                groupId = PUBLISH_GROUP_ID
                artifactId = PUBLISH_ARTIFACT_ID
                version = PUBLISH_VERSION

                pom {
                    name.set(PUBLISH_ARTIFACT_ID)
                    description.set("上传aar插件至mavencentral，方便使用implementation快速引入")
                    url.set("https://github.com/FDoubleman/LubanPlus")
                    licenses {
                        license {
                            name.set("The Apache License, Version 2.0")
                            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }
                    developers {
                        developer {
                            id.set("fumanman")
                            name.set("fumanman")
                            email.set("fmm_330@163.com")
                        }
                    }
                    scm {
                        connection.set("scm:git:github.com/FDoubleman/LubanPlus.git")
                        developerConnection.set("scm:git:ssh://github.com/FDoubleman/LubanPlus.git")
                        url.set("https://github.com/FDoubleman/LubanPlus/tree/master")
                    }
                }
            }
        }
        repositories {
            maven {
                name = "LubanPlus"
                val releasesRepoUrl = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
                val snapshotsRepoUrl = "https://s01.oss.sonatype.org/content/repositories/snapshots/"
                url = uri(if (PUBLISH_VERSION.endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)
                credentials {
                    username = ossrhUsername
                    password = ossrhPassword
                }
            }
        }
    }

    configure<SigningExtension> {
        val useInMemory = signingKeyId.isNotEmpty() && signingPassword.isNotEmpty()
        if (useInMemory) {
            useInMemoryPgpKeys(signingKeyId, signingPassword)
        }
        sign(the<PublishingExtension>().publications["release"])
    }
}
