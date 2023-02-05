package model;

public class Interface extends Classe {

    public Interface(Class classe) throws ClassNotFoundException {
        super(classe);
    }

    @Override
    public String toString() {
        return "interface " + getNom() + " <<interface>> {\n";
    }
}
