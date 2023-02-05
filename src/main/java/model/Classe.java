package model;

import model.Attribut;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Classe{

    private final Class classe;
    List<Attribut> attributs = new ArrayList<>();
    List<Methode> methodes = new ArrayList<>();
    List<Constructeur> constructeurs = new ArrayList<>();

    public Classe(Class classe) throws ClassNotFoundException {
        this.classe = classe;
        for(Field field : classe.getDeclaredFields())
        {
            Attribut attribut = new Attribut(field);
            attributs.add(attribut);
        }
        for(Constructor constructor : classe.getDeclaredConstructors())
        {
            Constructeur constructeur = new Constructeur(constructor);
            constructeurs.add(constructeur);
        }
        for(Method method : classe.getDeclaredMethods())
        {
            Methode methode = new Methode(method);
            methodes.add(methode);
        }
    }

    public String getNom(){
        return classe.getName();
    }

    public List<Attribut> getAttributs(){
        return attributs;
    }

    public List<Methode> getMethodes(){
        return methodes;
    }

    public List<Constructeur> getConstructeurs(){
        return constructeurs;
    }

    public Class transformInClass()
    {
        return this.classe;
    }

    @Override
    public String toString()
    {
        return "class " + getNom() + " {\n";
    }

}