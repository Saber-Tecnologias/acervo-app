package br.ufpe.sabertecnologias.acervoapp.ui.autenticacao;

import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import br.ufpe.sabertecnologias.acervoapp.R;
import br.ufpe.sabertecnologias.acervoapp.ui.basics.BasicSingleView;
import br.ufpe.sabertecnologias.acervoapp.ui.basics.ViewListener;


/**
 * Created by joaotrindade on 24/10/16.
 */

public class AutenticacaoView extends BasicSingleView {

    private ProgressBar progress;
    private Button bt_login;
    private String username;
    private String password;
    private TextInputLayout til_username;
    private TextInputLayout til_password;

    public AutenticacaoView(LayoutInflater inflater, ViewGroup container, ViewListener listener) {
        super(inflater, container, listener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_autenticacao, container, false);
        progress = (ProgressBar) view.findViewById(R.id.progress);

        til_username = (TextInputLayout) view.findViewById(R.id.til_username);
        til_password = (TextInputLayout) view.findViewById(R.id.til_password);

        bt_login = (Button) view.findViewById(R.id.bt_login);
        return view;
    }

    @Override
    protected void onViewCreated(View view) {

        til_username.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                username = s.toString();
            }
        });

        til_password.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                password = s.toString();
            }
        });

    }


    @Override
    public void setListenerOnViews(View.OnClickListener listener) {
        bt_login.setOnClickListener(listener);
    }

    public void showProgress() {

    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void onFail() {
        Toast.makeText(mContext, "Algo inesperado aconteceu, tente novamente!", Toast.LENGTH_LONG).show();
    }
}

