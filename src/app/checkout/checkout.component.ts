import {Component, inject} from '@angular/core';
import {CurrencyPipe} from '@angular/common';
import {OrderRepository} from '../service/order.repository';
import {Order} from '../model/order.model';
import {FormsModule, NgForm} from "@angular/forms";
import {RouterLink} from '@angular/router';
import {AuthenticationService} from '../service/authentication.service';

@Component({
  selector: 'app-checkout',
  imports: [
    RouterLink,
    FormsModule,
    CurrencyPipe
  ],
  templateUrl: './checkout.component.html',
  standalone: true,
  styleUrl: './checkout.component.css'
})
export class CheckoutComponent {

  orderRepo:OrderRepository = inject(OrderRepository);
  order:Order = inject(Order);
  auth = inject(AuthenticationService);

  orderSent: boolean = false;
  submitted:boolean = false;

  constructor() {
    const user = this.auth.currentUser;
    if (user) {
      this.order.userId = user.id;
      this.order.username = user.username;
      this.order.nom = user.nom;
      this.order.prenom = user.prenom;
      this.order.adresse = user.adresse;
      this.order.telephone = user.telephone;
    }
  }

  submitOrder(form: NgForm) {
    this.submitted = true;
    if (form.valid) {
      const user = this.auth.currentUser;
      this.order.userId = user?.id;
      this.order.username = user?.username;
      this.order.createdAt = new Date().toISOString();
      this.order.itemCount = this.order.cart.itemCount;
      this.order.total = this.order.cart.cartPrice;
      this.order.shipped = false;
      this.orderRepo.saveOrder(this.order).subscribe(order => {
        this.order.clear();
        this.orderSent = true;
        this.submitted = false;
      });
    }
  }

}
