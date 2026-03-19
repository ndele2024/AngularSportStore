import {Component, inject} from '@angular/core';
import {FormsModule, NgForm} from '@angular/forms';
import {RouterLink} from '@angular/router';
import {AuthenticationService} from '../service/authentication.service';
import {User} from '../model/user.model';

@Component({
  selector: 'app-profile',
  imports: [
    FormsModule,
    RouterLink
  ],
  templateUrl: './profile.component.html',
  standalone: true,
  styleUrl: './profile.component.css'
})
export class ProfileComponent {
  private auth = inject(AuthenticationService);

  profile: User = {
    nom: this.auth.currentUser?.nom,
    prenom: this.auth.currentUser?.prenom,
    adresse: this.auth.currentUser?.adresse,
    telephone: this.auth.currentUser?.telephone,
    username: this.auth.currentUser?.username
  };
  newPassword = "";
  successMessage?: string;
  errorMessage?: string;

  saveProfile(form: NgForm) {
    if (!form.valid) {
      this.errorMessage = "Veuillez remplir les champs obligatoires.";
      this.successMessage = undefined;
      return;
    }

    this.auth.updateProfile({
      ...this.profile,
      password: this.newPassword || undefined
    }).subscribe({
      next: user => {
        this.successMessage = "Vos informations ont ete mises a jour.";
        this.errorMessage = undefined;
        this.newPassword = "";
        this.profile = {
          nom: user.nom,
          prenom: user.prenom,
          adresse: user.adresse,
          telephone: user.telephone,
          username: user.username
        };
      },
      error: error => {
        this.successMessage = undefined;
        this.errorMessage = error?.error?.message ?? "La mise a jour a echoue.";
      }
    });
  }

  get currentUser() {
    return this.auth.currentUser;
  }
}
