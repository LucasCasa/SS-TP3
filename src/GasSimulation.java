import java.awt.*;
import java.awt.geom.Point2D;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Created by Lucas on 02/04/2017.
 */
public class GasSimulation {
    double deltaTime = 0.1;
    List<Particle> particles;
    List<Point2D> distribution;
    public static double apertura = 1;
    Queue<Collision> queue = new PriorityQueue<>();

    public GasSimulation(List<Particle> p){
        particles = p;
    }

    public void simulate(int maxTime) throws IOException {
        distribution = new ArrayList<>(maxTime);
        double currentTime = 0;
        setAllColisions(0);
        FileWriter dist = null;
        double lastdraw = 0;
        dist = new FileWriter("out.txt");
        while(currentTime < maxTime && !queue.isEmpty()) {
            Collision c = queue.poll();
            if (c.isValid()){
                if((c.time - lastdraw) > deltaTime) {
                    getDistribution();
                    for (double i = currentTime; i < c.time; i += deltaTime) {
                        updateAllParticles(i);
                        StringBuilder s = new StringBuilder();
                        int a = printBox(s);
                        dist.write((a +particles.size()) + "\n" + i + "\n");
                        dist.write(s.toString());
                        for (Particle p : particles)
                            dist.write(p.x + "\t" + p.y + "\t" + p.radius + "\t" + p.getSpeedX() + "\t" + p.getSpeedY() + "\t" +p.getMod()+ "\n");
                        currentTime = i;
                        lastdraw = currentTime;
                    }
                }
                currentTime = c.time;
                updateAllParticles(currentTime);
                c.collide();
                //System.out.println(currentTime);
                setCollision(c.p1, currentTime);
                if (c.p2 != null) {
                    setCollision(c.p2, currentTime);
                }
            }
        }
        dist.close();
        dist = new FileWriter("dist.txt");
        for(Point2D p : distribution){
            dist.write(p.getX() + "\t" + p.getY() + "\n");
        }
        dist.close();
    }

    private void getDistribution() {
        double left = 0,right = 0;
        for(Particle p : particles){
            if(p.x < 12){
                left+=1;
            }else{
                right+=1;
            }
        }
        distribution.add(new Point2D.Double(left / particles.size(),right / particles.size()));
    }

    private int printBox(StringBuilder s) {
        int count = 0;
        for(double i = 0; i<9;i+=0.1){
            s.append(0 + "\t" + i + "\t" + 0.15 + "\t" + 0 + "\t" + 0 + "\t0\n");
            s.append(24 + "\t" + i + "\t" + 0.15 + "\t" + 0 + "\t" + 0 + "\t0\n");
            count+= 2;
            if(i< (9.0/2 - apertura) || i > (9.0/2 + apertura)){
                s.append(12 + "\t" + i + "\t" + 0.15 + "\t" + 0 + "\t" + 0 + "\t0\n");
                count++;
            }
        }
        for(double i = 0; i<24;i+=0.1){
            s.append(i + "\t" + 9 + "\t" + 0.15 + "\t" + 0 + "\t" + 0 + "\t0\n");
            s.append(i + "\t" + 0 + "\t" + 0.15 + "\t" + 0 + "\t" + 0 + "\t0\n");
            count+= 2;
        }
        return count;
    }

    private void updateAllParticles(double time) {
        for(Particle p: particles){
            p.update(time);
        }
    }

    private void setAllColisions(double t) {
        for(int i = 0; i<particles.size();i++){
            Particle p = particles.get(i);
            checkWalls(p,t);
            for(int j = i+1; j<particles.size();j++){
                double d = p.predict(particles.get(j));
                if (d > 0) {
                    //System.out.println("Particula " + p.id + " y Particula " + particles.get(j).id + " van a chocar en tiempo " + d + t);
                    queue.offer(new Collision(p, particles.get(j), d + t));
                }
            }
        }
    }

    public void setCollision(Particle p,double t) {
        checkWalls(p,t);
        for(int i = 0; i<particles.size();i++){
            if(i != p.getId()) {
                double d = p.predict(particles.get(i));
                if (d > 0.00001) {
                    //System.out.println(d);
                    //System.out.println(t + ": Particula " + p.id + " y Particula " + particles.get(i).id + " van a chocar en tiempo " + (d + t));
                    queue.offer(new Collision(p, particles.get(i), d + t));
                }
            }
        }
    }

    private void checkWalls(Particle p, double t){
        double aux = p.timeToHitLeftWall();
        if(aux > 0){
            Collision c = new Collision(p,Wall.LEFT,aux + t);
            queue.offer(c);
        }
        if((aux = p.timeToHitRightWall()) > 0){
            Collision c = new Collision(p,Wall.RIGHT,aux + t);
            queue.offer(c);
        }
        if((aux = p.timeToHitTopWall()) > 0){
            Collision c = new Collision(p,Wall.TOP,aux + t);
            queue.offer(c);
        }
        if((aux = p.timeToHitBottomWall()) > 0){
            Collision c = new Collision(p,Wall.BOTTOM,aux + t);
            queue.offer(c);
        }
    }
}
