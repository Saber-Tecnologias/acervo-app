package br.ufpe.sabertecnologias.acervoapp.ui.controllers;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import br.ufpe.sabertecnologias.acervoapp.R;
import br.ufpe.sabertecnologias.acervoapp.modelo.AsyncExecutor;
import br.ufpe.sabertecnologias.acervoapp.modelo.Facade;
import br.ufpe.sabertecnologias.acervoapp.modelo.negocio.sync.SyncService;
import br.ufpe.sabertecnologias.acervoapp.ui.UIUtils;
import br.ufpe.sabertecnologias.acervoapp.ui.basics.ViewListener;
import br.ufpe.sabertecnologias.acervoapp.ui.callbacks.NavMenuCallback;
import br.ufpe.sabertecnologias.acervoapp.ui.phone.ActivityAcervoRemoto;
import br.ufpe.sabertecnologias.acervoapp.ui.phone.ActivityTransferencias;
import br.ufpe.sabertecnologias.acervoapp.ui.tablet.MultiPaneActivityAcervoRemoto;
import br.ufpe.sabertecnologias.acervoapp.ui.view.DrawerMenuView;
import br.ufpe.sabertecnologias.acervoapp.util.DebugLog;
import br.saber.downloadservice.Downloader;

public class DrawerMenuController extends Fragment  implements ServiceConnection{
	public static final String TAG = "drawerMenuController";
	public static final int MEUACERVO =0;
	public static final int ACERVOREMOTO =1;
	public static final int TRANSFERENCIAS=2;

	private DrawerMenuView mView;
	private ViewListener listener;

	private int selectedItem;
	private NavMenuCallback callback;

	private Facade facade;
	private final Messenger refreshCallback = new Messenger(new RefreshCallbackHandler());
	private SyncService mService;
	private boolean binded;

	String suporteDialogText;
	private DownloadStatusReceiver downloadStatusReceiver;
	private Context mContext;

	@Override
	public void onAttach(Context ctx) {
		super.onAttach(ctx);
		callback = (NavMenuCallback) getActivity();
		facade = (Facade) ctx.getApplicationContext();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	public DrawerMenuController init(Context ctx,int selectedItem){
		mContext = ctx;
		this.selectedItem = selectedItem;
		return this;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(mView == null){
			listener = new DrawerMenuListener();
			mView = new DrawerMenuView(inflater, container, listener);
			switch (selectedItem) {
			case ACERVOREMOTO:
				mView.selectItem(R.id.tv_acervoremoto);
				selectAcervoRemoto();
				break;
			case MEUACERVO:
				mView.selectItem(R.id.tv_meuacervo);
				selectMeuAcervo();
				break;
			case TRANSFERENCIAS:
				mView.selectItem(R.id.tv_transferencias);
				selectTransferencias();
				break;
			default:
				break;
			}
		} else {
			if(mView.isRefreshing){
				refresh();
			}
		}

		if(mView.isSmartphone()){
			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction(Downloader.DOWNLOAD_CANCELED_ACTION);
			intentFilter.addAction(Downloader.DOWNLOAD_TERMINATED_ACTION);
			downloadStatusReceiver = new DownloadStatusReceiver();
			mContext.getApplicationContext().registerReceiver(downloadStatusReceiver, intentFilter);
		}

		return mView.getView();
	}


	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if(mView.isRefreshing) {
			mView.stopRefresh(false);
		}

		if(mView.isSmartphone()){
			mContext.getApplicationContext().unregisterReceiver(downloadStatusReceiver);
		}
	}



	public boolean selectMeuAcervo() {
		callback.onSelectNavItem(MEUACERVO);
		return !mView.isSelected(R.id.tv_meuacervo);

	}

	public boolean selectAcervoRemoto() {
		callback.onSelectNavItem(ACERVOREMOTO);
		return !mView.isSelected(R.id.tv_acervoremoto);
	}


	public boolean selectTransferencias() {
		callback.onSelectNavItem(TRANSFERENCIAS);
		return !mView.isSelected(R.id.tv_transferencias);
	}

	private void refresh() {
		mView.refresh();
		facade.sync(true);
		facade.sync(refreshCallback, this);
	}


	private void cancelarRefresh() {
       if( mService.cancelSync()) {
           mService.stopSelf();
           facade.finishSync(DrawerMenuController.this);
       }
        mView.stopRefresh(true);
    }


	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		DebugLog.d(this, " onService Connected");
		SyncService.SyncServerBinder mBinder = ((SyncService.SyncServerBinder)service);
		mService = mBinder.getService();
		if(!mService.isRunning()) {
			mService.refresh();
		} else {
			if(mService.isFinished()){
				mService.stopSelf();
				mView.stopRefresh(true);
			}
		}
		binded = true;
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		DebugLog.d(this," onService Desconnected");
		mService = null;
	}


	class RefreshCallbackHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case SyncService.REFRESH_OK:
					DebugLog.d(this, "REFRESH RESULT OK!");
					mService.stopSelf();
					facade.finishSync(DrawerMenuController.this);
					DrawerMenuController.this.mView.stopRefresh(true);

					break;
				case SyncService.REFRESH_ERROR:
					DebugLog.d(this, "REFRESH RESULT ERROR!");
					mService.stopSelf();
					facade.finishSync(DrawerMenuController.this);
					DrawerMenuController.this.mView.stopRefresh(true);
					break;
				default:
					//Nothing
			}
		}
	}


	class DrawerMenuListener extends ViewListener{

		@Override
		public void onClick(View v) {
			Intent intent = null;
			switch (v.getId()) {
			case R.id.tv_acervoremoto:
				if(selectAcervoRemoto()){
					intent = new Intent(getActivity(),
							UIUtils.isTablet(getActivity())
							? MultiPaneActivityAcervoRemoto.class
									: ActivityAcervoRemoto.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					startActivity(intent);
				}
				break;
			case R.id.tv_meuacervo:
				if(selectMeuAcervo()){
					getActivity().finish();
				}
				break;
			case R.id.tv_transferencias:
				if(selectTransferencias()){
					intent =  new Intent(getActivity(), ActivityTransferencias.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					startActivity(intent);
				}
				break;
			case R.id.tv_atualizar:
			case R.id.iv_sync:
				refresh();
				callback.onSelectNavItem(0);
				break;
			case R.id.tv_suporte:
				abrirSuporteDialog();
				callback.onSelectNavItem(0);
				break;
			case R.id.tv_tutorial:
				callback.mostrarTutorial();
				callback.onSelectNavItem(0);

				break;
			case R.id.tv_creditos:
				abrirCreditosDialog();
				callback.onSelectNavItem(0);
				break;
			case R.id.bt_close:
				cancelarRefresh();
				break;
			default:
				//Nothing
				break;
			}
		}



		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub

		}

	}

	private void abrirCreditosDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
		builder.setView(LayoutInflater.from(getContext()).inflate(R.layout.creditos_layout, null, false));
		builder.setCancelable(true);
		builder.setPositiveButton(R.string.creditos_dialog_bt_positive,null );
		builder.show();
	}


	public void abrirSuporteDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
		builder.setTitle(R.string.suporte_dialog_title);

		final View input = LayoutInflater.from(mView.getContext()).inflate(R.layout.dialog_support, null, false);
		builder.setView(input);
		suporteDialogText = ((AppCompatEditText) input.findViewById(R.id.et_input)).getText().toString();

		builder.setPositiveButton(R.string.suporte_dialog_bt_positive, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				suporteDialogText = ((AppCompatEditText) input.findViewById(R.id.et_input)).getText().toString();

				if (!TextUtils.isEmpty(suporteDialogText)) {
					AsyncExecutor executor = new AsyncExecutor(null);
					facade.empilhaLogFeedback(executor, suporteDialogText);
				} else {
					//Snackbar.make(mView.getView(), "Voce precisa preencher!", Snackbar.LENGTH_LONG).show();
				}
			}
		});
		builder.setNegativeButton(R.string.suporte_dialog_bt_negative, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		builder.show();
	}

	public void addTransferencia(){
		if(mView!=null)
			mView.addTranferencia();
	}

	public void finishTranferencia(){
		if(mView!=null)
			mView.finishTransferencia();
	}


	public void setTransferencias(int qtd_transferencias) {
		if(mView!=null)
			mView.setTransferencias(qtd_transferencias);
	}


	public class DownloadStatusReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if(action.equals(Downloader.DOWNLOAD_TERMINATED_ACTION)) {
				DebugLog.d(this, "DOWNLOAD TERMINADO MenuCOntroller");
				finishTranferencia();
			} else if(action.equals(Downloader.DOWNLOAD_CANCELED_ACTION)){
				DebugLog.d(this, "DOWNLOAD CANCELADO MenuCOntroller");
				finishTranferencia();
			}
		}
	}
}
