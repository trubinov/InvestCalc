package ru.nts.investcalc;

import android.os.Bundle;
import android.support.v4.app.ListFragment;

/**
 * Created by Николай on 21.02.2015.
 */
public class PortfolioFragment extends ListFragment {

    PortfolioAdapter adapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter = new PortfolioAdapter(getActivity());
        setListAdapter(adapter);
    }

}
