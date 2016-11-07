package br.ufpe.sabertecnologias.acervoapp.ui.view;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.ufpe.sabertecnologias.acervoapp.R;
import br.ufpe.sabertecnologias.acervoapp.ui.controllers.ButtonDoMenuController;
import br.ufpe.sabertecnologias.acervoapp.ui.basics.BasicSingleView;
import br.ufpe.sabertecnologias.acervoapp.ui.basics.ViewListener;

public class ButtonDoMenuView extends BasicSingleView {

    private FloatingActionButton fabRoot;
    private ButtonDoMenuController.CONTEXTO contexto;

    public ButtonDoMenuView(LayoutInflater inflater, ViewGroup container, ViewListener listener) {
        super(inflater, container, listener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.button_root_do_menu, container, false);
        fabRoot = (FloatingActionButton) view.findViewById(R.id.floating_root_do_menu);
        fabRoot.setColorFilter(Color.WHITE);
        return view;
    }

    /*It make root button been invisible and not accessible to click. Should be called when should not present the button on view or come to present again.
    *
    *
    * */
    public void setButtonRootVisibility(int visibility){

        switch (visibility){
            case View.INVISIBLE:
            case View.GONE:
                fabRoot.hide();
                break;
            case View.VISIBLE:
                fabRoot.show();
        }
    }

    @Override
    protected void onViewCreated(View view) {}

    @Override
    public void setListenerOnViews(View.OnClickListener listener) {
        fabRoot.setOnClickListener(listener);
    }

    public void setContexto(ButtonDoMenuController.CONTEXTO contexto) {
        this.contexto = contexto;
        switch (contexto){
            case MeuAcervo:
                fabRoot.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#8dbf67")));
                break;
            case AcervoRemoto:
                 fabRoot.setImageResource(R.drawable.ic_filter_variant_white);
                fabRoot.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#005aaa")));
                break;
        }
    }
}
