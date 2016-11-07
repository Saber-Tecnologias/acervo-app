package br.ufpe.sabertecnologias.acervoapp.ui.autenticacao;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import br.ufpe.sabertecnologias.acervoapp.R;
import br.ufpe.sabertecnologias.acervoapp.modelo.Facade;
import br.ufpe.sabertecnologias.acervoapp.modelo.IExecutor;
import br.ufpe.sabertecnologias.acervoapp.util.ConnectionUtil;
import br.ufpe.sabertecnologias.acervoapp.util.DebugLog;

public class ActivityAutenticacao extends AppCompatActivity implements CallbackAutenticacao {

	private final String FRAG_TAG = "frag_auth_tag";
	private Facade facade;
	private Fragment webFrag;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		facade = (Facade)getApplication();

		setContentView(R.layout.activity_autenticacao);

		if(facade.existeUsuarioLogado()){

			iniciarSessao();

		}else {
			switch (ConnectionUtil.getConnectivityStatus(this)) {
				case ConnectionUtil.TYPE_NOT_CONNECTED:
					//NOTIFICAR Q ESTÁ SEM NET
					break;
				case ConnectionUtil.TYPE_WIFI:
					DebugLog.d(this, "TEM CONEXÃO");
				case ConnectionUtil.TYPE_MOBILE:
					DebugLog.d(this, "TEM CONEXÃO - Mobile");

					webFrag = getSupportFragmentManager().findFragmentByTag(FRAG_TAG);

					if(webFrag == null){
						webFrag = new FragmentAutenticacao();

						getSupportFragmentManager()
								.beginTransaction()
								.add(R.id.fragContainer, webFrag, FRAG_TAG)
								.commit();
					}


					break;
				default:
					break;
			}
		}

	}


	public void login(String username, String password, IExecutor executor){
		DebugLog.d(this, "OnLogin");
		facade.login(username, password, executor);
	}
	
	@Override
	public void iniciarSessao(){
		setResult(RESULT_OK);
		finish();
	}
}
