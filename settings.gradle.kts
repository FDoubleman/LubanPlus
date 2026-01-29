pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenLocal()
        maven {
            isAllowInsecureProtocol = true
            url = uri("http://maven.aliyun.com/nexus/content/groups/public/")
        }
        maven {
            isAllowInsecureProtocol = true
            url = uri("http://maven.aliyun.com/nexus/content/repositories/jcenter")
        }
        maven {
            isAllowInsecureProtocol = true
            url = uri("http://maven.aliyun.com/nexus/content/repositories/google")
        }
        maven {
            isAllowInsecureProtocol = true
            url = uri("http://maven.aliyun.com/nexus/content/repositories/gradle-plugin")
        }
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://repo1.maven.org/maven2/")}
        google()
        mavenCentral()
    }
}
rootProject.name = "LubanPlus"
include(":app")
include(":lubanPlus")
