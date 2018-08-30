import io.vavr.concurrent.Future;
import org.junit.Test;

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

    @Test
    public void testSemaforos(){

        int aOperar = 0; //Solo se puede acceder cuando el semaforo lo permite
        final Semaphore semaforo= new Semaphore(1);

        ExecutorService es = Executors.newFixedThreadPool(2);
        //p0
        Future<Integer> res1 = Future.of(es, () -> {
            System.out.println("Ejecutando el proceso 1...");
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

    }
}
