package com.sportstore.backend.dto;

public class UpdateUserRequestDto {
  private String role;
  private String nom;
  private String prenom;
  private String adresse;
  private String telephone;
  private String username;
  private String password;
  private StoredCartDto cart;

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
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

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public StoredCartDto getCart() {
    return cart;
  }

  public void setCart(StoredCartDto cart) {
    this.cart = cart;
  }
}
