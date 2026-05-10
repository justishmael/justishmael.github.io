package io.github.justishmael.personalsite

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform