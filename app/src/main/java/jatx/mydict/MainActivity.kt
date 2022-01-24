package jatx.mydict

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.gson.Gson
import jatx.mydict.contracts.*
import jatx.mydict.data.db.AppDatabase
import jatx.mydict.data.db.repository.WordRepositoryImpl
import jatx.mydict.ui.dict.DictFragmentDirections
import jatx.mydict.ui.main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.File
import java.io.PrintWriter
import java.util.*

class MainActivity : AppCompatActivity(), Navigator, Deps, Backup, Toasts, Dialogs {

    private lateinit var navController: NavController

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

        val navHost = supportFragmentManager.findFragmentById(R.id.fragment_container)
            as NavHostFragment
        navController = navHost.navController

        setupActionBarWithNavController(navController)
    }

    override fun navigateTo(screen: Screen) {
        when (screen) {
            is MainScreen -> {
                navController.navigate(R.id.mainFragment)
            }
            is DictScreen -> {
                val title = getString(R.string.title_dict) + screen.language.rusString
                val action = MainFragmentDirections.actionMainFragmentToDictFragment(screen.language, title)
                navController.navigate(action)
            }
            is WordScreen -> {
                val wordScreenStr =  Json.encodeToString(WordScreen.serializer(), screen)
                val title = when (screen) {
                    is AddWordScreen -> getString(R.string.title_add_word)
                    is EditWordScreen -> getString(R.string.title_edit_word)
                }
                val action = DictFragmentDirections.actionDictFragmentToWordFragment(wordScreenStr, title)
                navController.navigate(action)
            }
            is TestingScreen -> {
                val title = getString(R.string.title_testing) + screen.language.rusString
                val action = DictFragmentDirections.actionDictFragmentToTestingFragment(screen.language, title)
                navController.navigate(action)
            }
        }
    }

    override fun back() {
        if (navController.graph.startDestination == navController.currentDestination?.id) {
            finish()
        } else {
            navController.popBackStack()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        back()
        return true
    }

    override fun onBackPressed() {
        back()
    }

    override val wordRepository by lazy {
        WordRepositoryImpl(AppDatabase.invoke(applicationContext))
    }

    override fun loadData() = loadLauncher.launch(
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    override fun saveData() = saveLauncher.launch(
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
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

    override fun showToast(toastText: String) {
        Toast.makeText(this, toastText, Toast.LENGTH_LONG).show()
    }

    override fun showToast(resId: Int) = showToast(getString(resId))

    override fun showConfirmDialog(msg: String, onConfirm: () -> Unit) {
        AlertDialog.Builder(this)
            .setMessage(msg)
            .setPositiveButton(R.string.yes) { dialog, _ ->
                onConfirm()
                dialog.dismiss()
            }
            .setNegativeButton(R.string.no) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    override fun showConfirmDialog(msgResId: Int, onConfirm: () -> Unit) =
        showConfirmDialog(getString(msgResId), onConfirm)

}