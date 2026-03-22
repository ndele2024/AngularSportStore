package com.sportstore.backend.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class CustomerOrder {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long userId;

  @Column(nullable = false)
  private String username;

  @Column(nullable = false)
  private String nom;

  @Column(nullable = false)
  private String prenom;

  @Column(nullable = false)
  private String adresse;

  @Column(nullable = false)
  private String telephone;

  @Column(nullable = false)
  private Instant createdAt;

  @Column(nullable = false, precision = 12, scale = 2)
  private BigDecimal total;

  @Column(nullable = false)
  private Integer itemCount;

  @Column(nullable = false)
  private boolean shipped;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<OrderLine> lines = new ArrayList<>();

  public void replaceLines(List<OrderLine> nextLines) {
    this.lines.clear();
    for (OrderLine line : nextLines) {
      addLine(line);
    }
  }

  public void addLine(OrderLine line) {
    line.setOrder(this);
    this.lines.add(line);
  }

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

  public List<OrderLine> getLines() {
    return lines;
  }

  public void setLines(List<OrderLine> lines) {
    this.lines = lines;
  }
}
