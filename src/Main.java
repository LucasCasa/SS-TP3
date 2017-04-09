import java.awt.geom.Point2D;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lcasagrande on 31/03/17.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        //TODAS LAS UNIDADES EN Metros
        int n = 300;
        double mass = 1;
        boolean save = true;
        double vel = 0.01;
        double Ke = 1.38*Math.pow(10,-23);
        List<Particle> p = Generator.generateFile(0.12, 0.09, n, 0.0015, vel, mass);
        GasSimulation gs = new GasSimulation(p);
        double Te = vel * vel * mass / (3 * Ke);
        gs.simulate(save);
        double boxSize = gs.boxSize;
        if (save) {
            FileWriter fw = new FileWriter("out.txt");
            for (ArrayList<String> al : gs.values) {
                fw.write((al.size() - 1 + (int) boxSize) + "\n1050\n");
                for (String s : al) {
                    fw.write(s);
                }
            }
            fw.close();
            FileWriter dist = null;
            dist = new FileWriter("dist.txt");
            for (Point2D po : gs.distribution) {
                dist.write(po.getX() + "\t" + po.getY() + "\n");
            }
            dist.close();
        }
    }
}
