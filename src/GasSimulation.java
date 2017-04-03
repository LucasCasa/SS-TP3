import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Created by Lucas on 02/04/2017.
 */
public class GasSimulation {
    List<Particle> particles;
    Queue<Collision> queue = new PriorityQueue<>();

    public GasSimulation(List<Particle> p){
        particles = p;
    }

    public void simulate(int maxTime) throws IOException {
        double currentTime = 0;
        setAllColisions(0);
        FileWriter dist = null;
        dist = new FileWriter("out.txt");
        while(currentTime < maxTime && !queue.isEmpty()) {
            Collision c = queue.poll();
            if (c.isValid()){
                for(double i = currentTime; i<c.time;i+= 0.1) {
                    updateAllParticles(i);
                    dist.write(particles.size() + "\n" +i +"\n");
                    for (Particle p : particles)
                        dist.write(p.x + "\t" + p.y + "\t" + p.radius + "\t" + p.getSpeedX() + "\t" + p.getSpeedY() + "\n");
                    currentTime = i;
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
                    System.out.println(d);
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
