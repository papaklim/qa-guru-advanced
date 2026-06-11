package guru.qa.niffler.api;

import com.fasterxml.jackson.databind.JsonNode;

import guru.qa.niffler.config.Config;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GhApiClient {
    private static final Config CFG = Config.getInstance();

    private static final String GH_API_TOKEN_ENV = "GITHUB_TOKEN";

    private static final HttpLoggingInterceptor logging = new HttpLoggingInterceptor(System.out::println).setLevel(
        HttpLoggingInterceptor.Level.BODY);

    private static final OkHttpClient httpClient = new OkHttpClient.Builder().addInterceptor(logging).build();

    private final Retrofit retrofit = new Retrofit.Builder().baseUrl(CFG.ghApiUrl()).client(
        httpClient).addConverterFactory(JacksonConverterFactory.create()).build();

    private final GhApi ghApi = retrofit.create(GhApi.class);

    public String issueState(String issueNumber) {
        final Response<JsonNode> response;
        try {
            response = ghApi.getIssueById("Bearer ".concat(System.getenv(GH_API_TOKEN_ENV)), issueNumber).execute();

        } catch (IOException e) {
            throw new AssertionError();
        }
        assertEquals(200, response.code());

        return requireNonNull(response.body()).get("state").asText();
    }
}
