package scenes

import com.soywiz.klock.seconds
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.scene.sleep
import com.soywiz.korge.tween.get
import com.soywiz.korge.tween.tween
import com.soywiz.korge.view.Container
import com.soywiz.korge.view.image
import com.soywiz.korim.bitmap.BmpSlice
import com.soywiz.korim.bitmap.slice
import com.soywiz.korim.color.Colors
import com.soywiz.korim.format.readBitmap
import com.soywiz.korio.async.async
import com.soywiz.korio.async.launchImmediately
import com.soywiz.korio.file.std.resourcesVfs
import com.soywiz.korma.interpolation.Easing
import resources.Resources

class LoadingScene() : Scene() {

    override suspend fun Container.sceneInit() {

    }

    override suspend fun sceneAfterInit() {
        super.sceneAfterInit()
        val splash = async { sceneView.splash() }

        sleep(1.0.seconds)
        Resources(views).loadGfx()

        sleep(3.0.seconds)
        Resources(views).loadMusic()

        Resources(views).setLoaded()

        splash.await()
        sceneContainer.changeTo<TitleScene>()
    }

    suspend fun Container.splash() {
        views.clearColor = Colors.WHITE
        var map = resourcesVfs["korge.png"].readBitmap().slice()
        logo(map)
        map =  resourcesVfs["amakasoft.bmp"].readBitmap().slice()
        val anim = async {
            logo(map)
        }
        sleep(1.seconds)
        views.clearColor = Colors.BLACK
        anim.await()
    }

    suspend fun Container.logo(graph:BmpSlice) {
        val image = image(graph){
            alpha = 0.0
        }

        image.tween(image::alpha[1], time = 1.seconds, easing = Easing.EASE_IN_OUT)
        sleep(1.seconds)
        image.tween(image::alpha[0], time = 1.seconds, easing = Easing.EASE_IN_OUT)
    }


    /*function splash()
    private
        map=0;
    begin
        set_mode(256,192);
        set_clear_color(1,1,1);
        set_fps(30);
        map=load_map("logo.bmp");

        logo2(map,10000);
        frame(10000);
        map=load_map("amakasoft.bmp");
        logo2(map,9900);
        frame(5000);
        set_clear_color(0,0,0);
        frame(5500);
        unload_all_fpgs();
end*/
    /*process logo2(graph,espera)
    private
           i=0;
           j=31;
    begin
         alpha=0;
         x=128;
         y=96+32;
         for i=0,31 do
            alpha=i/31;
            frame;
         end
         frame(espera-6200);
         for i=0,31 do
            j=31-i
            alpha=j/31;
            frame;
         end
    end
 */




}