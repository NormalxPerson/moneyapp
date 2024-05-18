package parser;

import java.io.File;

public interface CSVParser {
    void setReader();
    boolean isValidCSVFile(File file);
}