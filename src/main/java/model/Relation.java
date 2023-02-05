package model;

public class Relation {

    private Class origin;
    private Class destination;

    public Relation(Class origin, Class destination){
        this.origin = origin;
        this.destination = destination;
    }

    public boolean estValide()
    {
        return !Boolean.class.isAssignableFrom(destination)
                && !Character.class.isAssignableFrom(destination)
                && !Byte.class.isAssignableFrom(destination)
                && !Short.class.isAssignableFrom(destination)
                && !Integer.class.isAssignableFrom(destination)
                && !Long.class.isAssignableFrom(destination)
                && !Float.class.isAssignableFrom(destination)
                && !Double.class.isAssignableFrom(destination);
    }

    @Override
    public String toString() {
        if(destination.isInterface()) {
            return this.destination.getName() + " <|.. " + this.origin.getName() + "\n";
        } else {
            return this.destination.getName() + " <|-- " + this.origin.getName() + "\n";
        }
    }
}
