package com.example.ourbook

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ourbook.databinding.ActivityMainBinding
import com.example.ourbook.databinding.ActivityTambahDataBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private  lateinit var db : DatabaseHelper
    private lateinit var bookAdapter: BookAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        db = DatabaseHelper(this)
        bookAdapter = BookAdapter(db.getAllBooks(),this)

        binding.bookrecyclerView.layoutManager = LinearLayoutManager(this)
        binding.bookrecyclerView.adapter = bookAdapter

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    binding.About.setOnClickListener{
        val intent = Intent(this,About::class.java)
        startActivity(intent    )
    }

    binding.floatingActionButton.setOnClickListener{
    val intent =Intent(this,TambahData::class.java)
    startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        bookAdapter.refreshData(db.getAllBooks())
    }

}