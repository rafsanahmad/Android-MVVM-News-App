/*
 * *
 *  * Created by Rafsan Ahmad on 9/27/21, 5:30 PM
 *  * Copyright (c) 2021 . All rights reserved.
 *
 */

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)
    repositories {
        google()
        mavenCentral()
    }
}
include(":app", ":core", ":domain", ":feature_news", ":feature_favorite", ":feature_search", ":feature_details", ":data")
rootProject.name = "News App"

