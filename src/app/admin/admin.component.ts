import {Component, inject} from '@angular/core';
import {MatToolbar} from '@angular/material/toolbar';
import {MatButton, MatIconButton} from '@angular/material/button';
import {MatIcon} from '@angular/material/icon';
import {MatSidenav, MatSidenavContainer, MatSidenavContent} from '@angular/material/sidenav';
import {Router, RouterLink, RouterLinkActive, RouterOutlet} from '@angular/router';
import {MatDivider} from '@angular/material/divider';
import {AuthenticationService} from '../service/authentication.service';

@Component({
  selector: 'app-admin',
  imports: [
    MatToolbar,
    MatIconButton,
    MatIcon,
    MatSidenavContainer,
    MatSidenavContent,
    MatSidenav,
    MatButton,
    RouterLink,
    RouterLinkActive,
    MatDivider,
    RouterOutlet
  ],
  templateUrl: './admin.component.html',
  standalone: true,
  styleUrl: './admin.component.css'
})
export class AdminComponent {

  private auth = inject(AuthenticationService);
  private router = inject(Router);


  logout() {
    //logout admin
    this.auth.clear();
    //rediect to home
    this.router.navigateByUrl("/");
  }
}
