package guru.qa.niffler.api;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SpendApiClient implements SpendClient {

    private static final Config CFG = Config.getInstance();

    private static final HttpLoggingInterceptor logging = new HttpLoggingInterceptor(System.out::println).setLevel(
        HttpLoggingInterceptor.Level.BODY);

    private static final OkHttpClient httpClient = new OkHttpClient.Builder().addInterceptor(logging).build();

    private final Retrofit retrofit = new Retrofit.Builder().baseUrl(CFG.spendUrl()).client(
        httpClient).addConverterFactory(JacksonConverterFactory.create()).build();

    private final SpendApi spendApi = retrofit.create(SpendApi.class);

    @Override
    public SpendJson createSpend(SpendJson spend) {
        final Response<SpendJson> response;
        try {
            response = spendApi.createSpend(spend).execute();
            assertEquals(201, response.code());
            return response.body();
        } catch (IOException e) {
            throw new AssertionError();
        }
    }

    @Override
    public SpendJson updateSpend(SpendJson spend) {
        final Response<SpendJson> response;
        try {
            response = spendApi.updateSpend(spend).execute();
            assertEquals(200, response.code());
            return response.body();
        } catch (IOException e) {
            throw new AssertionError();
        }
    }

    @Override
    public Optional<SpendJson> getSpendByIdAndUserName(UUID id, String username) {
        final Response<SpendJson> response;
        try {
            response = spendApi.getSpendById(id, username).execute();
            assertEquals(200, response.code());
            return Optional.of(response.body());
        } catch (IOException e) {
            throw new AssertionError();
        }
    }

    @Override
    public List<SpendJson> getAllSpends(String username, CurrencyValues filterCurrency, String from, String to) {
        final Response<List<SpendJson>> response;
        try {
            response = spendApi.getAllSpends(username, filterCurrency, from, to).execute();
            assertEquals(200, response.code());
            return response.body();
        } catch (IOException e) {
            throw new AssertionError();
        }
    }

    @Override
    public void deleteSpend(List<UUID> ids, String userName) {
        Response<Void> response;
        try {
            response = spendApi.deleteSpend(ids, userName).execute();
            assertEquals(202, response.code());
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }


    @Override
    public CategoryJson createCategory(CategoryJson category) {
        final Response<CategoryJson> response;
        try {
            response = spendApi.createCategory(category).execute();
            assertEquals(200, response.code());
            return response.body();
        } catch (IOException e) {
            throw new AssertionError();
        }
    }

    @Override
    public CategoryJson updateCategory(CategoryJson category) {
        final Response<CategoryJson> response;
        try {
            response = spendApi.updateCategory(category).execute();
            assertEquals(200, response.code());
            return response.body();
        } catch (IOException e) {
            throw new AssertionError();
        }
    }

    @Override
    public List<CategoryJson> getAllCategories(String username, Boolean excludeArchived) {
        final Response<List<CategoryJson>> response;
        try {
            response = spendApi.getAllCategories().execute();
            assertEquals(200, response.code());
            return response.body();
        } catch (IOException e) {
            throw new AssertionError();
        }
    }

    @Override
    public Optional<CategoryJson> findCategoryByNameAndUsername(String categoryName, String username) {
        throw new UnsupportedOperationException("Not implemented :(");
    }
}
