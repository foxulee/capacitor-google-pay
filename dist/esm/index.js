import { registerPlugin } from '@capacitor/core';
const GooglePay = registerPlugin('GooglePay', {
    web: () => import('./web').then(m => new m.GooglePayWeb()),
});
export * from './definitions';
export { GooglePay };
//# sourceMappingURL=index.js.map