package br.ufpe.sabertecnologias.acervoapp.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import br.ufpe.sabertecnologias.acervoapp.R;


public class ActivityTutorial extends FragmentActivity{


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_tutorial);
	}


	public void ok(View view) {
		setResult(RESULT_OK);
		finish();
	}
}
