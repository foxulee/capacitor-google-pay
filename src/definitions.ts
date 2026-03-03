import type { PluginListenerHandle } from '@capacitor/core';

export declare enum ErrorCodeReference {
  PUSH_PROVISION_ERROR = -1,
  PUSH_PROVISION_CANCEL = -2,
  MISSING_DATA_ERROR = -3,
  CREATE_WALLET_CANCEL = -4,
  IS_TOKENIZED_ERROR = -5,
  REMOVE_TOKEN_ERROR = -6,
  INVALID_TOKEN = -7,
  SELECT_TOKEN_ERROR = -8,
  SET_DEFAULT_PAYMENTS_ERROR = -9,
  ERROR_CODE_JSON_FORMATTING_EXCEPTION = 1,
  ERROR_CODE_WALLET_NOT_INTIALIZED = 2,
  ERROR_CODE_CARD_EXISTS_IN_WALLET = 3,
}

export declare enum TokenStatusReference {
  TOKEN_STATE_UNTOKENIZED = 1,
  TOKEN_STATE_PENDING = 2,
  TOKEN_STATE_NEEDS_IDENTITY_VERIFICATION = 3,
  TOKEN_STATE_SUSPENDED = 4,
  TOKEN_STATE_ACTIVE = 5,
  TOKEN_STATE_FELICA_PENDING_PROVISIONING = 6,
  TOKEN_STATE_NOT_FOUND = -1,
  REQUEST_CREATE_WALLET = 100,
  REQUEST_CODE_PUSH_TOKENIZE = 200,
  REQUEST_WALLET_INFORMATION = 300,
  EXCEPTION_TOKEN_PENDING_STATE = 400,
}

export interface GooglePayAddress {
  /**
   * Address name
   *
   * @since 1.0.0
   */
  name: string;

  /**
   * Full address
   *
   * @since 1.0.0
   */
  address1: string;

  /**
   * Apartment/Office
   *
   * @since 1.0.0
   */
  address2?: string;

  /**
   * Locality
   *
   * @since 1.0.0
   */
  locality: string;

  /**
   * Administrative area
   *
   * @since 1.0.0
   */
  administrativeArea: string;

  /**
   * Country code
   *
   * @since 1.0.0
   */
  countryCode: string;

  /**
   * Postal code
   *
   * @since 1.0.0
   */
  postalCode: string;

  /**
   * Phone number
   *
   * @since 1.0.0
   */
  phoneNumber: string;
}


export interface GooglePayProvisionOptions {
  /**
   * Sets Opaque Payment Card binary data.
   *
   * @since 1.0.0
   */
  opc: string;

  /**
   * Sets the TSP that should be used for the tokenization attempt (see TokenServiceProvider).
   *
   * @since 1.0.0
   */
  tsp: string;

  /**
   * Sets the clientName that should be used for the tokenization attempt (see TokenServiceProvider).
   *
   * @since 1.0.0
   */
  clientName: string;

  /**
   * Sets the lastDigits that should be used for the tokenization attempt (see TokenServiceProvider).
   *
   * @since 1.0.0
   */
  lastDigits: string;

  /**
   * Sets the address that should be used for the tokenization attempt (see TokenServiceProvider).
   *
   * @since 1.0.0
   */
  address: GooglePayAddress;
}

export interface GooglePayIsTokenizedOptions {
  /**
   * Sets the TSP that should be used for the tokenization attempt (see TokenServiceProvider).
   *
   * @since 1.0.0
   */
  tsp: string;

  /**
   * Sets the lastDigits that should be used for the tokenization attempt (see TokenServiceProvider).
   *
   * @since 1.0.0
   */
  lastDigits: string;
}

export interface GoogleWalletRequest {
  /**
   * Sets the Payment Network that should be used for the tokenization attempt (see TokenServiceProvider).
   *
   * @since 1.0.0
   */
  paymentNetwork: string;
  /**
   * Sets the Cardholder Name that should be used for the tokenization attempt (see TokenServiceProvider).
   *
   * @since 1.0.0
   */
  cardHolderName: string;
  /**
   * Sets the Card Nickname that should be used for the tokenization attempt (see TokenServiceProvider).
   *
   * @since 1.0.0
   */
  cardNickName: string;
  /**
   * Sets the Card Last4 that should be used for the tokenization attempt (see TokenServiceProvider).
   *
   * @since 1.0.0
   */
  last4CardNumber: string;
}

export interface GooglePayTokenOptions {
  /**
   * Sets the TSP that should be used for the tokenization attempt (see TokenServiceProvider).
   *
   * @since 1.0.0
   */
  tsp: string;

  /**
   * token registered to the active wallet
   *
   * @since 1.0.0
   */
  tokenReferenceId: string;
}

export type GooglePaySuccessEvent = {
  requestCode: number;
  data: Record<string, string>;
};

export type GooglePayFailureEvent = {
  errorCode: number;
  message: string;
};

export interface GooglePayPlugin {
  /**
   * Event called when an action is performed on a pusn notification.
   * @param eventName pushNotificationActionPerformed.
   * @param listenerFunc callback with the notification action.
   *
   * @since 1.0.0
   */
  addListener(eventName: 'registerDataChangedListener', listenerFunc: (response: any) => void): Promise<PluginListenerHandle>;

  /**
   * Event called when an action is performed successfully on a Google wallet action.
   * @param eventName actionPerformed.
   * @param listenerFunc callback with the action.
   *
   * @since 2.0.0
   */
  addListener(
      eventName: 'onSuccessForGoogle',
      listenerFunc: (event: GooglePaySuccessEvent) => void,
  ): Promise<PluginListenerHandle>;

  /**
   * Event called when an action is performed unsuccessfully on a Google wallet action.
   * @param eventName actionPerformed.
   * @param listenerFunc callback with the action.
   *
   * @since 2.0.0
   */
  addListener(
      eventName: 'onFailureForGoogle',
      listenerFunc: (event: GooglePayFailureEvent) => void,
  ): Promise<PluginListenerHandle>;


  removeAllListeners(): void;

  /**
   * returns the environment (e.g. production or sandbox)
   * @return {Promise<{ value: string }>}
   *
   * @since 1.0.0
   */
  getEnvironment(): Promise<{ value: 'PROD' | 'SANDBOX' | 'DEV' }>;

  /**
   * returns the stable hardware ID of the device
   * @return {Promise<{ hardwareId: string }>}
   *
   * @since 1.0.0
   */
  getStableHardwareId(): Promise<{ hardwareId: string }>;

  /**
   * returns the ID of the active wallet
   * @return {Promise<{ walletId: string }>}
   *
   * @since 1.0.0
   */
  getActiveWalletID(): Promise<{ walletId: string }>;

  /**
   *  Initializes create wallet
   * @return {Promise<{ isCreated: boolean }>}
   *
   * @since 4.0.1
   */
  createWallet(): Promise<{ isCreated: boolean }>;

  /**
   * returns the status of a token with a given token ID
   * @param options {GooglePayTokenOptions} Token Options
   * @return {Promise<any>}
   *
   * @since 1.0.0
   */
  getTokenStatus(options: GooglePayTokenOptions): Promise<{ state: TokenStatusReference, code: string }>;

  /**
   * returns a list of tokens registered to the active wallet
   * @return {Promise<{tokens: string[]}>}
   *
   * @since 1.0.0
   */
  listTokens(): Promise<{ tokens: string[] }>;

  /**
   *  Starts the push tokenization flow
   * @param options.tsp {string} Sets the TSP that should be used for the tokenization attempt (see TokenServiceProvider).
   * @param options.lastDigits {string} Sets the TSP that should be used for the tokenization attempt (see TokenServiceProvider).
   * @return {Promise<{isTokenized: string}>}
   *
   * @since 1.0.0
   */
  isTokenized(options: GooglePayIsTokenizedOptions): Promise<{ isTokenized: boolean }>;

  /**
   *  Starts the push tokenization flow
   * @param options.opc {string} Sets Opaque Payment Card binary data.
   * @param options.tsp {string} Sets the TSP that should be used for the tokenization attempt (see TokenServiceProvider).
   * @param options.clientName {string} Sets the TSP that should be used for the tokenization attempt (see TokenServiceProvider).
   * @param options.lastDigits {string} Sets the TSP that should be used for the tokenization attempt (see TokenServiceProvider).
   * @param options.address {GooglePayAddress} Sets the TSP that should be used for the tokenization attempt (see TokenServiceProvider).
   * @return {Promise<{tokenId: string}>}
   *
   * @since 1.0.0
   */
  pushProvision(options: GooglePayProvisionOptions): Promise<{ tokenId: string }>;

  /**
   *  Requests setting token as default in Google Pay
   * @param options {GooglePayTokenOptions} Token Options
   * @return {Promise<{ isSuccess: boolean }>}
   *
   * @since 1.0.0
   */
  requestSelectToken(options: GooglePayTokenOptions): Promise<{ isSuccess: boolean }>;

  /**
   *  Requests deleting token from Google Pay
   * @param options {GooglePayTokenOptions} Token Options
   * @return {Promise<{ isSuccess: boolean }>}
   *
   * @since 4.0.13
   */
  requestDeleteToken(options: GooglePayTokenOptions): Promise<{ isSuccess: boolean }>;

  /**
   *  Check if Google Pay is Default NFC payment App
   * @return {Promise<{ isDefault: boolean, isNFCOn: boolean }>}
   *
   * @since 4.0.4
   */
  isGPayDefaultNFCApp(): Promise<{ isDefault: boolean, isNFCOn: boolean }>;

  /**
   *  Sets Google Pay as Default NFC payment App
   * @return {Promise<{ isDefault: boolean }>}
   *
   * @since 4.0.4
   */
  setGPayAsDefaultNFCApp(): Promise<{ isDefault: boolean }>;

  /**
   * returns the status of a token with a given token ID
   * @return {Promise<any>}
   *
   * @since 1.0.0
   */
  registerDataChangedListener(): Promise<any>;

  getWalletInformation(option: GoogleWalletRequest): Promise<any>;

  pushToWallet(option: {response: string}): Promise<any>;

}
