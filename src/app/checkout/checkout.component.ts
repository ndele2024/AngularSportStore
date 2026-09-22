import {Component, inject} from '@angular/core';
import {CurrencyPipe} from '@angular/common';
import {FormsModule, NgForm} from "@angular/forms";
import {RouterLink} from '@angular/router';
import {OrderRepository} from '../service/order.repository';
import {Order} from '../model/order.model';
import {AuthenticationService} from '../service/authentication.service';

type CheckoutStep = "checkout" | "payment" | "success";

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
  orderRepo: OrderRepository = inject(OrderRepository);
  order: Order = inject(Order);
  auth = inject(AuthenticationService);

  step: CheckoutStep = "checkout";
  submitted: boolean = false;
  paymentSubmitted: boolean = false;
  isProcessingPayment: boolean = false;
  paymentError?: string;
  completedOrder?: Order;

  payment = {
    cardHolder: "",
    cardNumber: "",
    expiryMonth: "",
    expiryYear: "",
    cvv: ""
  };

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

  proceedToPayment(form: NgForm) {
    this.submitted = true;
    if (!form.valid) {
      return;
    }

    const user = this.auth.currentUser;
    this.order.userId = user?.id;
    this.order.username = user?.username;
    this.step = "payment";
    this.paymentError = undefined;
  }

  backToCheckout() {
    this.step = "checkout";
    this.paymentError = undefined;
  }

  submitPayment(form: NgForm) {
    this.paymentSubmitted = true;
    this.paymentError = undefined;
    if (!form.valid) {
      return;
    }

    const digits = this.payment.cardNumber.replace(/\D/g, "");
    if (digits.length < 13 || digits.length > 19) {
      this.paymentError = "Le numero de carte est invalide.";
      return;
    }

    this.isProcessingPayment = true;
    this.order.createdAt = new Date().toISOString();
    this.order.itemCount = this.order.cart.itemCount;
    this.order.total = this.order.cart.cartPrice;
    this.order.shipped = false;
    this.order.status = "EN_TRAITEMENT";
    this.order.paymentStatus = "PAYE";
    this.order.paymentMethod = "CARTE_CREDIT";
    this.order.paymentReference = this.generatePaymentReference();
    this.order.paymentLast4 = digits.slice(-4);

    this.orderRepo.saveOrder(this.order).subscribe({
      next: (savedOrder) => {
        this.completedOrder = savedOrder;
        this.order.clear();
        this.step = "success";
        this.submitted = false;
        this.paymentSubmitted = false;
        this.isProcessingPayment = false;
        this.payment = {
          cardHolder: "",
          cardNumber: "",
          expiryMonth: "",
          expiryYear: "",
          cvv: ""
        };
      },
      error: () => {
        this.paymentError = "Le paiement n'a pas pu etre valide. Veuillez reessayer.";
        this.isProcessingPayment = false;
      }
    });
  }

  downloadInvoice() {
    const orderId = this.completedOrder?.id;
    if (!orderId) {
      return;
    }

    this.orderRepo.downloadInvoice(orderId).subscribe(blob => {
      const url = window.URL.createObjectURL(blob);
      const anchor = document.createElement("a");
      anchor.href = url;
      anchor.download = `${this.completedOrder?.invoiceNumber ?? `facture-${orderId}`}.pdf`;
      anchor.click();
      window.URL.revokeObjectURL(url);
    });
  }

  get maskedCardPreview(): string {
    const last4 = this.payment.cardNumber.replace(/\D/g, "").slice(-4);
    return last4 ? `**** **** **** ${last4}` : "**** **** **** ****";
  }

  private generatePaymentReference(): string {
    const suffix = Math.floor(100000 + Math.random() * 900000);
    return `PAY-${Date.now()}-${suffix}`;
  }
}
