package com.example.ourbook

import android.Manifest
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.example.ourbook.databinding.ActivityTambahDataBinding
import com.squareup.picasso.Picasso
import java.util.Calendar

class TambahData : AppCompatActivity() {

    private lateinit var binding: ActivityTambahDataBinding
    private lateinit var db: DatabaseHelper
    private val CAMERA_REQUEST = 100
    private val STORAGE_PERMISSION = 101

    private val cameraPermissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private val storagePermissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

    private val cropImageLauncher = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            val uri: Uri? = result.uriContent
            Picasso.get().load(uri).into(binding.fotoimageView)
        } else {
            result.error?.printStackTrace()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTambahDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DatabaseHelper(this)

        binding.btnSave.setOnClickListener {
            val nama = binding.etNama.text.toString()
            val namapanggilan = binding.etNamaPanggilan.text.toString()
            val email = binding.etEmail.text.toString()
            val alamat = binding.etAlamat.text.toString()
            val tanggallahir = binding.etTanggallahir.text.toString()
            val telpon = binding.etNoHp.text.toString()

            when {
                nama.isEmpty() || namapanggilan.isEmpty() || email.isEmpty() || alamat.isEmpty() ||
                        tanggallahir.isEmpty() || telpon.isEmpty() -> {
                    Toast.makeText(this, "Fields cannot be empty!", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    val book = Book(0, nama, namapanggilan, db.ImageViewToByte(binding.fotoimageView), email, alamat, tanggallahir, telpon)
                    db.insertBook(book)
                    finish()
                    Toast.makeText(this, "Book Saved", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.fotoimageView.setOnClickListener {
            if (!checkCameraPermission()) {
                requestCameraPermission()
            } else {
                pickFromGallery()
            }
        }

        binding.etTanggallahir.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    binding.etTanggallahir.setText("$selectedDay/${selectedMonth + 1}/$selectedYear")
                },
                year, month, day
            )
            datePickerDialog.show()
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
