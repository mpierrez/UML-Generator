package model;

import com.github.javaparser.ast.body.MethodDeclaration;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Methode implements Options{
    
    private final Method method;
    List<Parametre> parametres = new ArrayList<>();
    
    public Methode(Method method)
    {
        this.method = method;
        for(Parameter parameter : method.getParameters())
        {
            Parametre parametre = new Parametre(parameter);
            parametres.add(parametre);
        }
    }

    public String getNom(){
        return method.getName();
    }

    public String convertir(String type)
    {
        return switch (type) {
            case "int" -> ": Integer";
            case "boolean" -> ": Boolean";
            case "char" -> ": Char";
            case "byte" -> ": Byte";
            case "short" -> ": Short";
            case "long" -> ": Long";
            case "float" -> ": Float";
            case "double" -> ": Double";
            case "void" -> "";
            default -> ": " + method.getReturnType().getSimpleName();
        };
    }

    @Override
    public String type() throws ClassNotFoundException {
        try {
            if (method.getReturnType().isArray()) {
                String type = method.getReturnType().getSimpleName().split("\\[")[0];
                return convertir(type) + "[*]";
            } else {
                ParameterizedType genericType = (ParameterizedType) method.getGenericReturnType();

                // Evènements pour voir si un catch intervient
                String type = ((Class) genericType.getActualTypeArguments()[0]).getSimpleName();
                type = ((Class) genericType.getActualTypeArguments()[1]).getSimpleName();

                return method.getReturnType().getSimpleName();
            }
        } catch (ClassCastException ex)
        {
            return convertir(method.getReturnType().getSimpleName());
        } catch(ArrayIndexOutOfBoundsException e)
        {
            ParameterizedType genericType = (ParameterizedType) method.getGenericReturnType();
            String type = ((Class) genericType.getActualTypeArguments()[0]).getSimpleName();
            if (List.class.isAssignableFrom(Class.forName(method.getReturnType().getName()))) {
                return type + "[*] {isOrdered}";
            } else if (Collection.class.isAssignableFrom(Class.forName(method.getReturnType().getName()))) {
                return method.getReturnType().getSimpleName() + " {isUnique}";
            }
            return type + "[*]";
        }
    }

    @Override
    public String estFinale() {
        return null;
    }

    @Override
    public String estStatic() {
        return Modifier.isStatic(method.getModifiers()) ? "{static}" : "";
    }


    @Override
    public String visibilite()
    {
        if(Modifier.isPublic(method.getModifiers()))
        {
            return "+";
        } else if(Modifier.isPrivate(method.getModifiers()))
        {
            return "-";
        } else if(Modifier.isProtected(method.getModifiers())) {
            return "#";
        } else {
            return "~";
        }
    }

    public List<Parametre> getParametres()
    {
        return parametres;
    }

    public String toInterface() throws ClassNotFoundException {
        StringBuilder res = new StringBuilder(visibilite() + " {abstract}" + estStatic() + " " + getNom() + "(");
        for(Parametre parametre : getParametres())
        {
            res.append(parametre.toString());
        }
        if(getParametres().size()!=0) return res.substring(0,res.length()-2) + ") " + type() + "\n";
        return res + ") " + type() + "\n";

    }

    @Override
    public String toString()
    {
        StringBuilder res = new StringBuilder(visibilite() + " " + estStatic() + " " + getNom() + "(");
        for(Parametre parametre : getParametres())
        {
            res.append(parametre.toString());
        }
        if(getParametres().size()!=0) {
            try {
                return res.substring(0,res.length()-2) + ") " + type() + "\n";
            } catch (ClassNotFoundException e) {
                return null;
            }
        }
        try {
            return res + ") " + type() + "\n";
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}