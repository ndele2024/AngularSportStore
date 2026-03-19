import {Component, inject, IterableDiffer, IterableDiffers, ViewChild} from '@angular/core';
import {
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatHeaderCell,
  MatHeaderCellDef, MatHeaderRow, MatHeaderRowDef, MatRow, MatRowDef,
  MatTable, MatTableDataSource,
  MatTextColumn
} from '@angular/material/table';
import {CurrencyPipe} from '@angular/common';
import {MatButton} from '@angular/material/button';
import {RouterLink} from '@angular/router';
import {Product} from '../model/product.model';
import {ProductRepository} from '../service/product.repository';
import {MatPaginator} from '@angular/material/paginator';

@Component({
  selector: 'app-product-table',
  imports: [
    MatTable,
    MatTextColumn,
    MatHeaderCell,
    MatCell,
    CurrencyPipe,
    MatColumnDef,
    MatHeaderCellDef,
    MatCellDef,
    MatButton,
    RouterLink,
    MatHeaderRowDef,
    MatRowDef,
    MatHeaderRow,
    MatRow,
    MatPaginator
  ],
  templateUrl: './product-table.component.html',
  standalone: true,
  styleUrl: './product-table.component.css'
})
export class ProductTableComponent {
  private repository = inject(ProductRepository);
  colsAndRows: string[] = ['id', 'name', 'category', 'price', 'buttons'];
  dataSource = new MatTableDataSource<Product>(this.repository.getProducts());
  differ: IterableDiffer<Product>;

  @ViewChild(MatPaginator)
  paginator? : MatPaginator

  constructor(differs: IterableDiffers) {
    this.differ = differs.find(this.repository.getProducts()).create();
  }

  ngDoCheck() {
    let changes = this.differ?.diff(this.repository.getProducts());
    if (changes != null) {
      this.dataSource.data = this.repository.getProducts();
    }
  }

  ngAfterViewInit() {
    if (this.paginator) {
      this.dataSource.paginator = this.paginator;
    }
  }

  deleteProduct(id: number) {
    this.repository.deleteProduct(id);
  }
}
