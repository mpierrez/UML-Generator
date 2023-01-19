import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Attribut implements Options{

    private final Field field;
    private final boolean estEnum;
    private String association;
    protected ArrayList<Integer> test;



    public Attribut(Field field)
    {
        this.field = field;
        this.estEnum = field.isEnumConstant();
    }

    public String getNom(){
        return field.getName();
    }

    public String getAssociation(){ return this.association; }

    public boolean estUneEnum()
    {
        return this.estEnum;
    }

    public String convertir(String type)
    {
        return switch (type) {
            case "int" -> "Integer";
            case "boolean" -> "Boolean";
            case "char" -> "Char";
            case "byte" -> "Byte";
            case "short" -> "Short";
            case "long" -> "Long";
            case "float" -> "Float";
            case "double" -> "Double";
            default -> field.getType().getSimpleName();
        };
    }

    @Override
    public String type() throws ClassNotFoundException {
        try {
            if (field.getType().isArray()) {
                String type = field.getType().getSimpleName().split("\\[")[0];
                return convertir(type) + "[*]";
            } else {
                ParameterizedType genericType = (ParameterizedType) field.getGenericType();

                // Evènements pour voir si un catch intervient
                String type = ((Class) genericType.getActualTypeArguments()[0]).getSimpleName();
                type = ((Class) genericType.getActualTypeArguments()[1]).getSimpleName();

                return field.getType().getSimpleName();
            }
        } catch (ClassCastException ex)
        {
            return convertir(field.getType().getSimpleName());
        } catch(ArrayIndexOutOfBoundsException e)
        {
            ParameterizedType genericType = (ParameterizedType) field.getGenericType();
            String type = ((Class) genericType.getActualTypeArguments()[0]).getSimpleName();
            if(List.class.isAssignableFrom(Class.forName(field.getType().getName()))) {
                association = visibilite() + ":" + field.getName() + ":" + field.getType().getName() + ":{isOrdered}";
                return null;
            }
                else if(Collection.class.isAssignableFrom(Class.forName(field.getType().getName())))
            {
                association = visibilite() + ":" + field.getName() + ":" + field.getType().getName() + ":{isUnique}";
                return null;
            }
            return type + "[*]";
        }
    }

    public String estFinale(){
        return Modifier.isFinal(field.getModifiers()) ? "{readOnly}" : "";
    }
    public String estStatic(){return Modifier.isStatic(field.getModifiers()) ? "{static}" : "";
    }

    @Override
    public String visibilite()
    {
        if(Modifier.isPublic(field.getModifiers()))
        {
            return "+";
        } else if(Modifier.isPrivate(field.getModifiers()))
        {
            return "-";
        } else if(Modifier.isProtected(field.getModifiers())) {
            return "#";
        } else {
            return "~";
        }
    }

    @Override
    public String toString()
    {
        try {
            return visibilite() + " " + estStatic() + " " + getNom() + " : " + type() + " " + estFinale() + "\n";
        } catch (ClassNotFoundException e) {
            return e.toString();
        }
    }

    public String toEnum()
    {
        return getNom() + "\n";
    }

}