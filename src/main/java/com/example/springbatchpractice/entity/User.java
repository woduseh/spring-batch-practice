package com.example.springbatchpractice.entity;

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
  private Long Id;

  @Column(name = "name")
  private String Name;

  @Column(name = "money")
  private Long Money;

  @Builder
  public User(Long Id, String Name, Long Money) {
    this.Id = Id;
    this.Name = Name;
    this.Money = Money;
  }

  @Builder
  public User(String Name, Long Money) {
    this.Name = Name;
    this.Money = Money;
  }

  public void updateMoney(Long Money) {
    this.Money = Money;
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
    return Id != null && Objects.equals(Id, user.Id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
