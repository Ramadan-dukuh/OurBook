package com.example.ourbook

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.example.ourbook.databinding.ActivityEditBinding
import com.squareup.picasso.Picasso

class EditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditBinding
    private lateinit var db: DatabaseHelper
    private var bookId: Int = -1
    private var updatedFoto: ByteArray? = null  // Variable to store new photo data
    private val CAMERA_REQUEST = 100
    private val STORAGE_PERMISSION = 101

    private val cameraPermissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private val storagePermissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

    private val cropImageLauncher = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            val uri: Uri? = result.uriContent
            Picasso.get().load(uri).into(binding.fotoimageView)

            // Convert the new photo to a byte array and store it in updatedFoto
            uri?.let {
                contentResolver.openInputStream(it)?.use { inputStream ->
                    updatedFoto = inputStream.readBytes()
                }
            }
        } else {
            result.error?.printStackTrace()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        db = DatabaseHelper(this)

        bookId = intent.getIntExtra("book_id", -1)
        if (bookId == -1) {
            finish()
            return
        }

        val book = db.getBookByID(bookId)
        if (book != null) {
            val bitmap = BitmapFactory.decodeByteArray(book.foto, 0, book.foto.size)
            binding.etNama.setText(book.nama)
            binding.etNamaPanggilan.setText(book.namapanggilan)
            binding.fotoimageView.setImageBitmap(bitmap)
            binding.etEmail.setText(book.email)
            binding.etAlamat.setText(book.alamat)
            binding.etTanggallahir.setText(book.tanggallahir)
            binding.etNoHp.setText(book.telpon)
        } else {
            Toast.makeText(this, "Data not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.btnSave.setOnClickListener {
            // Get updated values from the form
            val newNama = binding.etNama.text.toString()
            val newNamaPanggilan = binding.etNamaPanggilan.text.toString()
            val newEmail = binding.etEmail.text.toString()
            val newAlamat = binding.etAlamat.text.toString()
            val newTanggalLahir = binding.etTanggallahir.text.toString()
            val newNoHp = binding.etNoHp.text.toString()

            // Use the updated photo if available, otherwise keep the original
            val fotoToSave = updatedFoto ?: book.foto

            // Create an updated book object
            val updatedBook = Book(
                id = bookId,
                nama = newNama,
                namapanggilan = newNamaPanggilan,
                foto = fotoToSave,  // Set to updated or original photo
                email = newEmail,
                alamat = newAlamat,
                tanggallahir = newTanggalLahir,
                telpon = newNoHp
            )

            // Update the book in the database
            db.updateBook(updatedBook)
            Toast.makeText(this, "Changes Saved", Toast.LENGTH_SHORT).show()
            finish()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Set the fotoimageView OnClickListener inside onCreate
        binding.fotoimageView.setOnClickListener {
            if (!checkCameraPermission()) {
                requestCameraPermission()
            } else {
                pickFromGallery()
            }
        }
    }

    private fun requestCameraPermission() {
        requestPermissions(cameraPermissions, CAMERA_REQUEST)
    }

    private fun checkCameraPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        val result2 = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        return result && result2
    }

    private fun pickFromGallery() {
        cropImageLauncher.launch(CropImageContractOptions(null, CropImageOptions()))
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_REQUEST && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            pickFromGallery()
        } else {
            Toast.makeText(this, "Enable Camera and Storage Permissions", Toast.LENGTH_SHORT).show()
        }
    }
}

