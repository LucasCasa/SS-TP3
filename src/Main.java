import java.io.IOException;
import java.util.List;

/**
 * Created by lcasagrande on 31/03/17.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("TEST");
        //TODAS LAS UNIDADES EN CENTIMETROS
        List<Particle> p = Generator.generateFile(12,9,300, 0.15,1,1);

        GasSimulation gs = new GasSimulation(p);

        gs.simulate(1000);

    }
}
