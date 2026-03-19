import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { storeFirtGuard } from './store-firt.guard';

describe('storeFirtGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) => 
      TestBed.runInInjectionContext(() => storeFirtGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
