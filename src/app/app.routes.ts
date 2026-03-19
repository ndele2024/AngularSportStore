import { Routes } from '@angular/router';
import {StoreComponent} from './store/store.component';
import {CartDetailComponent} from './cart-detail/cart-detail.component';
import {CheckoutComponent} from './checkout/checkout.component';
import {AdminComponent} from './admin/admin.component';
import {AuthenticationComponent} from './authentication/authentication.component';
import {AuthGuard} from './guard/auth.guard';
import {ProductEditorComponent} from './product-editor/product-editor.component';
import {ProductTableComponent} from './product-table/product-table.component';
import {OrderTableComponent} from './order-table/order-table.component';
import {DashboardComponent} from './dashboard/dashboard.component';
import {UserGuard} from './guard/user.guard';
import {ProfileComponent} from './profile/profile.component';

export const routes: Routes = [
  { path: "", redirectTo: "dashboard", pathMatch: "full" },
  { path: "dashboard", component: DashboardComponent },
  { path: "profile", component: ProfileComponent, canActivate:[UserGuard] },
  { path: "store", component: StoreComponent },
  { path: "cart", component: CartDetailComponent },
  { path: "checkout", component: CheckoutComponent, canActivate:[UserGuard] },
  {path: "admin", component: AdminComponent, canActivate:[AuthGuard],
    children : [
      { path: "products/:mode/:id", component: ProductEditorComponent },
      { path: "products/:mode", component: ProductEditorComponent },
      { path: "products", component: ProductTableComponent },
      { path: "orders", component: OrderTableComponent },
      { path: "**", redirectTo: "products" }
    ]
  },

  {path: "auth", component: AuthenticationComponent},
  { path: "**", redirectTo: "/dashboard" }
];
