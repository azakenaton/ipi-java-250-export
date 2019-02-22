package com.example.demo.controller;

import com.example.demo.entity.Client;
import com.example.demo.service.ClientService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.*;

/**
 * Controlleur pour réaliser les exports.
 */
@Controller
@RequestMapping("/")
public class ExportController {

    @Autowired
    private ClientService clientService;

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
            writer.println(nom+";"+prenom+";"+client.getDateNaissance().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))+";"+client.getAge());
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
