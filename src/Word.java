import java.util.ArrayList;

public class Word implements Comparable<Word> {
    private String word;
    private String word_en;
    private String type;
    private ArrayList<String> singular;
    private ArrayList<String> plural;
    private ArrayList<Definition> definitions;

    Word(String word, String word_en, String type, ArrayList<String> singular, ArrayList<String> plural, ArrayList<Definition> definitions){
        this.word = word;
        this.word_en = word_en;
        this.type = type;
        this.singular = singular;
        this.plural = plural;
        this.definitions = definitions;
    }

    public String getWord(){
        return this.word;
    }
    public ArrayList<Definition> getDefinitions() {
        return this.definitions;
    }
    public String getWordEn() {
        return this.word_en;
    }
    public ArrayList<String> getSingular() {
        return this.singular;
    }
    public ArrayList<String> getPlural() {
        return this.plural;
    }

    public boolean addDefinition(Definition definition){
        int ok = 0;
        String dict = definition.getDict();
        for(Definition definitie : this.definitions){
            if(dict.equals(definitie.getDict())){
                ok++;
                ArrayList<String> text = definition.getText();
                for(String scris : text){
                    if(definitie.addText(scris))
                        ok++;
                }
            }
        }

        //Nu a fost gasita definitia, asa ca o adaugam
        if(ok == 0) {
            this.definitions.add(definition);
            return true;
        }

        //A fost gasita definitia si nu a fost modificat text-ul din aceasta (era identic)
        if(ok == 1)
            return false;

        //A fost gasita definitia, dar a fost modificat text-ul din aceasta
        return true;
    }

    public boolean removeDefinition(String dictionary){
        for(Definition definition : this.definitions){
            if(!dictionary.equals(definition.getDict()))
                continue;
            definitions.remove(definition);
            return true;
        }
        return false;
    }

    @Override
    public int compareTo(Word cuvant) {
        return this.word.compareTo(cuvant.getWord());
    }
}
