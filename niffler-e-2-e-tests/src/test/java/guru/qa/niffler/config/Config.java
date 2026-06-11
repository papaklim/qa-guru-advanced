package guru.qa.niffler.config;

public interface Config {

  static Config getInstance() {
    return LocalConfig.INSTANCE;
  }

  String authUrl();

  String authJdbcUrl();

  String frontUrl();

  String UserDataUrl();

  String UserDataJdbcUrl();

  String spendUrl();

  String spendJdbcUrl();

  String currencyJdbcUrl();

  String ghApiUrl();
}
