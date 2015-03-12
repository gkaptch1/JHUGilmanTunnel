import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

/**
 * We define a plane from -100 to 100 in each of the x and y directions let
 * there be n objects we create and m independant objects in the scene n >> m
 * 
 * @author Gabe
 *
 */
public class ViewGraph {

    private final float MAX_ENERGY = 100;
    private final float MOVEMENT_FACTOR = 5;
    private final float RESISTANCE_FACTOR = 1;
    private final float ENERGY_TRANSMISSION_FACTOR = 1 / 2;

    Collection<IndependantPoint> independantObjects_;
    Collection<Point> dependantObjects_;

    public ViewGraph(int numI, int numD) {
        independantObjects_ = new ArrayList<IndependantPoint>();
        Random rand = new Random();
        for (int i = 0; i < numI; i++) {
            independantObjects_
                    .add(new IndependantPoint(rand.nextFloat() * 200 - 100,
                            rand.nextFloat() * 200 - 100));
        }
        for (int i = 0; i < numD; i++) {
            dependantObjects_.add(new Point(rand.nextFloat() * 200 - 100, rand
                    .nextFloat() * 200 - 100));
        }
    }

    public void updateGraph() {

        for (IndependantPoint p : independantObjects_) {
            p.update();
        }

        for (Point p : dependantObjects_) {
            updatePosition(p);
            updateEnergy(p);
        }
    }

    private void updatePosition(Point p) {
        // TODO dissapate energy
        p.setX_(p.getX_() + p.getEnergyX_() * MOVEMENT_FACTOR
                - p.getDistanceFromHome() * RESISTANCE_FACTOR);
        p.setY_(p.getY_() + p.getEnergyY_() * MOVEMENT_FACTOR
                - p.getDistanceFromHome() * RESISTANCE_FACTOR);
    }

    private void updateEnergy(Point p) {
        for (IndependantPoint i : independantObjects_) {
            partialEnergyUpdate(p, i);
        }
    }

    private void partialEnergyUpdate(Point p, IndependantPoint i) {
        // TotalEnergyBoost is just the orthogonal linear projection of the
        // motion vector of i onto the vector connecting i and p
        // proj_s(v) = (V * S)/ (S * S) x S, where * is the dot product
        float xDistance = p.getX_() - i.getX();
        float yDistance = p.getY_() - i.getY();
        float multFactor = (i.getXMovement() * xDistance + i.getYMovement()
                * yDistance)
                / (xDistance * xDistance + yDistance * yDistance);
        float totalEnergyBoostX = multFactor * xDistance;
        float totalEnergyBoostY = multFactor * yDistance;
        // float totalEnergyBoostAngle = (float) Math.atan(yDistance /
        // xDistance);
        p.setEnergyX_(p.getEnergyX_() + totalEnergyBoostX);
        p.setEnergyY_(p.getEnergyY_() + totalEnergyBoostY);
    }

    public static void main(String[] args) {

    }

    private class Point {

        private float x_;
        private float y_;
        private float homeX_;
        private float homeY_;
        private float energyX_;
        private float energyY_;

        public Point(float x, float y) {
            x_ = x;
            y_ = y;
            homeX_ = x;
            homeY_ = y;
        }

        public float getX_() {
            return x_;
        }

        public void setX_(float x_) {
            this.x_ = x_;
        }

        public float getY_() {
            return y_;
        }

        public void setY_(float y_) {
            this.y_ = y_;
        }

        public float getHomeX_() {
            return homeX_;
        }

        public float getHomeY_() {
            return homeY_;
        }

        public float getEnergyX_() {
            return energyX_;
        }

        public void setEnergyX_(float energyX_) {
            this.energyX_ = energyX_;
        }

        public float getEnergyY_() {
            return energyY_;
        }

        public void setEnergyY_(float energyY_) {
            this.energyY_ = energyY_;
        }

        public float getDistanceFromHome() {
            return (float) Math.sqrt(Math.pow(x_ - homeX_, 2)
                    + Math.pow(y_ - homeY_, 2));
        }

    }

    private class IndependantPoint {

        private float previousX_;
        private float previousY_;
        private float currentX_;
        private float currentY_;

        public IndependantPoint(float x, float y) {
            previousX_ = x;
            currentX_ = x;
            previousY_ = y;
            currentY_ = y;
        }

        public float getXMovement() {
            return previousX_ - currentX_;
        }

        public float getYMovement() {
            return previousY_ - currentY_;
        }

        /**
         * Updates from previous part of project. Retrieves the coordinates of
         * the independant object from the sensors.
         */
        public void update() {
            // TODO
        }

        public float getMovementTotal() {
            return (float) Math.sqrt(getXMovement() * getXMovement()
                    + getYMovement() * getYMovement());
        }

        public float getX() {
            return currentX_;
        }

        public float getY() {
            return currentY_;
        }
    }
}
