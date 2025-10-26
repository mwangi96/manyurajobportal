pluginManagement {
    repositories {
        google()         // ✅ Keep this simple, don’t filter Firebase out
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()         // ✅ Ensure Firebase can resolve
        mavenCentral()
    }
}

rootProject.name = "ManyuraJobPortal" // ✅ Avoid spaces in project name
include(":app")
