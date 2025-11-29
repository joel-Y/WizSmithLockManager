package com.wizsmith.lockmanager.ui.locks

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.wizsmith.lockmanager.R
import com.wizsmith.lockmanager.databinding.FragmentLocksListBinding
import com.wizsmith.lockmanager.ui.adapters.LocksAdapter
import com.wizsmith.lockmanager.utils.gone
import com.wizsmith.lockmanager.utils.visible
import com.wizsmith.lockmanager.viewmodels.LockViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LocksListFragment : Fragment() {

    private var _binding: FragmentLocksListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LockViewModel by viewModels()
    private lateinit var adapter: LocksAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLocksListBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupClickListeners()
        setupObservers()
    }

    private fun setupRecyclerView() {
        adapter = LocksAdapter(
            onLockClick = { lock ->
                viewModel.selectLock(lock)
                findNavController().navigate(R.id.action_locks_list_to_lock_details)
            },
            onUnlockClick = { lock ->
                viewModel.unlockLock(lock)
            },
            onLockClick = { lock ->
                viewModel.lockLock(lock)
            }
        )

        binding.rvLocks.layoutManager = LinearLayoutManager(requireContext())
        binding.rvLocks.adapter = adapter
    }

    private fun setupClickListeners() {
        binding.fabAddLock.setOnClickListener {
            val intent = Intent(requireContext(), ScanLockActivity::class.java)
            startActivity(intent)
        }

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.syncLocksFromServer()
        }
    }

    private fun setupObservers() {
        viewModel.locks.observe(viewLifecycleOwner) { locks ->
            binding.swipeRefresh.isRefreshing = false
            
            if (locks.isEmpty()) {
                binding.tvEmptyState.visible()
                binding.rvLocks.gone()
            } else {
                binding.tvEmptyState.gone()
                binding.rvLocks.visible()
                adapter.submitList(locks)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_locks, menu)
        
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter(newText ?: "")
                return true
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}