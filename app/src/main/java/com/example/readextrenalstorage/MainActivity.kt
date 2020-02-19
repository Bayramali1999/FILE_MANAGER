package com.example.readextrenalstorage

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.readextrenalstorage.adapter.FolderAdapter
import com.example.readextrenalstorage.adapter.TitleAdapter
import com.example.readextrenalstorage.data.Model
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.io.File

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private val REQ_CODE_FOR_READ = 1001
    private val REQ_CODE_FOR_WRITE = 1002
    private val list = mutableListOf<File>()
    private lateinit var folderAdapter: FolderAdapter
    private lateinit var titleAdapter: TitleAdapter
    private var filePath = ""
    private val myPaths = mutableListOf<Model>()
    private var c = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        folderAdapter = FolderAdapter(this, list) { readFilesFromStorage(it) }
        titleAdapter = TitleAdapter(this, myPaths) { openFoldersByTitle(it) }
        rv.adapter = folderAdapter
        rv_title.adapter = titleAdapter

        checkPermission()

        fab.setOnClickListener {
            checkPermissionToWrite()
        }

        lv.setOnClickListener {
            readFilesFromStorage(null)
            myPaths.clear()
            c= -1
            filePath = ""
            titleAdapter.notifyDataSetChanged()
        }
    }

    private fun checkPermissionToWrite() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                == PackageManager.PERMISSION_GRANTED
            ) {
                //TODO()we have permission

                Toast.makeText(this, "have permission to write", Toast.LENGTH_LONG).show()
            } else {
                requestPermissions(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQ_CODE_FOR_WRITE
                )

                Toast.makeText(this, "have not permission to write", Toast.LENGTH_LONG).show()
            }
        } else {
            //TODO()we have permission

        }
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                readFilesFromStorage(null)
            } else {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ),
                    REQ_CODE_FOR_READ
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
        if (requestCode == REQ_CODE_FOR_READ
            && grantResults.isNotEmpty()
        ) {
            readFilesFromStorage(null)
        } else if (requestCode == REQ_CODE_FOR_WRITE &&
            grantResults.isNotEmpty()
        ) {
//            TODO() we give permission to write

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