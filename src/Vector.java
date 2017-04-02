/**
 * Created by lcasagrande on 21/03/17.
 */
public class Vector {
    double module;
    double angle;

    public Vector(double module, double angle){
        this.module = module;
        this.angle = angle;
    }

    public double getAngle() {
        return angle;
    }

    public double getModule() {
        return module;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public void setModule(double module) {
        this.module = module;
    }

    @Override
    public String toString() {
        return "[Vector Modulo: "+ module + " ,Angulo: " + angle;
    }
}