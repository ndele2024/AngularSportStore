import {Component, inject} from '@angular/core';
import {CartModel} from '../model/cart.model';
import {CurrencyPipe} from '@angular/common';
import {RouterLink} from '@angular/router';

@Component({
  selector: 'app-cart-detail',
  imports: [
    CurrencyPipe,
    RouterLink
  ],
  templateUrl: './cart-detail.component.html',
  standalone: true,
  styleUrl: './cart-detail.component.css'
})
export class CartDetailComponent {
  cart:CartModel = inject(CartModel);


}
