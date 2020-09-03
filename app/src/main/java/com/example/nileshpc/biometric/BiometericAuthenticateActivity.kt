@file:Suppress("SpellCheckingInspection")

package com.example.nileshpc.biometric

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import com.example.nileshpc.R
import kotlinx.android.synthetic.main.activity_biometeric_authenticate.*

/**
 * [BiometericAuthenticateActivity] will launch the Biometric with encrypt,decrypt and clear data option.
 *
 */
class BiometericAuthenticateActivity : AppCompatActivity() {
    /**
     * Tag for BiometericAuthenticateActivity
     */
    private val tag = BiometericAuthenticateActivity::class.java.canonicalName

    /**
     * Reference for CryptographyManager
     */
    private var cryptographyManager: CryptographyManager? = null

    /**
     * Reference for BiometricHelper
     */
    private var biometricHelper: BiometricHelper? = null

    /**
     * Object of finger print shared preference
     */
    private var biometricSharedPreference: BiometricSharedPreference? = null

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_biometeric_authenticate)
        //Sharedpreference
        biometricSharedPreference = BiometricSharedPreference(this)

        biometricHelper = BiometricHelper(this, ::biometricCallback)
        biometricHelper?.preparePromptInfo(
            "Biometric",
            "Authenticate to encrypt/decrypt Data",
            "Cancel"
        )
        //create key
        initializeCrypto()
        //clears data from shareprefeernce
        clearData.setOnClickListener {
            encryptionText.setText("")
            biometricSharedPreference?.clearFingerPrintPreferencesData()
            enableButton(true)
        }
        //encrypts Data
        encryptButton.setOnClickListener {
            if (validateTextEncrptionData()) {
                biometricHelper?.setuserInput(encryptionText.text.toString())
                biometricHelper?.showBiometricPrompt(true, null)
            } else {
                Toast.makeText(this, "Enter Valid Text", Toast.LENGTH_SHORT)
                    .show()
            }
            enableButton(false)
        }
        //Decrypts Data
        decryptButton.setOnClickListener {
            if (validateTextEncrptionData()) {
                val encryptionIV: String? = biometricSharedPreference?.encryptionIV
                encryptionIV?.let {
                    val encryptionIVByteArray =
                        Base64.decode(encryptionIV, Base64.DEFAULT)
                    biometricHelper?.setuserInput(encryptionText.text.toString())
                    biometricHelper!!.showBiometricPrompt(false, encryptionIVByteArray)
                }
            } else {
                Toast.makeText(this, "Enter Valid Text", Toast.LENGTH_SHORT)
                    .show()
            }
            enableButton(true)
        }
        //Default UI setting
        if (biometricSharedPreference?.isFingerPrintConfigured == true) {
            enableButton(false)
        } else {
            enableButton(true)

        }


    }

    /**
     * function will initalize the Crypto Key and open setting if fingerprint enrolled
     *
     */
    @RequiresApi(Build.VERSION_CODES.M)
    fun initializeCrypto() {
        cryptographyManager = CryptographyManager.instance
        if (biometricHelper!!.canAuthenticate() == BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                startActivity(Intent(Settings.ACTION_FINGERPRINT_ENROLL))
            } else {
                startActivity(Intent(Settings.ACTION_SECURITY_SETTINGS))
            }
            return
        }
        cryptographyManager?.createKey()

    }

    /**
     * This function will enable/disable the button
     *
     * @param boolean
     */
    private fun enableButton(boolean: Boolean) {
        encryptButton.isEnabled = boolean
        decryptButton.isEnabled = !boolean
    }

    /**
     * This function will validate input edittext
     *
     * @return :Boolean
     */
    private fun validateTextEncrptionData(): Boolean = encryptionText.text.length > 3

    /**
     * callback for Biometric helper
     *
     * @param result
     */
    private fun biometricCallback(result: BiometricResultData) {
        when (result.resultCode) {
            BiometricHelper.REQUEST_CODE_BIOMETRIC_ENROLLED -> {
                Log.d(tag, "Biometric Enrolled successfully")
                val encryptionOperation = biometricHelper?.tryEncrypt(
                    result.authenticationResult?.cryptoObject?.cipher
                )!!

                Log.d(tag, "encryptionOperation ::  + $encryptionOperation")
                if (!encryptionOperation) {
                    Toast.makeText(this, "Error Encrypting Data", Toast.LENGTH_SHORT).show()
                } else {
                    Log.d(tag, "Data save successful")
                    Toast.makeText(this, "Data Encrypted", Toast.LENGTH_SHORT).show()

                }
            }
            BiometricHelper.REQUEST_CODE_BIOMETRIC_AUTHENTICATED -> {
                Log.d(tag, "Biometric Authenticated successfully")

                val decryptedData: String? =
                    biometricHelper?.tryDecrypt(result.authenticationResult?.cryptoObject?.cipher)
                if (decryptedData.isNullOrEmpty()) {
                    Toast.makeText(this, "Error Retriving Encrypted Data", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(this, " Data is : $decryptedData", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            BiometricHelper.REQUEST_CODE_BIOMETRIC_CANCEL -> Log.d(tag, "Biometric Cancel")

            BiometricHelper.REQUEST_CODE_BIOMETRIC_AUTH_FAILED -> {

                Log.d(tag, "Biometric Auth Failed")

                Toast.makeText(this, "Biometric Auth Failed", Toast.LENGTH_SHORT)
                    .show()
            }
            BiometricHelper.REQUEST_CODE_BIOMETRIC_ERROR -> {
                Log.d(tag, "Biometric Auth Error")
                Toast.makeText(this, "Biometric Auth Error", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

}