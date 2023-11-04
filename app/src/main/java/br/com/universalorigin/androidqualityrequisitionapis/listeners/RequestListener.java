package br.com.universalorigin.androidqualityrequisitionapis.listeners;

import android.os.AsyncTask;
import android.view.View;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import br.com.universalorigin.androidqualityrequisitionapis.MainActivity;
import br.com.universalorigin.androidqualityrequisitionapis.entitys.EntityPost;
import br.com.universalorigin.androidqualityrequisitionapis.enums.TypeRequest;
import br.com.universalorigin.androidqualityrequisitionapis.interfaces.MainInterface;
import br.com.universalorigin.androidqualityrequisitionapis.interfaces.RemoteService;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RequestListener implements View.OnClickListener {

    private final String URL_BASE = "https://jsonplaceholder.typicode.com/";
    private final TypeRequest typeRequest;
    private final MainInterface mainInterface;

    private Date dataTimeInicial;
    private Date dataTimeFinal;

    public RequestListener(TypeRequest typeRequest, MainInterface mainInterface) {
        this.typeRequest = typeRequest;
        this.mainInterface = mainInterface;
    }

    private long getTempoRequisicao() {
        // Calcule a diferença em milissegundos
        long diferencaEmMilissegundos = dataTimeFinal.getTime() - dataTimeInicial.getTime();

        long diferencaEmSegundos = diferencaEmMilissegundos / 1000;
        return diferencaEmMilissegundos;
    }

    private final void requestFromOkHttp() {
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(URL_BASE + "posts")
                .build();

        OkHttpClient client = new OkHttpClient();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                mainInterface.showLoading(false);
            }

            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) {
                if (response.isSuccessful()) {
                    dataTimeFinal = Calendar.getInstance().getTime();

                    try {
                        String jsonData = response.body().source().readString(StandardCharsets.UTF_8);
                        ArrayList<EntityPost> entityPosts = new ArrayList<>();


                        JSONArray jsonArray = new JSONArray(jsonData);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            int id = jsonObject.getInt("id");
                            int idUser = jsonObject.getInt("userId");
                            String title = jsonObject.getString("title");
                            String body = jsonObject.getString("body");

                            EntityPost entityPost = new EntityPost(id, idUser, title, body);
                            entityPosts.add(entityPost);
                        }

                        mainInterface.showLoading(false);
                        mainInterface.onSucess(entityPosts, getTempoRequisicao());
                        // Agora, você tem a lista de EntityPost preenchida com os dados da API.
                    } catch (JSONException e) {
                        mainInterface.showLoading(false);
                        e.printStackTrace();
                        // Lidar com erros de parsing de JSON aqui
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    // Tratar resposta não bem-sucedida
                }
            }
        });
    }

    private final void requestFromVolley() {

        RequestQueue requestQueue = Volley.newRequestQueue(mainInterface.getContext());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL_BASE + "posts", null,
                response -> {
                    dataTimeFinal = Calendar.getInstance().getTime();

                    ArrayList<EntityPost> entityPosts = new ArrayList<>();

                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject postJson = response.getJSONObject(i);

                            int id = postJson.getInt("id");
                            int idUser = postJson.getInt("userId");
                            String title = postJson.getString("title");
                            String body = postJson.getString("body");

                            EntityPost entityPost = new EntityPost(id, idUser, title, body);
                            entityPosts.add(entityPost);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    mainInterface.showLoading(false);
                    mainInterface.onSucess(entityPosts, getTempoRequisicao());
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mainInterface.onFail(error);
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            String responseBody = new String(error.networkResponse.data);
                            int statusCode = error.networkResponse.statusCode;

                            // Verifique o statusCode e faça o tratamento adequado
                            if (statusCode == 404) {
                                // Trate um erro 404, por exemplo.
                            } else {
                                // Lidar com outros erros, como parsing de JSON inválido
                            }
                        }
                        mainInterface.showLoading(false);
                    }
                });


        requestQueue.add(jsonArrayRequest);
    }

    private final void requestFromRetrofit2() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL_BASE) // URL base da API
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RemoteService apiService = retrofit.create(RemoteService.class);
        Call<ArrayList<EntityPost>> call = apiService.getPost();

        call.enqueue(new Callback<ArrayList<EntityPost>>() {
            @Override
            public void onResponse(Call<ArrayList<EntityPost>> call, Response<ArrayList<EntityPost>> response) {
                dataTimeFinal = Calendar.getInstance().getTime();

                if (response.isSuccessful() && response.body() != null) {
                    mainInterface.showLoading(false);
                    mainInterface.onSucess(response.body(), getTempoRequisicao());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<EntityPost>> call, Throwable t) {
                // Lidar com falha na requisição
                mainInterface.showLoading(false);
                mainInterface.onFail(t);
            }
        });
    }

    private final void requestFromHttpUrlConnection() {
        AsyncTask.execute(()->{
            ArrayList<EntityPost> entityPosts = new ArrayList<>();

            try{
                URL url = new URL(URL_BASE+"posts");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                // Configurar a requisição GET
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json");

                // Ler a resposta
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                    dataTimeFinal = Calendar.getInstance().getTime();

                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    // Processar a resposta JSON
                    JSONArray jsonArray = new JSONArray(response.toString());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        int id = jsonObject.getInt("id");
                        int idUser = jsonObject.getInt("userId");
                        String title = jsonObject.getString("title");
                        String body = jsonObject.getString("body");
                        EntityPost entityPost = new EntityPost(id, idUser, title, body);
                        entityPosts.add(entityPost);
                    }
                } else {
                    // Lidar com resposta não bem-sucedida
                    throw new IOException("Requisição falhou com código de resposta: " + responseCode);
                }

                connection.disconnect();

                mainInterface.showLoading(false);
                mainInterface.onSucess(entityPosts, getTempoRequisicao());
            }
            catch (Exception s){
                mainInterface.showLoading(false);
                s.printStackTrace();
            }
        });
    }

    private final void request() {
        mainInterface.showLoading(true);
        dataTimeInicial = Calendar.getInstance().getTime();

        switch (typeRequest) {
            case OKHTTP: {
                requestFromOkHttp();
                break;
            }
            case VOLLEY: {
                requestFromVolley();
                break;
            }
            case RETROFIT2: {
                requestFromRetrofit2();
                break;
            }
            case HTTP_URL_CONNECTION: {
                requestFromHttpUrlConnection();
                break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        request();
    }
}
