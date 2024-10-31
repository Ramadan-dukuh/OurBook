package com.example.ourbook

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.widget.Button
import android.widget.ImageView
import java.io.ByteArrayOutputStream

class DatabaseHelper(context: Context): SQLiteOpenHelper(context,DATABASE_NAME,null,
    DATABASE_VERSION) {

    companion object{
        private const val DATABASE_NAME ="ourbook.db"
        private const val DATABASE_VERSION =1
        private const val TABLE_NAME ="allbooks"
        private const val COULUMN_ID ="id"
        private const val COULUMN_NAMA ="nama"
        private const val COULUMN_NAMAPANGGILAN ="namapanggilan"
        private const val COULUMN_FOTO ="foto"
        private const val COULUMN_EMAIL ="email"
        private const val COULUMN_ALAMAT ="alamat"
        private const val COULUMN_TANGGALLAHIR ="tanggallahir"
        private const val COULUMN_TELPON ="telpon"
    }
    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = "CREATE TABLE $TABLE_NAME ($COULUMN_ID INTEGER PRIMARY KEY," +
                "$COULUMN_NAMA TEXT,$COULUMN_NAMAPANGGILAN TEXT,$COULUMN_FOTO BLOB,$COULUMN_EMAIL TEXT" +
                ",$COULUMN_ALAMAT TEXT , $COULUMN_TANGGALLAHIR TEXT, $COULUMN_TELPON TEXT)"
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val dropTableQuery = "DROP TABLE IF EXISTS $TABLE_NAME"
        db?.execSQL(dropTableQuery)
        onCreate(db)
    }

    fun insertBook(book:Book){
        val db =writableDatabase
        val values =ContentValues().apply{
            put(COULUMN_NAMA,book.nama)
            put(COULUMN_NAMAPANGGILAN,book.namapanggilan)
            put(COULUMN_FOTO,book.foto)
            put(COULUMN_EMAIL,book.email )
            put(COULUMN_ALAMAT,book.alamat)
            put(COULUMN_TANGGALLAHIR,book.tanggallahir)
            put(COULUMN_TELPON,book.telpon)
        }
        db.insert(TABLE_NAME,null,values)
        db.close()
    }
    fun getAllBooks():List<Book>{
        val bookList = mutableListOf<Book>()
        val db = readableDatabase
        val query ="SELECT * FROM $TABLE_NAME"
        val cursor = db.rawQuery(query,null)

        while (cursor.moveToNext()){
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COULUMN_ID))
            val nama = cursor.getString(cursor.getColumnIndexOrThrow(COULUMN_NAMA))
            val namapanggilan  = cursor.getString(cursor.getColumnIndexOrThrow(COULUMN_NAMAPANGGILAN))
            val foto = cursor.getBlob(cursor.getColumnIndexOrThrow(COULUMN_FOTO))
            val email = cursor.getString(cursor.getColumnIndexOrThrow(COULUMN_EMAIL))
            val alamat = cursor.getString(cursor.getColumnIndexOrThrow(COULUMN_NAMA))
            val tanggallahir = cursor.getString(cursor.getColumnIndexOrThrow(COULUMN_TANGGALLAHIR))
            val telpon = cursor.getString(cursor.getColumnIndexOrThrow(COULUMN_TELPON))

            val book = Book(id,nama,namapanggilan, foto, email, alamat, tanggallahir, telpon)
            bookList.add(book)
        }
        cursor.close()
        db.close()
        return bookList
    }
    fun updateBook(book: Book){
        val db = writableDatabase
        val values =ContentValues().apply{
            put(COULUMN_NAMA,book.nama)
            put(COULUMN_NAMAPANGGILAN,book.namapanggilan)
            put(COULUMN_FOTO,book.foto)
            put(COULUMN_EMAIL,book.email )
            put(COULUMN_ALAMAT,book.alamat)
            put(COULUMN_TANGGALLAHIR,book.tanggallahir)
            put(COULUMN_TELPON,book.telpon)
        }
        val whereClause = "$COULUMN_ID = ?"
        val whereArgs = arrayOf(book.id.toString())
        db.update(TABLE_NAME,values, whereClause, whereArgs)
        db.close()
    }
    fun getBookByID(bookId: Int):Book{
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME WHERE $COULUMN_ID = $bookId"
        val cursor = db.rawQuery(query,null)
        cursor.moveToFirst()

        val id = cursor.getInt(cursor.getColumnIndexOrThrow(COULUMN_ID))
        val nama = cursor.getString(cursor.getColumnIndexOrThrow(COULUMN_NAMA))
        val namapanggilan  = cursor.getString(cursor.getColumnIndexOrThrow(COULUMN_NAMAPANGGILAN))
        val foto = cursor.getBlob(cursor.getColumnIndexOrThrow(COULUMN_FOTO))
        val email = cursor.getString(cursor.getColumnIndexOrThrow(COULUMN_EMAIL))
        val alamat = cursor.getString(cursor.getColumnIndexOrThrow(COULUMN_ALAMAT))
        val tanggallahir = cursor.getString(cursor.getColumnIndexOrThrow(COULUMN_TANGGALLAHIR))
        val telpon = cursor.getString(cursor.getColumnIndexOrThrow(COULUMN_TELPON))

        cursor.close()
        db.close()
        return Book(id,nama, namapanggilan, foto, email, alamat, tanggallahir, telpon)
    }
    fun deleteBook(bookId : Int){
        val db = writableDatabase
        val whereClause ="$COULUMN_ID = ?"
        val whereArgs = arrayOf(bookId.toString())
        db.delete(TABLE_NAME,whereClause, whereArgs)
        db.close()
    }
    fun ImageViewToByte(img: ImageView): ByteArray {
        val bitmap: Bitmap = (img.drawable as BitmapDrawable).bitmap
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream)
        val bytes: ByteArray = stream.toByteArray()
        return bytes
    }
}