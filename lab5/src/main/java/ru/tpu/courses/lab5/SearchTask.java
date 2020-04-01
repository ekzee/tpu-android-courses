package ru.tpu.courses.lab5;

import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Задача ищет список репозиториев через АПИ гитхаба - https://developer.github.com/v3/
 */
public class SearchTask extends Task<List<Repo>> {

    /**
     * {@link OkHttpClient} это класс из библиотеки для выполнения http запросов -
     * https://square.github.io/okhttp/4.x/okhttp/okhttp3/-ok-http-client/
     * Статичная переменная - опять точно также как и в случае с экзекьютором, необходимо переиспользовать
     * один и тот же объект, чтобы коннект переиспользовался при последовательных запросах.
     */
    private static OkHttpClient httpClient;

    public static OkHttpClient getHttpClient() {
        if (httpClient == null) {
            synchronized (SearchTask.class) {
                if (httpClient == null) {
                    // Логирование запросов в logcat
                    HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor()
                            .setLevel(HttpLoggingInterceptor.Level.BASIC);
                    httpClient = new OkHttpClient.Builder()
                            .addInterceptor(loggingInterceptor)
                            .build();
                }
            }
        }
        return httpClient;
    }

    public SearchTask(@Nullable Observer<List<Repo>> observer) {
        super(observer);
    }

    @Override
    @WorkerThread
    protected List<Repo> executeInBackground() throws Exception {
        String response = search();
        return parseSearch(response);
    }

    /**
     * Делаем запрос к АПИ гитхаба и возвращаем тело ответа
     */
    private String search() throws Exception {
        Request request = new Request.Builder()
                .url("https://api.github.com/search/repositories?q=android")
                .build();
        Response response = getHttpClient().newCall(request).execute();
        if (response.code() != 200) {
            throw new Exception("api returned unexpected http code: " + response.code());
        }

        return response.body().string();
    }

    /**
     * Парсинг ответа от АПИ в формате json. Используется интегриорванное средство Андроид, но
     * обычно используют библиотеки вроде Gson (https://github.com/google/gson)
     */
    private List<Repo> parseSearch(String response) throws JSONException {
        JSONObject responseJson = new JSONObject(response);
        List<Repo> repos = new ArrayList<>();
        JSONArray items = responseJson.getJSONArray("items");
        for (int i = 0; i < items.length(); i++) {
            JSONObject repoJson = items.getJSONObject(i);
            Repo repo = new Repo();
            repo.fullName = repoJson.getString("full_name");
            repo.url = repoJson.getString("html_url");
            repos.add(repo);
        }
        return repos;
    }
}
