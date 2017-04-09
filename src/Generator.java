
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lucas on 12/03/17.
 */
public class Generator {

    /**
     *
     * @param x Width
     * @param y Height
     * @param n Number of particles
     * @param r Radius
     * @param v Module of Speed
     * @param m Mass
     */
    public static List<Particle> generateFile(double x, double y, int n, double r,double v, double m) {
        List<Particle> ps = new ArrayList<>();
        for(int i = 0; i<n;i++){
            boolean collision = false;
            double rand = Math.random()*2*Math.PI;
            Particle p = new Particle(i,r,Math.random()*(x-2*r) + r,Math.random()*(y-2*r) + r,v*Math.cos(rand),v*Math.sin(rand),m);
            do {
                collision = false;
                for(Particle op : ps){
                    if(Particle.dist2(p,op) < (p.getRadius()+op.getRadius())*(p.getRadius()+op.getRadius())){
                        collision = true;
                        //System.out.println("COLISION: Particula1: "+p +" Existente:" + op);
                        p.setX(Math.random()*(x-2*r) + r);
                        p.setY(Math.random()*(y-2*r) + r);
                        break;
                    }
                }
            }while(collision);
            ps.add(p);
        }
        /*
        try {
            FileWriter d = new FileWriter("Inicial.txt");
            d.write(n + "\n" );//+ x + "\n" + y + "\n");
            for(Particle p : ps){
                //d.write(p.x + "\t" + p.y + "\t"+ p.getRadius() + "\t" + p.getMass() + "\t" + p.getSpeedX() + "\t" + p.getSpeedY() +"\n");
            }
            d.close();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        */
//        //  CellIndex.calculateNeighbors(ps,n,l,2);

        return ps;
    }
}