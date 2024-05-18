import database.Database;
import usermenustates.MainMenuState;

import java.io.IOException;
import java.sql.SQLException;


public class Main {


  public static void main(String[] args) throws SQLException, IOException {
    System.out.println(System.getProperty("java.class.path"));
    Database.getDatabaseInstance();
    MainMenuState s = MainMenuState.getMainMenuStateInstance();
    s.showState();



  }
}




