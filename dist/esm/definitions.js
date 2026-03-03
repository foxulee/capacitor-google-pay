export var ErrorCodeReference;
(function (ErrorCodeReference) {
    ErrorCodeReference[ErrorCodeReference["PUSH_PROVISION_ERROR"] = -1] = "PUSH_PROVISION_ERROR";
    ErrorCodeReference[ErrorCodeReference["PUSH_PROVISION_CANCEL"] = -2] = "PUSH_PROVISION_CANCEL";
    ErrorCodeReference[ErrorCodeReference["MISSING_DATA_ERROR"] = -3] = "MISSING_DATA_ERROR";
    ErrorCodeReference[ErrorCodeReference["CREATE_WALLET_CANCEL"] = -4] = "CREATE_WALLET_CANCEL";
    ErrorCodeReference[ErrorCodeReference["IS_TOKENIZED_ERROR"] = -5] = "IS_TOKENIZED_ERROR";
    ErrorCodeReference[ErrorCodeReference["REMOVE_TOKEN_ERROR"] = -6] = "REMOVE_TOKEN_ERROR";
    ErrorCodeReference[ErrorCodeReference["INVALID_TOKEN"] = -7] = "INVALID_TOKEN";
    ErrorCodeReference[ErrorCodeReference["SELECT_TOKEN_ERROR"] = -8] = "SELECT_TOKEN_ERROR";
    ErrorCodeReference[ErrorCodeReference["SET_DEFAULT_PAYMENTS_ERROR"] = -9] = "SET_DEFAULT_PAYMENTS_ERROR";
    ErrorCodeReference[ErrorCodeReference["ERROR_CODE_JSON_FORMATTING_EXCEPTION"] = 1] = "ERROR_CODE_JSON_FORMATTING_EXCEPTION";
    ErrorCodeReference[ErrorCodeReference["ERROR_CODE_WALLET_NOT_INTIALIZED"] = 2] = "ERROR_CODE_WALLET_NOT_INTIALIZED";
    ErrorCodeReference[ErrorCodeReference["ERROR_CODE_CARD_EXISTS_IN_WALLET"] = 3] = "ERROR_CODE_CARD_EXISTS_IN_WALLET";
})(ErrorCodeReference || (ErrorCodeReference = {}));
export var TokenStatusReference;
(function (TokenStatusReference) {
    TokenStatusReference[TokenStatusReference["TOKEN_STATE_UNTOKENIZED"] = 1] = "TOKEN_STATE_UNTOKENIZED";
    TokenStatusReference[TokenStatusReference["TOKEN_STATE_PENDING"] = 2] = "TOKEN_STATE_PENDING";
    TokenStatusReference[TokenStatusReference["TOKEN_STATE_NEEDS_IDENTITY_VERIFICATION"] = 3] = "TOKEN_STATE_NEEDS_IDENTITY_VERIFICATION";
    TokenStatusReference[TokenStatusReference["TOKEN_STATE_SUSPENDED"] = 4] = "TOKEN_STATE_SUSPENDED";
    TokenStatusReference[TokenStatusReference["TOKEN_STATE_ACTIVE"] = 5] = "TOKEN_STATE_ACTIVE";
    TokenStatusReference[TokenStatusReference["TOKEN_STATE_FELICA_PENDING_PROVISIONING"] = 6] = "TOKEN_STATE_FELICA_PENDING_PROVISIONING";
    TokenStatusReference[TokenStatusReference["TOKEN_STATE_NOT_FOUND"] = -1] = "TOKEN_STATE_NOT_FOUND";
    TokenStatusReference[TokenStatusReference["REQUEST_CREATE_WALLET"] = 100] = "REQUEST_CREATE_WALLET";
    TokenStatusReference[TokenStatusReference["REQUEST_CODE_PUSH_TOKENIZE"] = 200] = "REQUEST_CODE_PUSH_TOKENIZE";
    TokenStatusReference[TokenStatusReference["REQUEST_WALLET_INFORMATION"] = 300] = "REQUEST_WALLET_INFORMATION";
    TokenStatusReference[TokenStatusReference["EXCEPTION_TOKEN_PENDING_STATE"] = 400] = "EXCEPTION_TOKEN_PENDING_STATE";
})(TokenStatusReference || (TokenStatusReference = {}));
//# sourceMappingURL=definitions.js.map