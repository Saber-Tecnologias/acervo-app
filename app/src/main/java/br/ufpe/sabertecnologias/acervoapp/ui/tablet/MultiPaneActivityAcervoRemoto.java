package br.ufpe.sabertecnologias.acervoapp.ui.tablet;

import android.animation.ArgbEvaluator;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TabLayout.TabLayoutOnPageChangeListener;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import br.ufpe.sabertecnologias.acervoapp.R;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Item;
import br.ufpe.sabertecnologias.acervoapp.modelo.negocio.sync.BroadcastSync;
import br.ufpe.sabertecnologias.acervoapp.modelo.repositorios.RecentSearchProvider;
import br.ufpe.sabertecnologias.acervoapp.ui.callbacks.BuscaControllerCallback;
import br.ufpe.sabertecnologias.acervoapp.ui.callbacks.NavMenuCallback;
import br.ufpe.sabertecnologias.acervoapp.ui.callbacks.RecentesCallback;
import br.ufpe.sabertecnologias.acervoapp.ui.callbacks.RecomendacoesCallback;
import br.ufpe.sabertecnologias.acervoapp.ui.callbacks.TagsCallback;
import br.ufpe.sabertecnologias.acervoapp.ui.callbacks.TransferindoCallback;
import br.ufpe.sabertecnologias.acervoapp.ui.controllers.BuscaController;
import br.ufpe.sabertecnologias.acervoapp.ui.controllers.DetalhesItemController;
import br.ufpe.sabertecnologias.acervoapp.ui.controllers.DrawerMenuController;
import br.ufpe.sabertecnologias.acervoapp.ui.callbacks.MenuCallback;
import br.ufpe.sabertecnologias.acervoapp.ui.controllers.MenuController;
import br.ufpe.sabertecnologias.acervoapp.ui.controllers.RecomendacoesController;
import br.ufpe.sabertecnologias.acervoapp.ui.controllers.RecomendacoesControllerComposite;
import br.ufpe.sabertecnologias.acervoapp.ui.controllers.TagsController;
import br.ufpe.sabertecnologias.acervoapp.ui.controllers.TransferindoController;
import br.ufpe.sabertecnologias.acervoapp.ui.phone.ActivityDetalhes;
import br.ufpe.sabertecnologias.acervoapp.ui.ActivityTutorial;
import br.ufpe.sabertecnologias.acervoapp.ui.view.MenuView;
import br.ufpe.sabertecnologias.acervoapp.util.BroadcastUtil;
import br.ufpe.sabertecnologias.acervoapp.util.DebugLog;
import jpttrindade.widget.tagview.Tag;

public class MultiPaneActivityAcervoRemoto extends AppCompatActivity
		implements TagsCallback, BuscaControllerCallback, NavMenuCallback,
		TransferindoCallback, MenuCallback, RecomendacoesCallback, TabLayout.OnTabSelectedListener, RecentesCallback {


	private static final int FRAG_RECOMENDACOES=0;
	private static final int FRAG_BUSCA=1;
	private static final int FRAG_MENU = 2;
	private static final int FRAG_TAGS=3;
	private static final int FRAG_NAV_MENU = 4;
	private static final int FRAG_TRANSFERINDO = 5;
	private static final int FRAG_DETALHESITEM = 6;

	private static final String TERMOS_ID = "termoID";

	private int CONTENT_FRAG_BEFORE_DETALHES = FRAG_RECOMENDACOES;
	private String CONTENT_FRAG_TAG_BEFORE_DETALHES = RecomendacoesController.TAG;

	private static final String CONTENT_FRAG_KEY = "contentFrag";
	private static final String CONTENT_FRAG_TAG_KEY = "contentFragTag";
	private static final int REQUEST_CODE_TUTORIAL = 325;
	private static final int REQUEST_CODE_DETALHES = 326;

	private static final String CONTENT_FRAG_BEFORE_DETALHES_KEY = "contentFragBeforeDetalhes";
	private static final String CONTENT_FRAG_TAG_BEFORE_DETALHES_KEY = "contentFragTagBeforeDetalhes";
	private final String TITLE = "Acervo Remoto";
	private static final String PILHA = null;
	private int CONTENT_FRAG = FRAG_RECOMENDACOES;
	private String CONTENT_FRAG_TAG = BuscaController.TAG;
	private Fragment menuController, drawerMenu, tagsController,detalhesController, buscaController, transferindoController;
	private RecomendacoesControllerComposite recomendacoesController;
	private Toolbar toolbar;
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle toggle;
	private String query;
	private MenuItem menuItem;
	private SearchView searchView;
	private TextView tv_search;
	private int termosId;
	private boolean fromNotification;
	private TabLayout tabLayout;
	private ViewPager viewPager;
	private CollapsingToolbarLayout collapsingToolbarLayout;
	private ImageView iconType;
	private View panelRecomendacoes;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_acervo_remoto);
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		toolbar.setTitleTextColor(Color.WHITE);
		getSupportActionBar().setTitle(TITLE);
		panelRecomendacoes = findViewById(R.id.panel_recomendacoes);
		tabLayout = (TabLayout) findViewById(R.id.tab_layout);
		collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collasingtoolbar);
		iconType = (ImageView) findViewById(R.id.icon_type);
		tabLayout.setOnTabSelectedListener(this);
		viewPager = (ViewPager) findViewById(R.id.view_pager);
		viewPager.addOnPageChangeListener(new TabLayoutOnPageChangeListener(tabLayout));
		viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
				int colorTipos = getResources().getColor(R.color.tipos_color);
				Integer[] colors = {getResources().getColor(R.color.acervo_remoto_primary), colorTipos, colorTipos, colorTipos, colorTipos};
				int finalcolor;
				if (position < (viewPager.getAdapter().getCount() - 1) && position < (colors.length - 1)) {
					finalcolor = (Integer) new ArgbEvaluator().evaluate(positionOffset, colors[position], colors[position + 1]);
					tabLayout.setBackgroundColor(finalcolor);
					collapsingToolbarLayout.setBackgroundColor(finalcolor);
				} else {
					finalcolor = colors[colors.length - 1];
					tabLayout.setBackgroundColor(finalcolor);
					collapsingToolbarLayout.setBackgroundColor(finalcolor);
				}
			}

			@Override
			public void onPageSelected(int position) {}

			@Override
			public void onPageScrollStateChanged(int state) {}
		});
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
		toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name, R.string.app_name);
		mDrawerLayout.setDrawerListener(toggle);
		termosId = 0;
		if(savedInstanceState != null){
			termosId = savedInstanceState.getInt(TERMOS_ID);
			CONTENT_FRAG = savedInstanceState.getInt(CONTENT_FRAG_KEY);
			CONTENT_FRAG_TAG = savedInstanceState.getString(CONTENT_FRAG_TAG_KEY);
		} else{
			handleIntent(getIntent());
		}
		fromNotification = getIntent().getBooleanExtra("fromNotification", false);
		if(fromNotification){
			CONTENT_FRAG = FRAG_TRANSFERINDO;
		}
		initControllers();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onResume() {
		super.onResume();
		((DrawerMenuController)drawerMenu).selectAcervoRemoto();
		selectTags();
		BroadcastUtil.setBroadcastEnableState(this, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, BroadcastSync.class);
	}

	@Override
	protected void onPause() {
		super.onPause();
		BroadcastUtil.setBroadcastEnableState(this, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, BroadcastSync.class);
	}

	private void initControllers() {
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction transation = fm.beginTransaction();
		recomendacoesController = new RecomendacoesControllerComposite(this, getSupportFragmentManager());
		final RecomendacoesTabsAdapter tabsAdapter = recomendacoesController.getRecomendacoesTabAdapter();
		viewPager.setAdapter(tabsAdapter);
		tabLayout.setTabsFromPagerAdapter(tabsAdapter);
		drawerMenu = setFragment(FRAG_NAV_MENU, fm, transation);
		tagsController = setFragment(FRAG_TAGS, fm, transation);
		menuController = setFragment(FRAG_MENU, fm, transation);
		buscaController = setFragment(FRAG_BUSCA, fm, transation);
		transferindoController = setFragment(FRAG_TRANSFERINDO, fm, transation);
		detalhesController = setFragment(FRAG_DETALHESITEM, fm, transation);

		Fragment f = selectContentFragmet();
		if(!f.isAdded()) {
			transation
					.replace(R.id.panel_content, f, CONTENT_FRAG_TAG)
					.commit();
		}
	}

	private void selectTags() {
		ArrayList<Tag> tags = ((TagsController) tagsController).getTagsByTipo(TagsController.TYPE_TIPO);
		for (Tag tag : tags){
			((MenuController)menuController).selectFiltro(tag.id);
		}
	}

	private Fragment setFragment(int frag, FragmentManager fm, FragmentTransaction t) {
		Fragment f = null;
		switch (frag) {
			case FRAG_NAV_MENU:
				if((f = fm.findFragmentByTag(DrawerMenuController.TAG))==null){
					f = new DrawerMenuController().init(this, DrawerMenuController.ACERVOREMOTO);
					t.add(R.id.panel_navdrawer, f, DrawerMenuController.TAG);
				}
				break;
			case FRAG_RECOMENDACOES:
				break;
			case FRAG_BUSCA:
				if((f=fm.findFragmentByTag(BuscaController.TAG))==null){
					f = new BuscaController().init(this);
				}
				break;
			case FRAG_MENU:
				if((f=fm.findFragmentByTag(MenuController.TAG))== null){
					f = new MenuController().init(this, MenuView.TYPE.ACERVOREMOTO);
				}
				t.replace(R.id.container_tipos, f, MenuController.TAG);
				break;
			case FRAG_TAGS:
				if((f=fm.findFragmentByTag(TagsController.TAG))==null){
					f = new TagsController().init(TagsController.ContextualType.ACERVOREMOTO);
				}
				t.replace(R.id.panel_tagview, f, TagsController.TAG);
				break;
			case FRAG_TRANSFERINDO:
				if((f=fm.findFragmentByTag(TransferindoController.TAG))== null){
					f = new TransferindoController().init(this, TransferindoController.FROM_ACERVOREMOTO);
				}
				break;
			case FRAG_DETALHESITEM:
				if((f= fm.findFragmentByTag(DetalhesItemController.TAG)) == null) {
					f = new DetalhesItemController().init(this, DetalhesItemController.DetalhesContext.ACERVO_REMOTO);
				}				break;
			default:
				break;
		}
		return f;
	}

	private Fragment selectContentFragmet() {
		switch(CONTENT_FRAG){
			case FRAG_DETALHESITEM:
				return detalhesController;
			case FRAG_RECOMENDACOES:
				panelRecomendacoes.setVisibility(View.VISIBLE);
				return buscaController;
			case FRAG_BUSCA:
				panelRecomendacoes.setVisibility(View.GONE);
				return buscaController;
			case FRAG_TRANSFERINDO:
				return transferindoController;
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

	private void doMySearch(String query) {
		Tag tag = new Tag(query, Color.parseColor("#005aaa"), 0, TagsController.TYPE_TERMO_EDITABLE);
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
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		toggle.onConfigurationChanged(newConfig);
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
	public void showFiltro(int id, MenuView.TYPE type) {
		Fragment f = null;
		int container = R.id.panel_content;
		switch (id) {
			case MenuView.ID_MEUS_GRUPOS:
				//DO NOTHING
				break;
			case MenuView.ID_TRANSFERENCIAS:
				f = transferindoController;
				CONTENT_FRAG = FRAG_TRANSFERINDO;
				CONTENT_FRAG_TAG = TransferindoController.TAG;
				container = R.id.panel_content_list_filtros;
				break;
			default:
				break;
		}
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(container, f, CONTENT_FRAG_TAG);
		transaction.commit();
	}

	@Override
	public void hideFiltro(int id) {
		onBackPressed();
	}


	@Override
	public void showTransferindo() {}

	@Override
	public void showMeusGrupos() {}

	@Override
	public void addTag(Tag tag) {
		if(CONTENT_FRAG == FRAG_TRANSFERINDO) {
			onBackPressed();
		}
		showResultadoBusca();
		((TagsController)tagsController).addTag(tag);
	}

	@Override
	public void habilitaToolbarContextual(int valorParaGruposSelecionados, int itemStatus, boolean disable) {}

	@Override
	public void removeTag(Tag tag) {
		showResultadoBusca();
		((TagsController)tagsController).removeTag(tag);
		if(CONTENT_FRAG == FRAG_TRANSFERINDO) {
			onBackPressed();
		}
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
	public void showResultadoBusca() {
		((MenuController) menuController).deselectFiltro(-1);
		panelRecomendacoes.setVisibility(View.GONE);
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction
				.replace(R.id.panel_content, buscaController, BuscaController.TAG)
				.commit();
		CONTENT_FRAG = FRAG_BUSCA;
		CONTENT_FRAG_TAG = BuscaController.TAG;
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
	public void editTag(Tag tag) {
		searchView.setQuery(tag.text, false);
	}

	@Override
	public void onEmptyTags() {
		((BuscaController)buscaController).clear();
		showRecomendacoes();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt(CONTENT_FRAG_KEY, CONTENT_FRAG);
		outState.putString(CONTENT_FRAG_TAG_KEY, CONTENT_FRAG_TAG);
		outState.putInt(TERMOS_ID, termosId);
		/** CONTENT_FRAG_BEFORE_DETALHES **/
		outState.putInt(CONTENT_FRAG_BEFORE_DETALHES_KEY, CONTENT_FRAG_BEFORE_DETALHES);
		outState.putString(CONTENT_FRAG_TAG_BEFORE_DETALHES_KEY, CONTENT_FRAG_TAG_BEFORE_DETALHES);
		super.onSaveInstanceState(outState);
	}

	@Override
	public boolean onSelectNavItem(int position) {
		if(mDrawerLayout.isDrawerOpen(Gravity.LEFT))
			mDrawerLayout.closeDrawers();
		return true;
	}

	@Override
	public void abrirSuporte() {	}

	@Override
	public void abrirCreditos() {}

	@Override
	public void mostrarTutorial() {
		Intent it = new Intent(this, ActivityTutorial.class);
		startActivityForResult(it, REQUEST_CODE_TUTORIAL);
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
	public void finishTransferencia(){
		((MenuController)menuController).finishTranferencia();
	}

	@Override
	public void piscarTransferindo() {
		((MenuController)menuController).piscarTransferindo();
	}

	@Override
	public void showDetalhes(Item i) {
		Intent it = new Intent(this, ActivityDetalhes.class);
		it.putExtra("item", i);
		it.putExtra("color", Color.RED);
		it.putExtra("fromContext", 1);
		startActivityForResult(it, REQUEST_CODE_DETALHES);
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
				Item item;
				if (resultCode == RESULT_OK) {
					switch (CONTENT_FRAG){
						case FRAG_BUSCA:
							item = (Item) data.getParcelableExtra("item");
							((BuscaController)buscaController).updateFromDetalhes(item);
							break;
						case FRAG_RECOMENDACOES:
							item = (Item) data.getParcelableExtra("item");
							recomendacoesController.updateFromDetalhes(item);
							break;
					}
				} else {
				}
			default:
				break;
		}
	}

	@Override
	public void onBackPressed() {
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		switch(CONTENT_FRAG){
			case FRAG_TRANSFERINDO:
				((MenuController) menuController).deselectFiltro(-1);
				transaction
						.remove(transferindoController)
						.commit();
					CONTENT_FRAG = FRAG_BUSCA;
					CONTENT_FRAG_TAG = BuscaController.TAG;
				break;
			case FRAG_DETALHESITEM:
				CONTENT_FRAG = CONTENT_FRAG_BEFORE_DETALHES;
				CONTENT_FRAG_TAG = CONTENT_FRAG_TAG_BEFORE_DETALHES;
				if(CONTENT_FRAG == FRAG_TRANSFERINDO){
					((MenuController)menuController).selectFiltro(MenuView.ID_TRANSFERENCIAS);
				}
				super.onBackPressed();
				break;
			case FRAG_BUSCA:
				((TagsController)tagsController).removeAll();
				break;
			case FRAG_RECOMENDACOES:
				super.onBackPressed();
				break;
			default:
		}
	}


	private void showRecomendacoes() {
		FragmentManager fm = getSupportFragmentManager();
		for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
			fm.popBackStack();
		}
		panelRecomendacoes.setVisibility(View.VISIBLE);
		CONTENT_FRAG = FRAG_RECOMENDACOES;
		CONTENT_FRAG_TAG = RecomendacoesController.TAG;
	}

	@Override
	public void addTagFromRecomendacoes(Tag tag) {
		addTag(tag);
		((MenuController)menuController).selectFiltro(tag.id);
	}

	@Override
	public void addTagFromRecomendacoes(String query) {
		doMySearch(query);
	}

	@Override
	public void onTabSelected(TabLayout.Tab tab) {
		ScaleAnimation zoom = new ScaleAnimation(0.5f, 1, 0.5f,1);
		zoom.setDuration(500);
		switch (tab.getPosition()){
			case 0:
				iconType.setVisibility(View.GONE);
				break;
			case 1:
				iconType.setVisibility(View.VISIBLE);
				iconType.setImageResource(R.drawable.icon_texto);
				iconType.startAnimation(zoom);
				break;
			case 2:
				iconType.setVisibility(View.VISIBLE);
				iconType.setImageResource(R.drawable.icon_audio);
				iconType.startAnimation(zoom);
				break;
			case 3:
				iconType.setVisibility(View.VISIBLE);
				iconType.setImageResource(R.drawable.icon_video);
				iconType.startAnimation(zoom);
				break;
			case 4:
				iconType.setVisibility(View.VISIBLE);
				iconType.setImageResource(R.drawable.icon_imagem);
				iconType.startAnimation(zoom);
		}
		viewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(TabLayout.Tab tab) {}

	@Override
	public void onTabReselected(TabLayout.Tab tab) {}
}
