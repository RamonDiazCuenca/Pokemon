package com.grupo3.pokemon;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.GsonBuilder;
import com.grupo3.pokemon.databinding.FragmentItemListBinding;
import com.grupo3.pokemon.databinding.ItemListContentBinding;

import com.grupo3.pokemon.placeholder.PlaceholderContent;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ItemListFragment extends Fragment {

    ViewCompat.OnUnhandledKeyEventListenerCompat unhandledKeyEventListenerCompat = (v, event) -> {
        if (event.getKeyCode() == KeyEvent.KEYCODE_Z && event.isCtrlPressed()) {
            Toast.makeText(
                    v.getContext(),
                    "Undo (Ctrl + Z) shortcut triggered",
                    Toast.LENGTH_LONG
            ).show();
            return true;
        } else if (event.getKeyCode() == KeyEvent.KEYCODE_F && event.isCtrlPressed()) {
            Toast.makeText(
                    v.getContext(),
                    "Find (Ctrl + F) shortcut triggered",
                    Toast.LENGTH_LONG
            ).show();
            return true;
        }
        return false;
    };

    private FragmentItemListBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentItemListBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewCompat.addOnUnhandledKeyEventListener(view, unhandledKeyEventListenerCompat);

        RecyclerView recyclerView = binding.itemList;

        View itemDetailFragmentContainer = view.findViewById(R.id.item_detail_nav_container);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://pokeapi.co/api/v2/")
                .addConverterFactory(GsonConverterFactory.create(
                        new GsonBuilder().serializeNulls().create()
                ))
                .build();
        PokemonAPIService pokemonApiService = retrofit.create(PokemonAPIService.class);
        Call<PokemonFetchResults> call = pokemonApiService.getPokemons();

        call.enqueue(new Callback<PokemonFetchResults>() {
            @Override
            public void onResponse(Call<PokemonFetchResults> call, Response<PokemonFetchResults> response) {
                if (response.isSuccessful()) {
                    ArrayList<Pokemon> pokemonList = response.body().getResults();
                    View recyclerView = getActivity().findViewById(R.id.item_list);
                    assert recyclerView != null;
                    setupRecyclerView((RecyclerView) recyclerView, itemDetailFragmentContainer, pokemonList);
                } else {
                    Log.d("Error", "Something happened");
                    return;
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.d("Error", t.toString());
            }
        });

    }

    private void setupRecyclerView(
            RecyclerView recyclerView,
            View itemDetailFragmentContainer,
            ArrayList<Pokemon> pokemonList
    ) {

        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(
                pokemonList,
                itemDetailFragmentContainer
        ));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<Pokemon> mValues;

        SimpleItemRecyclerViewAdapter(List<Pokemon> items,
                                      View itemDetailFragmentContainer) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            ItemListContentBinding binding =
                    ItemListContentBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new ViewHolder(binding);

        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {

            holder.mIdView.setText(Integer.toString(position));
            holder.mContentView.setText(mValues.get(position).getName());

            holder.itemView.setTag(position);
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mIdView;
            final TextView mContentView;

            ViewHolder(ItemListContentBinding binding) {
                super(binding.getRoot());
                mIdView = binding.idText;
                mContentView = binding.content;
            }

        }


        private final View.OnClickListener mOnClickListener = new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                int index = (int) view.getTag();
                Pokemon item = mValues.get(index);

                Bundle arguments = new Bundle();
                arguments.putInt(String.valueOf((ItemDetailFragment.ARG_ITEM_ID)),(index + 1));
                arguments.putString(ItemDetailFragment.ARG_ITEM_NAME, item.getName());
                arguments.putString(ItemDetailFragment.ARG_DESCRIPTION, item.getDescription());

                Navigation.findNavController(view).navigate(R.id.show_item_detail, arguments);
            }
        };
    }
}