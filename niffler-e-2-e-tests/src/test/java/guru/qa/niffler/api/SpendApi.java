package guru.qa.niffler.api;

import guru.qa.niffler.model.spend.CategoryJson;
import guru.qa.niffler.model.spend.CurrencyValues;
import guru.qa.niffler.model.spend.SpendJson;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;
import java.util.UUID;

public interface SpendApi {
    @POST("/internal/spends/add")
    Call<SpendJson> createSpend(
        @Body SpendJson spend);

    @PATCH("internal/spends/edit")
    Call<SpendJson> updateSpend(
        @Body SpendJson spend);

    @GET("internal/spends/{id}")
    Call<SpendJson> getSpendById(
        @Path("id") UUID id,
        @Query("username") String username);

    @GET("internal/spends/all")
    Call<List<SpendJson>> getAllSpends(
        @Query("username") String username,
        @Query("filterCurrency") CurrencyValues filterCurrency,
        @Query("from") String from,
        @Query("to") String to);

    @DELETE("internal/spends/remove")
    Call<Void> deleteSpend(
        @Query("ids") List<UUID> ids,
        @Query("username") String username);

    @POST("internal/categories/add")
    Call<CategoryJson> createCategory(
        @Body CategoryJson category);

    @PATCH("internal/categories/update")
    Call<CategoryJson> updateCategory(
        @Body CategoryJson category);

    @GET("internal/categories/all")
    Call<List<CategoryJson>> getAllCategories();

}
