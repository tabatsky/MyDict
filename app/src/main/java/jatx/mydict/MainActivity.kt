package jatx.mydict

import android.Manifest
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import jatx.mydict.contracts.*
import jatx.mydict.domain.Language
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

class MainActivity : AppCompatActivity(), Navigator, Backup, Toasts, Dialogs, Auth, Speaker {

    private lateinit var navController: NavController

    private val deps: Deps by inject()

    private val activityProvider: ActivityProvider by inject()

    private lateinit var auth: FirebaseAuth
    private var theUser: FirebaseUser? = null

    val db = Firebase.firestore

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

    private lateinit var tts: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        val navHost = supportFragmentManager.findFragmentById(R.id.fragment_container)
            as NavHostFragment
        navController = navHost.navController

        setupActionBarWithNavController(navController)

        auth = Firebase.auth
        loadAuth { email, password ->
            if (email.isNotEmpty() && password.isNotEmpty()) {
                signIn(email, password)
            }
        }

        tts = TextToSpeech(this) {}
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
            is AuthScreen -> {
                val title = getString(R.string.title_auth)
                navController.navigate(MainFragmentDirections.actionMainFragmentToAuthFragment(title))
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

    override fun loadDataFromFirestore() {
        user?.let { theUser ->
            val userUid = theUser.uid

            db.collection("backups")
                .document(userUid)
                .get()
                .addOnSuccessListener { document ->
                    document?.let {
                        Log.e("load", "${document.id} => ${document.data}")
                        (document.data?.get("backupDataStr") as? String)?.let { backupDataStr ->
                            val backupData = Gson().fromJson(backupDataStr, BackupData::class.java)
                            Log.e("backup", backupData.toString())
                            lifecycleScope.launch {
                                withContext(Dispatchers.IO) {
                                    deps.wordRepository.insertReplaceList(backupData.words)
                                }
                                withContext(Dispatchers.Main) {
                                    showToast(R.string.toast_load_data_success)
                                }
                            }
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("load", "error", exception)
                    showToast(R.string.toast_some_error)
                }
        } ?: run {
            showToast(R.string.toast_need_login)
        }
    }

    override fun saveDataToFirestore() {
        user?.let { theUser ->
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    val words = deps.wordRepository.getAll()
                    val backupData = BackupData(words)
                    val backupDataStr = Gson().toJson(backupData)
                    val userUid = theUser.uid

                    val doc = hashMapOf(
                        "backupDataStr" to backupDataStr
                    )

                    db.collection("backups")
                        .document(userUid)
                        .set(doc)
                        .addOnSuccessListener {
                            Log.e("save", "success")
                            showToast(R.string.toast_save_data_success)
                        }
                        .addOnFailureListener { e ->
                            Log.w("save", "error", e)
                            showToast(R.string.toast_some_error)
                        }
                }
            }
        } ?: run {
            showToast(R.string.toast_need_login)
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
                        showToast(R.string.toast_load_data_success)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    withContext(Dispatchers.Main) {
                        showToast(R.string.toast_some_error)
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
                        showToast(R.string.toast_save_data_success)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    withContext(Dispatchers.Main) {
                        showToast(R.string.toast_some_error)
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

    override val user: FirebaseUser?
        get() = theUser

    override fun loadAuth(onSuccess: (String, String) -> Unit) {
        val sp = getSharedPreferences("MyDict", 0)
        val login = sp.getString("email", "")!!
        val password = sp.getString("password", "")!!
        onSuccess(login, password)
    }

    override fun saveAuth(email: String, password: String) {
        val sp = getSharedPreferences("MyDict", 0)
        val editor = sp.edit()
        editor.putString("email", email)
        editor.putString("password", password)
        editor.apply()
    }

    override fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    Log.e("user", user?.uid.toString())
                    saveAuth(email, password)
                    showToast(R.string.toast_sign_in_success)
                    theUser = user
                } else {
                    // If sign in fails, display a message to the user.
                    Log.e("sign in", "signInWithEmail:failure", task.exception)
                    showToast(R.string.toast_sign_in_error)
                }
            }
    }

    override fun signUp(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    Log.e("user", user?.uid.toString())
                    showToast(R.string.toast_sign_up_success)
                    saveAuth(email, password)
                    theUser = user
                } else {
                    // If sign in fails, display a message to the user.
                    Log.e("sign up", "createUserWithEmail:failure", task.exception)
                    showToast(R.string.toast_sign_up_error)
                }
            }
    }

    override fun speak(text: String, language: Language, isForeign: Boolean) {
        val locale = if (isForeign) {
            when (language) {
                Language.ENGLISH -> Locale.ENGLISH
                Language.DEUTSCH -> Locale.GERMAN
                Language.JAPANESE -> Locale.JAPANESE
            }
        } else {
            Locale("ru", "RU")
        }
        val actualText = text.split("、").first()

        tts.language = locale
        tts.speak(actualText, TextToSpeech.QUEUE_FLUSH, null)
    }
}