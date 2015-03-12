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

    // We cap the max energy something can gain. This is just to limit nonsense
    private final float MAX_ENERGY = 100;
    // The conversion rate between the energy of a point and the rate of motion
    private final float MOVEMENT_FACTOR = 5;
    // The conversion rate between the distance from home a point is and the
    // pull it feels towards home
    private final float RESISTANCE_FACTOR = 1;
    // The conversion rate between the velocity vector of the independent points
    // and the energy gain of the points
    private final float ENERGY_TRANSMISSION_FACTOR = 1 / 2;
    // Constant leak factor for energy
    private final float ENERGY_LEAK_FACTOR = 1;
    // The area an independant object can influence.  This is so someone moving into the graph doesnt effect EVERYTHING
    private final float AREA_OF_INFLUENCE = 10;

    // collection of all the points being tracked by the sensors
    Collection<IndependantPoint> independantObjects_;
    // Collections of all the points that we initialize into the system. ie.
    // leaves
    Collection<Point> dependantObjects_;

    /**
     * Constructor. Currently locations are initialized randomly
     * 
     * @param numI
     *            number of indepenant sensor objects. Mostly for testing
     *            purposes
     * @param numD
     *            number of dependant points in the system.
     */
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

    /**
     * Updates the graph. Collects new positions of the Independant points, then
     * updates based on their motion
     */
    public void updateGraph() {

        for (IndependantPoint p : independantObjects_) {
            // get new information from the sensors
            p.update();
        }

        for (Point p : dependantObjects_) {
            // Updates the position of the points
            updatePosition(p);
            // updates the energy of the dependant point
            updateEnergy(p);
        }
    }

    /**
     * position = oldPosition + energy*MOVEMENT_FACTOR -
     * distanceFromHome*RESISTANCE_FACTOR
     * 
     * @param p
     */
    private void updatePosition(Point p) {
        p.setX_(p.getX_() + p.getEnergyX_() * MOVEMENT_FACTOR
                - p.getDistanceFromHome() * RESISTANCE_FACTOR);
        p.setY_(p.getY_() + p.getEnergyY_() * MOVEMENT_FACTOR
                - p.getDistanceFromHome() * RESISTANCE_FACTOR);
    }

    private void updateEnergy(Point p) {
        for (IndependantPoint i : independantObjects_) {
            if (Math.sqrt(Math.pow(i.getX() - p.getX_(),2) + Math.pow(i.getY() - p.getY_(),2) < AREA_OF_INFLUENCE) {
                partialEnergyUpdate(p, i);
            }
        }
        p.setEnergyX_(p.getEnergyX_() - ENERGY_LEAK_FACTOR);
        p.setEnergyY_(p.getEnergyY_() - ENERGY_LEAK_FACTOR);
    }

    /**
     * Updates the energy of a point based off the motion of a single
     * independant point. We take the orthogonal projection of the movement
     * vector onto the vector connection the indpendant point and the dependant
     * point.
     * 
     * @param p
     * @param i
     */
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

    /**
     * Represents a dependant point
     * 
     * @author Gabe
     *
     */
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
