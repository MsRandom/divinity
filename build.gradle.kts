plugins {
    id("earth.terrarium.cloche") version "0.9.4"
    kotlin("jvm") version "2.1.0"
}

repositories {
    cloche.librariesMinecraft()

    mavenCentral()

    maven(url = "https://maven.neoforged.net/mojang-meta")
    cloche.mavenNeoforged()
}

cloche {
    metadata {
        modId = "project_i"
        name = "Project I"

        description = "Music for the soul"

        license = "CCL"
    }

    minecraftVersion = "1.21.1"

    singleTarget {
        neoforge {
            loaderVersion = "21.1.153"

            data()
            test()

            runs {
                server()
                client()

                data()
            }
        }
    }

    mappings {
        official()
        parchment("2024.11.17")
    }
}
