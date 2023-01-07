/*
 * Copyright (c) 2018, Nordic Semiconductor
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package no.nordicsemi.android.nrfmesh;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import no.nordicsemi.android.mesh.Group;
import no.nordicsemi.android.mesh.MeshNetwork;
import no.nordicsemi.android.nrfmesh.adapter.GroupAdapter;
import no.nordicsemi.android.nrfmesh.adapter.GroupItemUIState;
import no.nordicsemi.android.nrfmesh.databinding.FragmentGroupsBinding;
import no.nordicsemi.android.nrfmesh.dialog.DialogFragmentCreateGroup;
import no.nordicsemi.android.nrfmesh.viewmodels.SharedViewModel;
import no.nordicsemi.android.nrfmesh.widgets.ItemTouchHelperAdapter;
import no.nordicsemi.android.nrfmesh.widgets.RemovableItemTouchHelperCallback;
import no.nordicsemi.android.nrfmesh.widgets.RemovableViewHolder;
import top.defaults.colorpicker.ColorPickerPopup;



public class GroupsFragment extends Fragment implements
        ItemTouchHelperAdapter,
        GroupAdapter.OnItemClickListener,
        GroupCallbacks {
    private FragmentGroupsBinding binding;
    private SharedViewModel mViewModel;
    private Button setColorButton , pickColorButton;
    private View colorPreView;
    private int mDefaultColor;
    private NetworkFragment test;
    private TextView gfgTextView;
//    private Context test1 = new GroupsFragment().getActivity();


    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup viewGroup, @Nullable final Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        binding = FragmentGroupsBinding.inflate(getLayoutInflater());
//        pickColorButton = binding.pickColorButton;
//        setColorButton = binding.setColorButton;
//        colorPreView = binding.previewSelectedColor;
//        gfgTextView =binding.gfgHeading;
        mDefaultColor = 0;

//        pickColorButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new ColorPickerPopup.Builder(GroupsFragment.this.getActivity())
//                        .initialColor(Color.RED)
//                        .enableBrightness(true)
//                        .enableAlpha(true)
//                        .okTitle("Choose")
//                        .cancelTitle("Cancel")
//                        .showIndicator(true)
//                        .showValue(true)
//                        .build()
//                        .show(v,new ColorPickerPopup.ColorPickerObserver(){
//                    @Override
//                    public void onColorPicked(int color) {
//                        mDefaultColor = color;
//                        colorPreView.setBackgroundColor(mDefaultColor);
//                    }
//                });
//            }
//        });
//
//
//        setColorButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                gfgTextView.setTextColor(mDefaultColor);
//            }
//        });


//        final ExtendedFloatingActionButton fab = binding.fabAddGroup;

        // Configure the recycler view
        final RecyclerView recyclerViewGroups = binding.recyclerViewGroups;
        recyclerViewGroups.setLayoutManager(new LinearLayoutManager(requireContext()));
        final DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerViewGroups.getContext(), DividerItemDecoration.VERTICAL);
        recyclerViewGroups.addItemDecoration(dividerItemDecoration);
        final ItemTouchHelper.Callback itemTouchHelperCallback = new RemovableItemTouchHelperCallback(this);
        final ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchHelperCallback);
        itemTouchHelper.attachToRecyclerView(recyclerViewGroups);
        final GroupAdapter adapter = new GroupAdapter();
        adapter.setOnItemClickListener(this);
        recyclerViewGroups.setAdapter(adapter);

        mViewModel.getNetworkLiveData().observe(getViewLifecycleOwner(), meshNetworkLiveData -> {
            if (meshNetworkLiveData != null) {
                final MeshNetwork network = meshNetworkLiveData.getMeshNetwork();
                if (network.getGroups().isEmpty()) {
                    binding.empty.getRoot().setVisibility(View.VISIBLE);
                } else {
                    binding.empty.getRoot().setVisibility(View.INVISIBLE);
                }
                adapter.updateAdapter(populateGroups(network));
            }
        });

//        fab.setOnClickListener(v -> {
//            if (mViewModel.getNetworkLiveData().getProvisioner().getAllocatedGroupRanges().isEmpty()) {
//                displaySnackBar(getString(R.string.error_allocate_group_range), null);
//                return;
//            }
//            DialogFragmentCreateGroup fragmentCreateGroup = DialogFragmentCreateGroup.newInstance();
//            fragmentCreateGroup.show(getChildFragmentManager(), null);
//        });

//        recyclerViewGroups.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(@NonNull final RecyclerView recyclerView, final int dx, final int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                final LinearLayoutManager m = (LinearLayoutManager) recyclerView.getLayoutManager();
//                if (m != null) {
//                    if (m.findFirstCompletelyVisibleItemPosition() == 0) {
//                        fab.extend();
//                    } else {
//                        fab.shrink();
//                    }
//                }
//            }
//        });

        return binding.getRoot();

    }

    @Override
    public void onItemClick(final int address) {
        mViewModel.setSelectedGroup(address);
        startActivity(new Intent(requireContext(), GroupControlsActivity.class));
    }

    @Override
    public void onItemDismiss(final RemovableViewHolder viewHolder) {
        final int position = viewHolder.getAbsoluteAdapterPosition();
        final MeshNetwork network = mViewModel.getNetworkLiveData().getMeshNetwork();
        final Group group = network.getGroups().get(position);
        if (network.getModels(group).size() == 0) {
            network.removeGroup(group);
            final View.OnClickListener action = v -> {
                binding.empty.getRoot().setVisibility(View.INVISIBLE);
                final MeshNetwork network1 = mViewModel.getNetworkLiveData().getMeshNetwork();
                if (network1 != null) {
                    network1.addGroup(group);
                }
            };
            displaySnackBar(getString(R.string.group_deleted, group.getName()), action);
        }
    }

    @Override
    public void onItemDismissFailed(final RemovableViewHolder viewHolder) {
        final String message = getString(R.string.error_group_unsubscribe_to_delete);
        mViewModel.displaySnackBar(requireActivity(), binding.container, message, Snackbar.LENGTH_LONG);
    }

    @Override
    public Group createGroup() {
        final MeshNetwork network = mViewModel.getNetworkLiveData().getMeshNetwork();
        return network.createGroup(network.getSelectedProvisioner(), "Mesh Group");
    }

    @Override
    public Group createGroup(@NonNull final String name) {
        final MeshNetwork network = mViewModel.getNetworkLiveData().getMeshNetwork();
        return network.createGroup(network.getSelectedProvisioner(), name);
    }

    @Override
    public Group createGroup(@NonNull final UUID uuid, final String name) {
        final MeshNetwork network = mViewModel.getNetworkLiveData().getMeshNetwork();
        return network.createGroup(uuid, null, name);
    }

    @Override
    public boolean onGroupAdded(@NonNull final String name, final int address) {
        final MeshNetwork network = mViewModel.getNetworkLiveData().getMeshNetwork();
        return network.addGroup(network.createGroup(network.getSelectedProvisioner(), address, name));
    }

    @Override
    public boolean onGroupAdded(@NonNull final Group group) {
        final MeshNetwork network = mViewModel.getNetworkLiveData().getMeshNetwork();
        return network.addGroup(group);
    }

    @SuppressLint("ShowToast")
    private void displaySnackBar(final String message, final View.OnClickListener action) {
        Snackbar snack = Snackbar.make(binding.container, message, Snackbar.LENGTH_LONG);
        if (action != null)
            snack = snack.setActionTextColor(getResources().getColor(R.color.colorSecondary))
                    .setAction(R.string.undo, action);
        snack.show();
    }

    private List<GroupItemUIState> populateGroups(final MeshNetwork network) {
        final List<GroupItemUIState> groups = new ArrayList<>();
        for (Group group : network.getGroups()) {
            groups.add(new GroupItemUIState(group.getName(), group.getAddress(), network.getModels(group).size()));
        }
        return groups;
    }


}
