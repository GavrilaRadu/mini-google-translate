import java.util.ArrayList;

public class Definition implements Comparable<Definition> {
    private String dict;
    private String dictType;
    private int year;
    private ArrayList<String> text;

    Definition(String dict, String dictType, int year, ArrayList<String> text){
        this.dict = dict;
        this.dictType = dictType;
        this.year = year;
        this.text = text;
    }

    public String getDict() {
        return this.dict;
    }
    public ArrayList<String> getText() {
        return this.text;
    }
    public String getDictType() {
        return this.dictType;
    }
    public int getYear() {
        return this.year;
    }

    public boolean addText(String scrisNou){
        for(String scris : this.text){
            if(scris.equals(scrisNou))
               return false;
        }
        this.text.add(scrisNou);
        return true;
    }

    @Override
    public int compareTo(Definition definition) {
        return Integer.compare(this.year, definition.year);
    }
}
