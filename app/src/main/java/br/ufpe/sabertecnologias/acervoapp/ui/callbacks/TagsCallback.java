package br.ufpe.sabertecnologias.acervoapp.ui.callbacks;

import jpttrindade.widget.tagview.Tag;

/**
 * Created by joaotrindade on 25/10/16.
 */

public interface TagsCallback{
    public void deselectFiltro(Tag tag);
    public void editTag(Tag tag);
    public void onEmptyTags();
}
