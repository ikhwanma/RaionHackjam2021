package com.example.hackjamraion

data class Transaksi(
    val id_mitra: Int? = 0, val id_user: Int? = 0, val jenis: String? = null,
    val jumlah: Int? = 0, val tempat: String? = null, val waktu: String? = null,
    val image: String? = null, val namaUsr:String? = null)