var capacitorGooglePay = (function (exports, core) {
    'use strict';

    const GooglePay = core.registerPlugin('GooglePay', {
        web: () => Promise.resolve().then(function () { return web; }).then(m => new m.GooglePayWeb()),
    });

    class GooglePayWeb extends core.WebPlugin {
        getEnvironment() {
            throw this.unimplemented('Not implemented on web.');
        }
        getStableHardwareId() {
            throw this.unimplemented('Not implemented on web.');
        }
        getActiveWalletID() {
            throw this.unimplemented('Not implemented on web.');
        }
        createWallet() {
            throw this.unimplemented('Not implemented on web.');
        }
        getTokenStatus() {
            throw this.unimplemented('Not implemented on web.');
        }
        listTokens() {
            throw this.unimplemented('Not implemented on web.');
        }
        isTokenized() {
            throw this.unimplemented('Not implemented on web.');
        }
        pushProvision() {
            throw this.unimplemented('Not implemented on web.');
        }
        requestSelectToken() {
            throw this.unimplemented('Not implemented on web.');
        }
        requestDeleteToken() {
            throw this.unimplemented('Not implemented on web.');
        }
        isGPayDefaultNFCApp() {
            throw this.unimplemented('Not implemented on web.');
        }
        setGPayAsDefaultNFCApp() {
            throw this.unimplemented('Not implemented on web.');
        }
        getWalletInformation() {
            throw this.unimplemented('Not implemented on web.');
        }
        pushToWallet() {
            throw this.unimplemented('Not implemented on web.');
        }
        registerDataChangedListener() {
            throw this.unimplemented('Not implemented on web.');
        }
        addListener(eventName, listenerFunc) {
            return super.addListener(eventName, listenerFunc);
        }
        removeAllListeners() {
            throw this.unimplemented('Not implemented on web.');
        }
    }

    var web = /*#__PURE__*/Object.freeze({
        __proto__: null,
        GooglePayWeb: GooglePayWeb
    });

    exports.GooglePay = GooglePay;

    return exports;

})({}, capacitorExports);
//# sourceMappingURL=plugin.js.map
