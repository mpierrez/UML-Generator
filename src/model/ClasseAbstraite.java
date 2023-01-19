package model;

public class ClasseAbstraite extends Classe
{
    public ClasseAbstraite(Class classe) throws ClassNotFoundException
    {
        super(classe);
    }

    @Override
    public String toString()
    {
        return "abstract class " + getNom() + " <<abstract>> {\n";
    }
}
