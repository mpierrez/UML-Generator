import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;

public class Parametre implements Options{
    private final Parameter parameter;

    public Parametre(Parameter parameter){
        this.parameter = parameter;
    }

    public String getNom(){
        return parameter.getName();
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
            default -> parameter.getType().getSimpleName();
        };
    }

    @Override
    public String type() throws ClassNotFoundException {
        try {
            if (parameter.getType().isArray()) {
                String type = parameter.getType().getSimpleName().split("\\[")[0];
                return convertir(type) + "[*]";
            } else {
                ParameterizedType genericType = (ParameterizedType) parameter.getParameterizedType();

                // Evènements pour voir si un catch intervient
                String type = ((Class) genericType.getActualTypeArguments()[0]).getSimpleName();
                type = ((Class) genericType.getActualTypeArguments()[1]).getSimpleName();

                return parameter.getType().getSimpleName();
            }
        } catch (ClassCastException ex)
        {
            return convertir(parameter.getType().getSimpleName());
        } catch(ArrayIndexOutOfBoundsException e)
        {
            ParameterizedType genericType = (ParameterizedType) parameter.getParameterizedType();
            String type = ((Class) genericType.getActualTypeArguments()[0]).getSimpleName();
            if(List.class.isAssignableFrom(Class.forName(parameter.getType().getName()))) {
                return type + "[*] {isOrdered}";
            }
            else if(Collection.class.isAssignableFrom(Class.forName(parameter.getType().getName())))
            {
                return parameter.getType().getSimpleName() + " {isUnique}";
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
        return null;
    }


    @Override
    public String visibilite() {
        return null;
    }

    @Override
    public String toString()
    {
        try {
            return getNom() + " : " + type() + ", ";
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}
