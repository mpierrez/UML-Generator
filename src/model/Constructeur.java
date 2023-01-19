package model;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

public class Constructeur implements Options{
    
    private final Constructor constructeur;
    List<Parametre> parametres = new ArrayList<>();

    public Constructeur(Constructor constructeur)
    {
        this.constructeur = constructeur;
        for(Parameter parameter : constructeur.getParameters())
        {
            Parametre parametre = new Parametre(parameter);
            parametres.add(parametre);
        }

    }

    public String getNom(){
        String[] nom = constructeur.getName().split("\\.");
        return nom[nom.length-1];
    }

    @Override
    public String type(){
        return getNom();
    }

    @Override
    public String estFinale() {
        return null;
    }

    @Override
    public String estStatic() {
        return null;
    }

    @Override
    public String visibilite()
    {
        if(Modifier.isPublic(constructeur.getModifiers()))
        {
            return "+";
        } else if(Modifier.isPrivate(constructeur.getModifiers()))
        {
            return "-";
        } else if(Modifier.isProtected(constructeur.getModifiers())) {
            return "#";
        } else {
            return "~";
        }
    }

    public List<Parametre> getParametres()
    {
        return this.parametres;
    }

    @Override
    public String toString()
    {
        StringBuilder res = new StringBuilder(visibilite() + " <<Create>> " + getNom() + "(");
        for(Parametre parametre : getParametres())
        {
            res.append(parametre.toString());
        }
        if(getParametres().size()!=0) return res.substring(0,res.length()-2) + ")\n";
        return res + ")\n";
    }
}