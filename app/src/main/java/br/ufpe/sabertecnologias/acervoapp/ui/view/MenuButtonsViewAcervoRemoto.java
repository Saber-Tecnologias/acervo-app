package br.ufpe.sabertecnologias.acervoapp.ui.view;

import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import br.ufpe.sabertecnologias.acervoapp.R;
import br.ufpe.sabertecnologias.acervoapp.ui.controllers.TagsController;
import br.ufpe.sabertecnologias.acervoapp.ui.basics.ViewListener;
import jpttrindade.widget.tagview.Tag;

public class MenuButtonsViewAcervoRemoto extends MenuButtonsView {
    private FloatingActionButton fabRoot, fabVideos, fabImagens, fabAudios, fabTextos;
    private Animation animateOpen, animateClose, rotateForward, rotateBackward;
    private TextView txtViewTextos, txtViewAudios, txtViewVideos, txtViewImagens;
    private View view;
    private FloatingActionButton viewSelecionada;

    public MenuButtonsViewAcervoRemoto(LayoutInflater inflater, ViewGroup container, ViewListener listener) {
        super(inflater, container, listener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container) {
        view = inflater.inflate(R.layout.menu_button_list_acervo_remoto, container, false);
        view.setClickable(true);

        fabRoot = (FloatingActionButton) view.findViewById(R.id.floating_fling_root_item);
        fabAudios = (FloatingActionButton) view.findViewById(R.id.floating_fling_audios_item);
        fabTextos = (FloatingActionButton) view.findViewById(R.id.floating_fling_textos_item);

        fabVideos = (FloatingActionButton) view.findViewById(R.id.floating_fling_videos_item);
        fabImagens = (FloatingActionButton) view.findViewById(R.id.floating_fling_imagens_item);

        txtViewTextos = (TextView) view.findViewById(R.id.txt_view_menu_textos);
        txtViewAudios = (TextView) view.findViewById(R.id.txt_view_menu_audios);
        txtViewVideos = (TextView) view.findViewById(R.id.txt_view_menu_videos);
        txtViewImagens = (TextView) view.findViewById(R.id.txt_view_menu_imagens);

        animateOpen = AnimationUtils.loadAnimation(mContext, R.anim.fab_open);
        animateClose = AnimationUtils.loadAnimation(mContext, R.anim.fab_close);

        rotateForward = AnimationUtils.loadAnimation(mContext, R.anim.rotate_forward);
        rotateBackward = AnimationUtils.loadAnimation(mContext, R.anim.rotate_backward);

        fabRoot.setColorFilter(Color.WHITE);

        viewSelecionada = fabRoot;

        createTags();
        abrirBotoes();
        return view;
    }

    private void defineTextColorTextView(int txtViewID, boolean selectValue){
        if(selectValue)
            ((TextView) view.findViewById(txtViewID)).setTextColor(Color.parseColor("#70000000"));
        else
            ((TextView) view.findViewById(txtViewID)).setTextColor(Color.WHITE);
    }

    private void defineSelectedOrNot(int viewId, boolean selectValue){
        switch (viewId){
            case R.id.floating_fling_textos_item:
            case R.id.txt_view_menu_textos:
                (view.findViewById(R.id.container_textos_itens)).setSelected(selectValue);
                defineTextColorTextView(R.id.txt_view_menu_textos, selectValue);
                break;

            case R.id.floating_fling_audios_item:
            case R.id.txt_view_menu_audios:
                (view.findViewById(R.id.container_audios_itens)).setSelected(selectValue);
                defineTextColorTextView(R.id.txt_view_menu_audios, selectValue);
                break;

            case R.id.floating_fling_videos_item:
            case R.id.txt_view_menu_videos:
                (view.findViewById(R.id.container_videos_itens)).setSelected(selectValue);
                defineTextColorTextView(R.id.txt_view_menu_videos, selectValue);
                break;
        }
    }

    public void abrirBotoes(){
        fabRoot.startAnimation(rotateForward);
        animateElements(animateOpen);
        setVisibility(View.VISIBLE);
    }

    public void fecharBotoes(){
        fabRoot.startAnimation(rotateBackward);
        animateElements(animateClose);
        setVisibility(View.GONE);
    }

    private void setVisibility(int visibility){
        fabAudios.setVisibility(visibility);
        fabTextos.setVisibility(visibility);
        fabVideos.setVisibility(visibility);
        fabImagens.setVisibility(visibility);

        txtViewTextos.setVisibility(visibility);
        txtViewAudios.setVisibility(visibility);
        txtViewVideos.setVisibility(visibility);
        txtViewImagens.setVisibility(visibility);
    }

    private void animateElements(Animation animation){
        fabAudios.startAnimation(animation);
        fabTextos.startAnimation(animation);
        fabVideos.startAnimation(animation);
        fabImagens.startAnimation(animation);

        txtViewTextos.startAnimation(animation);
        txtViewAudios.startAnimation(animation);
        txtViewVideos.startAnimation(animation);
        txtViewImagens.startAnimation(animation);
    }

    @Override
    public void setListenerOnViews(View.OnClickListener listener) {
        fabRoot.setOnClickListener(listener);
        fabImagens.setOnClickListener(listener);
        fabVideos.setOnClickListener(listener);
        fabAudios.setOnClickListener(listener);
        fabTextos.setOnClickListener(listener);

        txtViewTextos.setOnClickListener(listener);
        txtViewAudios.setOnClickListener(listener);
        txtViewVideos.setOnClickListener(listener);
        txtViewImagens.setOnClickListener(listener);
        view.setOnClickListener(listener);
    }

    public void createTags(){
        Tag t;
        t = new Tag(txtViewAudios.getText().toString(), Color.parseColor("#f2a32c"), R.drawable.ic_filtro_audio_white, false);
        t.setID(ID_AUDIO);
        t.setType(TagsController.TYPE_TIPO);
        fabAudios.setTag(t);
        txtViewAudios.setTag(t);

        t = new Tag(txtViewTextos.getText().toString(), Color.parseColor("#f2a32c"), R.drawable.ic_filtro_texto_white, false);
        t.setID(ID_TEXTO);
        t.setType(TagsController.TYPE_TIPO);
        fabTextos.setTag(t);
        txtViewTextos.setTag(t);

        t = new Tag(txtViewVideos.getText().toString(), Color.parseColor("#f2a32c"), R.drawable.ic_filtro_video_white, false);
        t.setID(ID_VIDEO);
        t.setType(TagsController.TYPE_TIPO);
        fabVideos.setTag(t);
        txtViewVideos.setTag(t);

        t = new Tag(txtViewImagens.getText().toString(), Color.parseColor("#f2a32c"), R.drawable.ic_filtro_image_white, false);
        t.setID(ID_IMAGEM);
        t.setType(TagsController.TYPE_TIPO);
        fabImagens.setTag(t);
        txtViewImagens.setTag(t);
    }

    public Tag selectFiltro(int id) {
        Tag old_tag = (Tag)viewSelecionada.getTag();
        switch (id) {
            case ID_TEXTO:
                viewSelecionada = fabTextos;
                fabTextos.setSelected(true);
                txtViewTextos.setSelected(true);
                fabAudios.setSelected(false);
                txtViewAudios.setSelected(false);
                fabVideos.setSelected(false);
                txtViewVideos.setSelected(false);
                fabImagens.setSelected(false);
                txtViewImagens.setSelected(false);
                break;
            case ID_AUDIO:
                viewSelecionada = fabAudios;
                fabTextos.setSelected(false);
                txtViewTextos.setSelected(false);
                fabAudios.setSelected(true);
                txtViewAudios.setSelected(true);
                fabVideos.setSelected(false);
                txtViewVideos.setSelected(false);
                fabImagens.setSelected(false);
                txtViewImagens.setSelected(false);
                break;
            case ID_VIDEO:
                viewSelecionada = fabVideos;
                fabTextos.setSelected(false);
                txtViewTextos.setSelected(false);
                fabAudios.setSelected(false);
                txtViewAudios.setSelected(false);
                fabVideos.setSelected(true);
                txtViewVideos.setSelected(true);
                fabImagens.setSelected(false);
                txtViewImagens.setSelected(false);
                break;
            case ID_IMAGEM:
                viewSelecionada = fabImagens;
                fabTextos.setSelected(false);
                txtViewTextos.setSelected(false);
                fabAudios.setSelected(false);
                txtViewAudios.setSelected(false);
                fabVideos.setSelected(false);
                txtViewVideos.setSelected(false);
                fabImagens.setSelected(true);
                txtViewImagens.setSelected(true);
                break;
            default:
                //nada
        }
        return old_tag;
    }

    public Object deselectFiltro(int id) {
        Tag t;
        switch (id) {
            case ID_TEXTO:
                fabTextos.setSelected(false);
                txtViewTextos.setSelected(false);
                t = (Tag) fabTextos.getTag();
                break;
            case ID_AUDIO:
                fabAudios.setSelected(false);
                txtViewAudios.setSelected(false);
                t = (Tag) fabAudios.getTag();

                break;
            case ID_VIDEO:
                fabVideos.setSelected(false);
                txtViewVideos.setSelected(false);
                t = (Tag) fabVideos.getTag();

                break;
            case ID_IMAGEM:
                fabImagens.setSelected(false);
                txtViewImagens.setSelected(false);
                t = (Tag) fabImagens.getTag();
                break;
            default:
                t = null;
        }
        viewSelecionada = fabRoot;
        return t;
    }

    @Override
    protected void onViewCreated(View view) {}
}
