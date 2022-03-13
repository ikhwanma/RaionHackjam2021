package com.example.hackjamraion

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class HasilActivity : AppCompatActivity() {
    private lateinit var textHasil:TextView
    private lateinit var spinner: Spinner
    private lateinit var inputJumlah: EditText
    private lateinit var btnSelesai: Button
    private lateinit var btnCancel: Button
    private lateinit var database: DatabaseReference
    private lateinit var databaseUser: DatabaseReference
    private val sampah:Array<String> = arrayOf("Pilih jenis sampah","Gelas Plastik", "Botol", "Kaleng")
    private var jenisSampah = ""
    private var hasilScan = ""
    private lateinit var btnGambar:Button
    private lateinit var textGambar: TextView
    private lateinit var imgTes: ImageView
    private lateinit var takenImage: Bitmap
    private lateinit var imageURI: Uri
    private var url = ""
    private var fileName = ""
    var key = ""
    private lateinit var id: ArrayList<String>
    private var namaUser = ""
    private lateinit var userArrayList: ArrayList<User>
    private lateinit var textUrl:TextView
    private var poinUser = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hasil)
        btnSelesai = findViewById(R.id.btn_selesai)
        btnCancel = findViewById(R.id.btn_cancel)
        inputJumlah = findViewById(R.id.inputJumlah)
        textHasil = findViewById(R.id.textHasil)
        btnGambar = findViewById(R.id.btnGambar)
        textGambar = findViewById(R.id.textGambar)
        imgTes = findViewById(R.id.img_test)
        userArrayList = arrayListOf<User>()
        textUrl = findViewById(R.id.textUrl)

        val arrayAdapter = ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item, sampah)
        spinner = findViewById(R.id.spinner)
        spinner.adapter = arrayAdapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if(sampah[p2]=="Pilih jenis sampah"){

                }else{
                    if (sampah[p2] == "Gelas Plastik"){
                        jenisSampah = "Gelas Plastik"
                    }else if(sampah[p2] == "Botol"){
                        jenisSampah = "Botol"
                    }else if(sampah[p2] == "Kaleng"){
                        jenisSampah = "Kaleng"
                    }
                }

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }

        val bundle: Bundle? = intent.extras
        hasilScan = bundle?.getString("hasil").toString()
        database = FirebaseDatabase.getInstance().getReference("Transaksi")
        key = database.push().getKey().toString()
        getUserData()
        addData()
        openCam()
        cancel()
    }

    fun cancel(){
        btnCancel.setOnClickListener {
            intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    fun getUserData(){
        databaseUser = FirebaseDatabase.getInstance().getReference("User")

        databaseUser.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    for (userSnapshot in snapshot.children){
                        val user = userSnapshot.getValue(User::class.java)
                        //id.add(userSnapshot.key.toString()!!)
                        userArrayList.add(user!!)

                    }
                    for (a in userArrayList){
                        if (a.id_user.toString().equals(hasilScan)){
                            this@HasilActivity.namaUser = a.nama.toString()
                            poinUser += a.poin!!
                            textHasil.text = namaUser
                        }
                    }

                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@HasilActivity, "Gagal", Toast.LENGTH_SHORT).show()
            }

        })
    }


    fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }
    fun getCurrentDateTime(): Date {
        return Calendar.getInstance().time
    }

    private fun addData(){
        btnSelesai.setOnClickListener {
            var poin = 50
            val date = getCurrentDateTime()
            val dateInString = date.toString("dd-MM-yyyy")
            val tempat: String? = "Kelapa Gading"
            val waktu: String? = dateInString
            val id_mitra: Int? = 1
            val id_user: Int? = hasilScan.toInt()
            val jenis: String? = jenisSampah
            val jumlah: Int? = inputJumlah.text.toString().toInt()
            val image: String = textUrl.text.toString()
            val namaUsr: String = textHasil.text.toString()

            val transaksi = Transaksi(id_mitra, id_user, jenis, jumlah, tempat, waktu, image,namaUsr)

            poin = poin * jumlah!!
            poinUser+=poin

            databaseUser.child(hasilScan).child("poin").setValue(poinUser)
            databaseUser.child(hasilScan).child("id_transaksi").setValue(key)
            database.child(key).setValue(transaksi).addOnSuccessListener {
                Toast.makeText(this@HasilActivity, "Berhasil", Toast.LENGTH_SHORT).show()
                intent = Intent(this@HasilActivity, MainActivity::class.java)
                startActivity(intent)
            }.addOnFailureListener {
                Toast.makeText(this@HasilActivity, "Tidak Berhasil", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun openCam(){
        btnGambar.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, 123)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        takenImage = data?.extras?.get("data") as Bitmap
        val bytes = ByteArrayOutputStream()
        takenImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(this.contentResolver, takenImage,"Title", null)
        imageURI = Uri.parse(path)
        textGambar.text = "Gambar ditambahkan !"
        uploadImage()
    }

    private fun uploadImage(){
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Uploading File ...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss",Locale.getDefault())
        val now = Date()
        fileName = formatter.format(now)
        val storageReference = FirebaseStorage.getInstance().getReference("transaksi/$fileName")

        storageReference.putFile(imageURI).addOnSuccessListener {
            Toast.makeText(this@HasilActivity, "Gambar berhasil diunggah", Toast.LENGTH_SHORT).show()
            if (progressDialog.isShowing)progressDialog.dismiss()
            storageReference.downloadUrl.addOnCompleteListener {
                this.url = it.result.toString()
                textUrl.text = url
            }

        }.addOnFailureListener{
            if (progressDialog.isShowing)progressDialog.dismiss()
            Toast.makeText(this@HasilActivity, "Gambar tidak berhasil diunggah", Toast.LENGTH_SHORT).show()
        }


    }

}