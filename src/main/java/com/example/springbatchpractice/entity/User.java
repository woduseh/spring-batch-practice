package com.example.springbatchpractice.entity;

import java.time.LocalDate;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
public class User {

  @Id
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name")
  private String name;

  @Column(name = "money")
  private Long money;

  @Column(name = "delete_date")
  private LocalDate deleteDate;

  @Column(name = "delete_res")
  private LocalDate deleteRes;

  @Builder
  public User(Long Id, String Name, Long Money, LocalDate deleteDate, LocalDate deleteRes) {
    this.id = Id;
    this.name = Name;
    this.money = Money;
    this.deleteRes = deleteRes;
    this.deleteDate = deleteDate;
  }

  @Builder
  public User(String Name, Long Money, LocalDate deleteRes, LocalDate deleteDate) {
    this.name = Name;
    this.money = Money;
    this.deleteRes = deleteRes;
    this.deleteDate = deleteDate;
  }

  public void updateMoney(Long Money) {
    this.money = Money;
  }

  public void deleteUser() {
    this.deleteDate = LocalDate.now();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    User user = (User) o;
    return id != null && Objects.equals(id, user.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
