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
    public static double apertura = 0.005;
    public static double width = 0.24;
    public static double height = 0.09;
    int boxSize = 0;
    public int intFactor = 10000;
    public int widthInt = (int)(width*intFactor);
    public int heightInt = (int)(height*intFactor);
    ArrayList<ArrayList<String>> values;
    double startStationary = 0;
    double pressure = 0;
    Queue<Collision> queue = new PriorityQueue<>();

    public GasSimulation(List<Particle> p){
        particles = p;
    }

    public double simulate(int maxTime,boolean saveStates) throws IOException {

        if (saveStates){
            distribution = new ArrayList<>(maxTime*10);
            values = new ArrayList<>(maxTime * 10);
            for (int i = 0; i < maxTime * 10; i++) {
                values.add(new ArrayList<>(particles.size() + 1));
            }
        }
        int iter = 0;
        double currentTime = 0;
        setAllColisions(0);
        double lastdraw = -0.1;
        while(currentTime < maxTime && !queue.isEmpty()) {
            Collision c = queue.poll();
            if (c.isValid() && c.time <= maxTime) {
                getDistribution(currentTime,saveStates);
                if (saveStates && (c.time - lastdraw) > deltaTime) {
                    for (double i = lastdraw+deltaTime; i < c.time; i += deltaTime) {
                        saveValues(i,iter);
                        lastdraw = i;
                        iter++;
                    }
                }
                if (stationary) {
                    addPressure(c);
                }
                currentTime = collide(c);
            }
        }
        printData(maxTime);
        return pressure;
    }

    private void printData(double maxTime) {
        System.out.println("Empezo el estacionario en: " + startStationary);
        pressure /= (maxTime  - startStationary);
        pressure /= 2*width + 2*height;
        System.out.println("Presion: " + pressure);
    }

    public double simulate(boolean save) throws IOException{
        if (save){
            distribution = new ArrayList<>(1000*10);
            values = new ArrayList<>(1000 * 10);
        }
        int iter = 0;
        double currentTime = 0;
        setAllColisions(0);
        double lastdraw = -0.1;
        while(!stationary && !queue.isEmpty()) {
            Collision c = queue.poll();
            if (c.isValid()) {
                getDistribution(currentTime,save);
                if (save && (c.time - lastdraw) > deltaTime) {
                    for (double i = lastdraw+deltaTime; i < c.time; i += deltaTime) {
                        saveValues(i, iter);
                        lastdraw = i;
                        iter++;
                    }

                }
            currentTime = collide(c);
            }
        }
        /*
        if(stationary) {
            while (currentTime < startStationary*2) {
                Collision c = queue.poll();
                if (c.isValid()) {
                    getDistribution(currentTime,save);
                    if (save && (c.time - lastdraw) > deltaTime) {
                        for (double i = lastdraw+deltaTime; i < c.time; i += deltaTime) {
                            saveValues(i, iter);
                            lastdraw = i;
                            iter++;
                        }

                    }
                    double velp = 0;
                    for(Particle part : particles){
                        velp+= Math.sqrt(part.vx*part.vx + part.vy*part.vy);
                    }
                    //System.out.println(velp /particles.size());
                    addPressure(c);
                    currentTime = collide(c);
                }
            }
        }
        */
        printData(startStationary);
        return pressure;
    }

    private double collide(Collision c){
        double currentTime = c.time;
        updateAllParticles(currentTime);
        c.collide();
        setCollision(c.p1, currentTime);
        if (c.p2 != null) {
            setCollision(c.p2, currentTime);
        }
        return currentTime;
    }
    private void saveValues(double i,int iter){
        updateAllParticles(i);
        values.add(new ArrayList<>(boxSize + particles.size()));
        values.get(iter).add(printBox().toString());

        for (Particle p : particles)
            values.get(iter).add((int)(p.x*intFactor) + "\t" + (int)(p.y*intFactor) + "\t" + (int)(p.radius*intFactor) + "\t" + (int)(p.getMod()*intFactor) + "\n");
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

    private void getDistribution(double time,boolean save) {
        double left = 0,right = 0;
        for(Particle p : particles){
            if(p.x < width / 2){
                left+=1;
            }else{
                right+=1;
            }
        }
        if(save) {
            distribution.add(new Point2D.Double(left / particles.size(), right / particles.size()));
        }
        if(left / particles.size() > 0.49 && left / particles.size() < 0.51){
            if(!stationary){
                startStationary = time;
            }
            stationary = true;
        }
    }

    private StringBuilder printBox() {
        int count = 0;
        StringBuilder s = new StringBuilder();
        for(int i = 0; i<heightInt;i+=0.001*intFactor){
            s.append(0 + "\t" + i + "\t" + (int)(0.0015*intFactor) + "\t0\n");
            s.append(widthInt + "\t" + i + "\t" + (int)(0.0015*intFactor) + "\t0\n");
            count+= 2;
            if(i< (heightInt / 2 - apertura*intFactor /2 - 0.0015*intFactor) || i > (heightInt / 2 + apertura*intFactor/ 2 + 0.0015*intFactor )){
                s.append((widthInt/2) + "\t" + i + "\t" + (int)(0.0015*intFactor) + "\t0\n");
                count++;
            }
        }
        for(int i = 0; i<widthInt;i+=0.001*intFactor){
            s.append(i + "\t" + heightInt + "\t" + (int)(0.0015*intFactor) + "\t0\n");
            s.append(i + "\t" + 0 + "\t" + (int)(0.0015*intFactor) + "\t0\n");
            count+= 2;
        }
        boxSize = count;
        return s;
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
