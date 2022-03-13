package com.example.hackjamraion

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var textJumlah: TextView
    private lateinit var btnScan: Button
    private lateinit var database: DatabaseReference
    private lateinit var databaseMitra: DatabaseReference
    private lateinit var transaksiRecyclerView: RecyclerView
    private lateinit var transaksiArrayList: ArrayList<Transaksi>
    private var counter = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnScan = findViewById(R.id.btnScan)
        textJumlah = findViewById(R.id.textJumlah)
        transaksiRecyclerView = findViewById(R.id.list)
        transaksiRecyclerView.layoutManager = LinearLayoutManager(this)
        transaksiRecyclerView.setHasFixedSize(true)
        transaksiArrayList = arrayListOf<Transaksi>()

        getTransaksiData()
        database = FirebaseDatabase.getInstance().getReference("Mitra")

        btnScan.setOnClickListener {
            database.child("1").child("nama").setValue("Kelapa Gading")
        }


    }

    private fun getTransaksiData() {

        database = FirebaseDatabase.getInstance().getReference("Transaksi")
        database.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    for (transaksiSnapshot in snapshot.children){
                        val transaksi = transaksiSnapshot.getValue(Transaksi::class.java)
                        transaksiArrayList.add(transaksi!!)
                    }
                    transaksiRecyclerView.adapter = MyAdapter(transaksiArrayList)
                }
                val date = getCurrentDateTime()
                val dateInString = date.toString("dd-MM-yyyy")
                var i = 0
                for (a in transaksiArrayList){

                    if (a.waktu.equals(dateInString)){
                        counter += a.jumlah!!
                    }
                    i++
                }
                textJumlah.text = counter.toString()

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Gagal", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }
    private fun getCurrentDateTime(): Date {
        return Calendar.getInstance().time
    }
}