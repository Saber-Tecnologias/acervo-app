package br.ufpe.sabertecnologias.acervoapp.modelo.negocio;

import android.content.Context;

import java.util.List;

import br.ufpe.sabertecnologias.acervoapp.modelo.ExecutorListener;
import br.ufpe.sabertecnologias.acervoapp.modelo.IExecutor;
import br.ufpe.sabertecnologias.acervoapp.modelo.Task;
import br.ufpe.sabertecnologias.acervoapp.modelo.dados.User;
import br.ufpe.sabertecnologias.acervoapp.modelo.repositorios.RepositorioGrupo2;
import br.ufpe.sabertecnologias.acervoapp.modelo.repositorios.RepositorioItem2;
import br.ufpe.sabertecnologias.acervoapp.modelo.repositorios.RepositorioLogFeedback;
import br.ufpe.sabertecnologias.acervoapp.modelo.repositorios.RepositorioLogFimSessao;
import br.ufpe.sabertecnologias.acervoapp.modelo.repositorios.RepositorioLogGrupo;
import br.ufpe.sabertecnologias.acervoapp.modelo.repositorios.RepositorioLogInicioSessao;
import br.ufpe.sabertecnologias.acervoapp.modelo.repositorios.RepositorioLogItem;
import br.ufpe.sabertecnologias.acervoapp.modelo.repositorios.RepositorioSessaoControle;
import br.ufpe.sabertecnologias.acervoapp.modelo.repositorios.RepositorioUser2;

/**
 * Created by joaotrindade on 25/10/16.
 */

public abstract class BaseNegocio {

    protected final Context mContext;

    protected final RepositorioUser2 repositorioUser;
    protected final RepositorioItem2 repositorioItem;
    protected final RepositorioSessaoControle repSessaoControle;
    protected final RepositorioLogInicioSessao repLogInicioSessao;
    protected final RepositorioLogFimSessao repLogFimSessao;
    protected final RepositorioLogGrupo repLogGrupo;
    protected final RepositorioLogFeedback repLogFeedback;
    protected final RepositorioLogItem repLogItem;
    protected final RepositorioGrupo2 repositorioGrupo;

    protected User usuario;

    public BaseNegocio(Context context) {
        mContext = context.getApplicationContext();
        this.repositorioUser = RepositorioUser2.getInstance(mContext);
        this.repositorioItem = RepositorioItem2.getInstance(mContext);
        this.repSessaoControle = new RepositorioSessaoControle(mContext);
        this.repLogInicioSessao = new RepositorioLogInicioSessao(mContext);
        this.repLogFimSessao = new RepositorioLogFimSessao(mContext);
        this.repLogGrupo = new RepositorioLogGrupo(mContext);
        this.repLogFeedback = new RepositorioLogFeedback(mContext);
        this.repLogItem = new RepositorioLogItem(mContext);
        this.repositorioGrupo = RepositorioGrupo2.getInstance(mContext);

    }

    public void setUser(IExecutor executor) {
        Task task = new Task() {
            @Override
            public Object run(ExecutorListener executorListener) {
                List<User> users = repositorioUser.get();

                if(users.size() > 0){
                    usuario = users.get(0);
                }
                return null;
            }

            @Override
            public void postRun(ExecutorListener executorListener, Object obj) {
                if(executorListener != null)
                    executorListener.postResult(obj);
            }
        };

        executor.execute(task);
    }
}
