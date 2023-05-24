import com.google.gson.Gson;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class Administration {
    private final ArrayList<GeneralInfo> dictionaries;
    private final ArrayList<Word> translatedWords;
    private int useSynonyms;

    Administration(){
        this.useSynonyms = 0;
        this.translatedWords = new ArrayList<>();
        this.dictionaries = new ArrayList<>();

        File folder = new File("inputDict/");
        File[] listOfFiles = folder.listFiles();

        assert listOfFiles != null;
        for (File file : listOfFiles) {
            if (file.isFile()) {
                String text = file.getName();
                String[] split = text.split("_");

                if (!split[1].equals("dict.json"))
                    continue;

                this.dictionaries.add(new GeneralInfo(split[0], file));
            }
        }
    }

    public boolean addWord(Word word, String language){
        int ok = 0;
        String nume = word.getWord();
        for(GeneralInfo dictionary : this.dictionaries){
            if(!language.equals(dictionary.getLanguage()))
                continue;
            ArrayList<Word> words = dictionary.getWords();
            for(Word cuvant : words){
                if(!nume.equals(cuvant.getWord()))
                    continue;

                ok++;
                ArrayList<Definition> definitions = word.getDefinitions();
                for(Definition definition : definitions)
                    if(cuvant.addDefinition(definition))
                        ok++;
            }

            //Nu am gasit cuvantul, asa ca il adaugam
            if(ok == 0){
                dictionary.addWord(word);
                return true;
            }

            //Am gasit cuvantul si nu am facut modificari (informatiile sunt deja salvate)
            if(ok == 1)
                return false;

            //Am gasit cuvantul si am facut modificari
            return true;
        }

        //Daca nu am gasit "language" in dictionaries
        ArrayList<Word> cuvinte = new ArrayList<>();
        cuvinte.add(word);
        GeneralInfo generalInfo = new GeneralInfo(language, cuvinte);
        dictionaries.add(generalInfo);
        return true;
    }

    private Word findWord(String word, String language, boolean en){
        for(GeneralInfo dictionary : this.dictionaries) {
            if (!language.equals(dictionary.getLanguage()))
                continue;
            ArrayList<Word> words = dictionary.getWords();
            for (Word cuvant : words) {
                if(!en) {
                    if (!word.equals(cuvant.getWord()))
                        continue;
                }
                else {
                    if (!word.equals(cuvant.getWordEn()))
                        continue;
                }
                return cuvant;
            }
        }
        return null;
    }

    public boolean removeWord(String word, String language){
        for(GeneralInfo dictionary : this.dictionaries){
            if(!language.equals(dictionary.getLanguage()))
                continue;
            ArrayList<Word> words = dictionary.getWords();
            for(Word cuvant : words){
                if(!word.equals(cuvant.getWord()))
                    continue;
                words.remove(cuvant);
                if(words.size() == 0)
                    this.dictionaries.remove(dictionary);
                return true;
            }
        }
        return false;
    }

    public boolean addDefinitionForWord(String word, String language, Definition definition){
        Word cuvant = findWord(word, language, false);
        if(cuvant == null)
            return false;
        return cuvant.addDefinition(definition);
    }

    public boolean removeDefinition(String word, String language, String dictionary){
        Word cuvant = findWord(word, language, false);
        if(cuvant == null)
            return false;
        return cuvant.removeDefinition(dictionary);
    }

    public String translateWord(String word, String fromLanguage, String toLanguage){
        for(GeneralInfo dictionary : this.dictionaries){
            if(!fromLanguage.equals(dictionary.getLanguage()))
                continue;

            ArrayList<Word> words = dictionary.getWords();
            for(Word cuvant : words){
                //Cautam cuvantul la singular
                ArrayList<String> singular = cuvant.getSingular();
                for(String sg : singular) {
                    if (!word.equals(sg))
                        continue;
                    return translateToLanguage(cuvant.getWordEn(), word, toLanguage, false, singular.indexOf(sg));
                }

                //Cautam cuvantul la plural
                ArrayList<String> plural = cuvant.getPlural();
                for(String pl : plural) {
                    if (!word.equals(pl))
                        continue;
                    return translateToLanguage(cuvant.getWordEn(), word, toLanguage, true, plural.indexOf(pl));
                }
            }
        }
        if(this.useSynonyms == 1) {
            Word cuvant = new Word(word, null, null, null, null, null);
            this.translatedWords.add(cuvant);
        }
        return word;
    }

    private String translateToLanguage(String word_en, String wordInitial, String language, boolean pl, int index){
        Word cuvant = findWord(word_en, language, true);
        if(cuvant == null) {
            if (this.useSynonyms == 1) {
                cuvant = new Word(wordInitial, null, null, null, null, null);
                this.translatedWords.add(cuvant);
            }
            return wordInitial;
        }

        if(this.useSynonyms == 1)
            this.translatedWords.add(cuvant);

        if(!pl){
            ArrayList<String> singular = cuvant.getSingular();
            return singular.get(index);
        }
        ArrayList<String> plural = cuvant.getPlural();
        return plural.get(index);
    }

    public String translateSentence(String sentence, String fromLanguage, String toLanguage){
        String[] words = sentence.split(" ");
        StringBuilder stringBuilder = new StringBuilder();
        for(String word : words)
            addToString(stringBuilder, translateWord(word, fromLanguage, toLanguage));

        return stringBuilder.toString();
    }

    private void addToString(StringBuilder stringBuilder, String word){
        stringBuilder.append(word);
        stringBuilder.append(" ");
    }

    public ArrayList<String> translateSentences(String sentence, String fromLanguage, String toLanguage){
        this.useSynonyms = 1;

        ArrayList<String> rez = new ArrayList<>();
        String translatedSentence = translateSentence(sentence, fromLanguage, toLanguage);
        rez.add(translatedSentence);

        String[] splitTSentence = translatedSentence.split(" ");
        int checkAppend;
        for(int i = 0; i < 2; i++) {
            int nrSynonyms = i + 1;
            StringBuilder stringBuilder = new StringBuilder();

            int j;
            for(j = 0; j < translatedWords.size(); j++){
                Word translatedWord = this.translatedWords.get(j);
                if(translatedWord.getDefinitions() == null){
                    addToString(stringBuilder, translatedWord.getWord());
                    continue;
                }

                checkAppend = 1;

                /* nrSynonyms este numarul de sinonime de care mai avem nevoie.
                 * Daca este 0, atunci doar punem cuvantul care este si in translatedSentence.
                 */
                if(nrSynonyms != 0){
                    for(Definition definition : translatedWord.getDefinitions()) {
                        String dictType = definition.getDictType();
                        if (!dictType.equals("synonyms"))
                            continue;

                        ArrayList<String> text = definition.getText();
                        if(text.size() == 0)
                            continue;

                        /* Daca nrSynonyms este 2, iar text.size() este 1, atunci intra pe if
                         * (adica pune singurul sinonim care a fost gasit). Altfel, intra pe else
                         * si foloseste un sinonim nou
                         */
                        if (nrSynonyms > text.size()) {
                            addToString(stringBuilder, text.get(text.size() - 1));
                            nrSynonyms -= text.size();
                        }
                        else{
                            addToString(stringBuilder, text.get(nrSynonyms - 1));
                            nrSynonyms = 0;
                        }
                        //checkAppend verifica daca a fost facut deja append la stringBuilder
                        checkAppend = 0;
                        break;
                    }
                }
                //Daca checkAppend a ramas 1, atunci trebuie sa facem append la cuvantul care era in translatedSentence
                if(checkAppend == 1) {
                    addToString(stringBuilder, splitTSentence[j]);
                }
            }
            if(nrSynonyms == 0)
                rez.add(stringBuilder.toString());
            else break;
        }

        this.useSynonyms = 0;
        this.translatedWords.clear();
        return rez;
    }

    public ArrayList<Definition> getDefinitionsForWord(String word, String language){
        Word cuvant = findWord(word, language, false);
        if(cuvant == null) {
            System.out.println("Please enter a word that is in the dictionary!\n");
            return new ArrayList<>();
        }

        //Se ajunge aici doar daca s-a gasit cuvantul "word"
        ArrayList<Definition> definitions = cuvant.getDefinitions();
        Collections.sort(definitions);
        return definitions;
    }

    public void exportDictionary(String language){
        for(GeneralInfo dictionary : this.dictionaries) {
            if (!language.equals(dictionary.getLanguage()))
                continue;

            //Se ajunge aici doar daca am gasit dictionarul in limba "language"
            ArrayList<Word> words = dictionary.getWords();
            for(Word word : words) {
                ArrayList<Definition> definitions = word.getDefinitions();
                Collections.sort(definitions);
            }
            Collections.sort(words);

            try {
                File myFile = new File("outputDict/");
                myFile.mkdir();
                myFile = new File("outputDict/" + language + "_dict.json");
                myFile.createNewFile();
                FileWriter myWriter = new FileWriter("outputDict/" + language + "_dict.json");
                myWriter.write(new Gson().toJson(words));
                myWriter.close();
                System.out.println("Successfully wrote to the file.\n");
                return;

            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
        }
        System.out.println("File cannot be written.\n");
    }
}
