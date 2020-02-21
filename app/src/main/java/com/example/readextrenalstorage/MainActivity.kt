package com.example.readextrenalstorage

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.readextrenalstorage.adapter.FolderAdapter
import com.example.readextrenalstorage.adapter.TitleAdapter
import com.example.readextrenalstorage.data.Model
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.create_view.view.*
import java.io.File

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private val listSpinner = arrayOf(".text", ".docx")
    private val REQ_CODE = 1001
    private val list = mutableListOf<File>()
    private lateinit var folderAdapter: FolderAdapter
    private lateinit var titleAdapter: TitleAdapter
    private var filePath = ""
    private val myPaths = mutableListOf<Model>()
    private var c = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_main)

//
//        folderAdapter = FolderAdapter(this, list,
//            { readFilesFromStorage(it) },
//            { itemLongClicked(it) })
//        titleAdapter = TitleAdapter(this, myPaths) { openFoldersByTitle(it) }
//        rv.adapter = folderAdapter
//        rv_title.adapter = titleAdapter
//
//        checkPermission()
//
//        add.setOnClickListener {
//            createFolder()
//        }
//
//        lv.setOnClickListener {
//            readFilesFromStorage(null)
//            myPaths.clear()
//            c = -1
//            filePath = ""
//            titleAdapter.notifyDataSetChanged()
//        }
    }

    private fun itemLongClicked(it: View) {
        bottom_nav.visibility = View.INVISIBLE
        bottom_nav2.visibility = View.VISIBLE
//        it.del_chb.visibility = View.VISIBLE   todo add check box all items
//        it.isClickable = false
//        isLongClicked = true
    }

    private fun createFolder() {
        val view = LayoutInflater.from(this).inflate(R.layout.create_view, null, false)
        val dialog = AlertDialog.Builder(this)
            .setCancelable(false)
            .setPositiveButton("Save") { _, _ ->
                val fName = view.et_F_name.text.toString()
                createFileInStorage(fName)
            }
            .setView(view)
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()

        setUpSpinner(view)
        selectItem(view)

    }

    private fun createFileInStorage(fName: String) {
        val file = File(Environment.getExternalStorageDirectory().toString() + filePath + "/$fName")
        if (!file.exists()) {
            val isCreated = file.mkdirs()
            if (isCreated) {
                list.add(file)
                folderAdapter.notifyItemInserted(list.size - 1)
            }
        }
    }

    private fun setUpSpinner(view: View) {
        val arrayAdapter = ArrayAdapter<String>(
            this, android.R.layout.simple_spinner_item, listSpinner
        )
        arrayAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )
        view.spinner.adapter = arrayAdapter
    }

    private fun selectItem(view: View) {
        view.radio_group.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rb_file) {
                view.spinner.visibility = View.VISIBLE
            } else {
                view.spinner.visibility = View.INVISIBLE
            }
        }
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                == PackageManager.PERMISSION_GRANTED
            ) {
                readFilesFromStorage(null)
            } else {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    REQ_CODE
                )
            }
        } else {
            readFilesFromStorage(null)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQ_CODE
            && grantResults.isNotEmpty()
        ) {
            readFilesFromStorage(null)
        }
    }

    private fun readFilesFromStorage(fName: String?) {
        if (fName == null) {
            val fileArray = Environment.getExternalStorageDirectory().listFiles()
            if (fileArray != null) {
                putItemsToRv(fileArray)
            }
        } else {
            filePath += "/$fName"
            val myFilePath = Environment.getExternalStorageDirectory().toString() + "/$filePath"
            val file = File(myFilePath)
            val myFileArray = file.listFiles()
            if (myFileArray != null) {
                putItemsToRv(myFileArray)
            }
            c++
            myPaths.add(Model(fName, c))
            titleAdapter.notifyDataSetChanged()
        }
    }

    private fun putItemsToRv(fileArray: Array<File>) {
        val myFolders = mutableListOf<File>()
        val myFiles = mutableListOf<File>()
        list.clear()
        list.addAll(fileArray.toList())
        var f: File
        for (i in 0 until list.size) {
            f = list[i]
            if (f.isDirectory) {
                myFolders.add(f)
            } else {
                myFiles.add(f)
            }
        }
        myFolders.sortBy { it.name }
        myFiles.sortBy { it.name }
        list.clear()
        list.addAll(myFolders)
        list.addAll(myFolders.size, myFiles)
        folderAdapter.notifyDataSetChanged()
    }

    override fun onBackPressed() {
        if (myPaths.size == 0) {
            c = -1
            super.onBackPressed()
        } else {
            c--
            val pos = myPaths.size - 1
            myPaths.removeAt(pos)
            titleAdapter.notifyItemRemoved(pos)

            filePath = ""
            for (i in 0 until myPaths.size) {
                filePath += "/" + myPaths[i].name
            }
            readFile()

        }
    }

    private fun openFoldersByTitle(it: Model) {
        val myLocalList = mutableListOf<Model>()

        titleAdapter.notifyItemRangeRemoved(it.pos, c)
        c = it.pos
        filePath = ""

        for (i in 0..it.pos) {
            filePath += "/" + myPaths[i].name
            myLocalList.add(myPaths[i])
        }

        myPaths.clear()
        myPaths.addAll(myLocalList)
        titleAdapter.notifyDataSetChanged()
        readFile()
    }

    private fun readFile() {
        val path = Environment.getExternalStorageDirectory().toString() + "/$filePath"
        val files = File(path).listFiles()!!
        putItemsToRv(files)
    }
}