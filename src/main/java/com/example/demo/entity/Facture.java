package com.example.demo.entity;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Facture {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Client client;

    @OneToMany(mappedBy = "facture")
    private Set<FactureLigne> lignes;

    public double getTotal(){
        double prixTotal = 0;
        for(FactureLigne ligne : lignes){
            double prixLigne = ligne.getArticle().getPrix()*ligne.getQuantite();
            prixTotal+=prixLigne;
        }
        return prixTotal;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<FactureLigne> getLignes() {
        return lignes;
    }

    public void setLignes(Set<FactureLigne> lignes) {
        this.lignes = lignes;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
