package parser;

import model.Account;

import java.io.IOException;
import java.sql.SQLException;

public class HolderOfParsers extends AbstractCSVParser{

    public HolderOfParsers(Account account) throws IOException, SQLException {
        setAccount(account);
        setReader();
        gotNewHeaders();
        holdingEachLine();
    }



}
