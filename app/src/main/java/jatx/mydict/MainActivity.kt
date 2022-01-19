package jatx.mydict

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import jatx.mydict.backup.Backuper
import jatx.mydict.backup.BackupData
import jatx.mydict.data.db.AppDatabase
import jatx.mydict.data.db.repository.WordRepositoryImpl
import jatx.mydict.deps.Deps
import jatx.mydict.navigation.*
import jatx.mydict.ui.addword.WordFragment
import jatx.mydict.ui.dict.DictFragment
import jatx.mydict.ui.main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.PrintWriter
import java.util.*

class MainActivity : AppCompatActivity(), Navigator, Deps, Backuper {

    companion object {
        private var currentScreen: Screen = MainScreen
    }

    private val loadLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {
            if (it) {
                onLoadPermissionGranted()
            }
        }

    private val saveLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { map ->
            if (map.all { it.value }) {
                onSavePermissionGranted()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            navigateTo(MainScreen)
        }
    }

    override fun navigateTo(screen: Screen) {
        currentScreen = screen
        val fragment = when (screen) {
            is MainScreen -> {
                MainFragment.newInstance()
            }
            is DictScreen -> {
                DictFragment.newInstance(screen.language)
            }
            is WordScreen -> {
                WordFragment.newInstance(screen)
            }
        }
        commitFragment(fragment)
    }

    override fun back() {
        when (val screen = currentScreen) {
            is MainScreen -> {
                finish()
            }
            is DictScreen -> {
                navigateTo(MainScreen)
            }
            is AddWordScreen -> {
                navigateTo(DictScreen(screen.language))
            }
            is EditWordScreen -> {
                navigateTo(DictScreen(screen.word.language))
            }
        }
    }

    override fun onBackPressed() {
        back()
    }

    private fun commitFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commitNow()
    }

    override val wordRepository by lazy {
        WordRepositoryImpl(AppDatabase.invoke(applicationContext))
    }

    override fun loadData() = loadLauncher.launch(
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    override fun saveData() = saveLauncher.launch(
        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    )

    private fun onLoadPermissionGranted() {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val inFile = File(
                        Environment
                            .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                        "MyDict.json"
                    )
                    val sc = Scanner(inFile)
                    val backupDataStr = sc.nextLine()
                    sc.close()
                    val backupData = Gson().fromJson(backupDataStr, BackupData::class.java)
                    Log.e("backup", backupData.toString())
                    wordRepository.insertReplaceList(backupData.words)
                    withContext(Dispatchers.Main) {
                        showToast(getString(R.string.toast_load_data_success))
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    withContext(Dispatchers.Main) {
                        showToast(getString(R.string.toast_some_error))
                    }
                }
            }
        }
    }

    private fun onSavePermissionGranted() {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val words = wordRepository.getAll()
                val backupData = BackupData(words)
                val backupDataStr = Gson().toJson(backupData)
                Log.e("backup", backupDataStr)
                try {
                    val dir = Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                    dir.mkdirs()
                    val outFile = File(dir, "MyDict.json")
                    val pw = PrintWriter(outFile)
                    pw.println(backupDataStr)
                    pw.flush()
                    pw.close()
                    withContext(Dispatchers.Main) {
                        showToast(getString(R.string.toast_save_data_success))
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    withContext(Dispatchers.Main) {
                        showToast(getString(R.string.toast_some_error))
                    }
                }
            }
        }
    }

    private fun showToast(toastText: String) {
        Toast.makeText(this, toastText, Toast.LENGTH_LONG).show()
    }
}