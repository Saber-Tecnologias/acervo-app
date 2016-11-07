package br.ufpe.sabertecnologias.acervoapp.ui.controllers;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import br.ufpe.sabertecnologias.acervoapp.R;
import br.ufpe.sabertecnologias.acervoapp.modelo.AsyncExecutor;
import br.ufpe.sabertecnologias.acervoapp.modelo.ExecutorListener;
import br.ufpe.sabertecnologias.acervoapp.modelo.Facade;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Grupo;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.LogGrupoAcaoEnum;
import br.ufpe.sabertecnologias.acervoapp.ui.UIUtils;
import br.ufpe.sabertecnologias.acervoapp.ui.basics.ViewListener;
import br.ufpe.sabertecnologias.acervoapp.ui.callbacks.ButtonDoMenuCallback;
import br.ufpe.sabertecnologias.acervoapp.ui.view.ButtonDoMenuView;
import br.ufpe.sabertecnologias.acervoapp.ui.view.dialogs.DialogFragmentNovoGrupo;

public class ButtonDoMenuController extends Fragment {
    public static final String TAG = "buttonDoMenuController";

    private ButtonDoMenuView mView;
    private ViewListener mListener;
    private ButtonDoMenuCallback callback;
    private Facade facade;
    private CONTEXTO contexto;

    public static enum CONTEXTO {MeuAcervo, AcervoRemoto};


    public ButtonDoMenuController init(CONTEXTO ctx){
        contexto = ctx;
        setRetainInstance(true);
        return this;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        facade = (Facade) context.getApplicationContext();
        callback = (ButtonDoMenuCallback) getActivity();
    }

    /*It make root button been invisible and not accessible to click. Should be called when should not present the button on view or come to present again.
    *
    * */
    public void setButtonRootVisibility(int visibility){
        mView.setButtonRootVisibility(visibility);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mView == null) {
            mListener = new ViewListener() {

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {}

                @Override
                public void onClick(View view) {
                    switch (view.getId()){
                        case R.id.bt_criargrupo:
                            Grupo g;
                            g = (Grupo) view.getTag();
                            ExecutorListener.ExecutorResult executorResult = new ExecutorListener.ExecutorResult() {

                                @Override
                                public void onExecutorResult(Object obj) {
                                    if(obj instanceof Grupo) {
                                        Grupo g = (Grupo) obj;
                                        if(g.getID() != -2){
                                            AsyncExecutor executor = new AsyncExecutor(null);
                                            facade.empilhaLogGrupo(executor, LogGrupoAcaoEnum.ADICIONAR, g.getNome(), g.getUUID_grupo(), g.getCorHexa());
                                        } else{
                                            //Grupo já existente!
                                            Snackbar
                                                    .make(mView.getView(), "Já existe um grupo com este nome.", Snackbar.LENGTH_LONG)
                                                    .show();
                                        }
                                    }
                                }
                            };
                            addGrupo(g, executorResult);
                            break;
                        case R.id.floating_root_do_menu:
                            if(UIUtils.isTablet(getActivity())){
                                criarNovoGrupoDialog();
                            }else{
                                callback.adicionarMenuDeBotoes();
                            }
                            break;
                    }
                }
            };

            mView = new ButtonDoMenuView(inflater, container, mListener);
            mView.setContexto(contexto);
        }
        return mView.getView();
    }

    private void addGrupo(Grupo g, ExecutorListener.ExecutorResult executorResult) {
        AsyncExecutor executor = new AsyncExecutor(executorResult);
        facade.addGrupo(g, executor);
    }

    public void criarNovoGrupoDialog() {
        DialogFragmentNovoGrupo newFragment = new DialogFragmentNovoGrupo();
        newFragment.init(getActivity(), mListener);
        newFragment.show(getFragmentManager(), "op");
    }
}
