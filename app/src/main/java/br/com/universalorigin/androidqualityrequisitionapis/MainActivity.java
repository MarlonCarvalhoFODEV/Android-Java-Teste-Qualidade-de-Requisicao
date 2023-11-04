package br.com.universalorigin.androidqualityrequisitionapis;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

import br.com.universalorigin.androidqualityrequisitionapis.entitys.EntityPost;
import br.com.universalorigin.androidqualityrequisitionapis.enums.TypeRequest;
import br.com.universalorigin.androidqualityrequisitionapis.interfaces.MainInterface;
import br.com.universalorigin.androidqualityrequisitionapis.listeners.RequestListener;

public class MainActivity extends AppCompatActivity implements MainInterface<ArrayList<EntityPost>> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnRetrofit2 = findViewById(R.id.btn_retrofit2);
        btnRetrofit2.setOnClickListener(new RequestListener(TypeRequest.RETROFIT2, this));

        Button btnVolley = findViewById(R.id.btn_volley);
        btnVolley.setOnClickListener(new RequestListener(TypeRequest.VOLLEY, this));

        Button btnHttpUrlConnection = findViewById(R.id.btn_http_url_connection);
        btnHttpUrlConnection.setOnClickListener(new RequestListener(TypeRequest.HTTP_URL_CONNECTION, this));

        Button btnOkHttp = findViewById(R.id.btn_okhttp);
        btnOkHttp.setOnClickListener(new RequestListener(TypeRequest.OKHTTP, this));
    }

    @Override
    public final void showLoading(boolean show){
        runOnUiThread(()->{
            ProgressBar progressBar = findViewById(R.id.pb_loading_process);
            if(show)
                progressBar.setVisibility(View.VISIBLE);
            else
                progressBar.setVisibility(View.GONE);
        });
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void onSucess(ArrayList<EntityPost> response, long tempoRequisicao) {
        TextView textView = findViewById(R.id.tv_time_response);
        textView.setText(tempoRequisicao+" ms");
    }

    @Override
    public void onFail(Throwable throwable) {

    }

    @Override
    public void onNotInternetConnection() {

    }
}