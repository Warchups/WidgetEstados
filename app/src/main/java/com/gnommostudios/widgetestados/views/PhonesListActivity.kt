package com.gnommostudios.widgetestados.views

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.gnommostudios.widgetestados.R
import kotlinx.android.synthetic.main.activity_phones_list.*
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.support.v4.app.ActivityCompat
import android.annotation.SuppressLint
import android.support.v4.content.ContextCompat

class PhonesListActivity : AppCompatActivity(), AdapterView.OnItemClickListener {

    companion object {
        private const val MY_PERMISSIONS_REQUEST_CALL_PHONE = 0x2
    }

    private var listPhonesPrefs: SharedPreferences? = null
    private var map: Map<String, String>? = null

    private val phonesList: MutableList<String> = ArrayList()

    private var position = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phones_list)

        listPhonesPrefs = getSharedPreferences("phones", Context.MODE_PRIVATE)

        listViewPhones.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, phonesList)

        listViewPhones.onItemClickListener = this
    }

    override fun onResume() {
        super.onResume()
        refreshList()
    }

    override fun onItemClick(adapterView: AdapterView<*>?, view: View?, pos: Int, p3: Long) {
        position = pos

        //Compruebo los permisos de llamada
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.CALL_PHONE),
                    MY_PERMISSIONS_REQUEST_CALL_PHONE)
        } else {
            initCall()
        }

    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        //Si los habilito inicio la llamada, si no, no hago nada
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_CALL_PHONE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initCall()
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun initCall() {
        startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:${phonesList[position].split(" - ")[1]}")))
    }

    @Suppress("UNCHECKED_CAST")
    private fun refreshList() {
        phonesList.clear()
        map = listPhonesPrefs!!.all as Map<String, String>

        for (i in map!!.keys)
            phonesList.add("${i.split("_")[1]} - ${map!![i]!!}")

        val adapter = listViewPhones.adapter as ArrayAdapter<String>
        adapter.notifyDataSetChanged()
    }
}
