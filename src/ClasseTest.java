import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Parameter;
import java.time.Year;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class ClasseTest
{
    private Class Year;
    private Classe classe = new Classe(Year);
    List<Constructeur> constructeurs;
    List<Methode> methodes;
    List<Attribut> attributs;

    public ClasseTest() throws ClassNotFoundException {
    }

    @BeforeEach
    void setUp()
    {
        this.constructeurs = classe.getConstructeurs();
        this.methodes = classe.getMethodes();
        this.attributs = classe.getAttributs();
    }

    @Test
    void testNomsConstructeurs()
    {
        for(int i = 0; i<constructeurs.size(); i++)
        {
            assertEquals(Year.getSimpleName(), constructeurs.get(i).getNom());
        }
    }

    @Test
    void testNomsMethodes()
    {
        for(int i = 0; i<methodes.size(); i++)
        {
            assertEquals(Year.getDeclaredMethods()[i].getName(), methodes.get(i).getNom());
        }
    }

    @Test
    void testNomsAttributs()
    {
        for(int i = 0; i<attributs.size(); i++)
        {
            assertEquals(Year.getDeclaredFields()[i].getName(), attributs.get(i).getNom());
        }
    }


    @Test
    void testParametersNamesInMethodes() {
        for(int i = 0; i<methodes.size(); i++)
        {
            Parameter[] listeParametres = Year.getDeclaredMethods()[i].getParameters();
            for(int j = 0; j<listeParametres.length;j++)
            {
                assertEquals(listeParametres[j].getName(), methodes.get(i).getParametres().get(j).getNom());
            }
        }
    }

    @Test
    void testParametersTypesInMethodes() throws ClassNotFoundException {
        for(int i = 0; i<methodes.size(); i++)
        {
            Parameter[] listeParametres = Year.getDeclaredMethods()[i].getParameters();
            for(int j = 0; j<listeParametres.length;j++)
            {
                assertEquals(listeParametres[j].getType().getSimpleName(), methodes.get(i).getParametres().get(j).type());
            }
        }
    }
}
