package com.sportstore.backend.dto;

import java.math.BigDecimal;
import java.time.Instant;

public class OrderDto {
  private Long id;
  private Long userId;
  private String username;
  private String nom;
  private String prenom;
  private String adresse;
  private String telephone;
  private Instant createdAt;
  private BigDecimal total;
  private Integer itemCount;
  private boolean shipped;
  private StoredCartDto cart;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getNom() {
    return nom;
  }

  public void setNom(String nom) {
    this.nom = nom;
  }

  public String getPrenom() {
    return prenom;
  }

  public void setPrenom(String prenom) {
    this.prenom = prenom;
  }

  public String getAdresse() {
    return adresse;
  }

  public void setAdresse(String adresse) {
    this.adresse = adresse;
  }

  public String getTelephone() {
    return telephone;
  }

  public void setTelephone(String telephone) {
    this.telephone = telephone;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }

  public BigDecimal getTotal() {
    return total;
  }

  public void setTotal(BigDecimal total) {
    this.total = total;
  }

  public Integer getItemCount() {
    return itemCount;
  }

  public void setItemCount(Integer itemCount) {
    this.itemCount = itemCount;
  }

  public boolean isShipped() {
    return shipped;
  }

  public void setShipped(boolean shipped) {
    this.shipped = shipped;
  }

  public StoredCartDto getCart() {
    return cart;
  }

  public void setCart(StoredCartDto cart) {
    this.cart = cart;
  }
}
