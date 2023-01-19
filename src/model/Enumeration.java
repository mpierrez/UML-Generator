package model;

public class Enumeration extends Classe
{
    public Enumeration(Class classe) throws ClassNotFoundException
    {
        super(classe);
    }

    @Override
    public String toString()
    {
        return "enum " + getNom() + " <<enumeration>> {\n";
    }
}
