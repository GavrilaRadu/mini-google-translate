import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.util.ArrayList;

public class GeneralInfo {
    private String language;
    private ArrayList<Word> words;

    private String readJSONFromFile(File file){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append("\n");
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            reader.close();
            return stringBuilder.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.err.println("ERROR!");
        return "how did you get here?";
    }

    public GeneralInfo(String language, File file){
        this.language = language;
        this.words = new Gson().fromJson(readJSONFromFile(file), new TypeToken<ArrayList<Word>>(){}.getType());
    }
    public GeneralInfo(String language, ArrayList<Word> words){
        this.language = language;
        this.words = words;
    }

    public String getLanguage() {
        return this.language;
    }
    public ArrayList<Word> getWords() {
        return this.words;
    }

    public void addWord(Word word){
        this.words.add(word);
    }
}
