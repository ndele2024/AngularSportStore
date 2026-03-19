import {provideZoneChangeDetection} from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import {ProductRepository} from './service/product.repository';
import {StaticDataSource} from './model/static.datasource';
import {CartModel} from './model/cart.model';
import {StoreFirstGuard} from './guard/store-firt.guard';
import {Order} from './model/order.model';
import {OrderRepository} from './service/order.repository';
import {RestDataSource} from './model/rest.datasource';
import {provideHttpClient} from '@angular/common/http';
import {AuthenticationService} from './service/authentication.service';
import {AuthGuard} from './guard/auth.guard';

export const appConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient(),
    ProductRepository,
    StaticDataSource,
    CartModel,
    StoreFirstGuard,
    AuthGuard,
    Order,
    OrderRepository,
    { provide: StaticDataSource, useClass: RestDataSource },
    RestDataSource,
    AuthenticationService
  ]
};
