package com.gutotech.narutogame.ui.playing.currentvillage;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gutotech.narutogame.R;
import com.gutotech.narutogame.databinding.FragmentNinjaShopBinding;
import com.gutotech.narutogame.ui.SectionFragment;
import com.gutotech.narutogame.ui.adapter.ItemShopRecyclerAdapter;
import com.gutotech.narutogame.utils.FragmentUtil;

public class NinjaShopFragment extends Fragment implements SectionFragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentNinjaShopBinding binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_ninja_shop, container, false);

        NinjaShopViewModel viewModel = ViewModelProviders.of(this).get(NinjaShopViewModel.class);
        binding.setViewModel(viewModel);

        ItemShopRecyclerAdapter adapter = new ItemShopRecyclerAdapter(getActivity(),
                getFragmentManager(), viewModel);

        binding.shopItemsRecyclerView.setHasFixedSize(true);
        binding.shopItemsRecyclerView.setAdapter(adapter);

        viewModel.getShopItems().observe(this, adapter::setItemsList);

        FragmentUtil.setSectionTitle(getActivity(), R.string.section_ninja_shop);
        return binding.getRoot();
    }

    @Override
    public int getDescription() {
        return R.string.ninja_shop;
    }
}
