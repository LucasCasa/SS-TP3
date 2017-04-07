import java.io.IOException;
import java.util.List;

/**
 * Created by lcasagrande on 31/03/17.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("TEST");
        //TODAS LAS UNIDADES EN CENTIMETROS
        int n = 200;
        double vel = 0.01;
        double mass = 1;
        List<Particle> p = Generator.generateFile(0.12,0.09,n, 0.0015,vel,mass);

        GasSimulation gs = new GasSimulation(p);
        double energy = 0.5*1*vel*vel;
        double mol = n / (6.022*Math.pow(10,23));
        double Ke = 1.38*Math.pow(10,-23);
        double R = 8.3;
        double T = vel*vel /(3*Ke);
        System.out.println("Temperatura: " + T);
        gs.simulate(1000);

    }
}
