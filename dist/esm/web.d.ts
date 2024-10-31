import { WebPlugin } from '@capacitor/core';
import type { GooglePayPlugin } from './definitions';
import { PluginListenerHandle } from "@capacitor/core/types/definitions";
export declare class GooglePayWeb extends WebPlugin implements GooglePayPlugin {
    getEnvironment(): Promise<any>;
    getStableHardwareId(): Promise<any>;
    getActiveWalletID(): Promise<any>;
    createWallet(): Promise<any>;
    getTokenStatus(): Promise<any>;
    listTokens(): Promise<any>;
    isTokenized(): Promise<any>;
    pushProvision(): Promise<any>;
    requestSelectToken(): Promise<any>;
    requestDeleteToken(): Promise<any>;
    isGPayDefaultNFCApp(): Promise<any>;
    setGPayAsDefaultNFCApp(): Promise<any>;
    registerDataChangedListener(): Promise<any>;
    addListener(eventName: "registerDataChangedListener", listenerFunc: (response: any) => void): Promise<PluginListenerHandle>;
    removeAllListeners(): Promise<void>;
}
