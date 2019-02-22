package com.example.demo.service;

import com.example.demo.entity.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;

/**
 * Classe permettant d'insérer des données dans l'application.
 */
@Service
@Transactional
public class InitData implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private EntityManager em;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        insertTestData();
    }

    private void insertTestData() {
        Client client1 = newClient("PETR;ILLO", "Alexandre","Lyon", LocalDate.of(1989,12,7));
        em.persist(client1);

        Client client2 = newClient("Dup_ont", "Jérome","Bordeaux",LocalDate.now());
        em.persist(client2);

        Client client3 = newClient("Hetsch", "Yohan","Roanne",LocalDate.of(1989,12,7));
        em.persist(client3);

        Client client4 = newClient("Fohrer", "Timothé","Quelque part dans les montagnes",LocalDate.of(1989,12,7));
        em.persist(client4);

        Client client5 = newClient("Mazoyer", "Laurent","Lyon",LocalDate.of(1989,12,7));
        em.persist(client5);

    }

    private Client newClient(String nom, String prenom, String adresse, LocalDate date) {
        Client client = new Client();
        client.setNom(nom);
        client.setPrenom(prenom);
        client.setAdresse(adresse);
        client.setDateNaissance(date);
        return client;
    }
}
