import {Component, inject} from '@angular/core';
import {CartModel} from '../model/cart.model';
import {CurrencyPipe} from '@angular/common';
import {RouterLink} from '@angular/router';

@Component({
  selector: 'app-cart-summary',
  imports: [
    CurrencyPipe,
    RouterLink
  ],
  templateUrl: './cart-summary.component.html',
  standalone: true,
  styleUrl: './cart-summary.component.css'
})
export class CartSummaryComponent {

  cart : CartModel = inject(CartModel);

}
