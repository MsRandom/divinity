plugins {
    id("earth.terrarium.cloche") version "0.9.9"
    kotlin("jvm") version "2.1.0"
}

repositories {
    cloche.librariesMinecraft()

    mavenCentral()

    cloche {
        mavenNeoforgedMeta()
        mavenNeoforged()
    }
}

cloche {
    metadata {
        modId = "divinity"
        name = "Divinity"

        description = "Music for the soul"

        license = "CCL"
    }

    minecraftVersion = "1.21.1"

    singleTarget {
        neoforge {
            loaderVersion = "21.1.153"

            mixins.from("divinity.mixins.json")

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
