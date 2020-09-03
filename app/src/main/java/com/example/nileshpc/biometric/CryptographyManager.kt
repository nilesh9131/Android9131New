@file:Suppress("SpellCheckingInspection")

package com.example.nileshpc.biometric

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import androidx.annotation.RequiresApi
import java.io.IOException
import java.security.*
import java.security.cert.CertificateException
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

/**
 * [CryptographyManager] class will generate key for encryption and decryption of data.
 *
 */
@RequiresApi(Build.VERSION_CODES.M)
class CryptographyManager private constructor() {

    /**
     * Keystore which was created
     */
    private var keyStore: KeyStore? = null

    /**
     * KeyGenerator to generate keys
     */
    private var keyGenerator: KeyGenerator? = null

    /**
     * Cipher for User object
     */
    private var userCipher: Cipher? = null

    private val tag = "CryptographyManager"

    init {
        //initialization block
        keyStore = try {
            KeyStore.getInstance("AndroidKeyStore")
        } catch (e: KeyStoreException) {
            throw RuntimeException("Failed to get an instance of KeyStore", e)
        }

        keyGenerator = try {
            KeyGenerator
                .getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException("Failed to get an instance of KeyGenerator", e)
        } catch (e: NoSuchProviderException) {
            throw RuntimeException("Failed to get an instance of KeyGenerator", e)
        }


        userCipher = try {
            Cipher.getInstance(
                KeyProperties.KEY_ALGORITHM_AES + "/"
                        + KeyProperties.BLOCK_MODE_CBC + "/"
                        + KeyProperties.ENCRYPTION_PADDING_PKCS7
            )
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException("Failed to get an instance of Cipher", e)
        } catch (e: NoSuchPaddingException) {
            throw RuntimeException("Failed to get an instance of Cipher", e)
        }
    }

    /**
     * Creates a symmetric key in the Android Key Store which can only be used after the user has
     * authenticated with fingerprint.
     */
    @RequiresApi(Build.VERSION_CODES.M)
    fun createKey() {
        try {
            keyStore!!.load(null)
            val builder = KeyGenParameterSpec.Builder(
                USER_KEY_NAME,
                KeyProperties.PURPOSE_ENCRYPT or
                        KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC) // Require the user to authenticate with a fingerprint to authorize every use
                // of the key
                .setUserAuthenticationRequired(true)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)

            // This is a workaround to avoid crashes on devices whose API level is < 24
            // because KeyGenParameterSpec.Builder#setInvalidatedByBiometricEnrollment is only
            // visible on API level +24.
            // Ideally there should be a compat library for KeyGenParameterSpec.Builder but
            // which isn't available yet.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                builder.setInvalidatedByBiometricEnrollment(true)
            }
            keyGenerator!!.init(builder.build())
            keyGenerator!!.generateKey()
        } catch (e: NoSuchAlgorithmException) {
            throw java.lang.RuntimeException(e)
        } catch (e: InvalidAlgorithmParameterException) {
            throw java.lang.RuntimeException(e)
        } catch (e: CertificateException) {
            throw java.lang.RuntimeException(e)
        } catch (e: IOException) {
            throw java.lang.RuntimeException(e)
        }
    }


    /**
     * Initialize a encrypted cipher created using the `USER_KEY_NAME`
     *
     * @return Encrypted `userCipher` will return Null in case of any exception
     */
    fun generateEncryptionCipher(): Cipher? {
        try {
            keyStore?.load(null)
            val key = keyStore?.getKey(USER_KEY_NAME, null) as SecretKey
            userCipher?.init(Cipher.ENCRYPT_MODE, key)
        } catch (e: KeyStoreException) {
            Log.e(tag, "Exception while generating Encryption Cipher")
            return null
        } catch (e1: CertificateException) {
            Log.e(tag, "Exception while generating Encryption Cipher")
            return null
        } catch (e: UnrecoverableKeyException) {
            Log.e(tag, "Exception while generating Encryption Cipher")
            return null
        } catch (e: IOException) {
            Log.e(tag, "Exception while generating Encryption Cipher")
            return null
        } catch (e: NoSuchAlgorithmException) {
            Log.e(tag, "Exception while generating Encryption Cipher")
            return null
        } catch (e: InvalidKeyException) {
            Log.e(tag, "Exception while generating Encryption Cipher")
            return null
        }
        return userCipher
    }

    /**
     * Initialize a decrypted cipher created using the `USER_KEY_NAME`
     *
     * @return Decrypted `userCipher` will return Null in case of any exception
     */
    fun generateDecryptionEncryptionIVCipher(encryptionIV: ByteArray?): Cipher? {
        try {
            keyStore!!.load(null)
            val key = keyStore!!.getKey(USER_KEY_NAME, null) as SecretKey
            userCipher!!.init(Cipher.DECRYPT_MODE, key, IvParameterSpec(encryptionIV))
        } catch (e: KeyStoreException) {
            Log.e(tag, "Exception while generating Encryption Cipher")
            return null
        } catch (e: CertificateException) {
            Log.e(tag, "Exception while generating Encryption Cipher")
            return null
        } catch (e: UnrecoverableKeyException) {
            Log.e(tag, "Exception while generating Encryption Cipher")
            return null
        } catch (e: IOException) {
            Log.e(tag, "Exception while generating Encryption Cipher")
            return null
        } catch (e: NoSuchAlgorithmException) {
            Log.e(tag, "Exception while generating Encryption Cipher")
            return null
        } catch (e: InvalidKeyException) {
            Log.e(tag, "Exception while generating Encryption Cipher")
            return null
        } catch (e: InvalidAlgorithmParameterException) {
            Log.e(tag, "Exception while generating Encryption Cipher")
            return null
        }
        return userCipher
    }

    companion object {
        /**
         * Holder object
         */
        private object HOLDER {
            val INSTANCE = CryptographyManager()
        }

        /**
         * Instance of CryptographyManager
         */
        @JvmStatic
        val instance: CryptographyManager by lazy { HOLDER.INSTANCE }

        /**
         * User key which is created in the keystore
         */
        private const val USER_KEY_NAME = "user_key"
    }
}