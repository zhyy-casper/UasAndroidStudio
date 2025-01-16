package com.example.lotengnews

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lotengnews.databinding.ActivityBeritaBinding
import com.example.lotengnews.viewmodel.MainViewModel

class BeritaActivity : AppCompatActivity() {
    private var _binding: ActivityBeritaBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by viewModels()
    private var username: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityBeritaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        username = intent.getStringExtra("username")

        // Set up RecyclerView with LinearLayoutManager
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        // Set up user profile image click listener
        val imageUser: ImageView = binding.root.findViewById(R.id.imageUser)
        imageUser.setOnClickListener {
            showPopupMenu(it as ImageView)
        }

        viewModel.getBeritas()
        viewModel.berita.observe(this) { data ->
            if (data != null && data.isNotEmpty()) {
                val adapter = BeritaAdapter(data) { berita ->
                    // Pastikan data yang dikirim tidak kosong atau null
                    if (berita.title.isNullOrEmpty() || berita.description.isNullOrEmpty() || berita.thumbnail.isNullOrEmpty()) {
                        Toast.makeText(this, "Data berita tidak lengkap", Toast.LENGTH_SHORT).show()
                    } else {
                        // Handle berita item click and navigate to DetailBeritaActivity
                        val intent = Intent(this, DetailBeritaActivity::class.java)
                        intent.putExtra("title", berita.title)
                        intent.putExtra("description", berita.description)
                        intent.putExtra("imageUrl", berita.thumbnail)
                        intent.putExtra("pubDate", berita.pubDate)
                        intent.putExtra("LinkBerita", berita.link)
                        startActivity(intent)
                    }
                }
                binding.recyclerView.adapter = adapter
            } else {
                Toast.makeText(this, "Data berita tidak ditemukan", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun showPopupMenu(anchor: ImageView) {
        val popupMenu = PopupMenu(this, anchor)
        popupMenu.menuInflater.inflate(R.menu.dropdown, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_profile -> {
                    val intent = Intent(this, ProfileActivity::class.java)
                    intent.putExtra("username", username)
                    startActivity(intent)
                    true
                }
                R.id.menu_logout -> {
                    showLogoutConfirmationDialog()
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Logout")
            .setMessage("Apakah Anda yakin ingin keluar dari aplikasi?")
            .setPositiveButton("Ya") { _, _ -> logout() }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun logout() {
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()
        Toast.makeText(this, "Logout berhasil", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
