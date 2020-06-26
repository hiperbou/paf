package resources

import com.soywiz.korau.sound.NativeSound
import com.soywiz.korau.sound.readNativeSound
import com.soywiz.korge.atlas.Atlas
import com.soywiz.korge.atlas.readAtlas
import com.soywiz.korge.view.Views
import com.soywiz.korim.bitmap.Bitmap
import com.soywiz.korim.bitmap.BitmapSlice
import com.soywiz.korim.bitmap.slice
import com.soywiz.korim.font.BitmapFont
import com.soywiz.korim.font.readBitmapFont
import com.soywiz.korim.format.readBitmap

import com.soywiz.korio.file.std.resourcesVfs

class Resources(private val views: Views) {
    companion object{
        lateinit var pafAtlas: Atlas
        lateinit var arrow:BitmapSlice<Bitmap>
        lateinit var font: BitmapFont
        lateinit var disparoSound: NativeSound
        lateinit var botaSound: NativeSound
        lateinit var pafSound: NativeSound
        lateinit var music: NativeSound

        private var loaded = false
        private var loadedGfx = false
        private var loadedMusic = false
    }

    suspend fun loadAll() {
        if(loaded) return
        loaded = true

        loadGfx()
        loadMusic()
    }

    suspend fun loadGfx() {
        if(loadedGfx) return
        loadedGfx = true

        pafAtlas = resourcesVfs["fpg.atlas.json"].readAtlas(views)
        arrow = resourcesVfs["fpg/020.png"].readBitmap().slice()
        font = resourcesVfs["texts/I-pixel-u.fnt"].readBitmapFont()

        botaSound = resourcesVfs["bota.wav"].readNativeSound(false)
        disparoSound = resourcesVfs["disparo.wav"].readNativeSound(false)
        pafSound = resourcesVfs["paf.wav"].readNativeSound(false)
        pafSound = resourcesVfs["paf.wav"].readNativeSound(false)

    }

    suspend fun loadMusic() {
        if(loadedMusic) return
        loadedMusic = true

        music = resourcesVfs["music.mp3"].readNativeSound(true)
    }

    fun setLoaded() {
        loaded = true
    }
}

