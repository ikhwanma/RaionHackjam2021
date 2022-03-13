package com.example.hackjamraion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import java.util.ArrayList

class MyAdapter(private val transaksiList: ArrayList<Transaksi>): RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item,parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = transaksiList[itemCount-1-position]
        val list = currentItem.waktu!!.split("-")
        var tanggal = list[0]
        var bulan = ""
        var tahun = list[2]
        if (list[1] == "1"){
            bulan = "Januari"
        }else if (list[1] == "2"){
            bulan = "Februari"
        }else if (list[1] == "3"){
            bulan = "Maret"
        }else if (list[1] == "4"){
            bulan = "April"
        }else if (list[1] == "5"){
            bulan = "Mei"
        }else if (list[1] == "6"){
            bulan = "Juni"
        }else if (list[1] == "7"){
            bulan = "Juli"
        }else if (list[1] == "8"){
            bulan = "Agustus"
        }else if (list[1] == "9"){
            bulan = "September"
        }else if (list[1] == "10"){
            bulan = "Oktober"
        }else if (list[1] == "11"){
            bulan = "November"
        }else if (list[1] == "12"){
            bulan = "Desember"
        }
        var tgl = "$tanggal $bulan $tahun"
        holder.textWaktu.text = tgl
        holder.textJenis.text = currentItem.jenis
        holder.textJumlah.text = currentItem.jumlah.toString()
        Picasso.get().load(currentItem.image).into(holder.gambarSampah)
        holder.textNama.text = currentItem.namaUsr
    }

    override fun getItemCount(): Int {
        return transaksiList.size
    }

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val textWaktu: TextView = itemView.findViewById(R.id.textWaktu)
        val textJenis: TextView = itemView.findViewById(R.id.textJenis)
        val textJumlah: TextView = itemView.findViewById(R.id.textJumlah)
        val gambarSampah: ImageView = itemView.findViewById(R.id.img_sampah)
        val textNama: TextView = itemView.findViewById(R.id.textNamaUser)
    }
}