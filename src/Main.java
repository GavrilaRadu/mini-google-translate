import javax.swing.plaf.synth.SynthRadioButtonMenuItemUI;
import java.util.ArrayList;

public class Main {
    private static Administration admin = new Administration();

    public static Definition makeDefinition(String str){
        ArrayList<String> text = new ArrayList<>();
        text.add(str);
        Definition definition = new Definition("Dictionarul explicativ online", "definitions", 2009, text);
        return definition;
    }

    public static Word makeWord(){
        ArrayList<String> singular = new ArrayList<>();
        singular.add("portofel");
        ArrayList<String> plural = new ArrayList<>();
        plural.add("portofele");
        Definition definition = makeDefinition("Obiect din piele, din material plastic etc., în forma unor coperți mici cu mai multe despărțituri interioare, în care se păstrează bani, acte etc.");
        ArrayList<Definition> definitions = new ArrayList<>();
        definitions.add(definition);
        Word word = new Word("portofel", "wallet", "noun", singular, plural, definitions);
        return word;
    }

    public static void testAddWord(Word word, String language){
        System.out.println("===== addWord =====\n" + admin.addWord(word, language) + "\n");
        System.out.println("===== addWord x2 =====\n" + admin.addWord(word, language) + "\n");
    }

    public static void testAddDefinitionForWord(String word, String language, Definition definition){
        System.out.println("===== addDefinitionForWord =====\n" + admin.addDefinitionForWord(word, language, definition) + "\n");
        System.out.println("===== addDefinitionForWord x2 =====\n" + admin.addDefinitionForWord(word, language, definition) + "\n");
    }

    public static void testRemoveWord(String word, String language){
        System.out.println("===== removeWord =====\n" + admin.removeWord(word, language) + "\n");
        System.out.println("===== removeWord x2 =====\n" + admin.removeWord(word, language) + "\n");
    }

    public static void testRemoveDefinition(String word, String language, String dictionary){
        System.out.println("===== removeDefinition =====\n" + admin.removeDefinition(word, language, dictionary) + "\n");
        System.out.println("===== removeDefinition x2 =====\n" + admin.removeDefinition(word, language, dictionary) + "\n");
    }

    public static void testTranslateWord(String word, String fromLanguage, String toLanguage){
        System.out.println("===== translateWord =====\n" + admin.translateWord(word, fromLanguage, toLanguage) + "\n");
    }

    public static void testTranslateSentence(String sentence, String fromLanguage, String toLanguage){
        System.out.println("===== translateSentence =====\n" + admin.translateSentence(sentence, fromLanguage, toLanguage) + "\n");
    }

    public static void testTranslateSencences(String sentence, String fromLanguage, String toLanguage){
        ArrayList<String> sinonime = admin.translateSentences(sentence, fromLanguage, toLanguage);
        System.out.println("===== translateSentences =====");
        for(String sinonim : sinonime)
            System.out.println(sinonim);
        System.out.println();
    }

    public static void testGetDefinitionsForWord(String word, String language){
        System.out.println("===== getDefinitionsForWord =====");
        ArrayList<Definition> defs = admin.getDefinitionsForWord(word, language);
        for(Definition def : defs)
            System.out.println(def.getYear());
        System.out.println();
    }

    public static void testExportDictionary(String language){
        System.out.println("===== exportDictionary =====");
        admin.exportDictionary(language);
    }

    public static void testAll(){
        testAddDefinitionForWord("merge", "ro", makeDefinition("Nu sta, ci merge!"));

        Word word = makeWord();
        testAddWord(word, "sp");

        testRemoveWord("portofel", "sp");

        testRemoveDefinition("merge", "ro", "Dictionarul explicativ online");

        testTranslateWord("pisici", "ro", "fr");
        testTranslateWord("câine", "ro", "fr");

        testTranslateSentence("pisică - pisici", "ro", "fr");
        testTranslateSentence("merge - mergem", "ro", "fr");

        testTranslateSencences("o pisică merge acasa", "ro", "fr");
        testTranslateSencences("un câine merge acasa", "ro", "fr");

        testGetDefinitionsForWord("câine", "ro");

        testExportDictionary("ro");
        testExportDictionary("sp");
    }

    public static void main(String[] args) {
        testAll();
    }
}
