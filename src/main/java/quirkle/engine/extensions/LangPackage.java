package quirkle.engine.extensions;

import java.util.HashMap;
import java.util.Scanner;

public class LangPackage {
    public String languageName;
    public HashMap<String, String> elements = new HashMap<>();

    String seperator = " == ";

    public LangPackage(Scanner langReader) {
        languageName = langReader.nextLine(); //language name isalways be declared in first the first Line

        String line;

        while (langReader.hasNextLine()) {
            line = langReader.nextLine();
            if (!line.contains(seperator)) continue; //comments can just be added as normal text

            String[] lineParts = line.split(java.util.regex.Pattern.quote(seperator));

            elements.put(lineParts[0],lineParts[1]);
        }
    }
}