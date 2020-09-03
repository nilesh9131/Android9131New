package com.example.nileshpc.biometric;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Shared preference which would be responsible for storing the credentials using encryption
 */
@SuppressWarnings("SpellCheckingInspection")
public class BiometricSharedPreference {

    /**
     * Shared Pref location for storing userinput post encryption
     */
    private static final String BIOMETRIC_SHARED_PREF = "BIOMETRIC_SHARED_PREF";

    /**
     * Shared pref tag for storing userinput
     */
    private static final String USERINPUT = "USERINPUT";

    /**
     * Shared pref tag for storing encryption IV
     */
    private static final String ENCRYPTION_IV = "ENCRYPTION_IV";

    /**
     * Shared pref tag for storing whether finger print configured or not
     */
    private static final String BIOMETRIC_CONFIGURED = "BIOMETRIC_CONFIGURED";

    /**
     * Shared preference which will securely store userinput
     */
    private final SharedPreferences sharedPreferences;

    /**
     * Overridden constructor accepting context for initializing shared preference
     *
     * @param context - app/base context
     */
    public BiometricSharedPreference(@NonNull Context context) {
        this.sharedPreferences = context.getSharedPreferences(BIOMETRIC_SHARED_PREF, Context.MODE_PRIVATE);
    }

    /**
     * Saving encrypted userinput in Pref
     *
     * @param userInput    - String representing userinput
     * @param encryptionIV - String encryptionIV used for encrypting data
     */
    public void saveUserPassAndEncryptionIV(@NonNull String userInput,
                                            @NonNull String encryptionIV) {

        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.putString(USERINPUT, userInput);
        editor.putString(ENCRYPTION_IV, encryptionIV);
        editor.apply();
    }

    /**
     * Clears everything which has been stored
     */
    public void clearFingerPrintPreferencesData() {
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    /**
     * Set whether or not finger print is configured
     *
     * @param isFingerPrintConfigured whether finger print was configured or not
     */
    public void setFingerprintConfigured(boolean isFingerPrintConfigured) {
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.putBoolean(BIOMETRIC_CONFIGURED, isFingerPrintConfigured);
        editor.apply();
    }

    /**
     * Get Encrypted UserInput from pref
     *
     * @return encrypted string representing userInput saved in preference, if nothing is stored it would

     */

    @Nullable
    public String getUserInput() {
        return this.sharedPreferences.getString(USERINPUT, null);
    }

    /**
     * Get Encrypted IV from pref
     *
     * @return encryption IV used during encryption, if nothing is stored it would
     * return null
     */
    public
    @Nullable
    String getEncryptionIV() {
        return this.sharedPreferences.getString(ENCRYPTION_IV, null);
    }

    /**
     * Whether user has configured for finger print
     *
     * @return a boolean indicating if the finger print has been configured or not
     */
    public boolean isFingerPrintConfigured() {
        return this.sharedPreferences.getBoolean(BIOMETRIC_CONFIGURED, false);
    }


}
