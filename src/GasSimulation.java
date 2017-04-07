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
    boolean stationary = false;
    public static double apertura = 0.01;
    public static double width = 0.24;
    public static double height = 0.09;
    double startStationary = 0;
    double pressure = 0;
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
                    getDistribution(currentTime);
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
                if(stationary){
                    addPressure(c);
                }
                c.collide();
                setCollision(c.p1, currentTime);
                if (c.p2 != null) {
                    setCollision(c.p2, currentTime);
                }
            }
        }
        System.out.println("Empezo el estacionario en: " + startStationary);
        System.out.println("Presion total: " + pressure);
        System.out.println("Area: " + (width*height));
        System.out.println("Tiempo total: " + (maxTime  - startStationary));
        pressure /= (maxTime  - startStationary);
        pressure /= width*height;
        System.out.println(pressure);
        System.out.println("SQRT:" + Math.sqrt(pressure));
        System.out.println("T:" + (pressure*(2*width + 2*height)/(particles.size()*1.38*Math.pow(10,-23))) );
        dist.close();
        dist = new FileWriter("dist.txt");
        for(Point2D p : distribution){
            dist.write(p.getX() + "\t" + p.getY() + "\n");
        }
        dist.close();
    }

    private void addPressure(Collision c) {
        if(c.wall){
            if(c.w == Wall.BOTTOM || c.w == Wall.TOP){
                pressure += 2*Math.abs(c.p1.vy)*c.p1.mass;
            }else{
                pressure += 2*Math.abs(c.p1.vx)*c.p1.mass;
            }
        }
    }

    private void getDistribution(double time) {
        double left = 0,right = 0;
        for(Particle p : particles){
            if(p.x < width / 2){
                left+=1;
            }else{
                right+=1;
            }
        }
        distribution.add(new Point2D.Double(left / particles.size(),right / particles.size()));
        if(left / particles.size() > 0.48 && left / particles.size() < 0.52){
            if(!stationary){
                startStationary = time;
            }
            stationary = true;
        }
    }

    private int printBox(StringBuilder s) {
        int count = 0;
        for(double i = 0; i<height;i+=0.001){
            s.append(0 + "\t" + i + "\t" + 0.0015 + "\t" + 0 + "\t" + 0 + "\t0\n");
            s.append(width + "\t" + i + "\t" + 0.0015 + "\t" + 0 + "\t" + 0 + "\t0\n");
            count+= 2;
            if(i< (height / 2 - apertura) || i > (height / 2 + apertura)){
                s.append((width /2) + "\t" + i + "\t" + 0.0015 + "\t" + 0 + "\t" + 0 + "\t0\n");
                count++;
            }
        }
        for(double i = 0; i<width;i+=0.001){
            s.append(i + "\t" + height + "\t" + 0.0015 + "\t" + 0 + "\t" + 0 + "\t0\n");
            s.append(i + "\t" + 0 + "\t" + 0.0015 + "\t" + 0 + "\t" + 0 + "\t0\n");
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
