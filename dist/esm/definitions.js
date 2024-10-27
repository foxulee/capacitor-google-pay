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
})(TokenStatusReference || (TokenStatusReference = {}));
//# sourceMappingURL=definitions.js.map