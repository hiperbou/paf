package scenes

import com.soywiz.klock.seconds
import com.soywiz.kmem.unsetBits
import com.soywiz.korge.input.onKeyDown
import com.soywiz.korge.input.onKeyUp
import com.soywiz.korge.time.delay
import com.soywiz.korge.view.*
import com.soywiz.korio.async.launchAsap
import com.soywiz.korio.async.launchImmediately
import extensions.toBool
import gameplay.*
import input.*
import resources.Resources

var musicPlaying = 0



class TitleScene() : SceneBase() {

    lateinit var pafSounds:PafSounds


    override suspend fun Container.sceneInit() {

        Resources(views).loadAll()

        pafSounds = PafSounds(Resources.disparoSound,Resources.botaSound, Resources.pafSound, Resources.music)

        if(musicPlaying == 0){
            musicPlaying = 1
            pafSounds.playMusic()
        }

        currentGameState = GameState()

        inicio()
    }

    override suspend fun sceneDestroy() {
        super.sceneDestroy()
        println("Destroy")
    }

    override suspend fun sceneAfterDestroy() {
        super.sceneAfterDestroy()
        println("AfterDestroy")
    }

    inner class inicio:Process(sceneView) {
        override suspend fun main() {
            with(foto(30,160,119,100,100,0)){ //llamada para crear imagen del fondo
                launchAsap {
                    loop {
                        scaleX = 1.0
                        delay(0.25.seconds)
                        scaleX = -1.0
                        delay(0.25.seconds)
                    }
                }
            }

            letraTitulo(10,107,-60)       //llamada al proceso letra_titulo para la letra P
            letraTitulo(12,146,-80)       //para la letra A
            letraTitulo(14,187,-100)       //para la letra F
            letraTitulo(16,220,-120)      //para la letra !

            foto(5,160,220,100,100,0)      //llamada para crear imagen del nombre del creador

            protaDemo()

            pulsaIntro()

            delay(1.seconds)
            //sceneContainer.changeTo<LoadingScene>()
            //sceneContainer.changeTo<TransitionScene>()

            var counter = 0
            var key = 0

            //onKeyDown { key = key.setBits(getButtonPressed(it)) }
            onKeyDown { key = getButtonPressed(it) }
            onKeyUp { key = key.unsetBits(getButtonPressed(it)) }

            loop {
                //counter++
                //println("this shouldn't be called in another scene $counter")
                //delay(1.seconds)
                if ((key and (BUTTON_A or BUTTON_B or BUTTON_C or BUTTON_START)).toBool()){
                    sceneContainer.changeTo<TransitionGameScene>()
                }

                frame()
            }
        }
    }


    inner class pulsaIntro:Process(sceneView) {
        override suspend fun main() {
            position(160,200)
            anchor(0.5, 0.5)
            smoothing = false

            loop {
                graph = 6
                delay(0.125.seconds)
                graph = 7
                delay(0.125.seconds)
            }
        }
    }

    inner class letraTitulo(private val initialGraph:Int, private val initialX:Int, private val initialY:Int):Process(sceneView) {
        override suspend fun main() {
            var anima = 0
            graph = initialGraph

            position(initialX,initialY)

            anchor(0.5, 0.5)
            scale(1.0,1.0)
            smoothing = false

            loop {
                if (anima==0) {      //comprueba si anima es 0
                    y += 6
                    if (y > 90) {
                        y = 90.0
                        anima = 1
                        pafSounds.playPaf()
                        //play_wav(sfx1,0);
                    }    //si anima es 0 incremeta la y+6 y comprueba si y es mayor de 90 si es mayor, la variable anima se le da el valor 1 para seguir con otra animacion
                }
                if (anima==1) {      //comprueba si anima es 1
                    graph++
                    scale = 2.0
                    anima=2     //si anima es 1 hara otra animacion a la anterior y pasara a anima=2 para no volver a repetirlo
                }
                if (anima==2) {      //comprueba si anima es 2
                    scale -= 0.2
                    if (scale < 0.5) {
                        scale = 1.0
                        anima=3
                    }
                }
                if (anima==3) {
                    if (graph==11) { graph=1 }      //comprueba si graph es 11 para cambiarlo a 1
                    if (graph==13) { graph=2 }      //comprueba si graph es 13 para cambiarlo a 2
                    if (graph==15) { graph=3 }      //comprueba si graph es 15 para cambiarlo a 3
                    if (graph==17) { graph=4 }      //comprueba si graph es 17 para cambiarlo a 4
                    anima = 4
                }

                frame()
            }

        }
    }

    inner class protaDemo:Process(sceneView) {
        override suspend fun main() {
            var anima = 0
            graph = 22

            position(340,160)

            anchor(0.5, 0.5)
            scale(1.0,1.0)
            smoothing = false


            loop {
                if (anima==0) {       //comprueba si anima es 0
                    graph++
                    if (graph>25){ graph=22 }        //incrementa graph+1 y comprueba si es mayor de 25 si es mayor inicia al grafico 22
                    x=x-3
                    if (x<-20){
                        x=-20.0
                        anima=1
                        graph=26
                    }     //resta x-3 y comprueba si es menor que -20 y cambia de graph y de anima
                }
                if (anima==1) {       //comprueba si anima es 1
                    graph++
                    if (graph>29) { graph=26 }        //incrementa graph+1 y comprueba si es mayor de 29 si es mayor inicia al grafico 29
                    x=x+3
                    if (x>340){
                        x=340.0
                        anima=0
                        graph=22
                    }     //incrementa x+3 y comprueba si es mayor que 340 y cambia de graph y de anima
                }
                frame()
            }
        }
    }




    /*
    process prota_demo();
    private
        anima=0;    //variable para hacer saltos de animacion
    begin
        graph=22;   //esto es el para poner el grafico en pantalla
        x=340;      //posicion horizontal de pantalla
        y=160;      //posicion vertical de pantalla
        size_x,size_y=1;   //lo dejamos al tama�o normal
        z=10;       //esto hace la profundidad para los 2D si un grafico debe de estar delante de otro, si es menor estara delante si es mayor detras
        file=1;
            loop
                if (anima==0) then       //comprueba si anima es 0
                    graph=graph+1;if (graph>25)then graph=22;end        //incrementa graph+1 y comprueba si es mayor de 25 si es mayor inicia al grafico 22
                    x=x-3;if (x<-20)then x=-20;anima=1;graph=26;end     //resta x-3 y comprueba si es menor que -20 y cambia de graph y de anima
                end
                if (anima==1)then       //comprueba si anima es 1
                    graph=graph+1;if (graph>29) then graph=26;end        //incrementa graph+1 y comprueba si es mayor de 29 si es mayor inicia al grafico 29
                    x=x+3;if (x>340)then  x=340;anima=0;graph=22;end     //incrementa x+3 y comprueba si es mayor que 340 y cambia de graph y de anima
                end
        
            frame;
            end
        end
     */



    /*
    process letra_titulo(graph,x,y);
    private
        anima=0;    //variable para hacer saltos de animacion
    begin
        size_x,size_y=1;       //lo dejamos al tama�o normal, si fuera 200 seria el doble de grande
        z=10;       //esto hace la profundidad para los 2D si un grafico debe de estar delante de otro, si es menor estara delante si es mayor detras   
        file=1;
    loop

        if (anima==0) then      //comprueba si anima es 0
            y=y+6;if (y>90) then y=90;anima=1;play_wav(sfx1,0);end    //si anima es 0 incremeta la y+6 y comprueba si y es mayor de 90 si es mayor, la variable anima se le da el valor 1 para seguir con otra animacion
        end
        if (anima==1) then      //comprueba si anima es 1
            graph=graph+1;size_x,size_y=2;anima=2;     //si anima es 1 hara otra animacion a la anterior y pasara a anima=2 para no volver a repetirlo
        end
        if (anima==2) then      //comprueba si anima es 2
            size_x,size_y=size_x-0.2;if (size_x<0.5)then size_x,size_y=1;anima=3;end
        end
        if (anima==3) then
            if (graph==11) then graph=1;end      //comprueba si graph es 11 para cambiarlo a 1
            if (graph==13) then graph=2;end      //comprueba si graph es 13 para cambiarlo a 2
            if (graph==15) then graph=3;end      //comprueba si graph es 15 para cambiarlo a 3
            if (graph==17) then graph=4;end      //comprueba si graph es 17 para cambiarlo a 4
        end

    frame;
    end
end
     */

    /*
    process pulsa_intro();
    begin
        x=160;      //posicion horizontal de pantalla
        y=200;      //posicion vertical de pantalla
        size_x,size_y=1;       //lo dejamos al tama�o normal
        z=50;       //esto hace la profundidad para los 2D si un grafico debe de estar delante de otro, si es menor estara delante si es mayor detras

        loop        //el bucle hace que el parpadeo sea infinito
            graph=6;
            file=1;
            frame(300);     //este frame, hace que se mantenga un rato en color amarillo, si lo incrementas tardara mas en pasar a rojo
            graph=7;

            frame(300);     //este frame, hace que se mantenga un rato en color rojo, si lo incrementas tardara mas en pasar a amarillo
        end
    end
     */


    /* //proceso que deja imagen fija en pantalla

    process foto(graph,x,y,size_x,z,flags);
        begin
            file=1;
            size_x=size_x/100;
            size_y=size_x;
        loop
            if (graph==30) then
              flags=0;
              frame(600);
              flags=2;
              frame(500);
            end
            frame;
        end
    end
     */

}