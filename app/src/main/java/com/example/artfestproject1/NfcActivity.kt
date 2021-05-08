package com.example.artfestproject1

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.os.Parcelable
import android.provider.Settings.ACTION_NFC_SETTINGS
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.artfestproject1.utils.Utils
import org.json.JSONObject
import java.io.*

class NfcActivity : AppCompatActivity() {

    protected var mMyApp: MyApp? = null
    private var nfcAdapter: NfcAdapter? = null
    private var pendingIntent: PendingIntent? = null
    var fromWhere: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nfc)
        mMyApp = this.applicationContext as MyApp

        val previousIntent = this.intent
        fromWhere = previousIntent.getStringExtra("fromWhere").toString()
        Log.d("Test", fromWhere)

        initNfcAdapter()
    }

    override fun onResume() {
        super.onResume()
        // app reference relink
        mMyApp!!.setCurrentActivity(this)
        enableNfcForegroundDispatch()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        setIntent(intent)
        resolveIntent(intent)
    }

    override fun onPause() {
        super.onPause()

        disableNfcForegroundDispatch()
    }

    private fun initNfcAdapter() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (nfcAdapter == null) {
            Toast.makeText(this, "No NFC", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
    }

    private fun enableNfcForegroundDispatch() {
        val nfcAdapterRefCopy = nfcAdapter
        val intent = Intent(this, this.javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        if (nfcAdapterRefCopy != null) {
            if (!nfcAdapterRefCopy.isEnabled()) {
                showNFCSettings()
            }
            nfcAdapterRefCopy.enableForegroundDispatch(this, pendingIntent, null, null)
        }
    }

    private fun showNFCSettings() {
        Toast.makeText(this, "You need to enable NFC!", Toast.LENGTH_SHORT).show()
        val intent = Intent(ACTION_NFC_SETTINGS)
        startActivity(intent)
    }

    private fun disableNfcForegroundDispatch() {
        try {
            nfcAdapter?.disableForegroundDispatch(this)
        } catch (e: IllegalStateException) {
            // Log.e(getTag(), "Error disabling NFC foreground dispatch", e)
        }
    }

    private fun resolveIntent(intent: Intent) {
        val action = intent.action

        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            val rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
            if (rawMsgs != null) {
                // Toast.makeText(this, "rawMsgs is not null", Toast.LENGTH_SHORT).show()
            } else {

                if (fromWhere == "detail") {
                    // Toast.makeText(this, "rawMsgs is null", Toast.LENGTH_SHORT).show()
                    // 讀卡後進到這個地方或許是因為，收到的是 ACTION_TECH_DISCOVERED intent，
                    // 所以 EXTRA_NDEF_MESSAGES 裡面才沒有東西！
                    // val empty = ByteArray(0)
                    // val id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID)
                    val tag = intent.getParcelableExtra<Parcelable>(NfcAdapter.EXTRA_TAG) as Tag
                    // val payload = dumpTagData(tag).toByteArray()
                    // val record = NdefRecord(NdefRecord.TNF_UNKNOWN, empty, id, payload)
                    // val emptyMsg = NdefMessage(arrayOf(record))
                    // val emptyNdefMessages: Array<NdefMessage> = arrayOf(emptyMsg)
                    // Toast.makeText(this, dumpTagData(tag), Toast.LENGTH_SHORT).show()

                    // ======== Check whether the ID has appeared ========

                    val filename = "userList.txt"
                    val file = File(this.getFilesDir(), filename)

                    val filename2 = "vipList.txt"
                    val file2 = File(this.getFilesDir(), filename2)

                    // the way to delete file
                    // file.delete()

                    if (!file.exists()) {
                        val fileContents = ""
                        this.openFileOutput(filename, Context.MODE_PRIVATE).use {
                            it.write(fileContents.toByteArray())
                        }
                    }

                    if (!file2.exists()) {
                        val fileContents = ""
                        this.openFileOutput(filename2, Context.MODE_PRIVATE).use {
                            it.write(fileContents.toByteArray())
                        }
                    }

                    // Read data
                    val fileReader = FileReader(file)
                    val bufferedReader = BufferedReader(fileReader)
                    val stringBuilder = java.lang.StringBuilder()
                    var line: String
                    do {
                        try { line = bufferedReader.readLine() } catch (e: Exception) { stringBuilder.append(""); break }
                        stringBuilder.append(line).append("\n")
                    } while (true)
                    bufferedReader.close()
                    val response = stringBuilder.toString()

                    // Read data (vip)
                    val fileReader2 = FileReader(file2)
                    val bufferedReader2 = BufferedReader(fileReader2)
                    val stringBuilder2 = java.lang.StringBuilder()
                    var line2: String
                    do {
                        try { line2 = bufferedReader2.readLine() } catch (e: Exception) { stringBuilder2.append(""); break }
                        stringBuilder2.append(line2).append("\n")
                    } while (true)
                    bufferedReader2.close()
                    val response2 = stringBuilder2.toString()


                    // String to JSON
                    val jsonObject: JSONObject
                    if (response == "") {
                        jsonObject = JSONObject()
                    } else {
                        jsonObject = JSONObject(response)
                    }


                    // String to JSON (vip)
                    val jsonObject2: JSONObject
                    if (response2 == "") {
                        jsonObject2 = JSONObject()
                    } else {
                        jsonObject2 = JSONObject(response2)
                    }

                    // Check vip id
                    if (jsonObject2.has(dumpTagData((tag)))) {

                        Log.d("Test", "1")

                        Toast.makeText(this, "歡迎VIP！", Toast.LENGTH_LONG).show()

                        val intent_to_explain = Intent(this, ExplainActivity::class.java)
                        startActivity(intent_to_explain)
                    }

                    // Check whether the ID has appeared
                    else if (jsonObject.has(dumpTagData(tag))) {

                        Log.d("Test", "2")

                        Toast.makeText(this, "刷過囉！", Toast.LENGTH_SHORT).show()

                        // 回到主畫面讓下一位使用
                        val intent_to_main = Intent(this, MainActivity::class.java)
                        startActivity(intent_to_main)

                        // For Debug
//                        val intent_to_explain = Intent(this, ExplainActivity::class.java)
//                        startActivity(intent_to_explain)



                    } else {

                        Toast.makeText(this, "歡迎歡迎～", Toast.LENGTH_SHORT).show()

                        // Append new id
                        jsonObject.put(dumpTagData(tag), true);

                        // Convert JsonObject to String Format
                        val userString: String = jsonObject.toString()

                        // Write back to the file
                        val fileWriter = FileWriter(file)
                        val bufferedWriter = BufferedWriter(fileWriter)
                        bufferedWriter.write(userString)
                        bufferedWriter.close()

                        // 進到拍照的環節
                        val intent_to_explain = Intent(this, ExplainActivity::class.java)
                        startActivity(intent_to_explain)
                    }

                    // ======== Check whether the ID has appeared ========

                } else if (fromWhere == "admin") {

                    val tag = intent.getParcelableExtra<Parcelable>(NfcAdapter.EXTRA_TAG) as Tag

                    val filename = "vipList.txt"
                    val file = File(this.getFilesDir(), filename)

                    // the way to delete file
                    // file.delete()

                    if (!file.exists()) {
                        val fileContents = ""
                        this.openFileOutput(filename, Context.MODE_PRIVATE).use {
                            it.write(fileContents.toByteArray())
                        }
                    }

                    // Read data
                    val fileReader = FileReader(file)
                    val bufferedReader = BufferedReader(fileReader)
                    val stringBuilder = java.lang.StringBuilder()
                    var line: String
                    do {
                        try { line = bufferedReader.readLine() } catch (e: Exception) { stringBuilder.append(""); break }
                        stringBuilder.append(line).append("\n")
                    } while (true)
                    bufferedReader.close()
                    val response = stringBuilder.toString()

                    // String to JSON
                    val jsonObject: JSONObject
                    if (response == "") {
                        jsonObject = JSONObject()
                    } else {
                        jsonObject = JSONObject(response)
                    }

                    // Check whether the ID has appeared
                    if (jsonObject.has(dumpTagData(tag))) {

                        Toast.makeText(this, "這張卡之前加過囉！", Toast.LENGTH_LONG).show()

                        val intent_to_main = Intent(this, MainActivity::class.java)
                        startActivity(intent_to_main)

                    } else {

                        Toast.makeText(this, "歡迎成為VIP～", Toast.LENGTH_LONG).show()

                        // Append new id
                        jsonObject.put(dumpTagData(tag), true);

                        // Convert JsonObject to String Format
                        val userString: String = jsonObject.toString()

                        // Write back to the file
                        val fileWriter = FileWriter(file)
                        val bufferedWriter = BufferedWriter(fileWriter)
                        bufferedWriter.write(userString)
                        bufferedWriter.close()

                        val intent_to_main = Intent(this, MainActivity::class.java)
                        startActivity(intent_to_main)
                    }

                }

            }
        }
    }

    // Tag data is converted to string to display
    private fun dumpTagData(tag: Tag): String {
        val sb = StringBuilder()
        val id = tag.getId()

        // sb.append("ID: ").append(Utils.toHex(id)).append('\n')
        // sb.append("Have fun!")
        sb.append(Utils.toHex(id))

        return sb.toString()
    }

}