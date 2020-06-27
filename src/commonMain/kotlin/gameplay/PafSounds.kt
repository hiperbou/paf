package gameplay

import com.soywiz.korau.sound.NativeSound


class PafSounds (
        private val sfxDisparo: NativeSound,
        private val sfxBota: NativeSound,
        private val sfxPaf: NativeSound,
        private val music: NativeSound
){
    var mute = false

    fun playBota(){
        if(mute) return
        sfxBota.play()
    }

    fun playDisparo(){
        if(mute) return
        sfxDisparo.play()
    }

    fun playPaf(){
        if(mute) return
        sfxPaf.play()
    }

    fun playMusic() {
        if(mute) return
        music.playForever()
    }
}