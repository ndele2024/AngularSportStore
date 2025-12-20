import {ActivatedRouteSnapshot, RouterStateSnapshot, CanActivateFn, Router} from '@angular/router';
import {inject, Injectable} from '@angular/core';
import {AuthenticationService} from '../model/authentication.service';

@Injectable()
export class AuthGuard {
  private router = inject(Router);
  private auth = inject(AuthenticationService);

  canActivate = (route: ActivatedRouteSnapshot, state:RouterStateSnapshot) => {
    if (!this.auth.authenticated) {
      this.router.navigateByUrl("/auth");
      return false;
    }
    return true;
  };
}

