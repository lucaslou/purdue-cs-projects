/**
 * Shape -- provided code for Project 2
 * The Shape interface defines three methods that
 * all classes implementing Shape should provide. These
 * are common to all shapes and thus should logically be
 * included in any class defining a shape.
 */
interface Shape {
 public final int YES = 1;
 public final int NO = 2;
 public final int NOTSURE = 3;

    /**
     * calculates and returns the area of the current shape
     *
     * @return A double representing the 2d area of the shape
     */
    public double getArea();

    
    /**
     * calculates and returns the perimeter of the current shape
     *
     * @return A double representing the perimeter of the shape
     */
    public double getPerimeter();

    
    /**
     * determines whether the current object has the same dimensions and
     * type as the passed in shape
     *
     * @param s a Shape object to compare the current object to
     * @return b a boolean indicating whether the current shape is equal to the parameter
     */
   public boolean equals(Shape s);
   
    /**
     * determines whether the passed in shape can fit entirely inside current object
     * without mutation
     *
     * @param s a Shape object to compare the current object to
     * @return An int corresponding to one of the defined constants- either YES, NO, or NOTSURE
     */
   public int fitsInside(Shape s);
}

