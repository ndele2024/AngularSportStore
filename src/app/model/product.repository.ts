import { Injectable } from "@angular/core";
import { Product } from "./product.model";
import { StaticDataSource } from "./static.datasource";
@Injectable()
export class ProductRepository {
  private products: Product[] = [];
  private categories: string[] = [];
  constructor(private dataSource: StaticDataSource) { //infection of StaticDataSource class
    //asynchronous set the instance variable of class
    dataSource.getProducts().subscribe(data => {
      this.products = data;
      //get all different categories
      this.categories = data.map(p => p.category ?? "(None)")
        .filter((c, index, array) => array.indexOf(c) == index).sort();
    });
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
}
