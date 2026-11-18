package com.fisiofino.controlador;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;

import com.fisiofino.LectorXML;
import com.fisiofino.database.GestioPacients;
import com.fisiofino.model.MalaltiaPacient;
import com.fisiofino.model.Pacient;
import com.fisiofino.vista.MenuView;

public class Controller {

    private final MenuView vista;
    private final GestioPacients gestiopacients;
    private final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public Controller(MenuView vista, GestioPacients gestiopacients) {
        this.vista = vista;
        this.gestiopacients = gestiopacients;
    }

    public void inicia() {
        vista.mostraBenvinguda();
        boolean sortir = false;

        while (!sortir) {
            vista.MenuOpcions();
            try {
                String opcio = vista.demanaOpcio();
                if (opcio == null) break;

                switch (opcio.toUpperCase()) {
                    case "1": /************** Crida als mètodes que fan la carregarXml(); ************/
                        carregarXml();
                        break;
                    case "2": /************** consultarInfoPersonal(); *******************************/ 
                        consultarInfoPersonal();
                        break;
                    case "3": /************** consultarInfoMedica(); *********************************/   
                        consultarInfoMedica();
                        break;
                    case "4": /************** consultarTots(); ***************************************/         
                        consultarTots(); 
                        break;
                    case "5": /************** modificarEstat(); **************************************/        
                        modificarEstat();
                        break;
                    case "X": sortir = true;                break;
                    default:  vista.mostraError("Opció no vàlida");
                }
            } catch (IOException e) {
                vista.mostraError("Error de lectura: " + e.getMessage());
                sortir = true;
            } catch (Exception e) {
                vista.mostraError(e.toString());
            }
        }
        
        vista.mostraMissatge("Fins aviat!");
    }

    private void carregarXml() throws Exception {
        Path rutaXml = Path.of("pacientes_bluebutton.xml");
        List<Pacient> pacients = LectorXML.llegirPacients(rutaXml);
        List<MalaltiaPacient> malalties = LectorXML.llegirMalalties(rutaXml);

        int pacientsInserits = 0;
        for (Pacient p : pacients) {
            if (p.getDniNie() != null && gestiopacients.findBydniNie(p.getDniNie()) == null) {
                gestiopacients.CreatePatient(p);
                pacientsInserits++;
            }
        }

        int malaltiesInserides = 0;
        for (MalaltiaPacient mp : malalties) {
            if (mp.getSnomedCode() == null) continue;

            if (!gestiopacients.findMalaltia(mp.getSnomedCode())) {
                gestiopacients.CreateMalaltia(mp);
            }

            Pacient pBd = gestiopacients.findBydniNie(mp.getDniPacient());
            if (pBd != null && !gestiopacients.findMalaltiaPacient(mp.getDniPacient(), mp.getSnomedCode(), mp.getStatus())) {
                mp.setIdPacient(pBd.getIdPacient());
                if (gestiopacients.CreateMalaltiaPacient(mp)) {
                    malaltiesInserides++;
                }
            }
        }

        vista.mostraMissatge("Càrrega completada: " + pacientsInserits + " pacients nous i " + malaltiesInserides + " diagnòstics inserits.");
    }

    private void consultarInfoPersonal() throws IOException, SQLException {
        System.out.print("Introdueix el DNI/NIE del pacient: ");
        String dni = reader.readLine();
        Pacient p = gestiopacients.findBydniNie(dni);

        if (p != null) {
            vista.mostraMissatge(p.toString());
        } else {
            vista.mostraError("No s'ha trobat cap pacient amb el DNI/NIE: " + dni);
        }
    }

    private void consultarInfoMedica() throws IOException, SQLException {
        System.out.print("Introdueix el DNI/NIE del pacient: ");
        String dni = reader.readLine();
        Pacient p = gestiopacients.findBydniNie(dni);

        if (p == null) {
            vista.mostraError("No s'ha trobat cap pacient amb el DNI/NIE: " + dni);
            return;
        }

        List<MalaltiaPacient> malalties = gestiopacients.findDiseaseBydniNie(dni);
        if (malalties.isEmpty()) {
            vista.mostraMissatge("El pacient " + p.getNom() + " " + p.getCognoms() + " no té malalties registrades.");
        } else {
            vista.mostraMissatge("Malalties de " + p.getNom() + " " + p.getCognoms() + ":");
            for (MalaltiaPacient mp : malalties) {
                vista.mostraMissatge(" - " + mp.toString());
            }
        }
    }

    private void consultarTots() throws SQLException {
        List<Pacient> pacients = gestiopacients.findAllPacients();
        if (pacients.isEmpty()) {
            vista.mostraMissatge("No hi ha pacients registrats a la base de dades.");
        } else {
            for (Pacient p : pacients) {
                vista.mostraMissatge(p.toString());
            }
        }
    }

    private void modificarEstat() throws IOException, SQLException {
        System.out.print("Introdueix el DNI/NIE del pacient: ");
        String dni = reader.readLine();
        Pacient p = gestiopacients.findBydniNie(dni);

        if (p == null) {
            vista.mostraError("No s'ha trobat cap pacient amb el DNI/NIE: " + dni);
            return;
        }

        System.out.print("Introdueix el codi SNOMED de la malaltia: ");
        String snomed = reader.readLine();
        System.out.print("Introdueix el nou estat de la malaltia: ");
        String nouEstat = reader.readLine();

        boolean actualitzat = gestiopacients.updateStatus(p.getIdPacient(), snomed, nouEstat);
        if (actualitzat) {
            vista.mostraMissatge("Estat actualitzat correctament.");
        } else {
            vista.mostraError("No s'ha pogut actualitzar. Verifica que el pacient tingui assignat el codi SNOMED indicat.");
        }
    }

}
