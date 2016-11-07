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
import br.ufpe.sabertecnologias.acervoapp.ui.callbacks.NavMenuCallback;
import br.ufpe.sabertecnologias.acervoapp.ui.callbacks.TransferindoCallback;
import br.ufpe.sabertecnologias.acervoapp.ui.controllers.DetalhesItemController;
import br.ufpe.sabertecnologias.acervoapp.ui.controllers.DrawerMenuController;
import br.ufpe.sabertecnologias.acervoapp.ui.controllers.TransferindoController;
import br.ufpe.sabertecnologias.acervoapp.util.BroadcastUtil;
import br.ufpe.sabertecnologias.acervoapp.util.DebugLog;

/**
 * Created by joaotrindade on 04/11/15.
 */
public class ActivityTransferencias extends AppCompatActivity implements TransferindoCallback, NavMenuCallback {



    private static final int FRAG_NAV_MENU = 6;
    private static final int FRAG_TRANSFERINDO = 7;
    private static final int FRAG_DETALHESITEM = 11;
    private static final int REQUEST_CODE_DETALHES = 7894 ;

    private int CONTENT_FRAG = FRAG_TRANSFERINDO;
    private String CONTENT_FRAG_TAG = TransferindoController.TAG;
    private static final String CONTENT_FRAG_KEY = "contentFrag";
    private static final String CONTENT_FRAG_TAG_KEY = "contentFragTag";

    private Toolbar toolbar;
    private Fragment transferindoController, detalhesController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_transferencias);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setBackgroundResource(R.color.transferencias_color);
        getSupportActionBar().setTitle(R.string.transferencias);

        if(savedInstanceState != null){
            CONTENT_FRAG = savedInstanceState.getInt(CONTENT_FRAG_KEY);
            CONTENT_FRAG_TAG = savedInstanceState.getString(CONTENT_FRAG_TAG_KEY);
        }

       initControllers();


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
        DebugLog.d(this, "MultiPaneActivityMeuAcervo - InitControllers() ");
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transation = fm.beginTransaction();
        transferindoController = setFragment(FRAG_TRANSFERINDO, fm, transation);
        detalhesController = setFragment(FRAG_DETALHESITEM, fm, transation);
        Fragment f = selectContentFragmet();
        if(!f.isAdded()) {
            transation
                    .replace(R.id.transferencias_container,f , CONTENT_FRAG_TAG)
                    .commit();
        }
    }

    private Fragment selectContentFragmet() {
        switch (CONTENT_FRAG){
            case FRAG_DETALHESITEM:
                return detalhesController;

            case FRAG_TRANSFERINDO:
                return transferindoController;
        }
        return null;
    }

    private Fragment setFragment(int frag, FragmentManager fm,
                                 FragmentTransaction t) {


        DebugLog.d(this, "setFragment()");

        Fragment f = null;

        switch (frag) {
            case FRAG_NAV_MENU:
                if((f = fm.findFragmentByTag(DrawerMenuController.TAG)) == null){
                    DebugLog.d(this, "navmenu = null");
                    f = new DrawerMenuController().init(this, DrawerMenuController.TRANSFERENCIAS);
                    t.add(R.id.panel_navdrawer, f, DrawerMenuController.TAG);
                }
                break;
            case FRAG_TRANSFERINDO:
                if((f=fm.findFragmentByTag(TransferindoController.TAG)) == null){
                    DebugLog.d(this, "transferindo = null");
                    f = new TransferindoController().init(this, TransferindoController.FROM_TRANSFERENCIAS);
                }
                break;
            case FRAG_DETALHESITEM:
                if((f= fm.findFragmentByTag(DetalhesItemController.TAG)) == null) {
                    DebugLog.d(this, "detalhes = null");
                    f = new DetalhesItemController().init(this, DetalhesItemController.DetalhesContext.MEU_ACERVO);
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


    @Override
    public void piscarTransferindo() {

    }

    @Override
    public void showDetalhes(Item item) {
        Intent it = new Intent(this, ActivityDetalhes.class);
        it.putExtra("item", item);
        it.putExtra("color", Color.RED);
        it.putExtra("fromContext", 2    );
        startActivityForResult(it, REQUEST_CODE_DETALHES);
    }

    @Override
    public void addTransferencia() {

    }

    @Override
    public void setTransferencias(int qtd_transferencias) {

    }

    @Override
    public void finishTransferencia() {

    }

    @Override
    public boolean onSelectNavItem(int position) {
        return false;
    }

    @Override
    public void abrirSuporte() {

    }

    @Override
    public void abrirCreditos() {

    }

    @Override
    public void mostrarTutorial() {

    }

    @Override
    public void showTransferindo() {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        /** CONTENT_FRAG **/
        outState.putInt(CONTENT_FRAG_KEY, CONTENT_FRAG);
        outState.putString(CONTENT_FRAG_TAG_KEY, CONTENT_FRAG_TAG);
        super.onSaveInstanceState(outState);



    }

    @Override
    public void onBackPressed() {

        switch (CONTENT_FRAG){
            case FRAG_DETALHESITEM:

                CONTENT_FRAG = FRAG_TRANSFERINDO;
                CONTENT_FRAG_TAG = TransferindoController.TAG;
                break;
            case FRAG_TRANSFERINDO:

                break;
        }

        super.onBackPressed();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REQUEST_CODE_DETALHES:

                if(resultCode == RESULT_OK){
                    Item item = data.getParcelableExtra("item");
                    // ((GrupoController)grupoController).updateItemFromDetalhes(item);
                }
            default:
                break;
        }
    }

}
