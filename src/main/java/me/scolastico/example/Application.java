package me.scolastico.example;

import com.github.lalyos.jfiglet.FigletFont;
import me.scolastico.example.dataholders.Config;
import me.scolastico.example.dataholders.DatabaseConfig;
import me.scolastico.example.etc.DatabaseConnector;
import me.scolastico.tools.handler.ConfigHandler;
import me.scolastico.tools.handler.ErrorHandler;
import me.scolastico.tools.simplified.SimplifiedResourceFileReader;

public class Application {

  private static ConfigHandler<Config> configHandler;
  private static Config config;
  private final static String version = SimplifiedResourceFileReader.getInstance().getStringFromResources("staticVars/VERSION");
  private final static String branch = SimplifiedResourceFileReader.getInstance().getStringFromResources("staticVars/BRANCH");
  private final static String commit = SimplifiedResourceFileReader.getInstance().getStringFromResources("staticVars/COMMIT");

  public static void main(String[] args) {
    try {
      ErrorHandler.enableErrorLogFile();
      ErrorHandler.enableCatchUncaughtException();
      //ErrorHandler.enableSentry("");
      System.out.println(FigletFont.convertOneLine("Application"));
      System.out.println("Version: " + version + " | By: scolastico");
      final double startingTime = System.currentTimeMillis();

      System.out.print("Loading config... ");
      configHandler = new ConfigHandler<>(new Config(), "config.json");
      if (!configHandler.checkIfExists()) configHandler.saveDefaultConfig();
      config = configHandler.loadConfig();
      System.out.println("[OK]");

      System.out.print("Loading database config... ");
      ConfigHandler<DatabaseConfig> databaseConfigHandler = new ConfigHandler<>(new DatabaseConfig(), "dbConfig.json");
      if (!databaseConfigHandler.checkIfExists()) databaseConfigHandler.saveDefaultConfig();
      DatabaseConfig dbConfig = databaseConfigHandler.loadConfig();
      System.out.println("[OK]");

      System.out.print("Connecting to database... ");
      DatabaseConnector.connectToDatabase(dbConfig);
      System.out.println("[OK]");

      System.out.print("Migrate database... ");
      DatabaseConnector.runMigrations();
      System.out.println("[OK]");

      System.out.println("Done! Starting took " + ((System.currentTimeMillis()-startingTime)/1000) + " seconds.");
    } catch (Exception e) {
      ErrorHandler.handleFatal(e);
    }
  }

  public static String getBranch() {
    return branch;
  }

  public static String getCommit() {
    return commit;
  }

  public static String getVersion() {
    return version;
  }

  public static ConfigHandler<Config> getConfigHandler() {
    return configHandler;
  }

  public static void setConfigHandler(ConfigHandler<Config> configHandler) {
    Application.configHandler = configHandler;
  }

  public static Config getConfig() {
    return config;
  }

  public static void setConfig(Config config) {
    Application.config = config;
  }

}
