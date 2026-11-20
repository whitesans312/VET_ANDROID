package com.example.dogapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.dogapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), com.example.dogapp.view.detalle.DetalleCitaNavegacion {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun volverAlHome() {
        val navHost = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
                as? androidx.navigation.fragment.NavHostFragment
        navHost?.navController?.popBackStack(R.id.homeFragment, false)
    }

    override fun irAEditarCita(citaId: Long) {
        val navHost = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
                as? androidx.navigation.fragment.NavHostFragment
        val bundle = androidx.core.os.bundleOf("citaId" to citaId)
        navHost?.navController?.navigate(R.id.editarCitaFragment, bundle)
    }
}

