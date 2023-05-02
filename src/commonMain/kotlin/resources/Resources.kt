package resources

import korlibs.audio.sound.*
import korlibs.image.atlas.Atlas
import korlibs.image.atlas.readAtlas
import korlibs.image.bitmap.Bitmap
import korlibs.image.bitmap.BitmapSlice
import korlibs.image.font.BitmapFont
import korlibs.image.font.readBitmapFont
import korlibs.io.file.std.resourcesVfs
import korlibs.korge.view.Views

class Resources(private val views: Views) {
    companion object{
        lateinit var pafAtlas: Atlas
        lateinit var arrow:BitmapSlice<Bitmap>
        lateinit var font: BitmapFont
        lateinit var disparoSound: Sound
        lateinit var botaSound: Sound
        lateinit var pafSound: Sound
        lateinit var music: Sound

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

        pafAtlas = resourcesVfs["fpg.atlas.json"].readAtlas()
        font = resourcesVfs["texts/I-pixel-u.fnt"].readBitmapFont()

        botaSound = resourcesVfs["bota.wav"].readSound()
        disparoSound = resourcesVfs["disparo.wav"].readSound()
        pafSound = resourcesVfs["paf.wav"].readSound()
        pafSound = resourcesVfs["paf.wav"].readSound()

    }

    suspend fun loadMusic() {
        if(loadedMusic) return
        loadedMusic = true

        music = resourcesVfs["music.mp3"].readMusic()
    }

    fun setLoaded() {
        loaded = true
    }
}

