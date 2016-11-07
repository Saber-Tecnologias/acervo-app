package br.ufpe.sabertecnologias.acervoapp.ui.view;

import android.content.res.Configuration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import br.ufpe.sabertecnologias.acervoapp.R;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Item;
import br.ufpe.sabertecnologias.acervoapp.ui.UIUtils;
import br.ufpe.sabertecnologias.acervoapp.ui.basics.BasicSingleView;
import br.ufpe.sabertecnologias.acervoapp.ui.basics.ViewListener;
import br.ufpe.sabertecnologias.acervoapp.ui.view.adapters.AdapterBusca;

/**
 * Created by jpttrindade on 21/11/15.
 */
public class BuscaMeuAcervoView extends BasicSingleView {



    private ArrayList<Item> mDataset;
    private RecyclerView rv;
    private TextView tv_vazia;
    private RecyclerView.LayoutManager mLayoutManager;

    private AdapterBusca mAdapter;
    private View.OnClickListener mListener;

    private ProgressBar progresso;

    private View rl_semconexao;
    private View rl_content;

    private Button bt_retry;
    private TextView tv_totalresultados;


    public BuscaMeuAcervoView(LayoutInflater inflater, ViewGroup container, ViewListener listener) {
        super(inflater, container, listener);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container) {
        View v = inflater.inflate(R.layout.busca_meu_acervo_view, container, false);
        rl_content = v.findViewById(R.id.rl_content);
        rl_semconexao =  v.findViewById(R.id.rl_conexao);

        bt_retry = (Button) v.findViewById(R.id.bt_retry);

        tv_totalresultados = (TextView) v.findViewById(R.id.tv_totalresultados);
        rv = (RecyclerView) v.findViewById(R.id.rv_acervo_remoto);
        progresso = (ProgressBar) v.findViewById(R.id.progresso);
        tv_vazia = (TextView) v.findViewById(R.id.tv_vazia);
        rv.setHasFixedSize(true);
        //mLayoutManager = new LinearLayoutManager(container.getContext());


        setLayoutManager();
        return v;
    }



    public void setLayoutManager() {


//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (UIUtils.isTablet(mContext)) {

                switch (mContext.getResources().getConfiguration().orientation) {
                    case Configuration.ORIENTATION_PORTRAIT:
                        mLayoutManager = new GridLayoutManager(mContext, 1);

                        break;
                    case Configuration.ORIENTATION_LANDSCAPE:
                        mLayoutManager = new GridLayoutManager(mContext, 2);

                        break;
                }
            } else {
                mLayoutManager = new GridLayoutManager(mContext, 1);
            }
//        }else {
//            mLayoutManager =  new LinearLayoutManager(mContext);
//        }


        rv.setLayoutManager(mLayoutManager);

       /* RecyclerView.ItemAnimator animator = rv.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }*/
    }


    @Override
    protected void onViewCreated(View view) {

    }


    @Override
    public void setListenerOnViews(View.OnClickListener listener) {
        mListener = listener;

    }

    public void setAdapter(ArrayList<Item> mDataset, RecyclerView.OnScrollListener scrollListener) {
        this.mDataset = mDataset;
        this.mAdapter = new AdapterBusca(getContext(), mListener , mDataset);

        mAdapter.setHasStableIds(true);

        rv.setAdapter(mAdapter);
        rv.addOnScrollListener(scrollListener);

        bt_retry.setOnClickListener(mListener);

    }

    public void notifyItemRangeInserted(int positionStart, int itemCount) {
        mAdapter.notifyItemRangeInserted(positionStart, itemCount);
    }


    public void notifyItemRemoved(int position){
        mAdapter.notifyItemRemoved(position);
    }

    public void notifyDataSetChanged() {
        mAdapter.notifyDataSetChanged();
        hiddeProgress();
        if(mDataset.isEmpty()){
            tv_vazia.setVisibility(View.VISIBLE);
        }else {
            tv_vazia.setVisibility(View.GONE);

        }

        if(mDataset.size() == 1){
            tv_totalresultados.setText("Resultado da busca: " + mDataset.size() + " item encontrado");
        } else if(mDataset.size()>1) {
            tv_totalresultados.setText("Resultado da busca: " + mDataset.size() + " itens encontrados");
        }else {
            tv_totalresultados.setText("");
        }

    }

    public void showProgress() {
        progresso.setVisibility(View.VISIBLE);
        tv_vazia.setVisibility(View.GONE);
    }

    public void hiddeProgress() {
        progresso.setVisibility(View.GONE);
    }

    public RecyclerView.LayoutManager getLayoutManager(){
        return mLayoutManager;
    }

    public void notifyItemChanged(int position) {
        mAdapter.notifyItemChanged(position);
    }
}
