package parser;

import java.util.Arrays;
import java.util.List;

public enum HeaderName {
    DATE("date", "transaction date"), DESCRIPTION("description"), WITHDRAWAL("amount", "debit"), DEPOSIT("amount", "credit");

    private final List<String> keywords;

    HeaderName(String... keywords) {
        this.keywords = Arrays.asList(keywords);
    }


    public List<String> getKeywords() {
        return this.keywords;
    }
}