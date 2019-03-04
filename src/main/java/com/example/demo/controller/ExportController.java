package com.example.demo.controller;

import com.example.demo.entity.Client;
import com.example.demo.entity.Facture;
import com.example.demo.entity.LigneFacture;
import com.example.demo.repository.FactureRepository;
import com.example.demo.service.ClientService;
import com.example.demo.service.FactureService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Controlleur pour réaliser les exports.
 */
@Controller
@RequestMapping("/")
public class ExportController {

    @Autowired
    private ClientService clientService;
    @Autowired
    private FactureService factureService;


    @GetMapping("/clients/csv")
    public void clientsCSV(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"clients.csv\"");
        PrintWriter writer = response.getWriter();
        // TODO
        List<Client> clients = clientService.findAllClients();
        LocalDate now = LocalDate.now();
        for(Client client : clients){
            String nom = client.getNom().replaceAll("\\W","");
            String prenom = client.getPrenom().replaceAll("\\W","");
            writer.println(nom+";"+prenom+";"+client.getDateNaissance().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))+(now.getYear() - client.getDateNaissance().getYear()));
        }
    }

    @GetMapping("/clients/xlsx")
    public void clientsXSLX(HttpServletRequest request, HttpServletResponse response) throws IOException{
        response.setContentType("text/xlsx");
        response.setHeader("Content-Disposition", "attachment; filename=\"clients.xlsx\"");

        OutputStream out = response.getOutputStream();

        List<Client> clients = clientService.findAllClients();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Clients");
        Row headerRow = sheet.createRow(0);
        Cell cellNom = headerRow.createCell(0);
        cellNom.setCellValue("Nom");
        Cell cellPrenom = headerRow.createCell(1);
        cellPrenom.setCellValue("Prénom");
        Cell cellDate = headerRow.createCell(2);
        cellPrenom.setCellValue("Date de naissance");

        int rowNum = 1;

        for(Client client : clients){
            int column = 0;
            Row row = sheet.createRow(rowNum++);
            String nom = client.getNom().replaceAll("\\W","");
            String prenom = client.getPrenom().replaceAll("\\W","");
            row.createCell(column++).setCellValue(nom);
            row.createCell(column++).setCellValue(prenom);
            row.createCell(column++).setCellValue(client.getDateNaissance().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        }

        workbook.write(out);
        workbook.close();
    }

    @GetMapping("/factures/xlsx")
    public void facturesXlsx(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=\"factures.xlsx\"");

        List<Facture> allFactures = factureService.findAllFactures();
        List<Long> clientID = new ArrayList<Long>();

        Workbook workbook = new XSSFWorkbook();

        for (Facture facture : allFactures){

            Long clientId = facture.getClient().getId();
            if(!clientID.contains(clientId)){
                Sheet clientSheet = workbook.createSheet(facture.getClient().getNom() + ' ' + facture.getClient().getPrenom());
                clientID.add(clientId);
            }
            Sheet factureSheet = workbook.createSheet("Facture " + facture.getId().toString());
            Row headerRow = factureSheet.createRow(0);

            Cell cellHeaderNom = headerRow.createCell(0);
            cellHeaderNom.setCellValue("Nom");
            Cell cellHeaderQuantite = headerRow.createCell(1);
            cellHeaderQuantite.setCellValue("Quantite");
            Cell cellHeaderPrixU = headerRow.createCell(2);
            cellHeaderPrixU.setCellValue("Prix Unitaire");
            Cell cellHeaderPrixL = headerRow.createCell(3);
            cellHeaderPrixL.setCellValue("Prix Ligne");

            int rownum = 1;
            Set<LigneFacture> ligneFactureList = facture.getLigneFactures();
            for(LigneFacture ligne : ligneFactureList) {
                int column = 0;
                Row row = factureSheet.createRow(rownum++);
                row.createCell(column++).setCellValue(ligne.getArticle().getLibelle());
                row.createCell(column++).setCellValue(ligne.getQuantite());
                row.createCell(column++).setCellValue(ligne.getArticle().getPrix());
                row.createCell(column++).setCellValue(ligne.getArticle().getPrix() * ligne.getQuantite());
            }
            Row rowTotal = factureSheet.createRow(rownum++);
            Cell total = rowTotal.createCell(0);
            total.setCellValue("Total : ");

            CellStyle style = workbook.createCellStyle();
            Font font = workbook.createFont();

            font.setBold(true);
            font.setColor(IndexedColors.RED.getIndex());
            style.setFont(font);
            style.setBorderTop(BorderStyle.MEDIUM);
            style.setBorderBottom(BorderStyle.MEDIUM);
            style.setBorderLeft(BorderStyle.MEDIUM);
            style.setBorderRight(BorderStyle.MEDIUM);

            Cell totalValue = rowTotal.createCell(1);
            totalValue.setCellValue(facture.getTotal());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            total.setCellStyle(style);
            totalValue.setCellStyle(style);
        }

        workbook.write(response.getOutputStream());
        workbook.close();

    }

    @GetMapping("/clients/{id}/factures/xlsx")
    public void ClientXLSX(HttpServletRequest request, HttpServletResponse response, @PathVariable("id") Long id) throws IOException {
        Client client = clientService.findClientById(id);
        String nom = client.getNom().replaceAll("\\W","");
        String prenom = client.getPrenom().replaceAll("\\W","");

        response.setContentType("text/xlsx");
        response.setHeader("Content-Disposition", "attachment; filename=\""+nom+"_"+prenom+".xlsx\"");

        OutputStream out = response.getOutputStream();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(nom+"_"+prenom);
        Row headerRow = sheet.createRow(0);
        Cell cellNom = headerRow.createCell(0);
        cellNom.setCellValue("Nom");
        Cell cellPrenom = headerRow.createCell(1);
        cellPrenom.setCellValue("Prénom");
        Cell cellDate = headerRow.createCell(2);
        cellPrenom.setCellValue("Date de naissance");

        int column = 0;
        Row row = sheet.createRow(1);
        row.createCell(column++).setCellValue(nom);
        row.createCell(column++).setCellValue(prenom);
        row.createCell(column++).setCellValue(client.getDateNaissance().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        workbook.write(out);
        workbook.close();

    }

}

