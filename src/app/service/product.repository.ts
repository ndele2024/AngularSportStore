import { Injectable } from "@angular/core";
import { Product } from "../model/product.model";
//import { StaticDataSource } from "./static.datasource";
import {RestDataSource} from '../model/rest.datasource';
@Injectable()
export class ProductRepository {
  private products: Product[] = [];
  private categories: string[] = [];
  private isLoading : boolean = true;
  constructor(private dataSource: RestDataSource)  { //injection of StaticDataSource class
    //asynchronous set the instance variable of class
    dataSource.getProducts().subscribe(data => {
      this.products = data;
      //get all different categories
      this.categories = data.map(p => p.category ?? "(None)")
        .filter((c, index, array) => array.indexOf(c) == index).sort();
      this.isLoading = false;
    });
  }

  getIsLoading(){
    return this.isLoading;
  }

  //return the products list that match category pass as parameter or products that category value is undefined
  getProducts(category?: string): Product[] {
    return this.products
      .filter(p => category == undefined || category == p.category);
  }

  //return a product corresponding of the id pass as parameter or return undefined if no product match
  getProduct(id: number): Product | undefined {
    return this.products.find(p => p.id == id);
  }

  //get all categories of products
  getCategories(): string[] {
    return this.categories;
  }

  saveOrUpdateProduct(product: Product) {
    if (product.id == null || product.id == 0) {
      this.dataSource.saveProduct(product)
        .subscribe(p => this.products.push(p));
    }
    else{
      this.dataSource.updateProduct(product)
        .subscribe(p => {
          this.products.splice(
            this.products.findIndex(pd => pd.id == product.id),
            1,
            product
          );
        });
    }

  }

  deleteProduct(id: number) {
    this.dataSource.deleteProduct(id).subscribe(p => {
      this.products.splice(
        this.products.findIndex(p => p.id == id),
        1
      );
    });
  }


}
