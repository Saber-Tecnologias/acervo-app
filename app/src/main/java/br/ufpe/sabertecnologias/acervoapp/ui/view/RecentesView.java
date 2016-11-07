package br.ufpe.sabertecnologias.acervoapp.ui.view;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import br.ufpe.sabertecnologias.acervoapp.R;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Item;
import br.ufpe.sabertecnologias.acervoapp.ui.UIUtils;
import br.ufpe.sabertecnologias.acervoapp.ui.basics.BasicSingleView;
import br.ufpe.sabertecnologias.acervoapp.ui.basics.ViewListener;
import br.ufpe.sabertecnologias.acervoapp.ui.view.adapters.AdapterRecentes;

/**
 * Created by joaotrindade on 02/02/16.
 */
public class RecentesView extends BasicSingleView {
    private RecyclerView recyclerView;
    private ArrayList<Item> mDataset;
    private AdapterRecentes mAdapter;

    public RecentesView(LayoutInflater inflater, ViewGroup container, ViewListener listener) {
        super(inflater, container, listener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.recentes_view, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), UIUtils.isTablet(mContext) ? 2 : 1);
        recyclerView.setLayoutManager(layoutManager);
        return view;
    }

    @Override
    protected void onViewCreated(View view) {}

    public void setAdapter(Context context, ViewListener clickListener){
        mDataset = new ArrayList<Item>();
        mAdapter = new AdapterRecentes(context, mDataset, clickListener);
        recyclerView.setAdapter(mAdapter);
    }
    @Override
    public void setListenerOnViews(View.OnClickListener listener) {}

    public void notifyDatasetChanged(ArrayList<Item> itens) {
        if(itens != null) {
            mDataset.clear();
            mDataset.addAll(itens);
        }
        mAdapter.notifyDataSetChanged();
    }
}