package br.ufpe.sabertecnologias.acervoapp.ui.phone;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.provider.SearchRecentSuggestions;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Grupo;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Item;
import br.ufpe.sabertecnologias.acervoapp.modelo.negocio.sync.BroadcastSync;
import br.ufpe.sabertecnologias.acervoapp.modelo.repositorios.RecentSearchProvider;
import br.ufpe.sabertecnologias.acervoapp.ui.ActivityTutorial;
import br.ufpe.sabertecnologias.acervoapp.ui.callbacks.BuscaMeuAcervoControllerCallback;
import br.ufpe.sabertecnologias.acervoapp.ui.callbacks.ButtonDoMenuCallback;
import br.ufpe.sabertecnologias.acervoapp.ui.callbacks.GrupoCallback;
import br.ufpe.sabertecnologias.acervoapp.ui.callbacks.GruposCallback;
import br.ufpe.sabertecnologias.acervoapp.ui.callbacks.MenuButtonsCallback;
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
import br.ufpe.sabertecnologias.acervoapp.ui.controllers.MenuButtonsController;
import br.ufpe.sabertecnologias.acervoapp.ui.controllers.TagsController;
import br.ufpe.sabertecnologias.acervoapp.ui.controllers.TransferindoController;
import br.ufpe.sabertecnologias.acervoapp.ui.view.MenuView;
import br.ufpe.sabertecnologias.acervoapp.util.BroadcastUtil;
import br.ufpe.sabertecnologias.acervoapp.util.DebugLog;
import jpttrindade.widget.tagview.Tag;

public class ActivityMeuAcervo extends AppCompatActivity
        implements NavMenuCallback, GruposCallback, GrupoCallback,
        MenuCallback, TransferindoCallback, ButtonDoMenuCallback, MenuButtonsCallback, TagsCallback,
        BuscaMeuAcervoControllerCallback, ActionMode.Callback {

    private static final String TITLE = "Meu Acervo";

    private static final int FRAG_RECOMENDACOES=0;
    private static final int FRAG_GRUPOS=1;
    private static final int FRAG_BUSCA=2;
    private static final int FRAG_TAGS=5;
    private static final int FRAG_NAV_MENU = 6;
    private static final int FRAG_MENU_BUTTONS = 7;
    private static final int FRAG_GRUPO = 8;
    private static final int FRAG_DETALHESITEM = 11;
    private static final int FRAG_BUTTON_DO_MENU = 12;
    private static final int FRAG_TRANSFERINDO = 13;

    private static final int REQUEST_CODE_TUTORIAL = 799;
    private static final int REQUEST_CODE_DETALHES = 798;


    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle toggle;

    private int CONTENT_FRAG = FRAG_GRUPOS;
    private String CONTENT_FRAG_TAG = GruposController.TAG;

    private int CONTENT_FRAG_BEFORE_DETALHES = FRAG_GRUPO;
    private String CONTENT_FRAG_TAG_BEFORE_DETALHES = GrupoController.TAG;

    private int CONTENT_FRAG_BEFORE_BUSCA = FRAG_GRUPOS;
    private String CONTENT_FRAG_TAG_BEFORE_BUSCA = GruposController.TAG;


    private static final String CONTENT_FRAG_KEY = "contentFrag";
    private static final String CONTENT_FRAG_TAG_KEY = "contentFragTag";

    private static final String CONTENT_FRAG_BEFORE_DETALHES_KEY = "contentFragBeforeDetalhes";
    private static final String CONTENT_FRAG_TAG_BEFORE_DETALHES_KEY = "contentFragTagBeforeDetalhes";

    private static final String CONTENT_FRAG_BEFORE_BUSCA_KEY = "contentFragBeforeBusca";
    private static final String CONTENT_FRAG_TAG_BEFORE_BUSCA_KEY = "contentFragTagBeforeBusca";


    private final String ACTION_MODE_HABILITADO = "grupoSelecionado";
    private final String ACTION_MODE_COUNT_ITENS = "contaItensSelecionados";
    private final String ACTION_MODE_TODAS_VIEWS_SELECIONADAS = "seTodasViewsEstaoSelecionadas";
    private final String NAVIGATION_DRAWER_ABERTO = "seNavigationFoiAberto";
    private final String VALOR_ITEM_BAIXADO = "valorParaItensBaixadosSelecionados";
    private final String VALOR_ITEM_DISPONIVEL = "valorParaItensDisponiveisSelecionados";

    private static final String TERMOS_ID = "termoID";
    private static final String PILHA = "pilha";
    private String query;
    private MenuItem menuItem;
    private SearchView searchView;
    private TextView tv_search;

    private int termosId;

    private Fragment drawerMenu,
            menuButtonController, tagsController, gruposController, grupoController,
            detalhesController, buttonDoMenuController, buscaController, transferindoController;

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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setBackgroundResource(R.color.meu_acervo_primary);
        getSupportActionBar().setTitle(TITLE);

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

        termosId = 0;
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
            CONTENT_FRAG_TAG_BEFORE_BUSCA= savedInstanceState.getString(CONTENT_FRAG_TAG_BEFORE_BUSCA_KEY);
            CONTENT_FRAG_BEFORE_BUSCA = savedInstanceState.getInt(CONTENT_FRAG_BEFORE_BUSCA_KEY);
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
        BroadcastUtil.setBroadcastEnableState(this, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, BroadcastSync.class);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
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
        outState.putInt(CONTENT_FRAG_BEFORE_BUSCA_KEY, CONTENT_FRAG_BEFORE_BUSCA);
        outState.putString(CONTENT_FRAG_TAG_BEFORE_BUSCA_KEY, CONTENT_FRAG_TAG_BEFORE_BUSCA);
        outState.putBoolean(ACTION_MODE_HABILITADO, onActionModeHabilitado);
        outState.putInt(ACTION_MODE_COUNT_ITENS, contaItensSelecionados);
        outState.putBoolean(ACTION_MODE_TODAS_VIEWS_SELECIONADAS, onAllViewSelected);
        outState.putBoolean(NAVIGATION_DRAWER_ABERTO, onNavDrawerOpened);
        outState.putInt(VALOR_ITEM_BAIXADO, difereTiposSelecionados[1]);
        outState.putInt(VALOR_ITEM_DISPONIVEL, difereTiposSelecionados[0]);
    }

    private void initControllers() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transation = fm.beginTransaction();
        drawerMenu = setFragment(FRAG_NAV_MENU, fm, transation);
        tagsController = setFragment(FRAG_TAGS, fm, transation);
        gruposController = setFragment(FRAG_GRUPOS, fm, transation);
        buscaController = setFragment(FRAG_BUSCA, fm, transation);
        transferindoController = setFragment(FRAG_TRANSFERINDO, fm, transation);
        grupoController = setFragment(FRAG_GRUPO, fm, transation);
        detalhesController = setFragment(FRAG_DETALHESITEM, fm, transation);
        menuButtonController = setFragment(FRAG_MENU_BUTTONS, fm, transation);
        Fragment f = selectContentFragmet();
        if(!f.isAdded()) {
            transation.replace(R.id.panel_content, f, CONTENT_FRAG_TAG);
            buttonDoMenuController = setFragment(FRAG_BUTTON_DO_MENU, fm, transation);
            transation.commit();
        } else {
            buttonDoMenuController = setFragment(FRAG_BUTTON_DO_MENU, fm, transation);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu_phone_meu_acervo, menu);
        SearchManager searchManager = (SearchManager) ActivityMeuAcervo.this.getSystemService(Context.SEARCH_SERVICE);
        menuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        SearchableInfo info = searchManager.getSearchableInfo(getComponentName());
        searchView.setSearchableInfo(info);
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
            case FRAG_MENU_BUTTONS:
                if((f=fm.findFragmentByTag(MenuButtonsController.TAG))== null){
                    ArrayList<Tag> tags = ((TagsController) tagsController).getTagsByTipo(TagsController.TYPE_TIPO);
                    int idTipoSelecionado = 0;
                    if(tags.size() > 0){
                        idTipoSelecionado = tags.get(0).id;
                    }
                    f = new MenuButtonsController().init(MenuButtonsController.CONTEXTO.MeuAcervo, idTipoSelecionado);
                }
                break;
            case FRAG_BUTTON_DO_MENU:
                if((f=fm.findFragmentByTag(ButtonDoMenuController.TAG))== null){
                    f = buttonDoMenuController = new ButtonDoMenuController().init(ButtonDoMenuController.CONTEXTO.MeuAcervo);
                    t.add(R.id.menu_button_content, f, ButtonDoMenuController.TAG);
                }
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
            case FRAG_TRANSFERINDO:
                f = new TransferindoController().init(this, TransferindoController.FROM_MEUACERVO);

                break;
            default:
                break;
        }
        return f;
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

    public void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
    }

    private Fragment selectContentFragmet() {
        switch(CONTENT_FRAG){
            case FRAG_BUSCA:
                return buscaController;
            case FRAG_GRUPOS:
                return gruposController;
            case FRAG_GRUPO:
                return grupoController;
            case FRAG_DETALHESITEM:
                return detalhesController;
            case FRAG_MENU_BUTTONS:
                return menuButtonController;
            default:
                return null;
        }
    }

    @Override
    public boolean onSelectNavItem(int position) {
        if(mDrawerLayout.isDrawerOpen(Gravity.LEFT))
            mDrawerLayout.closeDrawers();
        showMeusGrupos();
        onNavDrawerOpened = false;
        onDestroyActionMode(null);
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
    public void transferirItem(Item i) {}

    @Override
    public void showDetalhes(Item i) {
        Intent it = new Intent(this, ActivityDetalhes.class);
        it.putExtra("item", i);
        it.putExtra("color", Color.RED);
        it.putExtra("fromContext", 0);
        startActivityForResult(it, REQUEST_CODE_DETALHES);
    }

    @Override
    public void addTransferencia() {
        ((DrawerMenuController) drawerMenu).addTransferencia();
    }

    @Override
    public void setTransferencias(int qtd_transferencias) {
        ((DrawerMenuController)drawerMenu).setTransferencias(qtd_transferencias);
    }

    @Override
    public void finishTransferencia() {
        ((DrawerMenuController)drawerMenu).finishTranferencia();
    }

    @Override
    public void backToMeuAcervo() {
        onBackPressed();
    }

    @Override
    public void abrirGrupo(Grupo g) {
        ((GrupoController) grupoController).setGrupo(g);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction
                .replace(R.id.panel_content, grupoController, GrupoController.TAG)
                .addToBackStack(PILHA)
                .commit();
        CONTENT_FRAG = FRAG_GRUPO;
        CONTENT_FRAG_TAG = GrupoController.TAG;
    }

    @Override
    public void exit() {
        super.onBackPressed();
    }

    @Override
    public void notifyGrupoController(ArrayList<Grupo> mGrupos) {
        if(GrupoController.TAG.equals(CONTENT_FRAG_TAG)){
            ((GrupoController)grupoController).notifyFromSync(mGrupos);
        }
    }

    @Override
    public void onBackPressed() {
        boolean toExit = false;
        if(menuButtonController.isAdded()){
            removerMenuDeBotoes();
        } else {
            switch (CONTENT_FRAG) {
                case FRAG_GRUPOS:
                    toExit = true;
                    break;
                case FRAG_GRUPO:
                    CONTENT_FRAG = FRAG_GRUPOS;
                    CONTENT_FRAG_TAG = GruposController.TAG;
                    super.onBackPressed();
                    break;
                case FRAG_DETALHESITEM:
                    CONTENT_FRAG = CONTENT_FRAG_BEFORE_DETALHES;
                    CONTENT_FRAG_TAG = CONTENT_FRAG_TAG_BEFORE_DETALHES;
                    ((ButtonDoMenuController)buttonDoMenuController).setButtonRootVisibility(View.VISIBLE);
                    super.onBackPressed();
                    break;
                case FRAG_BUSCA:
                    super.onBackPressed();
                    CONTENT_FRAG = CONTENT_FRAG_BEFORE_BUSCA;
                    CONTENT_FRAG_TAG = CONTENT_FRAG_TAG_BEFORE_BUSCA;
                    ((TagsController) tagsController).removeAll();
                    if(CONTENT_FRAG != FRAG_DETALHESITEM)
                        ((ButtonDoMenuController)buttonDoMenuController).setButtonRootVisibility(View.VISIBLE);
                    else
                        ((ButtonDoMenuController)buttonDoMenuController).setButtonRootVisibility(View.GONE);
                    break;
                default:
                    break;
            }
            if (toExit) {
                exit();
            }
        }
    }

    @Override
    public void showTransferindo() {}

    @Override
    public void showMeusGrupos() {
        if(menuButtonController != null)
            ((MenuButtonsController) menuButtonController).deselectFiltro(MenuView.ID_TRANSFERENCIAS);
        FragmentManager fm = getSupportFragmentManager();
        for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
        FragmentTransaction transaction = fm.beginTransaction();
        transaction
                .replace(R.id.panel_content, gruposController, GruposController.TAG)
                .commit();
        CONTENT_FRAG = FRAG_GRUPOS;
        CONTENT_FRAG_TAG = GruposController.TAG;
    }

    @Override
    public void showFiltro(int id, MenuView.TYPE type) {
        switch (id) {
            case MenuView.ID_NOVO_GRUPO:
                ((GruposController)gruposController).criarNovoGrupoDialog();
                removerMenuDeBotoes();
                break;
            default:
                break;
        }
    }

    @Override
    public void hideFiltro(int id) {
        onBackPressed();
    }

    @Override
    public void removeTag(Tag tag) {
        showResultadoBusca();
        ((TagsController)tagsController).removeTag(tag);
    }

    @Override
    public void showResultadoBusca() {
        if(CONTENT_FRAG != FRAG_BUSCA) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction
                    .replace(R.id.panel_content, buscaController, BuscaMeuAcervoController.TAG)
                    .addToBackStack(PILHA)
                    .commit();
            if(CONTENT_FRAG != FRAG_DETALHESITEM)
                ((ButtonDoMenuController)buttonDoMenuController).setButtonRootVisibility(View.VISIBLE);
            else
                ((ButtonDoMenuController)buttonDoMenuController).setButtonRootVisibility(View.GONE);
            CONTENT_FRAG_BEFORE_BUSCA = CONTENT_FRAG;
            CONTENT_FRAG_TAG_BEFORE_BUSCA = CONTENT_FRAG_TAG;
            CONTENT_FRAG = FRAG_BUSCA;
            CONTENT_FRAG_TAG = BuscaMeuAcervoController.TAG;
        }
    }

    @Override
    public void addTag(Tag tag) {
        showResultadoBusca();
        ((TagsController)tagsController).addTag(tag);
    }

    @Override
    public void piscarTransferindo() {}

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this, RecentSearchProvider.AUTHORITY, RecentSearchProvider.MODE);
            suggestions.saveRecentQuery(query, null);
            doMySearch();
        }
    }

    private void doMySearch() {
        Tag tag = new Tag(query, Color.parseColor("#8dbf67") , 0, TagsController.TYPE_TERMO_EDITABLE);
        tag.setType(TagsController.TYPE_TERMO);
        tag.setID(termosId);
        addTag(tag);
        tv_search.setText("");
        termosId++;
        searchView.clearFocus();
        menuItem.collapseActionView();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    @Override
    public void adicionarMenuDeBotoes() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction t = fm.beginTransaction();
        t.add(R.id.menu_button_content, menuButtonController, MenuButtonsController.TAG).commit();
        ((MenuButtonsController)menuButtonController).abrirBotoes();
    }

    @Override
    public void removerMenuDeBotoes() {
        ((MenuButtonsController)menuButtonController).fecharBotoes();
        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        FragmentManager fm = getSupportFragmentManager();
                        FragmentTransaction t = fm.beginTransaction();
                        t.remove(menuButtonController).commit();
                    }
                }, 200
        );
    }

    @Override
    public void deselectFiltro(Tag tag) {
        switch(tag.type){
            case TagsController.TYPE_TIPO:
                ((MenuButtonsController)menuButtonController).deselectFiltro(tag.id);
                break;
            case TagsController.TYPE_TERMO:
                break;
        }
    }

    @Override
    public void editTag(Tag tag) {
        menuItem.expandActionView();
        searchView.setQuery(tag.text, false);
    }

    @Override
    public void onEmptyTags() {
        ((BuscaMeuAcervoController) buscaController).clear();
        if(CONTENT_FRAG == FRAG_BUSCA) {
            onBackPressed();
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
            default:
                break;
        }
    }

    // Called when the action mode is created; startActionMode() was called
    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
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

    // Called each time the action mode is shown. Always called after onCreateActionMode, but
// may be called multiple times if the mode is invalidated.
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

    // Called when the user selects a contextual menu item
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

    // Called when the user exits the action mode
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

    @Override
    public void habilitaToolbarContextual(int valorParaGruposSelecionados, int itemStatus, boolean disable){
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
                if (contaItensSelecionados == ((GrupoController) grupoController).getItensSelecionadosSize()) {
                    onAllViewSelected = true;
                }
            }
        } else {
            /**
             * condicao para verificar se usuário deselecionou todos os grupos antes selecionados
             * e assim chamar o onDestroyActionMode após o finish
             *
             */
            if(contaItensSelecionados == 0) {
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
}
