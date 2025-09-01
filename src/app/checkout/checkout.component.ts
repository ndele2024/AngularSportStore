import {Component, inject} from '@angular/core';
import {OrderRepository} from '../model/order.repository';
import {Order} from '../model/order.model';
import {FormsModule, NgForm} from "@angular/forms";
import {RouterLink} from '@angular/router';

@Component({
  selector: 'app-checkout',
  imports: [
    RouterLink,
    FormsModule
  ],
  templateUrl: './checkout.component.html',
  standalone: true,
  styleUrl: './checkout.component.css'
})
export class CheckoutComponent {

  orderRepo:OrderRepository = inject(OrderRepository);
  order:Order = inject(Order);

  orderSent: boolean = false;
  submitted:boolean = false;

  submitOrder(form: NgForm) {
    this.submitted = true;
    if (form.valid) {
      this.orderRepo.saveOrder(this.order).subscribe(order => {
        this.order.clear();
        this.orderSent = true;
        this.submitted = false;
      });
    }
  }

}
