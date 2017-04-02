/**
 * Created by Lucas on 02/04/2017.
 */
public class Collision implements Comparable<Collision> {
    Particle p1;
    Particle p2;
    boolean wall;
    Wall w;
    int modifiedP1;
    int modifiedP2;
    double time;

    public Collision(Particle p1, Particle p2, double time){
        this.p1 = p1;
        this.p2 = p2;
        modifiedP1 = p1.getTimesModified();
        modifiedP2 = p2.getTimesModified();
        this.time = time;
    }

    public Collision(Particle p,Wall w, double time){
        p1 = p;
        wall = true;
        this.w = w;
        this.time = time;
        modifiedP1 = p1.getTimesModified();
    }

    public void collide(){
        if(wall) {
            collideWithWall();
        }else{
            collideWithParticle();
        }
    }

    private void collideWithParticle() {
        if(p1.getTimesModified() != modifiedP1 || p2.getTimesModified() != modifiedP2){
            return;
        }
    }

    private void collideWithWall() {
        if(p1.getTimesModified() != modifiedP1){
            return;
        }
        switch (w) {
            case TOP:
            case BOTTOM:
                p1.setSpeedY(-p1.getSpeedY());
                break;
            case LEFT:
            case RIGHT:
                p1.setSpeedX(-p1.getSpeedX());
                break;
        }
        p1.timesModified++;
    }
    @Override
    public int compareTo(Collision o) {
        return (int) ((time - o.time) * 100000);
    }
}
