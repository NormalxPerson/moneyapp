package parser;

import java.util.*;

public class HeaderChecker {
    private HashMap<HeaderName, Boolean> headerFound;

    public HeaderChecker() {
        headerFound = new HashMap<>();
        for (HeaderName name : HeaderName.values()) {
            headerFound.put(name, false);

        }
    }

    public HashMap<String, Integer> compareCSVHeader(String[] csvColumnHeaders) {
        HashMap<String, Integer> map = new HashMap<>();
        for ( int i = 0; i < csvColumnHeaders.length; i++ ) {
            List<String> titles = checkHeader(csvColumnHeaders[i]);
            for (String title : titles) {
                System.out.println(title + ", Column Number: " + (i+1));
                map.put(title, i+1);
            }
        }
        System.out.println("Map size = " + map.size());
        if (map.size() >= 3) {return map; }
        else return null;
    }

    public List<String> checkHeader(String header) {
        List<String> matchedHeaders = new ArrayList<>();
        for (HeaderName name : HeaderName.values()) {
            for (String keyword : name.getKeywords()) {
                if (header.toLowerCase().equalsIgnoreCase(keyword)) {
                    matchedHeaders.add(name.name().toLowerCase());
                    headerFound.put(name, true);
                    break;
                }
            }
        }
        return matchedHeaders;
    }


    public boolean isHeaderFound(HeaderName headerName) {
        return headerFound.getOrDefault(headerName, false);
    }

    // If you still want a method to access all keywords, it can be handled like this:
    public Map<HeaderName, List<String>> getAllKeywords() {
        Map<HeaderName, List<String>> allKeywords = new HashMap<>();
        for (HeaderName name : HeaderName.values()) {
            allKeywords.put(name, name.getKeywords());
        }
        return allKeywords;
    }

}