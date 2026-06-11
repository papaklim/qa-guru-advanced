package guru.qa.niffler.config;

public interface Config {

  static Config getInstance() {
    return LocalConfig.INSTANCE;
  }

  String authUrl();

  String authJdbcUrl();

  String frontUrl();

  String userDataUrl();

  String userDataJdbcUrl();

  String spendUrl();

  String spendJdbcUrl();

  String currencyJdbcUrl();

  String ghApiUrl();
}
