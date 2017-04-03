/**
 * Created by Lucas on 02/04/2017.
 */
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Particle {
    int id;
    double radius;
    double x;
    double y;
    double vx;
    double vy;
    double mass;
    //Vector nextSpeed;
    //Vector speed;
    List<Particle> neighbors;
    double lastUpdate = 0;
    int timesModified = 0;

    public Particle(int id,double radius, double x, double y,Vector speed,double mass){
        this.id = id;
        this.radius = radius;
        this.x = x;
        this.y = y;
        this.vx = Math.cos(speed.getAngle())*speed.getModule();
        this.vy = Math.sin(speed.getAngle())*speed.getModule();
        //this.nextSpeed = new Vector(0.03,0);
        this.neighbors = new ArrayList<>();
        this.mass = mass;
    }

    public Particle(int id,double radius, double x, double y,double velx,double vely,double mass){
        this.id = id;
        this.radius = radius;
        this.x = x;
        this.y = y;
        //this.nextSpeed = new Vector(0.03,0);
        //this.speed = new Vector(Math.sqrt(velx*velx + vely*vely),Math.atan2(velx, vely));
        this.vx = velx;
        this.vy = vely;
        this.neighbors = new ArrayList<>();
        this.mass = mass;
    }

    public Particle(int id, double radius){
        this.id = id;
        this.radius = radius;
        //this.nextSpeed = new Vector(0.03,0);
    }

    public double getRadius() {
        return radius;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public static double dist2(Particle p1, Particle p2){
        return (p2.x-p1.x)*(p2.x-p1.x) + (p2.y-p1.y)*(p2.y-p1.y);
    }

    public void setX(double x) {
        this.x = x;
    }
    public void setY(double y){
        this.y = y;
    }

    //System.out.println("CUANTO VALE EL MODULO DE SPEED? " + p.getSpeed().getModule())
    public int getId() {
        return id;
    }
/*
    public Vector getSpeed() {
        return speed;
    }

    public void setSpeed(Vector speed) {
        this.speed = speed;
    }
*/
    public double getSpeedX(){
        return vx;
    }
    public double getSpeedY(){
        return vy;
    }

    public double getMass(){
        return mass;
    }
    public int getTimesModified(){
       return timesModified;
    }
    public void bounceOff(Particle o) {
        double dx = o.x - this.x;
        double dy = o.y - this.y;
        double dist = radius + o.radius;
        /*if( (dx*dx + dy*dy) > (dist*dist)){
            System.out.println("MAAL");
            //timesModified++;
            //o.timesModified++;
            return;
        }*/
        double dvx = o.getSpeedX() - this.getSpeedX();
        double dvy = o.getSpeedY() - this.getSpeedY();
        double dvdr = dx * dvx + dy * dvy;

        double F = (2 * mass * o.mass * dvdr) / ((mass + o.mass) * dist);
        double fx = F * dx / dist;
        double fy = F * dy / dist;
        this.vx += fx / mass;
        this.vy += fy / mass;
        o.vx -= fx / o.mass;
        o.vy -= fy / o.mass;
    /*
        double d = Math.sqrt(Math.pow(x - o.x, 2) + Math.pow(y - o.y, 2));
        double nx = (o.x - x) / d;
        double ny = (o.y- y) / d;
        double p = 2 * (vx * nx + vy * ny - o.vx * nx - o.vy * ny) /
                (mass + o.mass);
        vx = vx - p * mass * nx;
        vy = vy - p * mass * ny;
        o.vx = o.vx + p * o.mass * nx;
        o.vy = o.vy + p * o.mass * ny;
*/
        timesModified++;
        o.timesModified++;
    }

    public double predict(Particle o){ // VER WHITE PAPER EN CAMPUS
        double dx = o.x - this.x;
        double dy = o.y - this.y;
        double dvx = o.getSpeedX() - this.getSpeedX();
        double dvy = o.getSpeedY() - this.getSpeedY();
        double dvdr = dvx*dx + dvy*dy;
        if(dvdr >= 0 ){
            return -1;
        }
        double dvdv = dvx*dvx + dvy*dvy;
        double drdr = dx*dx + dy*dy;
        double d = dvdr*dvdr - dvdv*(drdr - (radius + o.radius)*(radius + o.radius));
        if(d < 0 ){
            return -1;
        }

        return -(dvdr + Math.sqrt(d))/dvdv;
    }

        @Override
    public String toString() {
        return "Particula - Id: " + id + " Pos:(" +x+","+y+") Radio:"+radius;
    }

    public void setSpeedX(double speedX) {
        this.vx = speedX;
    }
    public void setSpeedY(double speedY){
        this.vy = speedY;
    }
    // TODO: BIEN LAS COLISIONES, MAS CUANDO HAYA MAS DE UNA PARED
    public double timeToHitLeftWall() {
        return (vx < 0)? (radius - x) / vx : -1;
    }
    public double timeToHitRightWall() {
        return (vx > 0)? ( 9 - radius - x) / vx : -1;
    }
    public double timeToHitTopWall() {
        return (vy > 0)? ( 12 - radius - y) / vy : -1;
    }
    public double timeToHitBottomWall() {
        return (vy < 0)? (radius - y) / vy : -1;
    }

    public void update(double time) {
        x += vx * (time - lastUpdate);
        y += vy * (time - lastUpdate);
        lastUpdate = time;
    }
}
