import {Component, inject} from '@angular/core';
import {FormsModule, NgForm} from '@angular/forms';
import {Router, RouterLink} from '@angular/router';
import {AuthenticationService} from '../service/authentication.service';

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

  private router = inject(Router);
  private auth = inject(AuthenticationService);
  public authenticate(form : NgForm) {
    if (form.valid) {
      //user authentication
      this.auth.authenticate(this.userName ?? "", this.passwd ?? "")
        .subscribe(response => {
          if (response) {
            this.router.navigateByUrl("/admin");
          }
          this.errorMessage = "Authentication Failed";
        })
    }
    else {
      //error in form
      this.errorMessage = "Invalid username or password";
    }
  }


}
