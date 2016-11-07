package br.ufpe.sabertecnologias.acervoapp.modelo;

import android.app.Application;
import android.content.Context;
import android.content.ServiceConnection;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Messenger;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import br.ufpe.sabertecnologias.acervoapp.modelo.dados.AppConfig;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Grupo;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Item;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.LogGrupoAcaoEnum;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.LogItemAcaoEnum;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.LogItemComplementoEnum;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.User;
import br.ufpe.sabertecnologias.acervoapp.modelo.negocio.NegocioAppConfig;
import br.ufpe.sabertecnologias.acervoapp.modelo.negocio.NegocioAutenticacao;
import br.ufpe.sabertecnologias.acervoapp.modelo.negocio.NegocioRecomendacao;
import br.ufpe.sabertecnologias.acervoapp.modelo.negocio.NegocioTransferindo;
import br.ufpe.sabertecnologias.acervoapp.modelo.negocio.acervo_remoto.BuscaResponseCallback;
import br.ufpe.sabertecnologias.acervoapp.modelo.negocio.acervo_remoto.INegocioRemoto;
import br.ufpe.sabertecnologias.acervoapp.modelo.negocio.acervo_remoto.NegocioAcervoRemoto;
import br.ufpe.sabertecnologias.acervoapp.modelo.negocio.acervo_remoto.TipoTermoBusca;
import br.ufpe.sabertecnologias.acervoapp.modelo.negocio.log.NegocioLog;
import br.ufpe.sabertecnologias.acervoapp.modelo.negocio.meu_acervo.INegocioMeuAcervo;
import br.ufpe.sabertecnologias.acervoapp.modelo.negocio.meu_acervo.NegocioMeuAcervo;
import br.ufpe.sabertecnologias.acervoapp.modelo.negocio.sync.NegocioSincronizacao;
import br.ufpe.sabertecnologias.acervoapp.modelo.negocio.sync.NegocioSincronizacao.SetupInicialListener;
import br.ufpe.sabertecnologias.acervoapp.modelo.negocio.sync.SyncCallback;
import br.ufpe.sabertecnologias.acervoapp.modelo.repositorios.DatabaseObserverManager;
import br.ufpe.sabertecnologias.acervoapp.ui.callbacks.BuscaMeuAcervoResponseCallback;
import br.ufpe.sabertecnologias.acervoapp.ui.callbacks.RecomendacoesResponseCallback;
import br.ufpe.sabertecnologias.acervoapp.util.DebugLog;

public class Facade extends Application implements INegocioRemoto, INegocioMeuAcervo {

	private NegocioAutenticacao negocioAutenticacao;
	private NegocioAcervoRemoto negocioAcervoRemoto;
	private NegocioMeuAcervo negocioMeuAcervo;
	private NegocioTransferindo negocioTransferindo;
	private NegocioLog negocioLog;
	private NegocioSincronizacao negocioSincronizacao;
	private NegocioAppConfig negocioAppConfig;
	private NegocioRecomendacao negocioRecomendacao;


	@Override
	public void onCreate() {
		super.onCreate();

		DatabaseObserverManager.init(this);

		negocioAutenticacao = NegocioAutenticacao.getInstance(this);
		negocioMeuAcervo = NegocioMeuAcervo.getInstance(this);
		negocioAcervoRemoto = NegocioAcervoRemoto.getInstance(this);
		negocioTransferindo = NegocioTransferindo.getInstance(this);
		negocioSincronizacao = NegocioSincronizacao.getInstance(this);
		
		negocioAppConfig = NegocioAppConfig.getInstance(this);
		negocioLog = NegocioLog.getInstance(this);
		negocioRecomendacao = NegocioRecomendacao.getInstance(this);
		DebugLog.d(this, "APPLICATION.ONCREATE()");

	}

	public User getUsuario(){
		return negocioAutenticacao.getUsuario();
	}

	public boolean existeUsuarioLogado() {
		return negocioAutenticacao.existeUsuarioLogado();
	}

	public void login(String username, String password, IExecutor executor){
		negocioAutenticacao.login(username, password, executor);
	}

	public void logout() {
		negocioAutenticacao.logout();
	}

	public void transferirItem(Item item, IExecutor executor){
		negocioTransferindo.transferirItem(item, executor);
	}
	
	public void getItens(IExecutor executor) {
		negocioMeuAcervo.getItens(executor);
	}

	@Override
	public void addItem(Item item, int idGrupo, IExecutor executor) {
		negocioMeuAcervo.addItem(item, idGrupo, executor);
	}

	@Override
	public void attItem(Item i, int idGrupo, IExecutor executor) {
		negocioMeuAcervo.attItem(i, idGrupo,executor);
	}

	@Override
	public void attItem(List<Item> is, int idGrupo, IExecutor executor) {
		negocioMeuAcervo.attItem(is, idGrupo, executor);
	}
	
	public void downloadItem(int item_id, String url, String formato, String md5) {
		negocioTransferindo.downloadItem(item_id, url, formato, md5);
	}
	
	@Override
	public boolean hasNextPage() {
		return negocioAcervoRemoto.hasNextPage();
	}

	@Override
	public void removeTermo(String termo, TipoTermoBusca tipoTermo) {
		negocioAcervoRemoto.removeTermo(termo, tipoTermo);
	}

	@Override
	public void setBuscaResponseListener(BuscaResponseCallback listener) {
		negocioAcervoRemoto.setBuscaResponseListener(listener);
	}

	@Override
	public void addTermoBuscaAcervoRemoto(String text, TipoTermoBusca tipo) {
		negocioAcervoRemoto.addTermoBuscaAcervoRemoto(text, tipo);
	}
	
	@Override
	public void removeAllTermos() {
		negocioAcervoRemoto.removeAllTermos();
	}

	@Override
	public void getNextPageBusca() {
		negocioAcervoRemoto.getNextPageBusca();
	}
	
	@Override
	public Item getItemByID(int item_codigo, IExecutor executor) {
		return negocioMeuAcervo.getItemByID(item_codigo, executor);
	}

	@Override
	public Item getItemByCodigo(int item_codigo, IExecutor executor) {
		return negocioMeuAcervo.getItemByCodigo(item_codigo, executor);
	}

	@Override
	public void abrirItem(Item item) {
		negocioMeuAcervo.abrirItem(item);
	}

	@Override
	public Grupo getDefaultGrupo() {
		return negocioMeuAcervo.getDefaultGrupo();
	}
	
	@Override
	public void addGrupo(Grupo g, IExecutor executor) {
		negocioMeuAcervo.addGrupo(g, executor);
	}
	
	@Override
	public void getItensByGrupo(int grupoID, IExecutor executor) {
		negocioMeuAcervo.getItensByGrupo(grupoID, executor);
	}
	
	@Override
	public void setDefaultGrupoID(int id) {
		negocioMeuAcervo.setDefaultGrupoID(id);
	}
	
	@Override
	public ArrayList<Integer> getItensCodigos(){
		return negocioMeuAcervo.getItensCodigos();
	}
	
	@Override
	public void removeItem(Item i, IExecutor executor) {
		negocioMeuAcervo.removeItem(i, executor);
	}

	@Override
	public void addTermoBuscaMeuAcervo(String termo, TipoTermoBusca tipoTermo) {
        negocioMeuAcervo.addTermoBuscaMeuAcervo(termo, tipoTermo);
	}

	public void cancelaDownload(IExecutor executor, int codigoItem) {
		negocioTransferindo.cancelaDownload(codigoItem);
		empilhaLogItem(executor, codigoItem, LogItemAcaoEnum.DOWNLOAD, LogItemComplementoEnum.CANCELADO.toString());
	}

	public ArrayList<Item> getItensTransferindo() {
		return (ArrayList<Item>) negocioTransferindo.getItensTransferindo();
	}

	public void limparTransferindo(IExecutor executor) {
		negocioTransferindo.limparTransferindo(executor);
	}

	public void removeGrupo(Grupo grupo, IExecutor executor) {
		negocioMeuAcervo.removeGrupo(grupo, executor);
	}

	public void updateGrupo(Grupo g1, IExecutor executor) {
		negocioMeuAcervo.updateGrupo(g1, executor);
	}

	public void setUser(IExecutor executor) {
		negocioLog.setUser(executor);
		negocioAcervoRemoto.setUser(executor);
		negocioAutenticacao.setUser(executor);
		negocioRecomendacao.setUser(executor);
	}

    public int empilhaLogItem(IExecutor executor, int codItem, LogItemAcaoEnum acao, String complemento)
    {
        return negocioLog.empilhaLogItem(executor, codItem, acao, complemento);
    }

	public int empilhaLogItem(IExecutor executor, int codItem, LogItemAcaoEnum acao, Long complemento, Context context)
	{
		return negocioLog.empilhaLogItem(executor, codItem, acao, complemento, context);
	}

    public int empilhaLogGrupo(IExecutor executor, LogGrupoAcaoEnum acao, String nome, String uuid, String cor)
    {
        return negocioLog.empilhaLogGrupo(executor, acao, nome, uuid, cor);
    }

	public void empilhaLogTransaction(IExecutor executor,final Item i, final Grupo oldGrupo,  final Grupo newGrupo, final boolean newGrupoExisist) {
		negocioLog.empilhaLogTransaction(executor, i, oldGrupo, newGrupo, newGrupoExisist);
	}
    
    public void enviarPilhaLog(IExecutor executor, SyncCallback syncCallback, Context context)
    {
        negocioLog.sendLog(executor, syncCallback, context);
    }

	public void setupServidor(SetupInicialListener listener) throws NameNotFoundException {
		negocioSincronizacao.setupServidor(listener);
	}

	public void getGrupos(IExecutor executor) {
		negocioMeuAcervo.getGrupos(executor);
	}

	public void getGruposFromServer(NegocioSincronizacao.GetGruposListener listener){
		negocioSincronizacao.getGruposFromServer(listener);
	}

	public void getItensFromServer(NegocioSincronizacao.GetItensListener listener){
		negocioSincronizacao.getItensFromServer(listener);
	}

	public void persistirGruposFromSync(IExecutor executor, JSONObject json){
		negocioSincronizacao.persistirGruposFromSync(executor, json);
	}

	public void persistirItensFromSync(IExecutor executor, JSONObject json, List<Item> itensOld){
		negocioSincronizacao.persistirItensFromSync(executor, json, itensOld);
	}

	public void getAppConfig(IExecutor executor) {
		negocioAppConfig.getAppConfig(executor);
	}

	public void attAppConfig(IExecutor executor, AppConfig appConfig) {
		negocioAppConfig.attAppConfig(executor, appConfig);
	}

	public void sync(boolean isFromRefresh){
		negocioSincronizacao.sync(isFromRefresh);
	}


	public void sync(Messenger callback, ServiceConnection conn){
		negocioSincronizacao.sync(callback, conn);
	}

	public void enableSyncAlarme() {
		negocioSincronizacao.enableSyncAlarm();
	}

	public void enableSetupServerAlarme(){
		negocioSincronizacao.enableSetupServerAlarme();
	}

	public void setupServidor() {
		negocioSincronizacao.setupServidor();
	}

	public void finishSync(ServiceConnection conn) {
		negocioSincronizacao.finishSync(conn);
	}

	public void empilhaLogItensRemovidosFromGrupoRemovido(IExecutor executor, Grupo g) {
		negocioLog.empilhaLogItensRemovidosFromGrupoRemovido(executor, g);
	}

    public void cancelaSendLog() {
        negocioLog.cancelaSendLog();
    }

    public void cancelaReceiveSync() {
        negocioSincronizacao.cancelSync();
    }

	public void empilhaLogFeedback(IExecutor executor, String suporteDialogText) {
		negocioLog.empilhaLogFeedback(executor, suporteDialogText);
	}

	public void setBuscaMeuAcervoResponseListener(BuscaMeuAcervoResponseCallback buscaMeuAcervoCallback) {
		negocioMeuAcervo.setBuscaMeuAcervoResponseListener(buscaMeuAcervoCallback);
	}

	public void removerTermoBuscaMeuAcervo(String text, TipoTermoBusca tipo) {
		negocioMeuAcervo.removerTermoBusca(text, tipo);
	}

	public void removeAllTermosMeuAcervo() {
		negocioMeuAcervo.removeAllTermos();
	}

	public void buscarListaRecomendacoes(RecomendacoesResponseCallback recomendacoesResponseCallback, int type) {
		negocioRecomendacao.buscarListaRecomendacoes(recomendacoesResponseCallback, type);
	}

	public void showAviso() {
		negocioTransferindo.showAviso();
	}

	public void hideAviso(){
		negocioTransferindo.hideAviso();
	}

	public boolean isShowingAviso(){
		return  negocioTransferindo.isShowingAviso();
	}

	public void buscarRecentes(String tipo, BuscaResponseCallback callback) throws Exception {
		negocioAcervoRemoto.buscarRecentes(tipo, callback);
	}
}