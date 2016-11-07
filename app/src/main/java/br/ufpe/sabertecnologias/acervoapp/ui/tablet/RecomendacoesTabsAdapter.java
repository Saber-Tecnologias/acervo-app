package br.ufpe.sabertecnologias.acervoapp.ui.tablet;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import br.ufpe.sabertecnologias.acervoapp.ui.controllers.RecomendacoesControllerComposite;

/**
 * Created by joaotrindade on 27/01/16.
 */
public class RecomendacoesTabsAdapter extends FragmentPagerAdapter {

    private final Context mContext;
    private RecomendacoesControllerComposite mComposite;

    public RecomendacoesTabsAdapter(FragmentManager fm, Context ctx, RecomendacoesControllerComposite composite) {
        super(fm);
        mContext = ctx;
        mComposite = composite;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment f = null;
        switch (position){
            case 0:
                f = mComposite.getRecomendacoesController();
                break;
            default:
                f = mComposite.getRecentesController(position);
        }
        return f;
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "Recomendados";
            case 1:
                return "Textos";
            case 2:
                return "Áudios";
            case 3:
                return "Vídeos";
            case 4:
                return "Imagens";
        }
        return super.getPageTitle(position);
    }
}
