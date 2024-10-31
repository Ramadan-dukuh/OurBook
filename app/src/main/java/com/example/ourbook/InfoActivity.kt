package com.example.ourbook

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ourbook.databinding.ActivityInfoBinding
import com.example.ourbook.databinding.ActivityMainBinding

class InfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInfoBinding
    private  lateinit var db : DatabaseHelper
    private lateinit var bookAdapter: BookAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        db = DatabaseHelper(this)
        bookAdapter = BookAdapter(db.getAllBooks(),this)

        db = DatabaseHelper(this)

        val bookId = intent.getIntExtra("book_id", -1)

        if (bookId != -1) {
            val book = db.getBookByID(bookId)

            binding.NamatextView.text = book.nama
            binding.PanggilantextView.text = book.namapanggilan
            binding.EmailtextView.text = book.email
            binding.AlamatTextView.text = book.alamat
            binding.TanggaltextView.text = book.tanggallahir
            binding.TelpontextView.text = book.telpon

            if (book.foto != null) {
                val bmp = BitmapFactory.decodeByteArray(book.foto, 0, book.foto.size)
                binding.FotoimageView.setImageBitmap(bmp)
            } else {
                binding.FotoimageView.setImageResource(R.drawable.baseline_account_circle_24)
            }
        }

        binding.Back.setOnClickListener{
            finish()
            }
        }
    override fun onResume() {
        super.onResume()
        bookAdapter.refreshData(db.getAllBooks())
    }
}