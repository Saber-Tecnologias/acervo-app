package br.ufpe.sabertecnologias.acervoapp.ui.phone;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import br.ufpe.sabertecnologias.acervoapp.R;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Item;
import br.ufpe.sabertecnologias.acervoapp.modelo.negocio.sync.BroadcastSync;
import br.ufpe.sabertecnologias.acervoapp.ui.controllers.DetalhesItemController;
import br.ufpe.sabertecnologias.acervoapp.util.BroadcastUtil;

/**
 * Created by joaotrindade on 04/11/15.
 */
public class ActivityDetalhes extends AppCompatActivity  {

    private static final int FRAG_DETALHESITEM = 11;
    private int CONTENT_FRAG = FRAG_DETALHESITEM;
    private String CONTENT_FRAG_TAG = DetalhesItemController.TAG;

    private static final String COLOR_KEY = "color";
    private static final String ITEM_KEY = "item";
    private static final String CONTENT_FRAG_KEY = "contentFrag";
    private static final String CONTENT_FRAG_TAG_KEY = "contentFragTag";
    private static final String FROM_CONTEXT_KEY = "fromContextKey";

    private Fragment detalhesController;
    private Item item;
    private int color;
    private DetalhesItemController.DetalhesContext fromContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setBackgroundResource(R.color.detalhes_color);
        getSupportActionBar().setTitle(R.string.title_detalhes);
        if(savedInstanceState != null){
            CONTENT_FRAG = savedInstanceState.getInt(CONTENT_FRAG_KEY);
            CONTENT_FRAG_TAG = savedInstanceState.getString(CONTENT_FRAG_TAG_KEY);
            item = savedInstanceState.getParcelable(ITEM_KEY);
            color=  savedInstanceState.getInt(COLOR_KEY);
            switch (savedInstanceState.getInt(FROM_CONTEXT_KEY)){
                case 1:
                    fromContext = DetalhesItemController.DetalhesContext.ACERVO_REMOTO;
                    break;
                case 2:
                    fromContext = DetalhesItemController.DetalhesContext.TRANSFERENCIAS;
                    break;
                default:
                    fromContext = DetalhesItemController.DetalhesContext.MEU_ACERVO;
            }
        } else {
            Intent it = getIntent();
            item = it.getParcelableExtra("item");
            color = it.getIntExtra("color", Color.BLUE);
            switch (it.getIntExtra("fromContext", 0)){
                case 1:
                    fromContext = DetalhesItemController.DetalhesContext.ACERVO_REMOTO;
                    break;
                case 2:
                    fromContext = DetalhesItemController.DetalhesContext.TRANSFERENCIAS;
                    break;
                default:
                    fromContext = DetalhesItemController.DetalhesContext.MEU_ACERVO;
            }
        }
        initControllers();
        showDetalhes(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        BroadcastUtil.setBroadcastEnableState(this, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, BroadcastSync.class);

    }

    @Override
    protected void onPause() {
        super.onPause();
        BroadcastUtil.setBroadcastEnableState(this, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, BroadcastSync.class);

    }

    private void initControllers() {
        FragmentManager fm = getSupportFragmentManager();
        detalhesController = setFragment(FRAG_DETALHESITEM, fm);
    }

    private Fragment setFragment(int frag, FragmentManager fm) {
        Fragment f = null;
        switch (frag) {
            case FRAG_DETALHESITEM:
                if((f= fm.findFragmentByTag(DetalhesItemController.TAG)) == null) {
                    f = new DetalhesItemController().init(this, fromContext);
                }
                break;
            default:
                break;
        }
        return f;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDetalhes(Item item) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction t = fm.beginTransaction();
        ((DetalhesItemController) detalhesController).setItem(item, Color.RED);
        t.replace(R.id.detalhes_container, detalhesController, DetalhesItemController.TAG)
                .commit();
        CONTENT_FRAG = FRAG_DETALHESITEM;
        CONTENT_FRAG_TAG = DetalhesItemController.TAG;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        /** CONTENT_FRAG **/
        outState.putInt(CONTENT_FRAG_KEY, CONTENT_FRAG);
        outState.putString(CONTENT_FRAG_TAG_KEY, CONTENT_FRAG_TAG);
        outState.putParcelable(ITEM_KEY, item);
        outState.putInt(COLOR_KEY, color);
        switch (fromContext){
            case MEU_ACERVO:
                outState.putInt(FROM_CONTEXT_KEY, 0);
                break;
            case ACERVO_REMOTO:
                outState.putInt(FROM_CONTEXT_KEY, 1);
                break;
            case TRANSFERENCIAS:
                outState.putInt(FROM_CONTEXT_KEY, 2);
                break;
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        Intent it = new Intent();
        it.putExtra("item", item);
        setResult(RESULT_OK, it);
        finish();
    }
}
