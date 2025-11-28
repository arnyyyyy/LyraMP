package com.arno.lyramp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform