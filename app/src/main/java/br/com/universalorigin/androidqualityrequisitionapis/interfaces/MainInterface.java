package br.com.universalorigin.androidqualityrequisitionapis.interfaces;

import android.content.Context;

import java.util.Date;

public interface MainInterface<T> {

    void showLoading(boolean show);

    Context getContext();
    void onSucess(T response, long tempoRequisicao);
    void onFail(Throwable throwable);
    void onNotInternetConnection();
}
