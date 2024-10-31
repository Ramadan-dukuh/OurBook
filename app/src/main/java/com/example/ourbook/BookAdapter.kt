package com.example.ourbook

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class BookAdapter (private var book:List<Book>,context: Context):
        RecyclerView.Adapter<BookAdapter.BookViewHolder>() {
    private val db: DatabaseHelper = DatabaseHelper(context)

    class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val fotoimageView: ImageView = itemView.findViewById(R.id.fotoimageView)
        val nicknameTextView: TextView = itemView.findViewById(R.id.nicknameTextView)
        val phoneTextView : TextView = itemView.findViewById(R.id.phoneTextView)
        val updateButton: ImageView = itemView.findViewById(R.id.updateButton)
        val deleteButton: ImageView = itemView.findViewById(R.id.deleteButton)
        val detailButton: LinearLayout = itemView.findViewById(R.id.card)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_detail,parent,false)
        return BookViewHolder(view)
    }

    override fun getItemCount(): Int =book.size

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book =book[position]

        val bitmap = BitmapFactory.decodeByteArray(book.foto, 0, book.foto.size)
        holder.titleTextView.text = book.nama
        holder.fotoimageView.setImageBitmap(bitmap)
        holder.nicknameTextView.text = book.namapanggilan
        holder.phoneTextView.text=book.telpon

        if (book.foto != null) {
            val bmp = BitmapFactory.decodeByteArray(book.foto, 0, book.foto.size)
            holder.fotoimageView.setImageBitmap(bmp)
        } else {
            holder.fotoimageView.setImageResource(R.drawable.baseline_add_24)
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailActivity::class.java).apply {
                putExtra("book_id", book.id)
            }
            holder.itemView.context.startActivity(intent)
        }

        holder.updateButton.setOnClickListener{
            val intent = Intent(holder.itemView.context,EditActivity::class.java).apply {
                putExtra("book_id",book.id)
            }
            holder.itemView.context.startActivity(intent)
        }

        holder.detailButton.setOnClickListener {
            val intent = Intent(holder.itemView.context,InfoActivity::class.java).apply {
                putExtra("book_id",book.id)
            }
            holder.itemView.context.startActivity(intent)
        }

        holder.deleteButton.setOnClickListener{
            db.deleteBook(book.id)
            refreshData(db.getAllBooks())
            Toast.makeText(holder.itemView.context,"Book Deleted", Toast.LENGTH_SHORT).show()
        }

    }

    fun refreshData(newBook: List<Book>){
        book = newBook
        notifyDataSetChanged()
    }
}