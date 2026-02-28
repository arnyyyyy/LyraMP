package com.arno.lyramp.feature.authorization.presentation

import com.arno.lyramp.feature.authorization.model.MusicServiceType

expect fun launchAuthUrl(url: String, service: MusicServiceType)
