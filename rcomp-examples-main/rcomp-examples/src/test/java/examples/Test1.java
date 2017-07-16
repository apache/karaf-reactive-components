package examples;


import static java.time.Duration.of;

import java.time.temporal.ChronoUnit;
import java.util.function.Function;

import org.junit.Test;

import reactor.core.publisher.Flux;
import reactor.math.MathFlux;

public class Test1 {
    double result = 0;

    @Test
    public void testStream() {
        Flux<Integer> flux = Flux.fromArray(new Integer[]{1,10,5,3,4});
        MathFlux.averageDouble(flux).subscribe(System.out::println);
    }
    
    @Test
    public void testSlidingWindow() throws InterruptedException {
        Flux.interval(of(1, ChronoUnit.MILLIS))
            .transform(averageOfLastTwo())
            .subscribe(System.out::println);
        Thread.sleep(100);
    }
    
    private Function<Flux<Long>, Flux<Double>> averageOfLastTwo() {
        return f -> f.window(2, 1)
            .flatMap(win -> MathFlux.averageDouble(win));
    }

}
