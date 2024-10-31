import { WebPlugin } from '@capacitor/core';
export class GooglePayWeb extends WebPlugin {
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
    registerDataChangedListener() {
        throw this.unimplemented('Not implemented on web.');
    }
    addListener(eventName, listenerFunc) {
        console.log(eventName);
        console.log(listenerFunc);
        throw this.unimplemented('Not implemented on web.');
    }
    removeAllListeners() {
        throw this.unimplemented('Not implemented on web.');
    }
}
//# sourceMappingURL=web.js.map