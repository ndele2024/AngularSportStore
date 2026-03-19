import { Component } from '@angular/core';
import {Product} from '../model/product.model';
import {ActivatedRoute, Router, RouterLink} from '@angular/router';
import {ProductRepository} from '../service/product.repository';
import {FormsModule} from '@angular/forms';
import {MatButton} from '@angular/material/button';
import {NgIf} from '@angular/common';
import {MatFormField, MatInput, MatLabel} from '@angular/material/input';

@Component({
  selector: 'app-product-editor',
  imports: [
    FormsModule,
    MatLabel,
    MatButton,
    RouterLink,
    NgIf,
    MatInput,
    MatLabel,
    MatFormField
  ],
  templateUrl: './product-editor.component.html',
  standalone: true,
  styleUrl: './product-editor.component.css'
})
export class ProductEditorComponent {

  editing: boolean = false;
  product: Product = new Product();

  constructor(private repository: ProductRepository,
              private router: Router,
              activeRoute: ActivatedRoute) {
    this.editing = activeRoute.snapshot.params["mode"] == "edit";
    if (this.editing) {
      Object.assign(this.product,
        repository.getProduct(activeRoute.snapshot.params["id"]));
    }
  }
  save() {
    this.repository.saveOrUpdateProduct(this.product);
    this.router.navigateByUrl("/admin/products");
  }

}
