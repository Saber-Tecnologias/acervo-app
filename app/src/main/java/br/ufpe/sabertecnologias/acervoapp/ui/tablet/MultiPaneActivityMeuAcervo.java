package br.ufpe.sabertecnologias.acervoapp.ui.tablet;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

import br.ufpe.sabertecnologias.acervoapp.R;
import br.ufpe.sabertecnologias.acervoapp.modelo.Facade;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Grupo;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Item;
import br.ufpe.sabertecnologias.acervoapp.modelo.negocio.sync.BroadcastSync;
import br.ufpe.sabertecnologias.acervoapp.modelo.repositorios.RecentSearchProvider;
import br.ufpe.sabertecnologias.acervoapp.ui.ActivityTutorial;
import br.ufpe.sabertecnologias.acervoapp.ui.callbacks.BuscaControllerCallback;
import br.ufpe.sabertecnologias.acervoapp.ui.callbacks.BuscaMeuAcervoControllerCallback;
import br.ufpe.sabertecnologias.acervoapp.ui.callbacks.ButtonDoMenuCallback;
import br.ufpe.sabertecnologias.acervoapp.ui.callbacks.GrupoCallback;
import br.ufpe.sabertecnologias.acervoapp.ui.callbacks.GruposCallback;
import br.ufpe.sabertecnologias.acervoapp.ui.callbacks.MenuCallback;
import br.ufpe.sabertecnologias.acervoapp.ui.callbacks.NavMenuCallback;
import br.ufpe.sabertecnologias.acervoapp.ui.callbacks.TagsCallback;
import br.ufpe.sabertecnologias.acervoapp.ui.callbacks.TransferindoCallback;
import br.ufpe.sabertecnologias.acervoapp.ui.controllers.BuscaMeuAcervoController;
import br.ufpe.sabertecnologias.acervoapp.ui.controllers.ButtonDoMenuController;
import br.ufpe.sabertecnologias.acervoapp.ui.controllers.DetalhesItemController;
import br.ufpe.sabertecnologias.acervoapp.ui.controllers.DrawerMenuController;
import br.ufpe.sabertecnologias.acervoapp.ui.controllers.GrupoController;
import br.ufpe.sabertecnologias.acervoapp.ui.controllers.GruposController;
import br.ufpe.sabertecnologias.acervoapp.ui.controllers.MenuController;
import br.ufpe.sabertecnologias.acervoapp.ui.controllers.TagsController;
import br.ufpe.sabertecnologias.acervoapp.ui.controllers.TransferindoController;
import br.ufpe.sabertecnologias.acervoapp.ui.phone.ActivityDetalhes;
import br.ufpe.sabertecnologias.acervoapp.ui.view.MenuView;
import br.ufpe.sabertecnologias.acervoapp.ui.view.MenuView.TYPE;
import br.ufpe.sabertecnologias.acervoapp.util.BroadcastUtil;
import br.ufpe.sabertecnologias.acervoapp.util.DebugLog;
import jpttrindade.widget.tagview.Tag;

public class MultiPaneActivityMeuAcervo extends AppCompatActivity
        implements BuscaMeuAcervoControllerCallback, TagsCallback, BuscaControllerCallback, NavMenuCallback, TransferindoCallback, MenuCallback,
        GruposCallback, GrupoCallback, ButtonDoMenuCallback, ActionMode.Callback{

    private static final String DEBUG_TAG = "MEU_ACERVO";

    private static final int FRAG_RECOMENDACOES=0;
    private static final int FRAG_GRUPOS=1;
    private static final int FRAG_BUSCA=2;
    private static final int FRAG_TAGS=5;
    private static final int FRAG_NAV_MENU = 6;
    private static final int FRAG_TRANSFERINDO = 7;
    private static final int FRAG_MENU = 8;
    private static final int FRAG_GRUPO = 9;
    private static final int FRAG_DETALHESITEM = 11;
    private static final int FRAG_BUTTON_DO_MENU = 12;
    private static final int REQUEST_CODE_TUTORIAL = 788 ;
    private static final int REQUEST_CODE_DETALHES = 789;

    private Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle toggle;

    private int CONTENT_FRAG = FRAG_GRUPOS;
    private String CONTENT_FRAG_TAG = GruposController.TAG;

    private int CONTENT_FRAG_BEFORE_TRANSFERINDO = FRAG_GRUPOS;
    private String CONTENT_FRAG_TAG_BEFORE_TRANSFERINDO = GruposController.TAG;


    private int CONTENT_FRAG_BEFORE_BUSCA = FRAG_GRUPOS;
    private String CONTENT_FRAG_TAG_BEFORE_BUSCA = GruposController.TAG;


    private int CONTENT_FRAG_BEFORE_DETALHES = FRAG_GRUPO;
    private String CONTENT_FRAG_TAG_BEFORE_DETALHES = GrupoController.TAG;


    private static final String CONTENT_FRAG_KEY = "contentFrag";
    private static final String CONTENT_FRAG_TAG_KEY = "contentFragTag";

    private static final String CONTENT_FRAG_BEFORE_TRANSFERINDO_KEY = "contentFragBeforeTransferindo";
    private static final String CONTENT_FRAG_TAG_BEFORE_TRANSFERINDO_KEY = "contentFragTagBeforeTransferindo";

    private static final String CONTENT_FRAG_BEFORE_DETALHES_KEY = "contentFragBeforeDetalhes";
    private static final String CONTENT_FRAG_TAG_BEFORE_DETALHES_KEY = "contentFragTagBeforeDetalhes";

    private static final String CONTENT_FRAG_BEFORE_BUSCA_KEY = "contentFragBeforeBusca";
    private static final String CONTENT_FRAG_TAG_BEFORE_BUSCA_KEY = "contentFragTagBeforeBusca";

    private static final String FROM_NOTIFICATION_KEY = "fronNotificationKey";

    private final String ACTION_MODE_HABILITADO = "grupoSelecionado";
    private final String ACTION_MODE_COUNT_ITENS = "contaItensSelecionados";
    private final String ACTION_MODE_TODAS_VIEWS_SELECIONADAS = "seTodasViewsEstaoSelecionadas";
    private final String NAVIGATION_DRAWER_ABERTO = "seNavigationFoiAberto";
    private final String VALOR_ITEM_BAIXADO = "valorParaItensBaixadosSelecionados";
    private final String VALOR_ITEM_DISPONIVEL = "valorParaItensDisponiveisSelecionados";

    private static final String TERMOS_ID = "termoID";
    private static final String PILHA = "pilha";
    private String query;

    private int termosId;


    private MenuItem menuItem;
    private SearchView searchView;
    private TextView tv_search;
    private Fragment drawerMenu,
            menuController, tagsController, transferindoController, gruposController, grupoController,
            detalhesController,buttonDoMenuController, buscaController;


    private Facade facade;
    private boolean onConfigurationChange;
    private boolean isHomeButtonClick;
    private boolean toBack;
    private boolean fromNotification;
    private boolean onSavedInstanceState;

    private ActionMode actionModeMenu;

    private int contaItensSelecionados;
    private boolean onActionModeHabilitado;
    private boolean onAllViewSelected;
    private boolean onNavDrawerOpened;

    private int [] difereTiposSelecionados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meu_acervo);
        facade = (Facade) getApplication();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setBackgroundResource(R.color.meu_acervo_primary);
        getSupportActionBar().setTitle("Meu Acervo");
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name, R.string.app_name){

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

                /**
                 * Para garantir que o toolbar contextual, uma vez tendo sido habilitado, irá voltar quando
                 * o navigation drawer for fechado
                 *
                 * */
                if(contaItensSelecionados > 0) {
                    habilitaToolbarContextual(0, -1, false);
                }

                onNavDrawerOpened = false;
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);

                if(!mDrawerLayout.isDrawerOpen(drawerView) && slideOffset == 0.0f) {
                    onNavDrawerOpened = false;
                    /**
                     * Para garantir que quando o usuário apenas abrir parcialmente o navigation
                     * drawer e em seguida fecha-lo, então o toolbar contextual será habilitado
                     *
                     * */
                    if(actionModeMenu == null && contaItensSelecionados > 0) {
                        habilitaToolbarContextual(0, -1, false);
                    }

                } else if (slideOffset <= 1.0f){
                    onNavDrawerOpened = true;
                    /**
                     * Condição para temporariamente remover o toolbar contextual quando o
                     * navigation drawer estiver sendo aberto
                     *
                     */
                    if(actionModeMenu != null) {
                        actionModeMenu.finish();
                    }
                }
            }
        };

        mDrawerLayout.setDrawerListener(toggle);

        final View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle.setDrawerIndicatorEnabled(true);
                if(toBack) {
                    onBackPressed();
                } else {
                    mDrawerLayout.openDrawer(Gravity.LEFT);
                }

            }
        };
        toolbar.setNavigationOnClickListener(clickListener);
        termosId = 0;
        onConfigurationChange = false;
        contaItensSelecionados = 0;
        onAllViewSelected = false;
        onNavDrawerOpened = false;
        difereTiposSelecionados = new int[]{0,0};

        if(savedInstanceState != null){
            termosId = savedInstanceState.getInt(TERMOS_ID);
            CONTENT_FRAG = savedInstanceState.getInt(CONTENT_FRAG_KEY);
            CONTENT_FRAG_TAG = savedInstanceState.getString(CONTENT_FRAG_TAG_KEY);
            CONTENT_FRAG_BEFORE_DETALHES = savedInstanceState.getInt(CONTENT_FRAG_BEFORE_DETALHES_KEY);
            CONTENT_FRAG_TAG_BEFORE_DETALHES = savedInstanceState.getString(CONTENT_FRAG_TAG_BEFORE_DETALHES_KEY);
            CONTENT_FRAG_BEFORE_TRANSFERINDO = savedInstanceState.getInt(CONTENT_FRAG_BEFORE_TRANSFERINDO_KEY);
            CONTENT_FRAG_TAG_BEFORE_TRANSFERINDO = savedInstanceState.getString(CONTENT_FRAG_TAG_BEFORE_TRANSFERINDO_KEY);
            CONTENT_FRAG_TAG_BEFORE_BUSCA= savedInstanceState.getString(CONTENT_FRAG_TAG_BEFORE_BUSCA_KEY);
            CONTENT_FRAG_BEFORE_BUSCA = savedInstanceState.getInt(CONTENT_FRAG_BEFORE_BUSCA_KEY);
            onSavedInstanceState = true;
            fromNotification = savedInstanceState.getBoolean(FROM_NOTIFICATION_KEY
            );
            contaItensSelecionados = savedInstanceState.getInt(ACTION_MODE_COUNT_ITENS, 0);
            onAllViewSelected = savedInstanceState.getBoolean(ACTION_MODE_TODAS_VIEWS_SELECIONADAS, false);
            onNavDrawerOpened = savedInstanceState.getBoolean(NAVIGATION_DRAWER_ABERTO, false);
            onActionModeHabilitado = savedInstanceState.getBoolean(ACTION_MODE_HABILITADO, false);
            difereTiposSelecionados[0] = savedInstanceState.getInt(VALOR_ITEM_DISPONIVEL);
            difereTiposSelecionados[1] = savedInstanceState.getInt(VALOR_ITEM_BAIXADO);
        } else {
            handleIntent(getIntent());
        }

        initControllers();

        if(onActionModeHabilitado && !onNavDrawerOpened) {
            habilitaToolbarContextual(0, -1, false);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        BroadcastUtil.setBroadcastEnableState(this, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, BroadcastSync.class);
    }

    @Override
    protected void onResume() {
        super.onResume();
        selectTags();
        if(CONTENT_FRAG == FRAG_GRUPOS){
            ((MenuController) menuController).selectFiltro(MenuView.ID_MEUS_GRUPOS);
        }
        BroadcastUtil.setBroadcastEnableState(this, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, BroadcastSync.class);
        if(!onSavedInstanceState) {
            fromNotification = getIntent().getBooleanExtra("fromNotification", false);
        } else {
            onSavedInstanceState = false;
        }
        if(fromNotification){
            ((MenuController) menuController).selectFiltro(MenuView.ID_TRANSFERENCIAS);
            showTransferindo();
        }
    }

    private void initControllers() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transation = fm.beginTransaction();
        drawerMenu = setFragment(FRAG_NAV_MENU, fm, transation);
        menuController = setFragment(FRAG_MENU, fm, transation);
        tagsController = setFragment(FRAG_TAGS, fm, transation);
        gruposController = setFragment(FRAG_GRUPOS, fm, transation);
        buscaController = setFragment(FRAG_BUSCA, fm, transation);
        transferindoController = setFragment(FRAG_TRANSFERINDO, fm, transation);
        grupoController = setFragment(FRAG_GRUPO, fm, transation);
        detalhesController = setFragment(FRAG_DETALHESITEM, fm, transation);

        Fragment f = selectContentFragmet();
        if(!f.isAdded()) {
            transation
                    .replace(R.id.panel_content,f , CONTENT_FRAG_TAG);
            buttonDoMenuController = setFragment(FRAG_BUTTON_DO_MENU, fm, transation);
            transation.commit();
        } else{
            buttonDoMenuController = setFragment(FRAG_BUTTON_DO_MENU, fm, transation);
        }
    }

    private void selectTags() {
        ArrayList<Tag> tags = ((TagsController) tagsController).getTagsByTipo(TagsController.TYPE_TIPO);
        for (Tag tag : tags){
            ((MenuController)menuController).selectFiltro(tag.id);
        }
    }

    private Fragment setFragment(int frag, FragmentManager fm,
                                 FragmentTransaction t) {
        Fragment f = null;
        switch (frag) {
            case FRAG_NAV_MENU:
                if((f = fm.findFragmentByTag(DrawerMenuController.TAG)) == null){
                    f = new DrawerMenuController().init(this, DrawerMenuController.MEUACERVO);
                    t.add(R.id.panel_navdrawer, f, DrawerMenuController.TAG);
                }
                break;
            case FRAG_RECOMENDACOES:
                break;
            case FRAG_BUSCA:
                if((f=fm.findFragmentByTag(BuscaMeuAcervoController.TAG))==null){
                    f = new BuscaMeuAcervoController().init(this);
                }
                break;
            case FRAG_GRUPOS:
                if((f=fm.findFragmentByTag(GruposController.TAG))==null){
                    f = new GruposController().init(this);
                }
                break;
            case FRAG_TAGS:
                if((f=fm.findFragmentByTag(TagsController.TAG))==null){
                    f = new TagsController().init(TagsController.ContextualType.MEUACERVO);
                }
                t.replace(R.id.panel_tagview, f, TagsController.TAG);
                break;
            case FRAG_TRANSFERINDO:
                if((f=fm.findFragmentByTag(TransferindoController.TAG)) == null){
                    f = new TransferindoController().init(this, TransferindoController.FROM_MEUACERVO);
                }
                break;
            case FRAG_MENU:
                if((f=fm.findFragmentByTag(MenuController.TAG))== null){
                    f = new MenuController().init(this, TYPE.MEUACERVO);
                }
                t.replace(R.id.container_novogrupo, f, MenuController.TAG);
                break;
            case FRAG_GRUPO:
                if((f=fm.findFragmentByTag(GrupoController.TAG))==null){
                    f = new GrupoController().init(this);
                }
                break;
            case FRAG_DETALHESITEM:
                if((f= fm.findFragmentByTag(DetalhesItemController.TAG)) == null) {
                    f = new DetalhesItemController().init(this, DetalhesItemController.DetalhesContext.MEU_ACERVO);
                }
                break;
            case FRAG_BUTTON_DO_MENU:
                if((f=fm.findFragmentByTag(ButtonDoMenuController.TAG))== null){
                    f = buttonDoMenuController = new ButtonDoMenuController().init(ButtonDoMenuController.CONTEXTO.MeuAcervo);
                    t.add(R.id.menu_button_content, f, ButtonDoMenuController.TAG);
                }
                break;
            default:
                break;
        }
        return f;
    }

    private Fragment selectContentFragmet() {
        switch(CONTENT_FRAG){
            case FRAG_BUSCA:
                return buscaController;
            case FRAG_GRUPOS:
                return gruposController;
            case FRAG_TRANSFERINDO:
                return transferindoController;
            case FRAG_GRUPO:
                return grupoController;
            case FRAG_DETALHESITEM:
                return detalhesController;
            default:
                return null;
        }
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this, RecentSearchProvider.AUTHORITY, RecentSearchProvider.MODE);
            suggestions.saveRecentQuery(query, null);
            doMySearch(query);
        }
    }

    private void doMySearch(String query2) {
        Tag tag = new Tag(query, Color.parseColor("#8dbf67"), 0, TagsController.TYPE_TERMO_EDITABLE);
        tag.setType(TagsController.TYPE_TERMO);
        tag.setID(termosId);
        addTag(tag);
        tv_search.setText("");
        termosId++;
        searchView.clearFocus();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        menuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        tv_search = (TextView) searchView.findViewById(R.id.search_src_text);
        tv_search.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(toggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(TERMOS_ID, termosId);
        /** CONTENT_FRAG **/
        outState.putInt(CONTENT_FRAG_KEY, CONTENT_FRAG);
        outState.putString(CONTENT_FRAG_TAG_KEY, CONTENT_FRAG_TAG);
        /** CONTENT_FRAG_BEFORE_DETALHES **/
        outState.putInt(CONTENT_FRAG_BEFORE_DETALHES_KEY, CONTENT_FRAG_BEFORE_DETALHES);
        outState.putString(CONTENT_FRAG_TAG_BEFORE_DETALHES_KEY, CONTENT_FRAG_TAG_BEFORE_DETALHES);
        /** CONTENT_FRAG_BEFORE_TRANSFERINDO **/
        outState.putInt(CONTENT_FRAG_BEFORE_TRANSFERINDO_KEY, CONTENT_FRAG_BEFORE_TRANSFERINDO);
        outState.putString(CONTENT_FRAG_TAG_BEFORE_TRANSFERINDO_KEY, CONTENT_FRAG_TAG_BEFORE_TRANSFERINDO);
        /** CONTENT_FRAG_BEFORE_BUSCA **/
        outState.putInt(CONTENT_FRAG_BEFORE_BUSCA_KEY, CONTENT_FRAG_BEFORE_BUSCA);
        outState.putString(CONTENT_FRAG_TAG_BEFORE_BUSCA_KEY, CONTENT_FRAG_TAG_BEFORE_BUSCA);

        outState.putBoolean(FROM_NOTIFICATION_KEY, fromNotification);
        outState.putBoolean(ACTION_MODE_HABILITADO, onActionModeHabilitado);
        outState.putInt(ACTION_MODE_COUNT_ITENS, contaItensSelecionados);
        outState.putBoolean(ACTION_MODE_TODAS_VIEWS_SELECIONADAS, onAllViewSelected);
        outState.putBoolean(NAVIGATION_DRAWER_ABERTO, onNavDrawerOpened);
        outState.putInt(VALOR_ITEM_BAIXADO, difereTiposSelecionados[1]);
        outState.putInt(VALOR_ITEM_DISPONIVEL, difereTiposSelecionados[0]);
    }

    @Override
    public void piscarTransferindo() {}

    @Override
    public boolean onSelectNavItem(int position) {
        if(mDrawerLayout.isDrawerOpen(Gravity.LEFT))
            mDrawerLayout.closeDrawers();
        if(position == DrawerMenuController.MEUACERVO){
            if(menuController!= null)
                ((MenuController)menuController).selectFiltro(MenuView.ID_MEUS_GRUPOS);
            if (CONTENT_FRAG != FRAG_GRUPOS && !fromNotification) {
                showMeusGrupos();
            }
        }
        if(onActionModeHabilitado) {
            onNavDrawerOpened=false;
            onDestroyActionMode(null);
        }
        return true;
    }

    @Override
    public void abrirSuporte() {}

    @Override
    public void abrirCreditos() {}

    @Override
    public void mostrarTutorial() {
        Intent it = new Intent(this, ActivityTutorial.class);
        startActivityForResult(it, REQUEST_CODE_TUTORIAL);
    }

    @Override
    public void showResultadoBusca() {
        if(CONTENT_FRAG != FRAG_BUSCA) {
            ((MenuController) menuController).deselectFiltro(MenuView.ID_TRANSFERENCIAS);
            ((MenuController) menuController).deselectFiltro(MenuView.ID_MEUS_GRUPOS);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction
                    .replace(R.id.panel_content, buscaController, BuscaMeuAcervoController.TAG)
                    .addToBackStack(PILHA)
                    .commit();
            CONTENT_FRAG_BEFORE_BUSCA = CONTENT_FRAG;
            CONTENT_FRAG_TAG_BEFORE_BUSCA = CONTENT_FRAG_TAG;
            CONTENT_FRAG = FRAG_BUSCA;
            CONTENT_FRAG_TAG = BuscaMeuAcervoController.TAG;
        }
    }

    @Override
    public void transferirItem(Item i) {
        addTransferencia();
    }


    @Override
    public void addTransferencia(){
        ((MenuController)menuController).addTransferencia();
    }

    @Override
    public void setTransferencias(int qtd_transferencias) {
        ((MenuController)menuController).setTransferencias(qtd_transferencias);
    }

    @Override
    public void finishTransferencia() {
        ((MenuController)menuController).finishTranferencia();
    }

    @Override
    public void deselectFiltro(Tag tag) {
        switch(tag.type){
            case TagsController.TYPE_TIPO:
                ((MenuController)menuController).deselectFiltro(tag.id);
                break;
            case TagsController.TYPE_TERMO:
                break;
        }
    }

    @Override
    public void editTag(Tag tag) {
        searchView.setQuery(tag.text, false);
    }

    @Override
    public void onEmptyTags() {
        ((BuscaMeuAcervoController)buscaController).clear();
        if(CONTENT_FRAG == FRAG_BUSCA)
            onBackPressed();
    }

    @Override
    public void addTag(Tag tag) {
        showResultadoBusca();
        ((TagsController)tagsController).addTag(tag);
    }

    @Override
    public void removeTag(Tag tag) {
        showResultadoBusca();
        ((TagsController)tagsController).removeTag(tag);
    }

    @Override
    public void showFiltro(int id, TYPE type) {
        switch (id) {
            case MenuView.ID_MEUS_GRUPOS:
                showMeusGrupos();
                break;
            case MenuView.ID_TRANSFERENCIAS:
                showTransferindo();
                break;
            default:
                break;
        }
    }

    @Override
    public void showTransferindo() {
        FragmentManager fm = getSupportFragmentManager();
        for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }

        FragmentTransaction transaction = fm.beginTransaction();
        transaction
                .replace(R.id.panel_content, transferindoController, TransferindoController.TAG)
                        //.addToBackStack(PILHA)
                .commit();

        CONTENT_FRAG_BEFORE_TRANSFERINDO = FRAG_GRUPOS;
        CONTENT_FRAG_TAG_BEFORE_TRANSFERINDO = GruposController.TAG;

        CONTENT_FRAG = FRAG_TRANSFERINDO;
        CONTENT_FRAG_TAG = TransferindoController.TAG;

        if(tagsController!=null)
            ((TagsController)tagsController).removeAll();

        DebugLog.d(this, "Show transferindo");
    }

    @Override
    public void showMeusGrupos() {
        if(menuController != null)
            ((MenuController) menuController).deselectFiltro(MenuView.ID_TRANSFERENCIAS);



        FragmentManager fm = getSupportFragmentManager();
        for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }

        FragmentTransaction transaction = fm.beginTransaction();

        transaction

                .replace(R.id.panel_content, gruposController, GruposController.TAG)
                        //.addToBackStack(PILHA)
                .commit();



        CONTENT_FRAG = FRAG_GRUPOS;
        CONTENT_FRAG_TAG = GruposController.TAG;


        if(tagsController!=null)
            ((TagsController)tagsController).removeAll();

    }

    @Override
    public void hideFiltro(int id) {

        onBackPressed();

        //showResultadoBusca();
        //((MenuController)menuController).deselectFiltro(id);
    }

    public void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        View view = getCurrentFocus();
        boolean ret = super.dispatchTouchEvent(event);
        if (view instanceof EditText) {

            View w = getCurrentFocus();


            int scrcoords[] = new int[2];
            w.getLocationOnScreen(scrcoords);
            float x = event.getRawX() + w.getLeft() - scrcoords[0];
            float y = event.getRawY() + w.getTop() - scrcoords[1];

            if (event.getAction() == MotionEvent.ACTION_UP
                    && (x < w.getLeft() || x >= w.getRight()
                    || y < w.getTop() || y > w.getBottom()) ) {

                hideSoftKeyboard();
            }
        }
        return ret;

    }

    @Override
    public void abrirGrupo(Grupo g) {

        ((GrupoController) grupoController).setGrupo(g);

        DebugLog.d(this, "Grupo = " + g.getItens().size());

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction
                .replace(R.id.panel_content, grupoController, GrupoController.TAG)
                .addToBackStack(PILHA)
                .commit();


        CONTENT_FRAG = FRAG_GRUPO;
        CONTENT_FRAG_TAG = GrupoController.TAG;

        ((MenuController)menuController).deselectFiltro(MenuView.ID_MEUS_GRUPOS);

    }

    @Override
    public void showDetalhes(Item i) {
        Intent it = new Intent(this, ActivityDetalhes.class);
        it.putExtra("item", i);
        it.putExtra("color", Color.RED);
        it.putExtra("fromContext", 0);
        startActivityForResult(it, REQUEST_CODE_DETALHES);
    }

    @Override
    public void backToMeuAcervo() {
        onBackPressed();
    }

    @Override
    public void habilitaToolbarContextual(int valorParaGruposSelecionados, int itemStatus, boolean disable) {
        contaItensSelecionados += valorParaGruposSelecionados;
        // item "disponivel"
        if(itemStatus == Item.FLAG_ITEM_DISPONIVEL){
            difereTiposSelecionados[0] += valorParaGruposSelecionados;
            // item "baixado"
        } else if (itemStatus == Item.FLAG_ITEM_BAIXADO) {
            difereTiposSelecionados[1] += valorParaGruposSelecionados;
        }

        if(actionModeMenu == null) {
            if (!disable) {
                actionModeMenu = startSupportActionMode(this);
                if(contaItensSelecionados == ((GrupoController) grupoController).getItensSelecionadosSize()){
                    onAllViewSelected = true;
                }
            }
        } else {
            /**
             * condicao para verificar se usuário deselecionou todos os grupos antes selecionados
             * e assim chamar o onDestroyActionMode após o finish
             *
             */
            if(contaItensSelecionados == 0 || disable) {
                actionModeMenu.finish();
            } else {
                if(onAllViewSelected && contaItensSelecionados < ((GrupoController) grupoController).getItensSelecionadosSize()) {
                    // ou seja, quando está "voltando" da seleção
                    onAllViewSelected = false;
                } else if(contaItensSelecionados == ((GrupoController) grupoController).getItensSelecionadosSize()){
                    onAllViewSelected = true;
                }
                /**
                 * It's gonna call the onPrepareActionMode method to refresh action mode view
                 *
                 * */
                actionModeMenu.invalidate();
            }
        }
    }

    @Override
    public void exit() {
        super.onBackPressed();
    }

    @Override
    public void notifyGrupoController(ArrayList<Grupo> mGrupos) {
        if(CONTENT_FRAG_TAG == GrupoController.TAG){
            ((GrupoController)grupoController).notifyFromSync(mGrupos);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case REQUEST_CODE_TUTORIAL:
                if(resultCode == RESULT_OK) {
                    DebugLog.d(this, "RETORNOU DO TUTORIAL");
                } else {
                    DebugLog.d(this, "RETORNOU DO TUTORIAL mas foi o Result_CANCELED");
                }
                break;
            case REQUEST_CODE_DETALHES:
                if(resultCode == RESULT_OK){

                } else {

                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
                boolean toExit = false;
        switch (CONTENT_FRAG) {
            case FRAG_TRANSFERINDO:
                fromNotification = false;
                ((MenuController) menuController).selectFiltro(MenuView.ID_MEUS_GRUPOS);
                showMeusGrupos();
                break;
            case FRAG_GRUPOS:
                toExit = true;
                break;
            case FRAG_GRUPO:
                CONTENT_FRAG = FRAG_GRUPOS;
                CONTENT_FRAG_TAG = GruposController.TAG;
                ((MenuController) menuController).selectFiltro(MenuView.ID_MEUS_GRUPOS);
                super.onBackPressed();
                break;
            case FRAG_DETALHESITEM:
                CONTENT_FRAG = CONTENT_FRAG_BEFORE_DETALHES;
                CONTENT_FRAG_TAG = CONTENT_FRAG_TAG_BEFORE_DETALHES;
                if (CONTENT_FRAG == FRAG_TRANSFERINDO) {
                    ((MenuController) menuController).selectFiltro(MenuView.ID_TRANSFERENCIAS);
                }
                super.onBackPressed();
                break;
            case FRAG_BUSCA:
                super.onBackPressed();
                CONTENT_FRAG = CONTENT_FRAG_BEFORE_BUSCA;
                CONTENT_FRAG_TAG = CONTENT_FRAG_TAG_BEFORE_BUSCA;
                ((TagsController) tagsController).removeAll();
                if (CONTENT_FRAG == FRAG_TRANSFERINDO) {
                    ((MenuController) menuController).selectFiltro(MenuView.ID_TRANSFERENCIAS);
                } else if(CONTENT_FRAG == FRAG_GRUPOS){
                    ((MenuController) menuController).selectFiltro(MenuView.ID_MEUS_GRUPOS);
                }
                break;
            default:
        }
        if (!toExit) {
        } else {
            exit();
        }
    }

    @Override
    public void adicionarMenuDeBotoes () {}

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        // Inflate a menu resource providing context menu items
        onActionModeHabilitado = true;
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.action_mode_menu, menu);
        if(difereTiposSelecionados[0] == 0 && difereTiposSelecionados[1] > 0) {
            menu.getItem(0).setVisible(true);
            menu.getItem(1).setVisible(true);
            menu.getItem(2).setVisible(true);
            menu.getItem(3).setVisible(false);  // desabilitando o "transferir"
            // selecionou um "disponivel" primeiro
        } else {
            menu.getItem(0).setVisible(true);
            menu.getItem(1).setVisible(true);
            menu.getItem(2).setVisible(true);
            menu.getItem(3).setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        // somente item "baixado" selecionado
        if(difereTiposSelecionados[0] == 0 && difereTiposSelecionados[1] > 0) {
            menu.getItem(0).setVisible(true);
            menu.getItem(1).setVisible(true);
            menu.getItem(2).setVisible(true);
            menu.getItem(3).setVisible(false);  // desabilitando o "transferir"
            // ou seja, "disponivel" e "baixado" selecionados
        } else if(difereTiposSelecionados[0] > 0 && difereTiposSelecionados[1] > 0) {
            menu.getItem(0).setVisible(true);
            menu.getItem(1).setVisible(true);
            menu.getItem(2).setVisible(false);  // desabilitando o "remover"
            menu.getItem(3).setVisible(false);
        } else {
            menu.getItem(0).setVisible(true);
            menu.getItem(1).setVisible(true);
            menu.getItem(2).setVisible(true);
            menu.getItem(3).setVisible(true);
        }

        if(actionModeMenu != null) {
            actionModeMenu.setTitle("(" + contaItensSelecionados + ")");
        } else {
            mode.setTitle("(" + contaItensSelecionados + ")");
        }
        return true; // Return false if nothing is done
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ic_select:
                if(onAllViewSelected) {
                    mode.finish();
                } else {
                    ((GrupoController) grupoController).manipularTodasSombras(true);
                    Map<Item, Boolean> itensSelecionadosGrupo = ((GrupoController) grupoController).getmItensSelecionados();
                    difereTiposSelecionados[0] = 0;
                    difereTiposSelecionados[1] = 0;
                    for(Item i: itensSelecionadosGrupo.keySet()){
                        if (i.getStatus() == Item.FLAG_ITEM_DISPONIVEL){
                            difereTiposSelecionados[0]++;
                        } else if (i.getStatus() == Item.FLAG_ITEM_BAIXADO) {
                            difereTiposSelecionados[1]++;
                        }
                    }
                    contaItensSelecionados = ((GrupoController) grupoController).getItensSelecionadosSize();
                    onAllViewSelected = true;
                    mode.invalidate();
                }
                return true;
            case R.id.ic_move:
                ((GrupoController) grupoController).showDialogMoverItemDoActionMode();
                return true;
            case R.id.ic_delete:
                ((GrupoController) grupoController).removerDoActionMode();
                return true;
            case R.id.ic_transferir:
                ((GrupoController) grupoController).transferirDoActionMode();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        actionModeMenu = null;
        if(!onNavDrawerOpened && onActionModeHabilitado) {
            ((GrupoController) grupoController).setLongClickFoiChamado(false);
            ((GrupoController) grupoController).manipularTodasSombras(false);
            contaItensSelecionados = 0;
            onAllViewSelected = false;
            onActionModeHabilitado=false;
            difereTiposSelecionados = new int[]{0,0};
        }
    }
}
