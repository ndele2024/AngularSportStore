import {Component, inject} from '@angular/core';
import {ProductRepository} from '../service/product.repository';
import {Product} from '../model/product.model';
import {CurrencyPipe} from '@angular/common';
import {CartModel} from '../model/cart.model';
import {CartSummaryComponent} from '../cart-summary/cart-summary.component';
import {Router, RouterLink} from '@angular/router';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import {FormsModule} from '@angular/forms';
import {AuthenticationService} from '../service/authentication.service';

@Component({
  selector: 'app-store',
  imports: [
    CurrencyPipe,
    CartSummaryComponent,
    RouterLink,
    MatProgressSpinner,
    FormsModule
  ],
  templateUrl: './store.component.html',
  standalone: true,
  styleUrl: './store.component.css'
})
export class StoreComponent {

  selectedCategory : string | undefined;
  searchTerm = "";
  productsPerPage = 4;
  selectedPage = 1;
  previewProduct?: Product;

  //injection of CartModel
  cart : CartModel = inject(CartModel);
  router : Router = inject(Router);
  auth = inject(AuthenticationService);
  //inject class ProductRepository in the constructor
  constructor(private repository : ProductRepository) {
  }

  //get all products of the selected category or all product if category is undefined
  get products() : Product[] {
    let pageIndex = (this.selectedPage - 1) * this.productsPerPage;
    return this.repository.getProducts(this.selectedCategory, this.searchTerm).slice(pageIndex, pageIndex + this.productsPerPage);
  }

  //get all categories
  get categories() : string[] {
    return this.repository.getCategories();
  }

  //modify selected category
  changeCategory(newCategory? : string){
    this.selectedCategory = newCategory;
    this.changePage(1);
  }

  changePage(value:number) {
    this.selectedPage = value;
  }

  changePageSize(value:number) {
    this.productsPerPage = Number(value);
    this.changePage(1);
  }

  updateSearchTerm(value: string) {
    this.searchTerm = value;
    this.changePage(1);
  }

  getPages():number[]{
    let pageNumber = Math.ceil(this.repository.getProducts(this.selectedCategory, this.searchTerm).length / this.productsPerPage);
    let pages = [];
    for (let i = 1; i <= pageNumber; i++) {
      pages[i-1] = i;
    }
    //console.log(`pages : ${pages.length}`);
    return pages;
  }

  addProductToCart(product: Product) {
    this.cart.addLine(product);
    this.router.navigateByUrl("/cart");
  }

  openImagePreview(product: Product) {
    this.previewProduct = product;
  }

  closeImagePreview() {
    this.previewProduct = undefined;
  }

  logout() {
    this.auth.clear();
    this.router.navigateByUrl("/dashboard");
  }

  getIsLoading(): boolean {
    return this.repository.getIsLoading();
  }

}
