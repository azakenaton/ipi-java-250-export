package com.example.demo.entity;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * Created by Alexandre on 09/04/2018.
 */
@Entity
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String prenom;

    @Column
    private String nom;

    @Column
    private Integer age;

    @Column
    private String adresse;

    @Column
    private LocalDate dateNaissance;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Integer getAge() {
        LocalDate now = LocalDate.now();
        return (now.getYear() - this.getDateNaissance().getYear());
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }
}
