package br.ufpe.sabertecnologias.acervoapp.modelo.negocio.log;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.ufpe.sabertecnologias.acervoapp.modelo.Executor;
import br.ufpe.sabertecnologias.acervoapp.modelo.ExecutorListener;
import br.ufpe.sabertecnologias.acervoapp.modelo.IExecutor;
import br.ufpe.sabertecnologias.acervoapp.modelo.Task;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Grupo;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Item;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.JsonObjectFieldsEnum;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.JsonObjectNamesEnum;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.LogBase;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.LogFeedback;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.LogFimSessao;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.LogGrupo;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.LogGrupoAcaoEnum;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.LogInicioSessao;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.LogItem;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.LogItemAcaoEnum;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.LogItemComplementoEnum;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.SessaoControle;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.SessaoControleStatusEnum;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.Timestamp;
import br.ufpe.sabertecnologias.acervoapp.modelo.mock.IService;
import br.ufpe.sabertecnologias.acervoapp.modelo.mock.LocalService;
import br.ufpe.sabertecnologias.acervoapp.modelo.mock.MockConfig;
import br.ufpe.sabertecnologias.acervoapp.modelo.negocio.BaseNegocio;
import br.ufpe.sabertecnologias.acervoapp.modelo.negocio.sync.SyncCallback;
import br.ufpe.sabertecnologias.acervoapp.util.ConnectionUtil;
import br.ufpe.sabertecnologias.acervoapp.util.DateUtil;
import br.ufpe.sabertecnologias.acervoapp.util.DebugLog;

public class NegocioLog extends BaseNegocio {
	private final IService server;
	private JSONObject sendInfo;
	List<LogBase> listaLog;

	private static NegocioLog instance;


	public static NegocioLog getInstance(Context context) {
		if(instance == null){
			instance = new NegocioLog(context);
		}
		return instance;
	}

	private NegocioLog(Context context) {
		super(context);
		server = LocalService.getInstance(mContext);//new RemoteService(mContext);
	}


	public void sendLog(IExecutor executor, final SyncCallback syncCallback, final Context context)
	{

		if(ConnectionUtil.getConnectivityStatus(mContext) != ConnectionUtil.TYPE_NOT_CONNECTED) {
			Task task = new Task() {

				@Override
				public Object run(ExecutorListener executorListener) {

					try {
						sendInfo = prepareJson();
					} catch (Exception e) {
						return  false;
					}
					return listaLog.size() > 0 ? true : false ;
				}

				@Override
				public void postRun(ExecutorListener executorListener, Object obj) {

					boolean hasLog  = (boolean) obj;

					if(hasLog) {
						server.sendLog(sendInfo, new IService.SendLogCallback() {
							@Override
							public void onResponseSendLog(JSONObject jsonObject) {
								DebugLog.d(this, "onResponseSendLog");
								syncCallback.onResponseSendLog(jsonObject);
							}

							@Override
							public void onErrorSendLog() {
								syncCallback.onErrorSendLog();
							}
						});

					} else {
						syncCallback.onResponseSendLog(null);
					}
				}
			};

			executor.execute(task);
		}
	}

    public void cancelaSendLog() {
      server.cancelSendLog();
    }

	private JSONObject prepareJson() throws Exception {
		JSONObject json = new JSONObject();
		JSONArray logArrayJson = getLogArray();

		if(usuario == null){
			setUser(new Executor(null));
		}

		//dados de sessão/usuário
		json.put(JsonObjectFieldsEnum.USER_ID.toString(), MockConfig.MOCK_USERID);
		json.put(JsonObjectFieldsEnum.DEVICE_ID.toString(),MockConfig.MOCK_DEVICE_ID );
		json.put(JsonObjectFieldsEnum.SIGNATURE.toString(), usuario.getToken());

		json.put(JsonObjectFieldsEnum.LISTALOG.toString(), logArrayJson);
		DebugLog.d(this, json.toString());
		return json;

	}

	private JSONArray getLogArray() throws JSONException {
		JSONArray retorno = new JSONArray();
		listaLog = getPilhaLogs();

		for(LogBase element : listaLog)
		{
			if(element instanceof LogItem)
			{
				retorno.put(((LogItem) element).toJSONObject(mContext));
			}
			else if(element instanceof LogInicioSessao)
			{
				retorno.put(((LogInicioSessao) element).toJSONObject());
			}
			else if(element instanceof LogFimSessao)
			{
				retorno.put(((LogFimSessao) element).toJSONObject());
			}
			else if(element instanceof LogGrupo)
			{
				retorno.put(((LogGrupo) element).toJSONObject());
			}
			else if(element instanceof LogFeedback)
			{
				retorno.put(((LogFeedback) element).toJSONObject());
			}
		}

		return retorno;
	}

	///Metodos de persistencia de log
	//metodos de controle da sessao local
	private SessaoControle getLastSessaoLocal(){
		List<SessaoControle> sessaoControleList = repSessaoControle.get();
		SessaoControle lastSessaoControle = null;
		if (sessaoControleList != null && !sessaoControleList.isEmpty()){
			lastSessaoControle = sessaoControleList.get((sessaoControleList.size() - 1));
		}
		return lastSessaoControle;
	}
	private int iniciaNovaSessaoLocal(){
		int out =-1;
		SessaoControle lastSessao = getLastSessaoLocal();
		if (lastSessao == null){
			long milisSec = System.currentTimeMillis();
			SessaoControle novaSessao = new SessaoControle(String.valueOf(milisSec), SessaoControleStatusEnum.ABERTO.toString());
			out = repSessaoControle.insertTransaction(novaSessao);
		}
		else if (lastSessao.getStatus().equals(SessaoControleStatusEnum.ABERTO.toString())){
			SessaoControle novaSessao = new SessaoControle(lastSessao.getSessaoMilis(), SessaoControleStatusEnum.FALHA.toString());
			out = repSessaoControle.insertTransaction(novaSessao);
			//adiciona um log de fim de sessao com falha
			Executor exec = new Executor(null);
			empilhaLogFimSessao(exec,true);
			iniciaNovaSessaoLocal();
		}
		else if((lastSessao.getStatus().equals(SessaoControleStatusEnum.OK.toString()))
				|| (lastSessao.getStatus().equals(SessaoControleStatusEnum.FALHA.toString()))){
			//int numLastSessao = Integer.parseInt(  lastSessao.getNumSessao());
			//agora pega o milisec atual
			long milisSec = System.currentTimeMillis();
			SessaoControle novaSessao = new SessaoControle(String.valueOf(milisSec), SessaoControleStatusEnum.ABERTO.toString());
			out = repSessaoControle.insertTransaction(novaSessao);
		}
		return out;
	}
	private int encerraSessaoLocal(){
		int out=-1;
		SessaoControle lastSessao = getLastSessaoLocal();
		if (lastSessao.getStatus().equals(SessaoControleStatusEnum.ABERTO.toString())){
			SessaoControle novaSessao = new SessaoControle(lastSessao.getSessaoMilis(), SessaoControleStatusEnum.OK.toString());
			out = repSessaoControle.insertTransaction(novaSessao);
		}
		return out;
	}

	//metodo para persistencia de log
	private List<LogBase> getPilhaLogs(){
		List<LogBase> listaCompleta = new ArrayList<LogBase>();

		for (LogBase it : repLogFimSessao.get()){
			listaCompleta.add(it);
		}
		for (LogBase it : repLogInicioSessao.get()){
			listaCompleta.add(it);
		}

		for (LogBase it : repLogGrupo.get()){
			listaCompleta.add(it);
		}

		for (LogBase it : repLogItem.get()){
			listaCompleta.add(it);
		}

		for (LogBase it : repLogFeedback.get()){
			listaCompleta.add(it);
		}

		Collections.sort(listaCompleta);

		DebugLog.d(this, "LISTA COMPLETA - \n" + listaCompleta.toString());

		return listaCompleta;
	}

	private List<LogBase> limpaPilhaLogs(){
		for (LogBase element : listaLog){
			if(element instanceof LogItem)
			{
				repLogItem.delete((LogItem) element);
			}
			else if(element instanceof LogInicioSessao)
			{
				repLogInicioSessao.delete((LogInicioSessao) element);
			}
			else if(element instanceof LogFimSessao)
			{
				repLogFimSessao.delete((LogFimSessao) element);
			}
			else if(element instanceof LogGrupo)
			{
				repLogGrupo.delete((LogGrupo) element);
			}
			else if(element instanceof LogFeedback)
			{
				repLogFeedback.delete((LogFeedback) element);
			}
		}
		return getPilhaLogs();
	}

	private String getSessaoLocalAtual(){
		//retorna a string dos milissec do start da ultima sessao
		SessaoControle lastSessao = getLastSessaoLocal();
		return lastSessao.getSessaoMilis();
	}

	private Timestamp getTimestamp(){
		long tsLong = System.currentTimeMillis();
		return DateUtil.getTimestampObject(tsLong);
	}

	private Timestamp getTimestampDefasado(){
		long tsLong = System.currentTimeMillis();
		tsLong -= 1;
		return DateUtil.getTimestampObject(tsLong);
	}

	//metodos para armazenar os logs
	public int empilhaLogInicioSessao(IExecutor executor){
		Task task = new Task() {
			@Override
			public Object run(ExecutorListener executorListener) {
				//RUN
				iniciaNovaSessaoLocal();
				int out=-1;
				LogInicioSessao newlog = new LogInicioSessao(JsonObjectNamesEnum.INICIO_SESSAO.toString()
						//, getSessaoLocalAtual()
						, getTimestamp()
						//ESPECIFICO
						, RedeInfo.getNetworkClass(mContext));
				repLogInicioSessao.insertTransaction(newlog);
				return out;
				//END RUN
			}
			@Override
			public void postRun(ExecutorListener executorListener, Object obj) {
				if (executorListener != null){
					executorListener.postResult(obj);
				}
			}
		};
		executor.execute(task);
		return 0;
	}

	public int empilhaLogFimSessao(IExecutor executor){
		return empilhaLogFimSessao(executor, false);
	}
	public int empilhaLogFimSessao(IExecutor executor, final boolean falha){
		Task task = new Task() {
			@Override
			public Object run(ExecutorListener executorListener){
				//RUN
				encerraSessaoLocal();
				int out=-1;
				Timestamp timestamp_end =  falha ? getTimestampDefasado() : getTimestamp();
				Timestamp timestamp_start = getLastTimestampStart();
				LogFimSessao newlog = new LogFimSessao(JsonObjectNamesEnum.FIM_SESSAO.toString()
						//, getSessaoLocalAtual()
						, timestamp_end
						,timestamp_start
						//ESPECIFICO
						, falha);
				repLogFimSessao.insertTransaction(newlog);
				return out;
				//END RUN
			}

			@Override
			public void postRun(ExecutorListener executorListener, Object obj) {
				if (executorListener != null){
					executorListener.postResult(obj);
				}
			}
		};
		executor.execute(task);
		return 0;
	}

	public int empilhaLogItem(IExecutor executor, final int codItem,final LogItemAcaoEnum acao, final long complemento){
		return empilhaLogItem(executor, codItem, acao , ""+complemento);
	}

	public int empilhaLogItem(IExecutor executor, final int codItem,final LogItemAcaoEnum acao, final int complemento){
		return empilhaLogItem(executor, codItem, acao, "" + complemento);
	}

	public int empilhaLogItem(IExecutor executor, int codItem, LogItemAcaoEnum acao, Long complemento, Context context){

		return empilhaLogItem(executor, codItem, acao , complemento +" "+RedeInfo.getNetworkClass(context));
	}

	public int empilhaLogItem(IExecutor executor, final int codItem,final LogItemAcaoEnum acao, final String complemento){
		final Context ctx = mContext;
		Task task = new Task(){


			@Override
			public Object run(ExecutorListener executorListener) {
				int out=-1;
				LogItem newlog = new LogItem(JsonObjectNamesEnum.ITEM.toString()
						//, getSessaoLocalAtual()
						, getTimestamp()
						//ESPECIFICO
						, codItem
						, acao
						, complemento);
				repLogItem.insertTransaction(newlog);		
				return out;
			}

			@Override
			public void postRun(ExecutorListener executorListener, Object obj) {
				if (executorListener != null){
					executorListener.postResult(obj);
				}
			}

		};
		executor.execute(task);
		return 0;

	}

	public void empilhaLogItensRemovidosFromGrupoRemovido(IExecutor executor, final Grupo g) {
		Task task = new Task() {
			@Override
			public Object run(ExecutorListener executorListener) {
				List<Item> itens = g.getItens();
				Item item;
				Executor ex;
				for(int i=0; i<itens.size();i++){
					item = itens.get(i);
					ex = new Executor(null);
					empilhaLogItem(ex, item.getCodigo(), LogItemAcaoEnum.EXCLUIR, LogItemComplementoEnum.LOCAL.toString());
					empilhaLogItem(ex, item.getCodigo(), LogItemAcaoEnum.REMOVER, null);

				}
				return null;
			}

			@Override
			public void postRun(ExecutorListener executorListener, Object obj) {
				if(executorListener!=null){
					executorListener.postResult(obj);
				}

			}
		};
		executor.execute(task);
	}

	public int empilhaLogGrupo(IExecutor executor, final LogGrupoAcaoEnum acao, final String nome, final String uuid, final String cor){
		Task task = new Task(){

			@Override
			public Object run(ExecutorListener executorListener) {
				int out=-1;
				LogGrupo newlog = new LogGrupo(JsonObjectNamesEnum.GRUPO.toString()
						//, getSessaoLocalAtual()
						, getTimestamp()
						//ESPECIFICO
						, acao
						, nome
						, uuid
						,cor );
				repLogGrupo.insertTransaction(newlog);		
				return out;
			}

			@Override
			public void postRun(ExecutorListener executorListener, Object obj) {
				if (executorListener != null){
					executorListener.postResult(obj);
				}
			}

		};
		executor.execute(task);
		return 0;		
	}

	public int empilhaLogFeedback(IExecutor executor, final String feedbackText) {
		Task task = new Task(){
			@Override
			public Object run(ExecutorListener executorListener) {
				int out=-1;
				LogFeedback newlog = new LogFeedback(JsonObjectNamesEnum.FEEDBACK.toString()
						, getTimestamp()
						, feedbackText );
				repLogFeedback.insertTransaction(newlog);
				return out;
			}

			@Override
			public void postRun(ExecutorListener executorListener, Object obj) {
				if (executorListener != null){
					executorListener.postResult(obj);
				}
			}

		};
		executor.execute(task);
		return 0;

	}

	public void empilhaLogTransaction(IExecutor executor,final Item i, final Grupo oldGrupo,  final Grupo newGrupo, final boolean newGrupoExisist) {


		Task task = new Task() {

			@Override
			public Object run(ExecutorListener executorListener) {
				if(oldGrupo != null) {
					//desagrupar;
					Executor executor2 = new Executor(null);
					empilhaLogItem(executor2, i.getCodigo(), LogItemAcaoEnum.DESAGRUPAR, oldGrupo.getUUID_grupo());
				} 
				if(!newGrupoExisist) {
					//adicionar
					Executor executor3 = new Executor(null);
					empilhaLogGrupo(executor3, LogGrupoAcaoEnum.ADICIONAR, newGrupo.getNome(), newGrupo.getUUID_grupo(), newGrupo.getCorHexa());
				}



				// agrupar;
				Executor executor4 = new Executor(null);
				empilhaLogItem(executor4, i.getCodigo(), LogItemAcaoEnum.AGRUPAR, newGrupo.getUUID_grupo());				return null;
			}

			@Override
			public void postRun(ExecutorListener executorListener, Object obj) {
				// TODO Auto-generated method stub

			}
		};

		executor.execute(task);


	}
	
	private Timestamp getLastTimestampStart() {
		String strMilis = getSessaoLocalAtual();
		return DateUtil.getTimestampObject(Long.parseLong(strMilis));
	}

}
