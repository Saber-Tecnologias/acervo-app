package br.ufpe.sabertecnologias.acervoapp.ui.view.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import br.ufpe.sabertecnologias.acervoapp.R;


public class AdapterDialogFragmentNovoGrupo extends Adapter<ViewHolder> {
	Context mContext;
	OnClickListener mClickListener;
	private final String[] cores = new String[]{
			"#b37a1d", "#b4523c", "#047779", "#1285ae",
			"#f9be6e", "#f69272", "#62ada2", "#c7d4ec"};
	private int selectedView;
	private View[] mViews;

	public AdapterDialogFragmentNovoGrupo(Context ctx, OnClickListener clickListener) {
		mContext = ctx;
		mClickListener = clickListener;
		mViews = new View[cores.length];
		selectedView = 0;
	}

	@Override
	public int getItemCount() {
		return cores.length;
	}

	@Override
	public void onBindViewHolder(ViewHolder vh, int position) {
		if(vh instanceof VH){
			VH holder = (VH) vh;
			mViews[position] = holder.cor;
			GradientDrawable gd = (GradientDrawable)holder.cor.getBackground();
			gd.setColor(Color.parseColor(cores[position]));
			gd.setStroke(5, Color.TRANSPARENT);
			holder.cor.setTag(position);
			if(position == selectedView){
				selectCor(position);
			}
		}
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int type) {
		View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_dialog_novogrupo, parent,false);
		VH vh = new VH(itemView);
		return vh;
	}

	class VH extends ViewHolder{
		ImageView cor;

		public VH(View itemView) {
			super(itemView);
			cor = (ImageView) itemView.findViewById(R.id.iv_cor);
			cor.setOnClickListener(mClickListener);
		}
	}

	public String getCor(int i) {
		if(i == -1){
			return cores[selectedView];
		}
		return cores[i];
	}
	
	public void selectCor(int i) {
		GradientDrawable gb = (GradientDrawable)((ImageView)mViews[selectedView]).getBackground(); 
		gb.setStroke(5, Color.TRANSPARENT);
		GradientDrawable gb2 = (GradientDrawable)((ImageView)mViews[i]).getBackground();
		gb2.setStroke(5, Color.BLACK);
		selectedView = i;
	}
	
	public int getColorIndex(String color){
		int i = 0;
		for(String cor: cores){
			if(cor.equals(color)){
				return i;
			}
			++i;
		}
		return i;
	}
}
