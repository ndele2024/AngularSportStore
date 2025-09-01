import {Component, inject} from '@angular/core';
import {ProductRepository} from '../model/product.repository';
import {Product} from '../model/product.model';
import {CurrencyPipe} from '@angular/common';
import {CartModel} from '../model/cart.model';
import {CartSummaryComponent} from '../cart-summary/cart-summary.component';
import {Router} from '@angular/router';

@Component({
  selector: 'app-store',
  imports: [
    CurrencyPipe,
    CartSummaryComponent
  ],
  templateUrl: './store.component.html',
  standalone: true,
  styleUrl: './store.component.css'
})
export class StoreComponent {

  selectedCategory : string | undefined;
  productsPerPage = 4;
  selectedPage = 1;

  //injection of CartModel
  cart : CartModel = inject(CartModel);
  router : Router = inject(Router);
  //inject class ProductRepository in the constructor
  constructor(private repository : ProductRepository) {
  }

  //get all products of the selected category or all product if category is undefined
  get products() : Product[] {
    let pageIndex = (this.selectedPage - 1) * this.productsPerPage;
    return this.repository.getProducts(this.selectedCategory).slice(pageIndex, pageIndex + this.productsPerPage);
  }

  //get all categories
  get categories() : string[] {
    return this.repository.getCategories();
  }

  //modify selected category
  changeCategory(newCategory? : string){
    this.selectedCategory = newCategory;
  }

  changePage(value:number) {
    this.selectedPage = value;
  }

  changePageSize(value:number) {
    this.productsPerPage = Number(value);
    this.changePage(1);
  }

  getPages():number[]{
    let pageNumber = Math.ceil(this.repository.getProducts(this.selectedCategory).length / this.productsPerPage);
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

}
