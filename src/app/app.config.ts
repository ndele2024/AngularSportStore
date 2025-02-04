import {provideZoneChangeDetection} from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import {ProductRepository} from './model/product.repository';
import {StaticDataSource} from './model/static.datasource';

export const appConfig = {
  providers: [provideZoneChangeDetection({ eventCoalescing: true }), provideRouter(routes), ProductRepository, StaticDataSource]
};
