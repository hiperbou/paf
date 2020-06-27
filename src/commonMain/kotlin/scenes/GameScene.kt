package scenes

import com.soywiz.klock.seconds
import com.soywiz.kmem.setBits
import com.soywiz.kmem.unsetBits
import com.soywiz.korge.input.onKeyDown
import com.soywiz.korge.input.onKeyUp
import com.soywiz.korge.scene.sleep
import com.soywiz.korge.view.*
import com.soywiz.korio.async.launchImmediately

import extensions.toBool
import gameplay.*
import input.BUTTON_A
import input.BUTTON_LEFT
import input.BUTTON_RIGHT
import input.getButtonPressed
import resources.Resources

class GameScene() : SceneBase() {

    var bolas_total = 0

    lateinit var scoreText:Text
    lateinit var recordText:Text

    lateinit var pafSounds:PafSounds

    override suspend fun Container.sceneInit() {
        Resources(views).loadAll()

        pafSounds = PafSounds(Resources.disparoSound,Resources.botaSound, Resources.pafSound, Resources.music)

        currentGameState.resetCollisions()


        foto(31,160,120,100,100,0)     //llamada para crear imagen del fondo
        foto(9,120,224,100,80,0)       //llamada para crear imagen del suelo
        foto(19,284,84,100,10,0)      //llamada para crear imagen de las vidas
        controlador()     //llamamos a un proceso para controlar la barra de vidas
        foto(8,282,120,100,0,0)        //llamada para crear imagen del marcador


        //write_int(1,284,120,4,offset puntos);
        scoreText = text("000000", 10.0, font = Resources.font).position(260, 112)
        //write_int(1,284,157,4,offset record);
        recordText = text("000000", 10.0, font = Resources.font).position(260, 149)
        updateScore()
        updateRecord()

        prota(120 /*,key_left,key_right,key_a*/)

        with(currentGameState){
            when(escenario) {
                0-> {
                    bolas_total = 14
                    bola(12, 100, 100, 100, 0, 1, 1)
                    bola(12, 140, 100, 100, 0, 2, 1)

                    /*bola(10,100,100,100,0,1,1)
                    bola(10,140,100,100,0,2,1)

                    bola(14,100,100,100,0,1,1)
                    bola(14,140,100,100,0,2,1)

                    bola(16,100,100,100,0,1,1)
                    bola(16,140,100,100,0,2,1)*/
                }
                1->{
                    bolas_total=14

                    bola(10,100,100,100,0,1,1)
                    bola(10,140,100,100,0,2,1)
                }
                2->{
                    bolas_total=14

                    bola(14,100,100,100,0,1,1)
                    bola(14,140,100,100,0,2,1)
                }
                3->{
                    bolas_total=20

                    bola(12,70,100,100,0,1,1)
                    bola(14,105,80,50,0,2,1)
                    bola(14,135,80,50,0,1,1)
                    bola(12,170,100,100,0,2,1)
                }
                4->{
                    bolas_total=28

                    bola(14,40,40,100,0,2,1)
                    bola(14,40,180,100,0,2,2)
                    bola(14,200,40,100,0,1,1)
                    bola(14,200,180,100,0,1,2)
                }
                5->{
                    bolas_total=35

                    bola(12,40,100,100,0,1,1)
                    bola(12,80,80,100,0,2,1)
                    bola(12,120,100,100,0,1,1)
                    bola(12,160,80,100,0,2,1)
                    bola(12,200,100,100,0,1,1)
                }
                6->{
                    bolas_total=28

                    bola(14,40,180,100,0,2,2)
                    bola(14,200,180,100,0,1,2)
                    bola(10,40,60,100,0,2,1)
                    bola(10,200,60,100,0,1,1)
                }
                7->{
                    bolas_total=22

                    bola(14,40,100,25,0,1,2)
                    bola(10,60,100,50,0,1,1)
                    bola(12,100,100,100,0,1,1)
                    bola(12,140,100,100,0,2,1)
                    bola(10,180,100,50,0,2,1)
                    bola(14,200,100,25,0,2,2)
                }
                8->{
                    bolas_total=42

                    bola(14,20,60,100,0,1,2)
                    bola(12,60,80,100,0,1,1)
                    bola(10,100,100,100,0,1,1)
                    bola(10,140,100,100,0,2,1)
                    bola(12,180,80,100,0,2,1)
                    bola(14,220,60,100,0,2,2)
                }

                else-> {
                    launchImmediately {
                        bolas_total = -1

                        val felicidades = text("Felicidades", 24.0, font = Resources.font).position(64, 100)
                        sleep(1.seconds)
                        felicidades.removeFromParent()

                        val record = text("Escenario Record", 24.0, font = Resources.font).position(32, 100)
                        sleep(2.seconds)
                        record.removeFromParent()

                        controlador_de_bolas()
                    }
                }
            }

            if(escenario<9) {
                launchImmediately {
                    currentGameState.pauseBalls = true
                    val getReady = text("Escenario ${escenario}", 24.0, font = Resources.font).position(64, 100)
                    sleep(2.seconds)
                    currentGameState.pauseBalls = false
                    getReady.removeFromParent()
                }
            }

            loop {
                if (puntos> record) {
                    record = puntos
                    updateRecord()
                }
                if (bolas_total == 0) {
                    escenario = escenario + 1
                    println("FINISHED!")
                    sleep(2.seconds)
                    sceneContainer.changeTo<GameScene>()
                }

                //if (key(key_select)==1)then  inicio();end     //si pulsas la tecla esc, saldremos del juego hacia la presentacion con la llamada a inicio()

                frame()
            }
        }


    }


    fun updateScore(){
        scoreText.text = currentGameState.puntos.toString().padStart(6, '0')
    }

    fun updateRecord(){
        recordText.text = record.toString().padStart(6, '0')
    }

    inner class bola(val initialGraph:Int, val initialX:Int, val initialY:Int, val size_x:Int, val flags:Int, val anima_x:Int, val anima_y:Int):Process(sceneView), ICollider {

        val collider = Collider(currentGameState.ballCollision,  this)
        override var alive = collider.alive
        override fun destroy() { collider.destroy() }

        override suspend fun main() {
            graph = initialGraph
            var anima_x = anima_x
            var anima_y = anima_y
            var flags = 0
            var size_x = size_x / 100.0
            var velocidad = 0    //variable para controlar la velocidad de subida y bajada de la bola
            var id_disp: disparo? = null        //variable para identificar si colisiona con el disparo del prota

            position(initialX, initialY)
            anchor(0.5, 1)
            scale(size_x, size_x)
            smoothing = false

            loop {
                if (currentGameState.pauseBalls) {
                    frame()
                    return@loop
                }
                //comprueba si la bola tiene distintos graph para que hagan distintos movimientos en pantalla

                if (graph == 10) {     //comprueba si el graph del proceso es 10
                    if (anima_x == 0 && anima_y == 0) {     //si anima_x & anima_y es 0
                        y += 1      //saldra la bola en la parte superior de la pantalla bajando a 1
                        if (y >= 30) {     //comprueba si y es mayor o igual de 30
                            anima_x = (1..2).random()
                            anima_y = 1
                            flags = 0    //y hace un rand para que vaya saltado hacia la izquierda o hacia la derecha
                        }
                    }
                    if (anima_x == 1) {
                        x -= 3
                        if (x < 10) {
                            x = 10.0
                            anima_x = 2
                        }
                    }
                    if (anima_x == 2) {
                        x += 3
                        if (x > 230) {
                            x = 230.0
                            anima_x = 1
                        }
                    }
                    if (anima_y == 1) {
                        velocidad += 1
                        y += velocidad
                        if (velocidad > 12) {
                            velocidad = 12
                        }
                        if (y > 222) {
                            /*play_wav(sfx0,0);*/
                            pafSounds.playBota()
                            y = 222.0
                            anima_y = 2
                        }
                    }
                    if (anima_y == 2) {
                        y -= velocidad
                        velocidad -= 1
                        if (velocidad < 0) {
                            velocidad = 0
                            anima_y = 1
                        }
                    }
                }

                if (graph == 12) {     //comprueba si el graph del proceso es 12
                    if (anima_x == 0 && anima_y == 0) {      //si anima_x & anima_y es 0
                        y += 1      //saldra la bola en la parte superior de la pantalla bajando a 1
                        if (y >= 30) {      //comprueba si y es mayor o igual de 30
                            anima_x = (1..2).random()
                            anima_y = 1
                            flags = 0    //y hace un rand para que vaya saltado hacia la izquierda o hacia la derecha
                        }
                    }
                    if (anima_x == 1) {
                        x -= 1
                        if (x < 10) {
                            x = 10.0
                            anima_x = 2
                        }
                    }
                    if (anima_x == 2) {
                        x += 1
                        if (x > 230) {
                            x = 230.0
                            anima_x = 1
                        }
                    }
                    if (anima_y == 1) {
                        velocidad += 1
                        y += velocidad
                        if (velocidad > 16) {
                            velocidad = 16
                        }
                        if (y > 222) { /*play_wav(sfx0,0)*/
                            pafSounds.playBota()
                            y = 222.0
                            anima_y = 2
                        }
                    }
                    if (anima_y == 2) {
                        y -= velocidad
                        velocidad -= 1
                        if (velocidad < 0) {
                            velocidad = 0
                            anima_y = 1
                        }
                    }
                }

                if (graph == 14) {      //comprueba si el graph del proceso es 14
                    if (anima_x == 0 && anima_y == 0) {      //si anima_x & anima_y es 0
                        y += 1      //saldra la bola en la parte superior de la pantalla bajando a 1
                        if (y >= 30) {      //comprueba si y es mayor o igual de 30
                            anima_x = (1..2).random()
                            anima_y = 1
                            flags = 0    //y hace un rand para que vaya saltado hacia la izquierda o hacia la derecha
                        }
                    }
                    if (anima_x == 1) {
                        x -= 2
                        if (x < 10) {
                            x = 10.0
                            anima_x = 2
                        }
                    }
                    if (anima_x == 2) {
                        x += 2
                        if (x > 230) {
                            x = 230.0
                            anima_x = 1
                        }
                    }
                    if (anima_y == 1) {
                        y += 2
                        if (y > 222) {
                            y = 222.0
                            anima_y = 2
                        }
                    }
                    if (anima_y == 2) {
                        y -= 2
                        if (y < 20) {
                            y = 20.0
                            anima_y = 1
                        }
                    }
                }

                if (graph == 16) {      //comprueba si el graph del proceso es 16
                    if (anima_x == 0 && anima_y == 0) {      //si anima_x & anima_y es 0
                        y++      //saldra la bola en la parte superior de la pantalla bajando a 1
                        if (y >= 30) {      //comprueba si y es mayor o igual de 30
                            anima_x = (1..2).random()
                            anima_y = 1
                            flags = 0    //y hace un rand para que vaya saltado hacia la izquierda o hacia la derecha
                        }
                    }
                    if (anima_x == 1) {
                        x -= 4
                        if (x < 10) {
                            x = 10.0
                            anima_x = 2
                        }
                    }
                    if (anima_x == 2) {
                        x += 4
                        if (x > 230) {
                            x = 230.0
                            anima_x = 1
                        }
                    }
                    if (anima_y == 1) {
                        y += 4
                        if (y > 222) {
                            y = 222.0
                            anima_y = 2
                        }
                    }
                    if (anima_y == 2) {
                        y -= 4
                        if (y < 20) {
                            y = 20.0
                            anima_y = 1
                        }
                    }
                }


                //ahora toca lo de la colision a las bolas con el proceso disparo

                if (anima_x != 0 && anima_y != 0) {      //comprueba que anima_x y anima_y sean distintos a 0

                    id_disp = currentGameState.arrowCollisions.colidesWith(this, (20 * size_x).toInt())

                    if (id_disp != null && id_disp!!.alive && collider.alive) {     //comprueba si la bola a colisionado con el proceso disparo
                        id_disp!!.destroy() //si ha colisionado con el disparo, borra el proceso disparo con un signal
                        explota( graph + 1, x.toInt(), y.toInt(), size_x + 1.0, 25)       //y hacemos que la bola llame al proceso explosion para que haga la explosion de la bola
                        currentGameState.porcentaje++    //incrementamos el porcentaje +1
                        bolas_total--

                        if (size_x == 1.0) {      //comprueba si la bola era de tama�o normal
                            currentGameState.puntos += 25       //si era del tama�o normal te daran 25 puntos
                            bola(graph, x.toInt() - 10, y.toInt() - 10, 50, 0, 1, 2)     //y hacemos dos llamadas para hacer que salten dos bolas a mitad de tama�o
                            bola(graph, x.toInt() + 10, y.toInt() - 10, 50, 0, 2, 2)
                            collider.destroy()//break          //eliminamos el proceso de la bola que ha explotao
                        }
                        if (size_x == 0.50) {      //comprueba si la bola era la mitad del tama�o normal
                            currentGameState.puntos += 50       //si era la mitad del tama�o te daran 50 puntos
                            bola(graph, x.toInt() - 10, y.toInt() - 10, 25, 0, 1, 2)     //y hacemos dos llamadas para hacer que salten dos bolas a mitad de tama�o
                            bola(graph, x.toInt() + 10, y.toInt() - 10, 25, 0, 2, 2)
                            collider.destroy()//break          //eliminamos el proceso de la bola que ha explotao
                        }
                        if (size_x == 0.25) {       //comprueba si el tama�o de la bola era la mas peque�a
                            currentGameState.puntos += 100      //te incrementara 100 puntos
                            collider.destroy()//break          //eliminamos el proceso de la bola que ha explotao, sin hacer mas llamadas
                        }
                        updateScore()
                    }
                }

                if (!collider.alive) collider.destroy()
                frame()
            }
        }
    }


    inner class explota(val initialGraph:Int, val initialX:Int, val initialY:Int, val size_x: Double, val z:Int):Process(sceneView) {
        override suspend fun main() {
            //play_wav(sfx1,0)
            pafSounds.playPaf()
            graph = initialGraph
            position(initialX, initialY)
            anchor(0.5, 0.5)
            scale(size_x, size_x)
            smoothing = false

            loop {
                scaleX -= 0.20       //disminuye el tama�o con size a -20
                scaleY -= 0.20

                if (scaleX < 1) alpha = 0.5   //comprueba si size es menor de 100 para poner la explosion trasparente
                if (scaleX < 0.01) removeFromParent() //break      //comprueba si size es menor de 10 para quitar el proceso

                frame()
            }
        }
    }

    inner class prota(val initialX: Int) :Process(sceneView) {
        override suspend fun main() {
            var anima = 1        //variable para controlar los saltos de animacion
            var pulsado = 0       //variable para controlar el disparo del protagonista y si no se pulsa ninguna tecla el protagonista se quedara con el grafico de parado
            var inmune = 0       //variable para controlar que pases un rato de inmunidad

            graph = 27       //ponemos ese grafico de inicio
            //x=120          //centramos al protagonista en pantalla en la posicion horizontal
            var initialY = 212          //ponemos al protagonista encima del suelo
            var size_x = 1
            var size_y = 1       //ponemos tama�o normal
            var z = 20           //ponemos profundidad de 20, delante del fondo y del suelo

            var key = 0

            onKeyDown { key = key.setBits(getButtonPressed(it)) }
            onKeyUp { key = key.unsetBits(getButtonPressed(it)) }

            val k_left = BUTTON_LEFT
            val k_right = BUTTON_RIGHT
            val k_shoot = BUTTON_A

            position(initialX, initialY)
            anchor(0.5, 0.5)
            scale(size_x, size_x)
            smoothing = false

            loop {
                if (pulsado < 3) {    //comprueba si pulsado es menor de 3
                    if ((key and k_left).toBool() && x > 5) {     //si pulsado es menor de 3 y pulsas el cursor a la izquierda siempre que la posicion x del personaje sea mayor de 5
                        x -= 3
                        pulsado = 2        //se disminuira la x-3 y pondremos pulsado a 2
                        if (anima == 1) {
                            graph = 22
                            anima = 0
                        }      //comprueba si anima es 1 para darle un grafico inicial y cambiarlo a anima 0
                    }
                    if ((key and k_right).toBool() && x < 240) {      //si pulsado es menor de 3 y pulsas el cursor a la izquierda siempre que la posicion x del personaje sea mayor de 5
                        x += 3
                        pulsado = 2            //se incrementara la x+3 y pondremos pulsado a 2
                        if (anima == 0) {
                            graph = 26
                            anima = 1
                        }      //comprueba si anima es 1 para darle un grafico inicial y cambiarlo a anima 1
                    }
                }

                if ((key and k_shoot).toBool()) {
                    pulsado = 5
                    graph = 21
                }      //si pulsamos las teclas b, n, m pondremos pulsado a 5 y el grafico del prota mirando arriba

                pulsado = pulsado - 1      //restamos a pulsando-1
                if (pulsado == 0 && anima == 0) {
                    graph = 23
                }       //si ha llegado aki el programa quiere decir que no se ha pulsado ninguna tecla de control y pondra el grafico del protagonista quieto
                if (pulsado == 0 && anima == 1) {
                    graph = 27
                }       //aki lo mismo que el de arriba pero mirando el protagonista a la derecha
                if (pulsado > 0 && pulsado < 3) {       //controla si pulsado es mayor de 0 y menor de 3
                    if (graph != 21) {
                        graph++
                    }        //si es mayor de 0 y menor de 3 y el graph es distinto a 21 incrementara graph+1
                    if (graph == 21 && anima == 0) {
                        graph = 22
                    }    //esto es para que una vez haya disparado el prota mire a la izquierda, aunque es mas comodo con los flags
                    if (graph == 21 && anima == 1) {
                        graph = 26
                    }    //esto es para que una vez haya disparado el prota mire a la derecha, aunque es mas comodo con los flags
                    if (anima == 0 && graph > 25) {
                        graph = 22
                    }     //comprueba que el prota esta andando para la izquierda y si es mayor el grafico a 25 volverlo a poner desde el grafico 22
                    if (anima == 1 && graph > 29) {
                        graph = 26
                    }     //comprueba que el prota esta andando para la derecha y si es mayor el grafico a 29 volverlo a poner desde el grafico 26
                }

                //las funciones que he hecho aki con anima, se podria hacer mas comodo con flags=1 o flags=0, pero el protagonista lleva el arma apoyada al brazo derecho y son distintos graficos

                if (pulsado == 3) {
                    disparo(x.toInt() + 6, y.toInt() - 5/*,size_x*100*/)
                }       //esto comprueba si pulsado es 3 y hace una llamada al disparo, usease es para cuando una vez hayas pulsado b, n, m y lo sueltes el prota dispare

                inmune += 1        //incrementa inmune+1
                if (inmune < 60) {
                    alpha = 0.5
                }      //comprueba si inmune es menor de 50, si es menor pone al protagonista trasparente para que se vea que es inmune
                if (inmune >= 60) {     //comprueba si inmune es mayor o igual de 50, si es mayor pondremos la colision para que el protagonista sea vulnerable
                    alpha = 1.0        //si es mayor o igual le quita la trasparencia al protagonista y lo pone normal
                    //if(fcollision_cc2(id.idspr,2,0)!=0) {
                    val col = currentGameState.ballCollision.colidesWith(this, 15, -15)
                    if (col != null && col.alive) {
                        //-- if (collision_cc("bola")!=0) {       //comprueba si te toca una bola

                        println("$x,$y - ${col.pos}")

                        currentGameState.vidas--
                        println("vidas ${currentGameState.vidas}")
                        inmune = 0     //si te ha tocado resta vidas-1 y pone inmune a 0, para volver al protagonista inmunerable por unos instantes
                        if (currentGameState.vidas <= -1) {
                            //signal("bola",s_freeze)
                            //frame(2400)
                            println("GAME OVER")
                            currentGameState.pauseBalls = true
                            sleep(1.seconds)
                            sceneContainer.changeTo<TitleScene>()
                            //inicio()
                        }     //comprueba si vidas es menor o igual a -1, si es igual o menor a -1 quiere decir que te han quitado todas las vidas y volveras a inicio del programa con la llamada inicio()
                    }
                }

                frame()
            }
        }
    }


    inner class disparo(val initialX: Int, val initialY:Int) :Process(sceneView), ICollider {

        val collider = Collider(currentGameState.arrowCollisions,  this)
        override var alive = collider.alive
        override fun destroy() { collider.destroy() }

        override suspend fun main() {
            graph=20   //le ponemos el grafico
            var z=25       //le ponemos la profundidad, que este detras del prota

            pafSounds.playDisparo()

            position(initialX, initialY)

            anchor(0.5, 0.5)
            smoothing = false

            loop {
                y -= 6
                if (y < -20) {
                    destroy()
                }      //va incrementado y-6 si no ha tocado ninguna bola saldra de la pantalla y borramos el proceso con break

                scaleX = if(scaleX == 1.0) -1.0 else 1.0

                frame()
            }
        }
    }

    inner class controlador:Process(sceneView) {
        override suspend fun main() {
            graph=32       //este graph sera el que tapa la barra de porcentaje y de vidas
            var z=5        //la z de las vidas y de porcentaje es de 10 y la del grafico de marcador 0 este se pone entre ambos para que tape el de las vidas y el del porcentaje

            position(0,84.0)
            anchor(0.5, 0.5)
            smoothing = false

            loop {
                val vidas = currentGameState.vidas

                if (vidas == 4) x = 340.0
                if (vidas == 3) x = 324.0     //si vidas es 3 o el protagonista tiene 3 vidas de juego, no tapara nada
                if (vidas == 2) x = 309.0     //si vidas es 2 posaremos el graph 32 encima de la ultima vida
                if (vidas == 1) x = 296.0     //si vidas es 1 posaremos el graph 32 encima de dos vidas mostrando solo 1 en pantalla
                if (vidas == 0) x = 284.0     //si vidas es 0 se taparan todas las vidas

                frame()
            }
        }
    }

    suspend fun Container.controlador_de_bolas() {
        var grafico_bola=0     //variable para hacer el rand del grafico de la bola
        var posicion_x=0       //variable para la posicion x de la bola
        var tamano_bola=0      //variable para hacer el rand del tama�o de la bola

        sleep(2.seconds)    //cuando inicia el juego esperamos un ratito antes de que salga las bolas

        loop {
            grafico_bola=(0..3).random()     //hacemos un rando para que saque uno de los cuantro graficos de la bola
            if (grafico_bola==0) grafico_bola=10
            if (grafico_bola==1) grafico_bola=12
            if (grafico_bola==2) grafico_bola=14
            if (grafico_bola==3) grafico_bola=16

            posicion_x=(20..220).random()      //hacemos un rando para que la bola salga en la posicion x, cuando esta la bola arriba, aleatoria

            tamano_bola=(0..2).random()      //otro random para los 3 tama�os de la bola size=100, size=50 y size=25
            if (tamano_bola==0)  tamano_bola=100
            if (tamano_bola==1)  tamano_bola=50
            if (tamano_bola==2)  tamano_bola=25

            bola(grafico_bola,posicion_x,-20,tamano_bola,4,0,0)

            //los datos que mandamos al proceso bola son bola(graph,x,y,size,flags,anima_x,anima_y)
            sleep(3.seconds)
        }
    }


    /*
    //proceso que va llamando a los procesos bola aleatoriamente

process controlador_de_bolas()
    private
        grafico_bola=0;     //variable para hacer el rand del grafico de la bola
        posicion_x=0;       //variable para la posicion x de la bola
        tamano_bola=0;      //variable para hacer el rand del tama�o de la bola
    begin
file=1;
    frame(4500);    //cuando inicia el juego esperamos un ratito antes de que salga las bolas

    loop
        grafico_bola=math.random(0,3);     //hacemos un rando para que saque uno de los cuantro graficos de la bola
        if (grafico_bola==0) then grafico_bola=10;end
        if (grafico_bola==1) then grafico_bola=12;end
        if (grafico_bola==2) then grafico_bola=14;end
        if (grafico_bola==3) then grafico_bola=16;end

        posicion_x=math.random(20,220);      //hacemos un rando para que la bola salga en la posicion x, cuando esta la bola arriba, aleatoria

        tamano_bola=math.random(0,2);      //otro random para los 3 tama�os de la bola size=100, size=50 y size=25
        if (tamano_bola==0)then  tamano_bola=100;end
        if (tamano_bola==1)then  tamano_bola=50;end
        if (tamano_bola==2)then  tamano_bola=25;end

        bola(grafico_bola,posicion_x,-20,tamano_bola,4,0,0);

        //los datos que mandamos al proceso bola son bola(graph,x,y,size,flags,anima_x,anima_y);

    frame(7000);
    end
end

     */


    /*
    //controlar la barra de porcentaje de juego y la barra de las vidas
process controlador(tipo)
    begin
        graph=32;       //este graph sera el que tapa la barra de porcentaje y de vidas

        if (tipo==0)then y=84;end       //compruba si tipo es 0 para poner el graph 32 a la altura de la barra de vidas
        if (tipo==1)then  y=158;end      //compruba si tipo es 1 para poner el graph 32 a la altura de la barra de porcentaje
file=1;
        size_x=1;       //le ponemos tama�o normal
        size_y=1;
        z=5;        //la z de las vidas y de porcentaje es de 10 y la del grafico de marcador 0 este se pone entre ambos para que tape el de las vidas y el del porcentaje

    loop
        if (tipo==0)then         //comprueba si tipo es 0 para poder tapar las vidas
            if (vidas==4)then  x=340;end
            if (vidas==3)then  x=324;end     //si vidas es 3 o el protagonista tiene 3 vidas de juego, no tapara nada
            if (vidas==2)then  x=309;end     //si vidas es 2 posaremos el graph 32 encima de la ultima vida
            if (vidas==1)then  x=296;end     //si vidas es 1 posaremos el graph 32 encima de dos vidas mostrando solo 1 en pantalla
            if (vidas==0)then  x=284;end     //si vidas es 0 se taparan todas las vidas
        end
        /*if (tipo==1)        //comprueba si tipo es 1 para destapar la barra de porcentaje
            x=284+porcentaje;       //ponemos el graph 32 delante de la barra de %juego e ira destapandose a medida que incremente porcentaje
        end*/

    frame;
    end
end
     */


    /*
    //proceso de disparo del prota le pasamos los datos x, y, size del process prota

process disparo(x,y,size_x);
    begin
    set_group(id.idspr,1);

        graph=20;   //le ponemos el grafico
        z=25;       //le ponemos la profundidad, que este detras del prota
        flags=0;    //le ponemos flags normal sin espejado
file=1;
    size_x=size_x/100;
        size_y=size_x;
     play_wav(sfx2,0);

    loop
        y=y-6;if (y<-20) then break;end      //va incrementado y-6 si no ha tocado ninguna bola saldra de la pantalla y borramos el proceso con break
        flags=flags+1;if (flags>1) then flags=0;end      //esto vale para hacer la animacion del disparo cambiando constantemente el flags de 0 a 1

    frame;
    end
end
     */


    /*
    //proceso para controlar protagonista
process prota(x,k_left,k_right,k_shoot);
    private
        anima=1;        //variable para controlar los saltos de animacion
        pulsado=0;      //variable para controlar el disparo del protagonista y si no se pulsa ninguna tecla el protagonista se quedara con el grafico de parado
        inmune=0;       //variable para controlar que pases un rato de inmunidad

    begin
        graph=27;       //ponemos ese grafico de inicio
        //x=120;          //centramos al protagonista en pantalla en la posicion horizontal
        y=212;          //ponemos al protagonista encima del suelo
        size_x,size_y=1;       //ponemos tama�o normal
        z=20;           //ponemos profundidad de 20, delante del fondo y del suelo
file=1;
    loop
        if (pulsado<3)  then    //comprueba si pulsado es menor de 3
            if (key(k_left)==1 and x>5) then     //si pulsado es menor de 3 y pulsas el cursor a la izquierda siempre que la posicion x del personaje sea mayor de 5
                x=x-3;pulsado=2;        //se disminuira la x-3 y pondremos pulsado a 2
                if (anima==1) then graph=22;anima=0;end      //comprueba si anima es 1 para darle un grafico inicial y cambiarlo a anima 0
            end
            if (key(k_right)==1 and x<240) then      //si pulsado es menor de 3 y pulsas el cursor a la izquierda siempre que la posicion x del personaje sea mayor de 5
                x=x+3;pulsado=2;            //se incrementara la x+3 y pondremos pulsado a 2
                if (anima==0)  then graph=26;anima=1;end      //comprueba si anima es 1 para darle un grafico inicial y cambiarlo a anima 1
            end
        end

        if (key(k_shoot)==1) then pulsado=5;graph=21;end       //si pulsamos las teclas b, n, m pondremos pulsado a 5 y el grafico del prota mirando arriba

        pulsado=pulsado-1;      //restamos a pulsando-1
        if (pulsado==0 and anima==0) then graph=23;end       //si ha llegado aki el programa quiere decir que no se ha pulsado ninguna tecla de control y pondra el grafico del protagonista quieto
        if (pulsado==0 and anima==1) then graph=27;end       //aki lo mismo que el de arriba pero mirando el protagonista a la derecha
        if (pulsado>0 and pulsado<3)  then       //controla si pulsado es mayor de 0 y menor de 3
            if (graph!=21) then graph=graph+1;end        //si es mayor de 0 y menor de 3 y el graph es distinto a 21 incrementara graph+1
            if (graph==21 and anima==0) then graph=22;end    //esto es para que una vez haya disparado el prota mire a la izquierda, aunque es mas comodo con los flags
            if (graph==21 and anima==1) then graph=26;end    //esto es para que una vez haya disparado el prota mire a la derecha, aunque es mas comodo con los flags
            if (anima==0 and graph>25) then graph=22;end     //comprueba que el prota esta andando para la izquierda y si es mayor el grafico a 25 volverlo a poner desde el grafico 22
            if (anima==1 and graph>29) then graph=26;end     //comprueba que el prota esta andando para la derecha y si es mayor el grafico a 29 volverlo a poner desde el grafico 26
        end

        //las funciones que he hecho aki con anima, se podria hacer mas comodo con flags=1 o flags=0, pero el protagonista lleva el arma apoyada al brazo derecho y son distintos graficos

        if (pulsado==3) then disparo(x+6,y-5,size_x*100);end       //esto comprueba si pulsado es 3 y hace una llamada al disparo, usease es para cuando una vez hayas pulsado b, n, m y lo sueltes el prota dispare

        inmune=inmune+1;        //incrementa inmune+1
        if (inmune<60) then alpha=0.5;end      //comprueba si inmune es menor de 50, si es menor pone al protagonista trasparente para que se vea que es inmune
        if (inmune=>60) then     //comprueba si inmune es mayor o igual de 50, si es mayor pondremos la colision para que el protagonista sea vulnerable
            alpha=1;        //si es mayor o igual le quita la trasparencia al protagonista y lo pone normal
           if(fcollision_cc2(id.idspr,2,0)!=0) then
           -- if (collision_cc("bola")!=0) then       //comprueba si te toca una bola
                vidas=vidas-1;inmune=0;     //si te ha tocado resta vidas-1 y pone inmune a 0, para volver al protagonista inmunerable por unos instantes
                if (vidas<=-1) then
                    signal("bola",s_freeze);
                    frame(2400);
                    inicio();
                end     //comprueba si vidas es menor o igual a -1, si es igual o menor a -1 quiere decir que te han quitado todas las vidas y volveras a inicio del programa con la llamada inicio()
            end
        end

    frame;
    end
end
     */



    /*//proceso para la explosion de las bolas, le pasa los datos del proceso bola

process explota(graph,x,y,size_x,z)
    begin
        flags=0;    //pone la bola normal sin trasparencias y sin espejados
file=1;
    //size_x=size_x/100;
        size_y=size_x;
        play_wav(sfx1,0);

    loop
        size_y=size_x-0.20;       //disminuye el tama�o con size a -20
        size_x=size_x-0.20;

        if (size_x<1)then alpha=0.5;end   //comprueba si size es menor de 100 para poner la explosion trasparente
        if (size_x<0.01)then break;end      //comprueba si size es menor de 10 para quitar el proceso

    frame;
    end
end


     */


    /*

process bola(graph,x,y,size_x,flags,anima_x,anima_y);
    private
        velocidad=0;    //variable para controlar la velocidad de subida y bajada de la bola
        id_disp;        //variable para identificar si colisiona con el disparo del prota

    begin
    set_group(id.idspr,2);

        z=30;
        size_x=size_x/100
        size_y=size_x;
file=1;
    loop
        //comprueba si la bola tiene distintos graph para que hagan distintos movimientos en pantalla

        if (graph==10) then     //comprueba si el graph del proceso es 10
            if (anima_x==0 and anima_y==0)  then     //si anima_x & anima_y es 0
                y=y+1;      //saldra la bola en la parte superior de la pantalla bajando a 1
                if (y=>30)  then     //comprueba si y es mayor o igual de 30
                    anima_x=math.random(1,2);anima_y=1;flags=0;    //y hace un rand para que vaya saltado hacia la izquierda o hacia la derecha
                end
            end
            if (anima_x==1) then
                x=x-3;
                if (x<10) then x=10;anima_x=2;end
            end
            if (anima_x==2) then
                x=x+3;
                if (x>230) then x=230;anima_x=1;end
            end
            if (anima_y==1) then
                velocidad=velocidad+1;y=y+velocidad;
                if (velocidad>12) then velocidad=12;end
                if (y>222) then play_wav(sfx0,0); y=222;anima_y=2;end
            end
            if (anima_y==2) then
                y=y-velocidad;velocidad=velocidad-1;
                if (velocidad<0) then velocidad=0;anima_y=1;end
            end
        end

        if (graph==12)  then     //comprueba si el graph del proceso es 12
            if (anima_x==0 and anima_y==0) then      //si anima_x & anima_y es 0
                y=y+1;      //saldra la bola en la parte superior de la pantalla bajando a 1
                if (y=>30) then      //comprueba si y es mayor o igual de 30
                    anima_x=math.random(1,2);anima_y=1;flags=0;    //y hace un rand para que vaya saltado hacia la izquierda o hacia la derecha
                end
            end
            if (anima_x==1) then
                x=x-1;
                if (x<10) then x=10;anima_x=2;end
            end
            if (anima_x==2) then
                x=x+1;
                if (x>230) then x=230;anima_x=1;end
            end
            if (anima_y==1) then
                velocidad=velocidad+1;y=y+velocidad;
                if (velocidad>16) then velocidad=16;end
                if (y>222) then play_wav(sfx0,0); y=222;anima_y=2;end
            end
            if (anima_y==2) then
                y=y-velocidad;velocidad=velocidad-1;
                if (velocidad<0) then velocidad=0;anima_y=1;end
            end
        end

        if (graph==14) then      //comprueba si el graph del proceso es 14
            if (anima_x==0 and anima_y==0) then      //si anima_x & anima_y es 0
                y=y+1;      //saldra la bola en la parte superior de la pantalla bajando a 1
                if (y=>30) then      //comprueba si y es mayor o igual de 30
                    anima_x=math.random(1,2);anima_y=1;flags=0;    //y hace un rand para que vaya saltado hacia la izquierda o hacia la derecha
                end
            end
            if (anima_x==1) then
                x=x-2;
                if (x<10) then x=10;anima_x=2;end
            end
            if (anima_x==2) then
                x=x+2;
                if (x>230) then x=230;anima_x=1;end
            end
            if (anima_y==1) then
                y=y+2;
                if (y>222) then y=222;anima_y=2;end
            end
            if (anima_y==2) then
                y=y-2;
                if (y<20) then y=20;anima_y=1;end
            end
        end

        if (graph==16) then      //comprueba si el graph del proceso es 16
            if (anima_x==0 and anima_y==0) then      //si anima_x & anima_y es 0
                y=y+1;      //saldra la bola en la parte superior de la pantalla bajando a 1
                if (y=>30) then      //comprueba si y es mayor o igual de 30
                    anima_x=math.random(1,2);anima_y=1;flags=0;    //y hace un rand para que vaya saltado hacia la izquierda o hacia la derecha
                end
            end
            if (anima_x==1) then
                x=x-4;
                if (x<10) then x=10;anima_x=2;end
            end
            if (anima_x==2) then
                x=x+4;
                if (x>230) then x=230;anima_x=1;end
            end
            if (anima_y==1) then
                y=y+4;
                if (y>222) then y=222;anima_y=2;end
            end
            if (anima_y==2) then
                y=y-4;
                if (y<20) then y=20;anima_y=1;end
            end
        end

        //ahora toca lo de la colision a las bolas con el proceso disparo

        if (anima_x!=0 and anima_y!=0) then      //comprueba que anima_x y anima_y sean distintos a 0
           id_disp=fcollision2(id.idspr,1);
           --print("tmp:");
           --print(id_disp);
           if(id_disp>0) then
           id_disp=procesos_id[id_disp-1];
           --print(fcollision2(id.idspr,1));
           end
           --id_disp=collision("disparo")
            if ((id_disp!=0 and id_disp!=nil))  then     //comprueba si la bola a colisionado con el proceso disparo
                signal(id_disp,s_kill);     //si ha colisionado con el disparo, borra el proceso disparo con un signal
                explota(graph+1,x,y,size_x+1,25);       //y hacemos que la bola llame al proceso explosion para que haga la explosion de la bola
                porcentaje=porcentaje+1;    //incrementamos el porcentaje +1
                bolas_total=bolas_total-1;

                if (size_x==1) then      //comprueba si la bola era de tama�o normal
                    puntos=puntos+25;       //si era del tama�o normal te daran 25 puntos
                    bola(graph,x-10,y-10,50,0,1,2);     //y hacemos dos llamadas para hacer que salten dos bolas a mitad de tama�o
                    bola(graph,x+10,y-10,50,0,2,2);
                    break;          //eliminamos el proceso de la bola que ha explotao
                end
                if (size_x==0.50)  then      //comprueba si la bola era la mitad del tama�o normal
                    puntos=puntos+50;       //si era la mitad del tama�o te daran 50 puntos
                    bola(graph,x-10,y-10,25,0,1,2);     //y hacemos dos llamadas para hacer que salten dos bolas a mitad de tama�o
                    bola(graph,x+10,y-10,25,0,2,2);
                    break;          //eliminamos el proceso de la bola que ha explotao
                end
                if (size_x==0.25) then       //comprueba si el tama�o de la bola era la mas peque�a
                    puntos=puntos+100;      //te incrementara 100 puntos
                    break;          //eliminamos el proceso de la bola que ha explotao, sin hacer mas llamadas
                end
            end
        end

    frame;
    end
end
     */

}