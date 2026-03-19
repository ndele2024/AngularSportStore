import {Component, inject} from '@angular/core';
import {FormsModule, NgForm} from '@angular/forms';
import {ActivatedRoute, Router, RouterLink} from '@angular/router';
import {AuthenticationService} from '../service/authentication.service';
import {User} from '../model/user.model';

@Component({
  selector: 'app-authentication',
  imports: [
    FormsModule,
    RouterLink
  ],
  templateUrl: './authentication.component.html',
  standalone: true,
  styleUrl: './authentication.component.css'
})
export class AuthenticationComponent {

  userName? : string;
  passwd? : string;
  errorMessage? : string;
  successMessage? : string;
  mode: "login" | "register" = "login";
  newUser: User = {};

  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private auth = inject(AuthenticationService);
  public authenticate(form : NgForm) {
    if (form.valid) {
      //user authentication
      this.auth.authenticate(this.userName ?? "", this.passwd ?? "")
        .subscribe(response => {
          if (response.success) {
            const returnUrl = this.route.snapshot.queryParamMap.get("returnUrl");
            this.errorMessage = undefined;
            this.router.navigateByUrl(returnUrl ?? (this.auth.isAdmin ? "/admin" : "/dashboard"));
            return;
          }
          this.errorMessage = response.message ?? "Authentication Failed";
        })
    }
    else {
      //error in form
      this.errorMessage = "Invalid username or password";
    }
  }

  register(form: NgForm) {
    if (!form.valid) {
      this.errorMessage = "Veuillez remplir tous les champs";
      return;
    }

    this.auth.register(this.newUser).subscribe(response => {
      if (response.success) {
        this.errorMessage = undefined;
        this.successMessage = "Compte cree avec succes";
        this.router.navigateByUrl("/dashboard");
        return;
      }

      this.successMessage = undefined;
      this.errorMessage = response.message ?? "Creation du compte impossible";
    });
  }

  setMode(mode: "login" | "register") {
    this.mode = mode;
    this.errorMessage = undefined;
    this.successMessage = undefined;
  }


}
