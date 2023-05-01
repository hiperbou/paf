package gameplay

import korlibs.audio.sound.Sound
import korlibs.audio.sound.playbackTimes


class PafSounds (
        private val sfxDisparo: Sound,
        private val sfxBota: Sound,
        private val sfxPaf: Sound,
        private val music: Sound
){
    var mute = false

    fun playBota(){
        if(mute) return
        sfxBota.playNoCancel(1.playbackTimes)
    }

    fun playDisparo(){
        if(mute) return
        sfxDisparo.playNoCancel(1.playbackTimes)
    }

    fun playPaf(){
        if(mute) return
        sfxPaf.playNoCancel(1.playbackTimes)
    }

    fun playMusic() {
        if(mute) return
        music.playNoCancelForever()
    }
}