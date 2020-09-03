@file:Suppress("SpellCheckingInspection")

package com.example.nileshpc.biometric


import android.os.Build
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Base64
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.nileshpc.R
import java.io.UnsupportedEncodingException
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException


/**
 * BiometricHelper class handles to perform  biometric authentication. This class contains all functions required to authenticate with biometric.
 * This class has function to check Biometric availability status in the device and it check whether  biometric has been enrolled in the device or not.

 */
class BiometricHelper(
    private val activity: FragmentActivity,
    private val callback: (result: BiometricResultData) -> Unit
) {

    /**
     * Refers [BiometricManager]
     */
    private val biometricManager = BiometricManager.from(activity)

    /**
     * Refers [BiometricPrompt.PromptInfo] to configure prompt
     */
    private var promptInfo: BiometricPrompt.PromptInfo? = null

    private var userinput: String? = null


    private val tag = "BiometricHelper"

    /**
     * Refers [BiometricPrompt] biometric prompt
     */
    private val bioMetricPrompt = BiometricPrompt(
        activity, ContextCompat.getMainExecutor(activity),
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                Log.e(tag, "Error Code: $errorCode")
                Log.e(tag, "Error Message: $errString")
                //Lockout and Negative button click callback
                if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON || errorCode == BiometricPrompt.ERROR_USER_CANCELED) {
                    callback(
                        BiometricResultData(
                            REQUEST_CODE_BIOMETRIC_CANCEL,
                            errString.toString(),
                            null
                        )
                    )
                } else {
                    callback(
                        BiometricResultData(
                            REQUEST_CODE_BIOMETRIC_ERROR,
                            errString.toString(),
                            null
                        )
                    )
                }
                super.onAuthenticationError(errorCode, errString)
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                Log.d(tag, "Result: ${result.cryptoObject}")
                val biometricSharedPreference = BiometricSharedPreference(activity)
                if (biometricSharedPreference.isFingerPrintConfigured) {
                    callback(
                        BiometricResultData(
                            REQUEST_CODE_BIOMETRIC_AUTHENTICATED, "success", result
                        )
                    )
                } else {
                    callback(
                        BiometricResultData(
                            REQUEST_CODE_BIOMETRIC_ENROLLED,
                            "success",
                            result
                        )
                    )
                }

                super.onAuthenticationSucceeded(result)

            }

            override fun onAuthenticationFailed() {
                Log.e(tag, "Biometric AuthenticationFailed")
                // Callback is not required,Biometric handles UI failure repsonse
                super.onAuthenticationFailed()
            }
        })

    /**
     * This function checks the availablity of Biometric device and service both.
     * @return Returns the Int value from Biometric Framework  of Android.
     */
    fun canAuthenticate(): Int {
        val result = biometricManager.canAuthenticate()
        when (result) {
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Log.e(tag, " Biometric Hardware un available, service not connected with hardware")
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                Log.e(tag, " Biometric Hardware not available in your device.")
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                Log.e(tag, "Biometric is not enrolled")

            }
            BiometricManager.BIOMETRIC_SUCCESS -> {
                Log.d(tag, " Biometric id ready to use")
                // If success then no need to send the callback we can prompt the biometric authentication
            }
        }
        return result
    }

    /**
     * This function prepares PromptInfo.This function configure title, subtitle and navigation button caption on Biometric Prompt
     * @param title String title
     * @param subTitle String subtitle
     * @param navigationTitle String navigation button caption
     * @return Return Instance of [BiometricHelper]
     */
    fun preparePromptInfo(
        title: String,
        subTitle: String,
        navigationTitle: String
    ): BiometricHelper {
        val spannableTitle = SpannableString(title)
        val spannableButton = SpannableString(navigationTitle)
        //This is required for below Q only.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            spannableTitle.setSpan(
                ForegroundColorSpan(
                    ContextCompat.getColor(
                        activity,
                        R.color.black
                    )
                ), 0, title.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            spannableButton.setSpan(
                ForegroundColorSpan(
                    ContextCompat.getColor(
                        activity,
                        R.color.black
                    )
                ), 0, navigationTitle.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(spannableTitle)
            .setSubtitle(subTitle)
            .setNegativeButtonText(spannableButton)
            .setConfirmationRequired(false)
            .build()
        return this
    }


    /**
     * This sets input
     *
     *@param input  : input
     */
    fun setuserInput(input: String?) {
        this.userinput = input
    }

    /**
     * This function will show Biometric prompt to authentiate while encrypt/decrypting.
     * @param isEncrypt : If true then generate encryption cipher vice-versa creates decrypt cipher on false
     * @param encryptionIV : encryptionIV for Decrypting.
     */
    @RequiresApi(Build.VERSION_CODES.M)
    fun showBiometricPrompt(isEncrypt: Boolean, encryptionIV: ByteArray?) {
        if (promptInfo != null) {
            if (canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS) {
                val cipher = if (isEncrypt) {
                    CryptographyManager.instance.generateEncryptionCipher()
                } else {
                    CryptographyManager.instance.generateDecryptionEncryptionIVCipher(encryptionIV)
                }

                cipher.let {
                    bioMetricPrompt.authenticate(
                        promptInfo!!,
                        BiometricPrompt.CryptoObject(cipher!!)
                    )
                }

            }
        } else {

            Log.e(tag, "PromptInfo Object is not initialized!")
        }
    }


    /**
     * Encrypt using the cipher provided
     *
     * @param cipher - object to be used for encryption
     * @return a boolean suggesting if the operation was successful or failure
     */
    fun tryEncrypt(cipher: Cipher?): Boolean {
        var encryptionOperation = false
        try {
            cipher?.let {
                val userInput = userinput
                val encryptionIVByteArray = cipher.iv
                val encryptedUserInputByteArray =
                    cipher.doFinal(userInput?.toByteArray(charset(CHARSET)))
                val encryptedUserInput = Base64.encodeToString(
                    encryptedUserInputByteArray,
                    Base64.DEFAULT
                )
                val encryptionIV = Base64.encodeToString(encryptionIVByteArray, Base64.DEFAULT)
                Log.d(tag, "encryptedUserInput :: $encryptedUserInput")
                Log.d(tag, "encryptionIV :: $encryptionIV")

                val biometricSharedPreference = BiometricSharedPreference(activity)
                biometricSharedPreference.saveUserPassAndEncryptionIV(
                    encryptedUserInput,
                    encryptionIV
                )
                biometricSharedPreference.setFingerprintConfigured(true)
                encryptionOperation = true
            }
        } catch (e: BadPaddingException) {
            Log.e(tag, "Error while encrypting")
        } catch (e: IllegalBlockSizeException) {
            Log.e(tag, "Error while encrypting")
        } catch (e: UnsupportedEncodingException) {
            Log.e(tag, "Error while encrypting")
        }
        return encryptionOperation
    }

    /**
     * Decrypt data using the cipher provided after reading from shared preference
     *
     * @param cipher - object to be used for decryption
     * @return a boolean suggesting if the operation was successful or failure
     */
    fun tryDecrypt(cipher: Cipher?): String? {
        try {
            cipher?.let {
                val biometricSharedPreference = BiometricSharedPreference(activity)
                val encryptedUserInput: String? =
                    biometricSharedPreference.userInput
                Log.d(tag, "encryptedUserInput :: $encryptedUserInput")

                val encryptedUserInputByteArray =
                    Base64.decode(encryptedUserInput, Base64.DEFAULT)
                val decryptedUserInputByteArray =
                    cipher.doFinal(encryptedUserInputByteArray)

                Log.d(
                    tag,
                    "Plaintext Userinput is" + String(
                        decryptedUserInputByteArray,
                        charset(CHARSET)
                    )
                )

                return String(decryptedUserInputByteArray, charset(CHARSET))
            }
        } catch (e: BadPaddingException) {
            Log.e(tag, "Error while encrypting")
        } catch (e: IllegalBlockSizeException) {
            Log.e(tag, "Error while encrypting")
        } catch (e: UnsupportedEncodingException) {
            Log.e(tag, "Error while encrypting")
        }
        return null
    }


    companion object {

        /**
         * Refers Constant for biometric enrolled
         */
        const val REQUEST_CODE_BIOMETRIC_ENROLLED = 60

        /**
         * Refers Constant for biometric authentication
         */
        const val REQUEST_CODE_BIOMETRIC_AUTHENTICATED = 61

        /**
         * Refers Constant for biometric error
         */
        const val REQUEST_CODE_BIOMETRIC_ERROR = 62

        /**
         * Refers Constant for biometric error
         */
        const val REQUEST_CODE_BIOMETRIC_CANCEL = 63

        /**
         * Refers Constant for biometric failed
         */
        const val REQUEST_CODE_BIOMETRIC_AUTH_FAILED = 64

        /**
         * Charset to be used to store details
         */
        private const val CHARSET = "UTF-8"
    }

}

/**
 * This data class represent the Result of Biometric authentication.
 * @property resultCode Result code
 * @property message String message
 * @property authenticationResult [BiometricPrompt.AuthenticationResult] result of biometric authentication.
 */
data class BiometricResultData(
    val resultCode: Int,
    val message: String,
    val authenticationResult: BiometricPrompt.AuthenticationResult?
)