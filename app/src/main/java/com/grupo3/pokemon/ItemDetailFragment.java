package com.grupo3.pokemon;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.grupo3.pokemon.databinding.FragmentItemDetailBinding;

public class ItemDetailFragment extends Fragment {

    public static final Integer ARG_ITEM_ID = 0;
    public static final String ARG_ITEM_NAME = "item_name";
    public static final String ARG_DESCRIPTION = "item_description";

    private Integer mItemId;
    private String mItemName;
    private String mItemDescription;


    private CollapsingToolbarLayout mToolbarLayout;
    private TextView mTextView;

    private FragmentItemDetailBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(String.valueOf(ARG_ITEM_ID))) {
            mItemId = getArguments().getInt(String.valueOf(ARG_ITEM_ID));
            mItemName = getArguments().getString(ARG_ITEM_NAME);
            mItemDescription = getArguments().getString(ARG_DESCRIPTION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentItemDetailBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();

        mToolbarLayout = rootView.findViewById(R.id.toolbar_layout);
        mTextView = binding.itemDetail;

        updateContent();
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void updateContent() {
        if (mItemId != null) {
            mTextView.setText(mItemDescription);
            if (mToolbarLayout != null) {
                mToolbarLayout.setTitle(mItemName);
            }
            ImageView itemImage = binding.itemImage;
            String pokemonImageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + mItemId + ".png";
            Glide.with(this)
                    .load(pokemonImageUrl)
                    .into(itemImage);
        }
    }
}