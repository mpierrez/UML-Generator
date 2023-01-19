import java.io.FileWriter;
import java.io.IOException;

class Generation{

    private final FileWriter ecriture;
    private final Classe classe;
    private String uml="";

    public Generation(FileWriter ecriture, Classe classe){
        this.ecriture = ecriture;
        this.classe = classe;
    }
    public String getUml(){
        return uml;
    }

    public void ecrireEnTete() {
        uml += """
                @startuml

                skinparam style strictuml
                skinparam classAttributeIconSize 0
                skinparam classFontStyle Bold
                hide enum methods
                hide empty members

                """;
    }

    public void ecrirePiedPage() {
        uml += "@enduml";
    }

    public void ecrireClasse() {
        uml += classe.toString();
    }

    public void ecrireAttributs() throws ClassNotFoundException {
        for (Attribut attribut : classe.getAttributs()) {
            if(attribut.type() != null) {
                if (classe instanceof Enumeration) {
                    if (attribut.estUneEnum()) {
                        uml += attribut.toEnum();
                    }
                } else {
                    uml += attribut.toString();
                }
            }
        }
    }

    public void ecrireMethodes() throws ClassNotFoundException {
        for(Methode methode : classe.getMethodes())
        {
            if(classe instanceof Interface){
                uml += methode.toInterface();
            }
            else{
                uml += methode.toString();
            }
        }
        uml+="}\n";

    }

    public void ecrireConstructeurs() {
        for(Constructeur constructeur : classe.getConstructeurs())
        {
            uml += constructeur.toString();
        }
    }

    public void ecrireRelations() {
        // Ecriture super classe
        Relation relation = new Relation(classe.transformInClass(), classe.transformInClass().getSuperclass());
        try {
            if (relation.estValide()) {
                uml += relation.toString();
            }

            // Ecriture interface(s)
            for (Class c : classe.transformInClass().getInterfaces()) {
                relation = new Relation(classe.transformInClass(), c);
                if (relation.estValide()) {
                    uml += relation.toString();
                }
            }
            // Ecriture relation(s) d'association
            for(Attribut a : classe.getAttributs())
            {
                String association = a.getAssociation();
                if(association != null)uml+= classe.getNom() + " o---> \"" + association.split(":")[0] + association.split(":")[1] + " *\"" + association.split(":")[2] + "\n";
            }
        // Si la classe est une interface / enum
        } catch(NullPointerException e)
        {
            // Aucune relation
        }
    }

    public void ecrireGlobale() throws ClassNotFoundException {
        ecrireAttributs();
        ecrireConstructeurs();
        ecrireMethodes();
        ecrireRelations();
    }

    public void ecrire() throws IOException {
        ecriture.write(uml);
    }
    public void reset(){
        uml.replace(uml, "");
    }
}