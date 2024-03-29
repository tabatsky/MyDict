package jatx.mydict

import android.Manifest
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.gson.Gson
import jatx.mydict.contracts.*
import jatx.mydict.ui.dict.DictFragmentDirections
import jatx.mydict.ui.main.MainFragmentDirections
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.koin.android.ext.android.inject
import java.io.File
import java.io.PrintWriter
import java.util.*

class MainActivity : AppCompatActivity(), Navigator, Backup, Toasts, Dialogs {

    private lateinit var navController: NavController

    private val deps: Deps by inject()

    private val activityProvider: ActivityProvider by inject()

    private val loadLauncher =
        registerForActivityResult(
            ActivityResultContracts.OpenDocument()
        ) {
            it?.let { uri ->
                onLoadFromUri(uri)
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

    override fun onStart() {
        super.onStart()
        activityProvider.currentActivity = this
    }

    override fun onStop() {
        super.onStop()
        activityProvider.currentActivity = null
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
        Log.e("navigator", "back")
        if (navController.graph.startDestinationId == navController.currentDestination?.id) {
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

    override fun loadData() = loadLauncher.launch(arrayOf("*/*"))
    override fun saveData() {
        if (Build.VERSION.SDK_INT <= 29) {
            saveLauncher.launch(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
        } else if (Build.VERSION.SDK_INT <= 32) {
            saveLauncher.launch(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            )
        } else {
            onSavePermissionGranted()
        }
    }

    private fun onLoadFromUri(uri: Uri) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val sc = Scanner(contentResolver.openInputStream(uri))
                    val backupDataStr = sc.nextLine()
                    sc.close()
                    val backupData = Gson().fromJson(backupDataStr, BackupData::class.java)
                    Log.e("backup", backupData.toString())
                    deps.wordRepository.insertReplaceList(backupData.words)
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
                val words = deps.wordRepository.getAll()
                val backupData = BackupData(words)
                val backupDataStr = Gson().toJson(backupData)
                Log.e("backup", backupDataStr)
                try {
                    val dir = Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                    dir.mkdirs()
                    val uid = android.os.Process.myUid()
                    val outFile = File(dir, "MyDict_$uid.json.txt")
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