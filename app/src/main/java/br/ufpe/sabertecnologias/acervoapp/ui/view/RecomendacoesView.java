package br.ufpe.sabertecnologias.acervoapp.ui.view;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import br.ufpe.sabertecnologias.acervoapp.R;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Item;
import br.ufpe.sabertecnologias.acervoapp.modelo.negocio.NegocioRecomendacao;
import br.ufpe.sabertecnologias.acervoapp.ui.basics.BasicSingleView;
import br.ufpe.sabertecnologias.acervoapp.ui.basics.ViewListener;
import br.ufpe.sabertecnologias.acervoapp.ui.view.adapters.AdapterRecomendacao;
import jpttrindade.widget.tagview.Tag;

/**
 * Created by jpttrindade on 22/11/15.
 */
public class RecomendacoesView extends BasicSingleView{

    private TextView tv_label_titulo1,tv_label_titulo2,tv_label_titulo3;

    ProgressBar pg1, pg2, pg3;

    private RecyclerView rv1, rv2, rv3;
    private LinearLayoutManager lm1;
    private LinearLayoutManager lm2;
    private LinearLayoutManager lm3;
    private ArrayList<Item> listTextos, listAudios, listVideos ;
    private AdapterRecomendacao adp1,adp2,adp3;

    private View v1, v2, v3;

    public RecomendacoesView(LayoutInflater inflater, ViewGroup container, ViewListener listener) {
        super(inflater, container, listener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container) {
        View v = inflater.inflate(R.layout.recomendacoes_view,container,false);
        listTextos = new ArrayList<Item>();
        listAudios = new ArrayList<Item>();
        listVideos = new ArrayList<Item>();

        tv_label_titulo1 = (TextView) v.findViewById(R.id.label_titulo1);
        tv_label_titulo2 = (TextView) v.findViewById(R.id.label_titulo2);
        tv_label_titulo3 = (TextView) v.findViewById(R.id.label_titulo3);

        rv1 = (RecyclerView) v.findViewById(R.id.rv1);
        rv2 = (RecyclerView) v.findViewById(R.id.rv2);
        rv3 = (RecyclerView) v.findViewById(R.id.rv3);

        pg1 = (ProgressBar) v.findViewById(R.id.progress1);
        pg2 = (ProgressBar) v.findViewById(R.id.progress2);
        pg3 = (ProgressBar) v.findViewById(R.id.progress3);

        v1 = v.findViewById(R.id.v1);
        v2 = v.findViewById(R.id.v2);
        v3 = v.findViewById(R.id.v3);
        setLayoutManager();
        return v;
    }

    private void setLayoutManager() {
        lm1 = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        rv1.setLayoutManager(lm1);
        lm2 = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        rv2.setLayoutManager(lm2);
        lm3 = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        rv3.setLayoutManager(lm3);
    }

    @Override
    protected void onViewCreated(View view) {}

    @Override
    public void setListenerOnViews(View.OnClickListener listener) {
        v1.setOnClickListener(listener);
        v2.setOnClickListener(listener);
        v3.setOnClickListener(listener);
    }

    public void setAdapters(int tipo,ArrayList<Item> itens, ViewListener listener){
        switch (tipo){
            case NegocioRecomendacao.TYPE_TEXTO:
                listTextos = itens;
                adp1 = new AdapterRecomendacao(mContext,listener, listTextos);
                rv1.setAdapter(adp1);
                pg1.setVisibility(View.GONE);

                break;
            case NegocioRecomendacao.TYPE_AUDIO:
                listAudios = itens;
                adp2 = new AdapterRecomendacao(mContext,listener, listAudios);
                rv2.setAdapter(adp2);
                pg2.setVisibility(View.GONE);
                break;
            case  NegocioRecomendacao.TYPE_VIDEO:
                listVideos = itens;
                adp3 = new AdapterRecomendacao(mContext,listener, listVideos);
                rv3.setAdapter(adp3);
                pg3.setVisibility(View.GONE);

                break;
        }
    }


    public void setAdapters(ArrayList<Item> itensTextos, ArrayList<Item> itensAudios,ArrayList<Item> itensVideos, ViewListener listener){

    }

    public void setTagLabels(Tag t1, Tag t2, Tag t3){

        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        int padding = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, dm);
        if(t1 != null) {

            tv_label_titulo1.setText(t1.text+"s");
            tv_label_titulo1.setTag(t1);
            tv_label_titulo1.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_filtro_texto_black, 0);
            tv_label_titulo1.setCompoundDrawablePadding(padding);

            tv_label_titulo2.setText(t2.text + "s");
            tv_label_titulo2.setTag(t2);
            tv_label_titulo2.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_filtro_audio_black, 0);
            tv_label_titulo2.setCompoundDrawablePadding(padding);

            tv_label_titulo3.setText(t3.text + "s");
            tv_label_titulo3.setTag(t3);
            tv_label_titulo3.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_filtro_video_black, 0);
            tv_label_titulo3.setCompoundDrawablePadding(padding);

            v1.setTag(t1);
            v2.setTag(t2);
            v3.setTag(t3);
        }
    }

    public void finishLoad(){
        pg1.setVisibility(View.GONE);
        pg2.setVisibility(View.GONE);
        pg3.setVisibility(View.GONE);

    }

    public void startLoad(){
        pg1.setVisibility(View.VISIBLE);
        pg2.setVisibility(View.VISIBLE);
        pg3.setVisibility(View.VISIBLE);
    }

    public void notifyDatasetChanged() {

        if(adp1 != null) adp1.notifyDataSetChanged();
        if(adp2 != null) adp2.notifyDataSetChanged();
        if(adp3 != null) adp3.notifyDataSetChanged();
    }
}
