package com.wizsmith.lockmanager.ui.locks

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.wizsmith.lockmanager.api.RetrofitClient
import com.wizsmith.lockmanager.databinding.ActivityLocksBinding
import com.wizsmith.lockmanager.model.Lock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LocksActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLocksBinding
    private val adapter = LocksAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocksBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.recycler.layoutManager = LinearLayoutManager(this)
        binding.recycler.adapter = adapter

        loadLocks()
    }

    private fun loadLocks() {
        lifecycleScope.launch {
            try {
                val resp = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.getLocks()
                }
                adapter.submitList(resp.list)
            } catch (ex: Exception) {
                Toast.makeText(this@LocksActivity, "Failed to load locks: ${ex.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
