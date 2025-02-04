import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {StoreComponent} from './store/store.component';

@Component({
  selector: 'app',
  imports: [RouterOutlet, StoreComponent],
  templateUrl: './app.component.html',
  standalone: true,
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'sportStore';
}
