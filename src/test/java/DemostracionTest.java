import io.vavr.concurrent.Future;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import static org.junit.Assert.assertTrue;

public class DemostracionTest {

    public int sumar2(int a){
        return a + 2;
    }

    public int sumar3(int a){
        return a+3;
    }

//    @Test
//    public void testSemaforos(){
/*
        int aOperar = 0; //Solo se puede acceder cuando el semaforo lo permite
        final Semaphore semaforo= new Semaphore(1);

        ExecutorService es = Executors.newFixedThreadPool(2);
        //p0
        Future<Integer> res1 = Future.of(es, () -> {
            System.out.println("Ejecutando el proceso 1...");
            System.out.println(semaforo);
            semaforo.acquire();
            int x = sumar2(aOperar);
            System.out.println("Se han sumado 2 unidades...");
            Thread.sleep(7000);
            semaforo.release();
            return x;
        });

        //System.out.println(res1.getOrElse(-1));

        //p1
        Future<Integer> res2 = Future.of(es, () -> {
            System.out.println("Ejecutando el proceso 2...");
            System.out.println(semaforo);
            semaforo.acquire();
            int x = sumar3(aOperar);
            System.out.println("Se han sumado 3 unidades...");
            Thread.sleep(7000);
            semaforo.release();
            return x;
        });

        res1.await();
        res2.await();

        assertTrue(res1.getOrElse(0) == 2);
        assertTrue(res2.getOrElse(0) == 3);
        //System.out.println(res2.getOrElse(-1));
        System.out.println(semaforo);
*/

        /**
         * Hacer un semaforo contador para 2 y van a ser 3 procesos
         * los procesos van a intentar entrar a la zona critica al mismo tiempo
         *
         */
//    }

    public int sumar1(int a){ return a + 1;}

    @Test
    public void interrupciones(){
        int numero = 1;
        while(numero > 0){
            numero = sumar1(numero);
        }
        System.out.println("El numero se volvio negativo");
        System.out.println(numero);
        System.out.println("Se vuelve negativo ya que hay un desborde de " +
                "memoria y vuelve el numero a complemento a 1 que da el signo al numero, " +
                "por lo que 1 indica negativo");
    }

    public class Proceso{
        int proceso = 0;
        int tiempoLlegada= 0;
        int ncpu = 0;
        int gastoes = 0;
        int ncpu2 = 0;
        int reentro = 0;

        public Proceso(int tiempo, int aProcesar, int proceso, int gastoes, int ncpu2, int reentro){
            tiempoLlegada = tiempo;
            ncpu = aProcesar;
            this.proceso = proceso;
            this.gastoes = gastoes;
            this.ncpu2 = ncpu2;
            this.reentro = reentro;
        }
    }

    @Test
    public void RoundRobin(){
        int tiempo = 0;
        int quantum = 40;
        int intercambio = 20;
        boolean darProcesador = true;

        Proceso p1 = new Proceso(0, 2, 1, 2, 1, 1);
        Proceso p2 = new Proceso(50, 3, 2, 2, 2, 1);
        Proceso p3 = new Proceso(200, 5, 3, 0, 0, 0);

        Proceso[] procesos = new Proceso[20];

        if(p1.tiempoLlegada == 0){
            procesos[0] = p1;
        }else if (p2.tiempoLlegada == 0){
            procesos[0] = p2;
        }else {
            procesos[0] = p3;
        }



        while (darProcesador){
            System.out.println("procesando el proceso:" + (procesos[0].proceso - 1));
            tiempo += quantum;
            tiempo += intercambio;
            System.out.println("tiempo global " + tiempo);

            if(procesos[0].proceso == 1 && procesos[0].ncpu > 0){
                p1.ncpu = p1.ncpu - 1;
                p1.tiempoLlegada = tiempo;
            }else if (procesos[0].proceso == 2 && procesos[0].ncpu > 0){
                p2.ncpu = p2.ncpu - 1;
                p2.tiempoLlegada = tiempo;
            }else if (procesos[0].proceso == 3 && procesos[0].ncpu > 0) {
                p3.ncpu = p3.ncpu - 1;
                p3.tiempoLlegada = tiempo;
            } else {
                darProcesador = false; break;
            }

            if (p1.ncpu == 0){
                if (p1.reentro > 0){
                    p1.ncpu = p1.ncpu2;
                    p1.tiempoLlegada = (p1.gastoes * quantum) + tiempo;
                    p1.reentro = p1.reentro -1;
                }
            }

            if (p2.ncpu == 0){
                if (p2.reentro > 0){
                    p2.ncpu = p2.ncpu2;
                    p2.tiempoLlegada = (p2.gastoes * quantum) + tiempo;
                    p2.reentro = p2.reentro - 1;
                }
            }

            if (p3.ncpu == 0){
                if (p3.reentro > 0){
                    p3.ncpu = p3.ncpu2;
                    p3.tiempoLlegada = (p3.gastoes * quantum) + tiempo;
                    p3.reentro = p3.reentro - 1;
                }
            }

            System.out.println("tiempo p0: " + p1.tiempoLlegada + "->" + p1.ncpu);
            System.out.println("tiempo p1: " + p2.tiempoLlegada + "->" + p2.ncpu);
            System.out.println("tiempo p2: " + p3.tiempoLlegada + "->" + p3.ncpu);

            if ((p1.tiempoLlegada < p2.tiempoLlegada) && (p1.tiempoLlegada < p3.tiempoLlegada) && (p1.ncpu > 0)){
                procesos[0]= p1;
            } else if ((p2.tiempoLlegada < p3.tiempoLlegada) && (p2.ncpu > 0)) {
                procesos[0] = p2;
            } else if (p3.ncpu > 0 && p3.tiempoLlegada < p2.tiempoLlegada){
                procesos[0] = p3;
            } else if(p1.ncpu == 0 && p2.ncpu == 0 && p3.ncpu == 0){
                darProcesador = false;
                break;
            }
        }

        System.out.println();

        //proceso tiene timpo de llegada, quantum que requiere, gasto e/s
        // y si tiene entrada y salida tiene otro quantum



    }
}
