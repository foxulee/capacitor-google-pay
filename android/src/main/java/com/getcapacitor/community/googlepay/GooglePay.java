package com.getcapacitor.community.googlepay;

import static com.google.android.gms.tapandpay.TapAndPayStatusCodes.TAP_AND_PAY_NO_ACTIVE_WALLET;
import static com.google.android.gms.tapandpay.TapAndPayStatusCodes.TAP_AND_PAY_TOKEN_NOT_FOUND;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.nfc.cardemulation.CardEmulation;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.getcapacitor.Bridge;
import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.PluginCall;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tapandpay.TapAndPay;
import com.google.android.gms.tapandpay.TapAndPayClient;
import com.google.android.gms.tapandpay.issuer.IsTokenizedRequest;
import com.google.android.gms.tapandpay.issuer.PushTokenizeRequest;
import com.google.android.gms.tapandpay.issuer.TokenInfo;
import com.google.android.gms.tapandpay.issuer.UserAddress;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.nio.charset.StandardCharsets;

import java.util.List;
import java.util.Objects;

import android.text.TextUtils;
import android.util.Base64;

public class GooglePay {
    /**
     * Interface for callbacks when network status changes.
     */
    interface DateChangeListener {
        void onDateChanged(String event, JSObject result, Boolean state);
    }

//     add GoogleUtilityListener
    public interface GoogleUtilityListener {
        void onSuccessForGoogle(int requestCode, @NonNull Bundle data);
        void onFailureForGoogle(int errorCode, @NonNull String message);
    }

    @Nullable
    private GoogleUtilityListener googleUtilityListener;

    public void setGoogleUtilityListener(@Nullable GoogleUtilityListener listener) {
        this.googleUtilityListener = listener;
    }




    private final TapAndPayClient tapAndPay;
    private static final String TAG = "GooglePayPlugin";
    private final Bridge bridge;
    public String callBackId;
    public String dataChangeCallBackId;
    // protected static final int REQUEST_CODE_PUSH_TOKENIZE = 3;
    protected static final int REQUEST_CODE_CREATE_WALLET = 4;
    protected static final int REQUEST_CODE_ACTION_TOKEN = 5;
    protected static final int RESULT_CANCELED = 0;
    protected static final int RESULT_OK = -1;
    protected static final int RESULT_INVALID_TOKEN = 15003;


    public static final int REQUEST_CREATE_WALLET = 100;
    public static final int REQUEST_CODE_PUSH_TOKENIZE = 200;
    public static final int REQUEST_WALLET_INFORMATION = 300;
    public static final int EXCEPTION_TOKEN_PENDING_STATE = 400;

    public static final int ERROR_CODE_JSON_FORMATTING_EXCEPTION = 01;
    public static final int ERROR_CODE_WALLET_NOT_INTIALIZED = 02;
    public static final int ERROR_CODE_CARD_EXISTS_IN_WALLET = 03;

    public static final String PASSTHRUFROMAPP_WALLET_INFORMATION = "passthrufromapp_wallet_information";
    public static final String PENDING_VERIFICATION_TOKEN = "pending_verification_token";

    private static final String ERROR_MESSAGE_CARD_EXISTS = "Card already exist in wallet";
    private static final String ERROR_MESSAGE_WALLET_NOT_INTIALIZED = "Wallet not intialized";
    private static final String ERROR_MESSAGE_JSON_FORMATTING_EXCEPTION = "JSON Formatting exception, Incorrect data";
    private static final String ERROR_MESSAGE_INVALID_PAYMENT_NETWORK = "Invalid payment network";

    //Internal error
    private static final String ERROR_MESSAGE_ENCODING_STRING_EMPTY = "String empty for encoding";
    private static final String ERROR_MESSAGE_DECODING_STRING_EMPTY = "String empty for decoding";
    private static final String GOOGLE_PAY = "Google_Pay";
    private static final String VISA = "VISA";
    private static final String AMEX = "AMEX";
    private static final String DISCOVER = "DISCOVER";
    private static final String MASTERCARD = "MASTERCARD";

    public enum ErrorCodeReference {
        PUSH_PROVISION_ERROR(-1),
        PUSH_PROVISION_CANCEL(-2),
        MISSING_DATA_ERROR(-3),
        CREATE_WALLET_CANCEL(-4),
        IS_TOKENIZED_ERROR(-5),
        ACTION_TOKEN_ERROR(-6),
        INVALID_TOKEN(-7),
        SET_DEFAULT_PAYMENTS_ERROR(-9);

        private final Integer code;

        ErrorCodeReference(Integer code) {
            this.code = code;
        }

        public String getError() {
            return code.toString();
        }
    }

    public enum TokenStatusReference {
        TOKEN_STATE_UNTOKENIZED(1),
        TOKEN_STATE_PENDING(2),
        TOKEN_STATE_NEEDS_IDENTITY_VERIFICATION(3),
        TOKEN_STATE_SUSPENDED(4),
        TOKEN_STATE_ACTIVE(5),
        TOKEN_STATE_FELICA_PENDING_PROVISIONING(6),
        TOKEN_STATE_NOT_FOUND(-1);

        public final int referenceId;

        public static TokenStatusReference getName(int referenceId) {
            for (TokenStatusReference reference : values()) {
                if (reference.referenceId == referenceId) {
                    return reference;
                }
            }
            return null;
        }

        TokenStatusReference(int referenceId) {
            this.referenceId = referenceId;
        }
    }

    @Nullable
    private DateChangeListener dataChangeListener;

    public void setDataChangeListener(@Nullable DateChangeListener listener) {
        this.dataChangeListener = listener;
    }

    public GooglePay(@NonNull Bridge bridge) {
        this.tapAndPay = TapAndPay.getClient(bridge.getActivity());
        this.bridge = bridge;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult --- " + resultCode + " --- " + requestCode);
        Log.i(TAG, "onActivityResultData --- " + data);
        Log.i(TAG, "CallBackID --- " + callBackId);

        // Get the previously saved call
        PluginCall call = this.bridge.getSavedCall(callBackId);

        if (call == null) {
            return;
        }

        JSObject result = new JSObject();

        if (requestCode == REQUEST_CODE_CREATE_WALLET) {
            if (resultCode == RESULT_CANCELED) {
                // The user canceled the request.
                call.reject("Google wallet create cancelled", ErrorCodeReference.CREATE_WALLET_CANCEL.getError());
            } else if (resultCode == RESULT_OK) {
                Log.i(TAG, "Google wallet created --- ");
                result.put("isCreated", true);
                call.resolve(result);
            }
        } else if (requestCode == REQUEST_CODE_PUSH_TOKENIZE) {
            if (resultCode == RESULT_CANCELED) {
                call.reject("PUSH_PROVISION_CANCEL", ErrorCodeReference.PUSH_PROVISION_CANCEL.getError());
            } else if (resultCode == RESULT_OK) {
                // The action succeeded.
                String tokenId = data.getStringExtra(TapAndPay.EXTRA_ISSUER_TOKEN_ID);
                result.put("tokenId", tokenId);
                call.resolve(result);
            }
        } else if (requestCode == REQUEST_CODE_ACTION_TOKEN) {
            Log.i(TAG, "ACTION_TOKEN --- ");

            if (resultCode == RESULT_CANCELED) {
                // The user canceled the request.
                Log.i(TAG, "ACTION_TOKEN CANCEL --- ");
                result.put("isSuccess", false);
                call.resolve(result);
            } else if (resultCode == RESULT_OK) {
                Log.i(TAG, "ACTION_TOKEN SUCCESS --- ");
                result.put("isSuccess", true);
                call.resolve(result);
            } else if (resultCode == RESULT_INVALID_TOKEN) {
                Log.i(TAG, "ACTION_TOKEN WRONG TOKEN --- ");
                call.reject("Invalid TokenReferenceID", ErrorCodeReference.INVALID_TOKEN.getError());
            } else {
                Log.i(TAG, "ACTION_TOKEN ERROR --- ");
                Log.i(TAG, call.toString());
                call.reject("ACTION_TOKEN ERROR", ErrorCodeReference.ACTION_TOKEN_ERROR.getError());
            }
        } else {
            call.resolve();
        }
        this.bridge.releaseCall(callBackId);
        call.setKeepAlive(false);
    }

    public void getEnvironment(PluginCall call) {
        try {
            this.tapAndPay
                    .getEnvironment()
                    .addOnCompleteListener(
                            task -> {
                                Log.i(TAG, "onComplete (getEnvironment) - " + task.isSuccessful());
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "getEnvironment: " + task.getResult());
                                    JSObject result = new JSObject();
                                    result.put("value", task.getResult());
                                    call.resolve(result);
                                } else {
                                    call.reject("Environment not found", "ENV_ERROR");
                                }
                            }
                    );
        } catch (Exception e) {
            call.reject(e.getMessage());
        }
    }

    public void getStableHardwareId(PluginCall call) {
        try {
            this.tapAndPay.getStableHardwareId()
                    .addOnCompleteListener(
                            task -> {
                                Log.i(TAG, "onComplete (getStableHardwareId) - " + task.isSuccessful());
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "getStableHardwareId: " + task.getResult());
                                    JSObject result = new JSObject();
                                    result.put("hardwareId", task.getResult());
                                    call.resolve(result);
                                } else {
                                    Exception exception = task.getException();

                                    if (exception instanceof ApiException apiException) {
                                        call.reject(apiException.getMessage());
                                    } else {
                                        call.reject("Hardware ID not found", "NO_HARDWARE_ID");
                                    }
                                }
                            }
                    );
        } catch (Exception e) {
            call.reject(e.getMessage());
        }
    }

    public void getActiveWalletID(PluginCall call) {
        try {
            this.tapAndPay.getActiveWalletId()
                    .addOnCompleteListener(
                            task -> {
                                Log.i(TAG, "onComplete (getActiveWalletID) - " + task.isSuccessful());
                                if (task.isSuccessful()) {
                                    // Next: look up token ids for the active wallet
                                    // This typically involves network calls to a server with knowledge
                                    // of wallets and tokens.
                                    Log.d(TAG, "getActiveWalletID: " + task.getResult());
                                    JSObject result = new JSObject();
                                    result.put("walletId", task.getResult());
                                    call.resolve(result);
                                } else {
                                    Exception exception = task.getException();

                                    if (exception instanceof ApiException apiException) {
                                        if (apiException.getStatusCode() == TAP_AND_PAY_NO_ACTIVE_WALLET) {
                                            call.reject("Active wallet not found", "NO_ACTIVE_WALLET");
                                        } else {
                                            call.reject(apiException.getMessage());
                                        }
                                    } else {
                                        call.reject("Active wallet not found", "NO_ACTIVE_WALLET");
                                    }
                                }
                            }
                    );
        } catch (Exception e) {
            call.reject(e.getMessage());
        }
    }

    public void createWallet(PluginCall call) {
        try {
            this.bridge.saveCall(call);
            this.callBackId = call.getCallbackId();
            call.setKeepAlive(true);
            tapAndPay.createWallet(bridge.getActivity(), REQUEST_CODE_CREATE_WALLET);
        } catch (Exception e) {
            call.reject(e.getMessage());
        }
    }

    public void getTokenStatus(PluginCall call) {

        final String tokenReferenceId = call.getString("tokenReferenceId");
        if (tokenReferenceId == null) {
            call.reject("No tokenReferenceId found");
            return;
        }

        final String tsp = call.getString("tsp");
        if (tsp == null) {
            call.reject("No tsp found", ErrorCodeReference.MISSING_DATA_ERROR.getError());
            return;
        }

        try {
            this.tapAndPay.getTokenStatus(getTSP(tsp), tokenReferenceId)
                    .addOnCompleteListener(
                            task -> {
                                Log.i(TAG, "onComplete (getTokenStatus) - " + task.isSuccessful());
                                if (task.isSuccessful()) {
                                    @TapAndPay.TokenState
                                    int tokenStateInt = task.getResult().getTokenState();
//                                    boolean isSelected = task.getResult().isSelected();
                                    // Next: update payment card UI to reflect token state and selection
                                    JSObject result = new JSObject();
                                    result.put("state", tokenStateInt);
                                    result.put("code", GooglePay.TokenStatusReference.getName(tokenStateInt));
                                    call.resolve(result);
                                } else {
                                    Exception exception = task.getException();

                                    if (exception instanceof ApiException apiException) {
                                        if (apiException.getStatusCode() == TAP_AND_PAY_TOKEN_NOT_FOUND) {
                                            // Could not get token status
                                            call.reject(apiException.getMessage(), "TAP_AND_PAY_TOKEN_NOT_FOUND");
                                        } else {
                                            call.reject(apiException.getMessage());
                                        }
                                    } else {
                                        call.reject("TOKEN_NOT_FOUND", "TAP_AND_PAY_TOKEN_NOT_FOUND");
                                    }

                                }
                            }
                    );
        } catch (Exception e) {
            call.reject(e.getMessage());
        }
    }

    public void listTokens(PluginCall call) {
        try {
            this.tapAndPay.listTokens()
                    .addOnCompleteListener(
                            task -> {
                                if (task.isSuccessful()) {
                                    JSObject result = new JSObject();
                                    JSArray tokens = new JSArray();
                                    Log.i(TAG, "listTokens: " + task.getResult());
                                    for (TokenInfo token : task.getResult()) {
                                        Log.d(TAG, "Found token with ID: " + token.getIssuerTokenId());
                                        tokens.put(token.getIssuerTokenId());
                                    }
                                    result.put("tokens", tokens);
                                    call.resolve(result);
                                } else {
                                    Exception exception = task.getException();
                                    Log.i(TAG, "listTokens" + exception);
                                    if (exception instanceof ApiException apiException) {
                                        call.reject(apiException.getMessage());
                                    } else {
                                        call.reject("LIST_TOKEN_ERROR", "LIST_TOKEN_ERROR");
                                    }
                                }
                            }
                    );
        } catch (Exception e) {
            call.reject(e.getMessage());
        }
    }

    public void isTokenized(PluginCall call) {
        String tsp = call.getString("tsp");
        String lastDigits = call.getString("lastDigits");
        if (tsp == null) {
            call.reject("No tsp found", ErrorCodeReference.MISSING_DATA_ERROR.getError());
            return;
        }
        if (lastDigits == null) {
            call.reject("No lastDigits found", ErrorCodeReference.MISSING_DATA_ERROR.getError());
            return;
        }
        try {
            IsTokenizedRequest request = new IsTokenizedRequest.Builder()
                    .setIdentifier(lastDigits)
                    .setNetwork(getCardNetwork(tsp))
                    .setTokenServiceProvider(getTSP(tsp))
                    .build();

            this.tapAndPay.isTokenized(request)
                    .addOnCompleteListener(
                            task -> {
                                if (task.isSuccessful()) {
                                    Boolean isTokenized = task.getResult();
                                    JSObject result = new JSObject();
                                    result.put("isTokenized", isTokenized.booleanValue());
                                    call.resolve(result);
                                } else {

                                    Exception exception = task.getException();
                                    if (exception instanceof ApiException apiException) {
                                        call.reject(apiException.getMessage(), ErrorCodeReference.IS_TOKENIZED_ERROR.getError());
                                    } else {
                                        if (exception != null) {
                                            call.reject(exception.getMessage(), ErrorCodeReference.IS_TOKENIZED_ERROR.getError());
                                        } else {
                                            call.reject("IS_TOKENIZED_ERROR", ErrorCodeReference.IS_TOKENIZED_ERROR.getError());
                                        }
                                    }

                                    Log.i(TAG, "isTokenized" + exception);
                                }
                            }
                    );
        } catch (Exception e) {
            call.reject(e.getMessage(), ErrorCodeReference.IS_TOKENIZED_ERROR.getError());
        }
    }

    public void pushProvision(PluginCall call) {
        Log.i(TAG, "PUSHPROVISION --- 1");
        String opcData = call.getString("opc");
        if (opcData == null) {
            call.reject("No OPC found");
            return;
        }
        byte[] opc = opcData.getBytes();
        String tsp = call.getString("tsp");
        String clientName = call.getString("clientName");
        String lastDigits = call.getString("lastDigits");
        JSONObject address = call.getObject("address");
        if (tsp == null) {
            call.reject("No tsp found", ErrorCodeReference.MISSING_DATA_ERROR.getError());
            return;
        }
        if (lastDigits == null) {
            call.reject("No lastDigits found", ErrorCodeReference.MISSING_DATA_ERROR.getError());
            return;
        }
        if (clientName == null) {
            call.reject("No clientName found", ErrorCodeReference.MISSING_DATA_ERROR.getError());
            return;
        }
        if (Objects.isNull(address)) {
            call.reject("No address found", ErrorCodeReference.MISSING_DATA_ERROR.getError());
            return;
        }
        try {
            UserAddress userAddress = UserAddress
                    .newBuilder()
                    .setName(Objects.requireNonNullElse(address.getString("name"), ""))
                    .setAddress1(Objects.requireNonNullElse(address.getString("address1"), ""))
                    .setAddress2(Objects.requireNonNullElse(address.getString("address2"), ""))
                    .setLocality(Objects.requireNonNullElse(address.getString("locality"), ""))
                    .setAdministrativeArea(Objects.requireNonNullElse(address.getString("administrativeArea"), ""))
                    .setCountryCode(Objects.requireNonNullElse(address.getString("countryCode"), ""))
                    .setPostalCode(Objects.requireNonNullElse(address.getString("postalCode"), ""))
                    .setPhoneNumber(Objects.requireNonNullElse(address.getString("phoneNumber"), ""))
                    .build();

            PushTokenizeRequest pushTokenizeRequest = new PushTokenizeRequest.Builder()
                    .setOpaquePaymentCard(opc)
                    .setNetwork(getCardNetwork(tsp))
                    .setTokenServiceProvider(getTSP(tsp))
                    .setDisplayName(clientName)
                    .setLastDigits(lastDigits)
                    .setUserAddress(userAddress)
                    .build();
            Log.i(TAG, "PUSHPROVISION --- 2");
            this.bridge.saveCall(call);
            this.callBackId = call.getCallbackId();
            call.setKeepAlive(true);
            // Start the Activity for result using the name of the callback method
            Log.i(TAG, "PUSHPROVISION --- 3");

            // a request code value you define as in Android's startActivityForResult
            tapAndPay.pushTokenize(bridge.getActivity(), pushTokenizeRequest, REQUEST_CODE_PUSH_TOKENIZE);
        } catch (Exception e) {
            call.reject(e.getMessage(), ErrorCodeReference.PUSH_PROVISION_ERROR.getError());
        }
    }

    public void requestSelectToken(PluginCall call) {
        Log.i(TAG, "selectToken --- 1");
        String tokenReferenceId = call.getString("tokenReferenceId");
        if (tokenReferenceId == null) {
            call.reject("No tokenReferenceId found");
            return;
        }
        String tsp = call.getString("tsp");
        if (tsp == null) {
            call.reject("No tsp found");
            return;
        }
        try {
            this.bridge.saveCall(call);
            this.callBackId = call.getCallbackId();
            call.setKeepAlive(true);
            Log.i(TAG, "selectToken --- 2");
            this.tapAndPay.requestSelectToken(bridge.getActivity(), tokenReferenceId, getTSP(tsp), REQUEST_CODE_ACTION_TOKEN);
        } catch (Exception e) {
            call.reject(e.getMessage());
        }
    }

    public void requestDeleteToken(PluginCall call) {
        Log.i(TAG, "removeToken --- 1");
        String tokenReferenceId = call.getString("tokenReferenceId");
        if (tokenReferenceId == null) {
            call.reject("No tokenReferenceId found");
            return;
        }
        String tsp = call.getString("tsp");
        if (tsp == null) {
            call.reject("No tsp found");
            return;
        }
        try {
            this.bridge.saveCall(call);
            this.callBackId = call.getCallbackId();
            call.setKeepAlive(true);
            Log.i(TAG, "removeToken --- 2");
            this.tapAndPay.requestDeleteToken(bridge.getActivity(), tokenReferenceId, getTSP(tsp), REQUEST_CODE_ACTION_TOKEN);
        } catch (Exception e) {
            call.reject(e.getMessage());
        }
    }

    public void isGPayDefaultNFCApp(PluginCall call) {
        try {
            NfcManager nfcManager = (NfcManager) this.bridge.getContext().getSystemService(Context.NFC_SERVICE);
            NfcAdapter adapter = nfcManager.getDefaultAdapter();
            if (adapter != null) {
                JSObject result = new JSObject();
                if (adapter.isEnabled()) {
                    CardEmulation emulation = CardEmulation.getInstance(adapter);
                    boolean isDefault = emulation.isDefaultServiceForCategory(
                            new ComponentName(GoogleApiAvailability.GOOGLE_PLAY_SERVICES_PACKAGE,
                                    "com.google.android.gms.tapandpay.hce.service.TpHceService"),
                            CardEmulation.CATEGORY_PAYMENT);
                    result.put("isDefault", isDefault);
                    result.put("isNFCOn", true);
                    call.resolve(result);
                } else {
                    result.put("isDefault", false);
                    result.put("isNFCOn", false);
                    call.resolve(result);
                }
            } else {
                call.reject("NFC is not supported", "NFC_SERVICE_NOT_SUPPORTED");
            }
        } catch (Exception e) {
            call.reject(e.getMessage());
        }
    }

    public void setGPayAsDefaultNFCApp(PluginCall call) {
        try {
            Intent intent = new Intent(CardEmulation.ACTION_CHANGE_DEFAULT);
            intent.putExtra(CardEmulation.EXTRA_CATEGORY, CardEmulation.CATEGORY_PAYMENT);
            intent.putExtra(
                    CardEmulation.EXTRA_SERVICE_COMPONENT,
                    new ComponentName(
                            "com.google.android.gms",
                            "com.google.android.gms.tapandpay.hce.service.TpHceService"));

            this.bridge.saveCall(call);
            this.callBackId = call.getCallbackId();
            call.setKeepAlive(true);
            ActivityResultLauncher<Intent> launcher = this.bridge.registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        // Handle the result here
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // Success, do something
                            JSObject ret = new JSObject();
                            ret.put("isDefault", true);
                            call.resolve(ret);
                        } else {
                            // Failure, handle error
                            call.reject("Default payment set cancelled", ErrorCodeReference.SET_DEFAULT_PAYMENTS_ERROR.getError());
                        }
                    });

            launcher.launch(intent);
        } catch (Exception e) {
            call.reject(e.getMessage());
        }
    }


    public void registerDataChangedListener(PluginCall call) {
        try {
            this.tapAndPay.registerDataChangedListener(
                    () -> {
                        Log.i(TAG, call.toString());
                        JSObject result = new JSObject();
                        result.put("value", "OK");
                        assert this.dataChangeListener != null;
                        this.dataChangeCallBackId = call.getCallbackId();
                        call.setKeepAlive(true);
                        this.dataChangeListener.onDateChanged("registerDataChangedListener", result, true);
                    }
            );
        } catch (Exception e) {
            call.reject(e.getMessage());
        }
    }

    public void getWalletInformation(PluginCall call) {
        String paymentNetwork = call.getString("paymentNetwork");
        String cardHolderName = call.getString("cardHolderName");
        String cardNickName = call.getString("cardNickName");
        String last4CardNumber = call.getString("last4CardNumber");
        try{
            this.tapAndPay
                .listTokens()
                .addOnCompleteListener(
                    new OnCompleteListener<List<TokenInfo>>() {
                        @Override
                        public void onComplete(@NonNull Task<List<TokenInfo>> task) {
                            if (task.isSuccessful()) {
                                for (TokenInfo token : task.getResult()) {
                                    if (TextUtils.equals(token.getFpanLastFour(), last4CardNumber)) {
                                        if (token.getTokenState() == TapAndPay.TOKEN_STATE_ACTIVE || token.getTokenState() == TapAndPay.TOKEN_STATE_PENDING || token.getTokenState() == TapAndPay.TOKEN_STATE_SUSPENDED) {
                                            sendErrorStatus(ERROR_CODE_CARD_EXISTS_IN_WALLET, ERROR_MESSAGE_CARD_EXISTS);
                                        } else if (token.getTokenState() == TapAndPay.TOKEN_STATE_NEEDS_IDENTITY_VERIFICATION) {
                                            Bundle responseBundle = new Bundle();
                                            responseBundle.putString(PENDING_VERIFICATION_TOKEN, token.getIssuerTokenId());
                                            if (googleUtilityListener != null)
                                                googleUtilityListener.onSuccessForGoogle(EXCEPTION_TOKEN_PENDING_STATE, responseBundle);

                                        }
                                        return;
                                    }
                                }
                                getInformation(paymentNetwork, cardHolderName, cardNickName);
                            } else {
                                getInformation(paymentNetwork, cardHolderName, cardNickName);
                            }
                        }
                    }
                );
        }
        catch (Exception e) {
            call.reject(e.getMessage());
        }

    }

    public void pushToWallet(PluginCall call) throws Exception {
        String response = call.getString("response");
        WalletResponse walletResponse = new Gson().fromJson(decodeBase64Encoding(response), WalletResponse.class);
        final OPCResponse opcResponse = new Gson().fromJson(decodeBase64Encoding(walletResponse.getForWalletSdk()), OPCResponse.class);
        int tokenProvider = 0, cardNetwork = 0;
        switch (opcResponse.getCardNetwork()) {
            case VISA:
                tokenProvider = TapAndPay.TOKEN_PROVIDER_VISA;
                cardNetwork = TapAndPay.CARD_NETWORK_VISA;
                break;
            case AMEX:
                tokenProvider = TapAndPay.TOKEN_PROVIDER_AMEX;
                cardNetwork = TapAndPay.CARD_NETWORK_AMEX;
                break;
            case DISCOVER:
                tokenProvider = TapAndPay.TOKEN_PROVIDER_DISCOVER;
                cardNetwork = TapAndPay.CARD_NETWORK_DISCOVER;
                break;
            case MASTERCARD:
                tokenProvider = TapAndPay.TOKEN_PROVIDER_MASTERCARD;
                cardNetwork = TapAndPay.CARD_NETWORK_MASTERCARD;
                break;
        }
        UserAddress userAddress = UserAddress.newBuilder()
                .setAddress1(opcResponse.getUserAddress().getLine1())
                .setAddress2(opcResponse.getUserAddress().getLine2())
                .setCountryCode(opcResponse.getUserAddress().getCountry())
                .setLocality(opcResponse.getUserAddress().getCity())
                .setAdministrativeArea(opcResponse.getUserAddress().getState())
                .setName(opcResponse.getCardholderName())
                .setPhoneNumber(opcResponse.getPhoneNumber())
                .setPostalCode(opcResponse.getUserAddress().getPostalCode())
                .build();
        PushTokenizeRequest pushTokenizeRequest = new PushTokenizeRequest.Builder()
                .setOpaquePaymentCard(opcResponse.getOpc().getBytes())
                .setNetwork(cardNetwork)
                .setUserAddress(userAddress)
                .setTokenServiceProvider(tokenProvider)
                .setDisplayName(opcResponse.getDisplayName())
                .setLastDigits(opcResponse.getLastDigits())
                .build();
        this.tapAndPay.pushTokenize(
                bridge.getActivity(),
                pushTokenizeRequest,
                REQUEST_CODE_PUSH_TOKENIZE);
    }

    private void getInformation(final String paymentNetwork, final String cardHolderName, final String cardNickName) {
        this.tapAndPay
            .getActiveWalletId()
            .addOnCompleteListener(
                    new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            if (task.isSuccessful()) {
                                final String walletId = task.getResult();
                                GooglePay.this.tapAndPay
                                        .getStableHardwareId()
                                        .addOnCompleteListener(
                                                new OnCompleteListener<String>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<String> task) {
                                                        if (task.isSuccessful()) {
                                                            try {
                                                                String base64JsonString = generatePassThruFromApp(walletId, task.getResult(), paymentNetwork, cardHolderName, cardNickName);
                                                                Bundle responseBundle = new Bundle();
                                                                responseBundle.putString(PASSTHRUFROMAPP_WALLET_INFORMATION, base64JsonString);
                                                                if (googleUtilityListener != null)
                                                                    googleUtilityListener.onSuccessForGoogle(REQUEST_WALLET_INFORMATION, responseBundle);
                                                            } catch (Exception e) {
                                                                sendErrorStatus(ERROR_CODE_JSON_FORMATTING_EXCEPTION, ERROR_MESSAGE_JSON_FORMATTING_EXCEPTION);
                                                            }
                                                        } else {
                                                            //TODO:Send a proper error Message
                                                        }
                                                    }
                                                });
                            } else {
                                sendErrorStatus(ERROR_CODE_WALLET_NOT_INTIALIZED, ERROR_MESSAGE_WALLET_NOT_INTIALIZED);
                            }
                        }
                    });
    }

     private void sendErrorStatus(int errorCode, @NonNull String message) {
        if (googleUtilityListener != null) {
            googleUtilityListener.onFailureForGoogle(errorCode, message);
        }
    }

    private String generatePassThruFromApp(String walledId, String hardwareId, String paymentNetwork, String cardHolderName, String cardNickName) throws Exception {
        Gson gson = new Gson();

        GoogleWallet googleWallet = new GoogleWallet(walledId, hardwareId);
        String googleWalletJsonString = gson.toJson(googleWallet);
        String base64GoogleWalletJsonString = generateBase64Encoding(googleWalletJsonString);

        passThruCardDataFromApp passThruCardDataFromApp = new passThruCardDataFromApp(cardNickName, paymentNetwork, cardHolderName);
        String passThruCardDataFromAppJsonString = gson.toJson(passThruCardDataFromApp);
        String base64PassthruCardDataFromAppJsonString = generateBase64Encoding(passThruCardDataFromAppJsonString);

        PassThruFromApp passThruFromApp = new PassThruFromApp(GOOGLE_PAY, base64GoogleWalletJsonString, base64PassthruCardDataFromAppJsonString);
        String passThruFromAppJsonString = gson.toJson(passThruFromApp);

        return generateBase64Encoding(passThruFromAppJsonString);
    }

    private String generateBase64Encoding(String jsonString) throws Exception {
        if (jsonString != null) {
            try {
                byte[] jsonData = jsonString.getBytes(StandardCharsets.UTF_8);
                return Base64.encodeToString(jsonData, Base64.NO_WRAP);
            } catch (Exception e) {
                throw e;
            }
        }
        throw new Exception(ERROR_MESSAGE_ENCODING_STRING_EMPTY);
    }

    private String decodeBase64Encoding(String encodedString) throws Exception {
        String text = encodedString;
        if (text != null) {
            try {
                byte[] data = Base64.decode(encodedString, Base64.DEFAULT);
                text = new String(data, StandardCharsets.UTF_8);
                return text;
            } catch (Exception e) {
                throw e;
            }
        }
        throw new Exception(ERROR_MESSAGE_DECODING_STRING_EMPTY);
    }

    public static String getPaymentNetwork(String paymentNetwork) throws Exception {
        switch (paymentNetwork) {
            case "VISA":
                return VISA;
            case "AMEX":
                return AMEX;
            case "DISCOVER":
                return DISCOVER;
            case "MASTERCARD":
                return MASTERCARD;
            case "MASTER":
                return MASTERCARD;
            default:
                throw new InvalidPaymentNetwork();
        }
    }

    private static class InvalidPaymentNetwork extends Exception {
            private InvalidPaymentNetwork() {
                super(ERROR_MESSAGE_INVALID_PAYMENT_NETWORK);
            }
        }

        private class PassThruFromApp {
            private String walletType;
            private String walletData;
            private String passthruCardDataFromApp;

            private PassThruFromApp(String walletType, String walletData, String passthruCardDataFromApp) {
                this.walletType = walletType;
                this.walletData = walletData;
                this.passthruCardDataFromApp = passthruCardDataFromApp;
            }

            public String getWalletType() {
                return walletType;
            }

            public void setWalletType(String walletType) {
                this.walletType = walletType;
            }

            public String getWalletData() {
                return walletData;
            }

            public void setWalletData(String walletData) {
                this.walletData = walletData;
            }

            public String getPassthruCardDataFromApp() {
                return passthruCardDataFromApp;
            }

            public void setPassthruCardDataFromApp(String passthruCardDataFromApp) {
                this.passthruCardDataFromApp = passthruCardDataFromApp;
            }
        }

        private class passThruCardDataFromApp {
            private String cardNickname;
            private String paymentNetwork;
            private String cardholderName;

            private passThruCardDataFromApp(String cardNickname, String paymentNetwork, String cardholderName) {
                this.cardNickname = cardNickname;
                this.paymentNetwork = paymentNetwork;
                this.cardholderName = cardholderName;
            }

            public String getCardNickname() {
                return cardNickname;
            }

            public void setCardNickname(String cardNickname) {
                this.cardNickname = cardNickname;
            }

            public String getPaymentNetwork() {
                return paymentNetwork;
            }

            public void setPaymentNetwork(String paymentNetwork) {
                this.paymentNetwork = paymentNetwork;
            }

            public String getCardholderName() {
                return cardholderName;
            }

            public void setCardholderName(String cardholderName) {
                this.cardholderName = cardholderName;
            }
        }

        private class GoogleWallet {
            private String walletId;
            private String stableHardwareId;

            private GoogleWallet(String walletId, String stableHardwareId) {
                this.walletId = walletId;
                this.stableHardwareId = stableHardwareId;
            }

            public String getWalletId() {
                return walletId;
            }

            public void setWalletId(String walletId) {
                this.walletId = walletId;
            }

            public String getStableHardwareId() {
                return stableHardwareId;
            }

            public void setStableHardwareId(String stableHardwareId) {
                this.stableHardwareId = stableHardwareId;
            }

        }

        private class Payload {

            @SerializedName("statusCode")
            @Expose
            private String statusCode;
            @SerializedName("passthruToIdiSdk")
            @Expose
            private String passthruToIdiSdk;

            private String getStatusCode() {
                return statusCode;
            }

            private void setStatusCode(String statusCode) {
                this.statusCode = statusCode;
            }

            private String getPassthruToIdiSdk() {
                return passthruToIdiSdk;
            }

            private void setPassthruToIdiSdk(String passthruToIdiSdk) {
                this.passthruToIdiSdk = passthruToIdiSdk;
            }

        }

        private class WalletResponse {
            @SerializedName("walletType")
            @Expose
            private String walletType;
            @SerializedName("forWalletSdk")
            @Expose
            private String forWalletSdk;

            private String getWalletType() {
                return walletType;
            }

            private void setWalletType(String walletType) {
                this.walletType = walletType;
            }

            private String getForWalletSdk() {
                return forWalletSdk;
            }

            private void setForWalletSdk(String forWalletSdk) {
                this.forWalletSdk = forWalletSdk;
            }

        }

        private class OPCResponse {

            @SerializedName("cardNetwork")
            @Expose
            private String cardNetwork;

            @SerializedName("tokenProvider")
            @Expose
            private String tokenProvider;

            @SerializedName("displayName")
            @Expose
            private String displayName;

            @SerializedName("lastDigits")
            @Expose
            private String lastDigits;

            @SerializedName("opc")
            @Expose
            private String opc;

            @SerializedName("userAddress")
            @Expose
            private BillingAddress userAddress;

            @SerializedName("cardholderName")
            @Expose
            private String cardholderName;

            @SerializedName("phoneNumber")
            @Expose
            private String phoneNumber;


            private BillingAddress getUserAddress() {
                return userAddress;
            }

            private void setUserAddress(BillingAddress userAddress) {
                this.userAddress = userAddress;
            }

            private String getCardNetwork() {
                return cardNetwork;
            }

            private void setCardNetwork(String cardNetwork) {
                this.cardNetwork = cardNetwork;
            }

            private String getTokenProvider() {
                return tokenProvider;
            }

            private void setTokenProvider(String tokenProvider) {
                this.tokenProvider = tokenProvider;
            }

            private String getDisplayName() {
                return displayName;
            }

            private void setDisplayName(String displayName) {
                this.displayName = displayName;
            }

            private String getLastDigits() {
                return lastDigits;
            }

            private void setLastDigits(String lastDigits) {
                this.lastDigits = lastDigits;
            }

            private String getOpc() {
                return opc;
            }

            private void setOpc(String opc) {
                this.opc = opc;
            }

            private String getCardholderName() {
                return cardholderName;
            }

            private void setCardholderName(String cardholderName) {
                this.cardholderName = cardholderName;
            }

            private String getPhoneNumber() {
                return phoneNumber;
            }

            private void setPhoneNumber(String phoneNumber) {
                this.phoneNumber = phoneNumber;
            }

        }

        private class BillingAddress {

            @SerializedName("line1")
            @Expose
            private String line1;
            @SerializedName("line2")
            @Expose
            private String line2;
            @SerializedName("city")
            @Expose
            private String city;
            @SerializedName("state")
            @Expose
            private String state;
            @SerializedName("country")
            @Expose
            private String country;
            @SerializedName("postalCode")
            @Expose
            private String postalCode;

            private String getLine1() {
                return line1;
            }

            private void setLine1(String line1) {
                this.line1 = line1;
            }

            private String getLine2() {
                return line2;
            }

            private void setLine2(String line2) {
                this.line2 = line2;
            }

            private String getCity() {
                return city;
            }

            private void setCity(String city) {
                this.city = city;
            }

            private String getState() {
                return state;
            }

            private void setState(String state) {
                this.state = state;
            }

            private String getCountry() {
                return country;
            }

            private void setCountry(String country) {
                this.country = country;
            }

            private String getPostalCode() {
                return postalCode;
            }

            private void setPostalCode(String postalCode) {
                this.postalCode = postalCode;
            }

    }

    private int getCardNetwork(String tsp) {
        return switch (tsp) {
            case "VISA" -> TapAndPay.CARD_NETWORK_VISA;
            case "MASTERCARD" -> TapAndPay.CARD_NETWORK_MASTERCARD;
            case "MIR" -> TapAndPay.CARD_NETWORK_MIR;
            default -> 0;
        };
    }

    private int getTSP(String tsp) {
        return switch (tsp) {
            case "VISA" -> TapAndPay.TOKEN_PROVIDER_VISA;
            case "MASTERCARD" -> TapAndPay.TOKEN_PROVIDER_MASTERCARD;
            case "MIR" -> TapAndPay.TOKEN_PROVIDER_MIR;
            default -> 0;
        };
    }

}
