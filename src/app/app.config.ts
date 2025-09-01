import {provideZoneChangeDetection} from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import {ProductRepository} from './model/product.repository';
import {StaticDataSource} from './model/static.datasource';
import {CartModel} from './model/cart.model';
import {StoreFirstGuard} from './store-firt.guard';
import {Order} from './model/order.model';
import {OrderRepository} from './model/order.repository';
import {RestDataSource} from './model/rest.datasource';
import {provideHttpClient} from '@angular/common/http';

export const appConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient(),
    ProductRepository,
    StaticDataSource,
    CartModel,
    StoreFirstGuard,
    Order,
    OrderRepository,
    { provide: StaticDataSource, useClass: RestDataSource }
  ]
};
