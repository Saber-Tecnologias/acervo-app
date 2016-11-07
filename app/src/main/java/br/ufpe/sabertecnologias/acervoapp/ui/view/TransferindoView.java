package br.ufpe.sabertecnologias.acervoapp.ui.view;

import java.util.ArrayList;

import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import br.ufpe.sabertecnologias.acervoapp.R;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Item;
import br.ufpe.sabertecnologias.acervoapp.ui.basics.BasicSingleView;
import br.ufpe.sabertecnologias.acervoapp.ui.basics.ViewListener;
import br.ufpe.sabertecnologias.acervoapp.ui.view.adapters.AdapterTransferindo;

public class TransferindoView extends BasicSingleView {

	private RecyclerView rv;
	private LinearLayoutManager mLayoutManager;
	private ArrayList<Item> mDataset;
	private AdapterTransferindo mAdapter;
	private OnClickListener mListener;
	private ArrayList<ProgressBar> progressBarTransferindo;
	private ArrayList<TextView> textViewTransferindo;
	private RelativeLayout rl_aviso;
	private AppCompatButton bt_tentar_novamente;

	public TransferindoView(LayoutInflater inflater, ViewGroup container,
			ViewListener listener) {
		super(inflater, container, listener);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container) {
		View v = inflater.inflate(R.layout.transferindo_view, container, false);
		rv = (RecyclerView) v.findViewById(R.id.rv);
		mLayoutManager = new LinearLayoutManager(container.getContext(), LinearLayoutManager.VERTICAL, false);



		rv.setLayoutManager(mLayoutManager);


		rv.setHasFixedSize(true);
		rl_aviso = (RelativeLayout) v.findViewById(R.id.container_aviso);
		bt_tentar_novamente = (AppCompatButton) v.findViewById(R.id.bt_tentar_novamente);
		return v;
	}

	@Override
	protected void onViewCreated(View view) {}

	@Override
	public void setListenerOnViews(OnClickListener listener) {
		mListener = listener;
		bt_tentar_novamente.setOnClickListener(listener);
	}

	public void setAdapter(ArrayList<Item> mDataset, OnScrollListener scrollListener){
		this.mDataset = mDataset;
		progressBarTransferindo = new ArrayList<ProgressBar>();
		textViewTransferindo = new ArrayList<TextView>();
		this.mAdapter = new AdapterTransferindo(getContext(), mListener, mDataset, progressBarTransferindo, textViewTransferindo);
		rv.setAdapter(mAdapter);
		rv.addOnScrollListener(scrollListener);
	}

	public void notifyItemRangeInserted(int positionStart, int itemCount) {
		mAdapter.notifyItemRangeInserted(positionStart, itemCount);		
	}

	public void notifyItemRemoved(int position){
		if(mDataset.size() == 0){
			mAdapter.hideButtonClean();
			mAdapter.notifyItemChanged(2);
		}
		mAdapter.notifyItemRemoved(position+1);
	}

	public LinearLayoutManager getLayoutManager(){
		return mLayoutManager;
	}

	public void notifyItemInserted(int position) {
		mAdapter.notifyItemInserted(position+1);
		mAdapter.showButtonClean();
		mAdapter.notifyItemChanged(position+2);
	}

	public void setProgresso(int codigo, int progresso) {
		if(progressBarTransferindo.size() > 0){
			progressBarTransferindo.get(0).setProgress(progresso);
			textViewTransferindo.get(0).setText(progresso+"%");
		}
	}

	public void notifyItemUpdated(int position) {
		mAdapter.notifyItemChanged(position+1);
	}

	public void notifyDataSetChanged() {
		mAdapter.notifyDataSetChanged();
	}

	public void showAviso(){
/*
		Animation fadeIn = new AlphaAnimation(0, 1);
		fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
		fadeIn.setDuration(500);
		rl_aviso.startAnimation(fadeIn);*/


		rl_aviso.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

		final int targtetHeight = rl_aviso.getMeasuredHeight();

			rl_aviso.getLayoutParams().height = 0;

			rl_aviso.setVisibility(View.VISIBLE);

			Animation a = new Animation() {
				@Override
				protected void applyTransformation(float interpolatedTime,
												   Transformation t) {
					rl_aviso.getLayoutParams().height = interpolatedTime == 1 ? ViewGroup.LayoutParams.WRAP_CONTENT
							: (int) (targtetHeight * interpolatedTime);
					rl_aviso.requestLayout();
				}

				@Override
				public boolean willChangeBounds() {
					return true;
				}
			};
			a.setDuration((int) (500));
			rl_aviso.startAnimation(a);



		//rl_aviso.setVisibility(RelativeLayout.VISIBLE);


	}

	public void hideAviso(){

		final int initialHeight = rl_aviso.getMeasuredHeight();
		Animation a = new Animation() {
			@Override
			protected void applyTransformation(float interpolatedTime,
											   Transformation t) {
				if (interpolatedTime == 1) {
					rl_aviso.setVisibility(View.GONE);
				} else {
					rl_aviso.getLayoutParams().height = initialHeight
							- (int) (initialHeight * interpolatedTime);
					rl_aviso.requestLayout();
				}
			}

			@Override
			public boolean willChangeBounds() {
				return true;
			}
		};

		a.setDuration((int) (500));
		rl_aviso.startAnimation(a);

		//rl_aviso.setVisibility(RelativeLayout.GONE);
	}
}
