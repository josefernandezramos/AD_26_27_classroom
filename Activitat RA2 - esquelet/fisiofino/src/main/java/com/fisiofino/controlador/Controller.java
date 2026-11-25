package com.fisiofino.controlador;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.fisiofino.LectorXML;
import com.fisiofino.database.GestioPacients;
import com.fisiofino.model.MalaltiaPacient;
import com.fisiofino.model.Pacient;
import com.fisiofino.vista.MenuView;

public class Controller {

    private final MenuView vista;
    private final GestioPacients gestiopacients;

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
                    case "1":
                        carregarXml();
                        break;
                    case "2":
                        consultarInfoPersonal();
                        break;
                    case "3":
                        consultarInfoMedica();
                        break;
                    case "4":
                        consultarTots();
                        break;
                    case "5":
                        modificarEstat();
                        break;
                    case "X":
                        sortir = true;
                        break;
                    default:
                        vista.mostraError("Opció no vàlida");
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

    private void carregarXml() {
        Path rutaXml = Path.of("pacientes_bluebutton.xml");

        try {
            // 1. Pacients y sus comprobaciones/inserciones
            List<Pacient> pacients = LectorXML.llegirPacients(rutaXml);
            int pacientsInserits = 0;
            for (Pacient p : pacients) {
                if (p.getDniNie() != null && gestiopacients.findBydniNie(p.getDniNie()) == null) {
                    gestiopacients.CreatePatient(p);
                    pacientsInserits++;
                }
            }

            // 2. Malalties y sus comprobaciones/inserciones
            List<MalaltiaPacient> malalties = LectorXML.llegirMalalties(rutaXml);
            int malaltiesInserides = 0;
            for (MalaltiaPacient mp : malalties) {
                if (mp.getSnomedCode() == null) continue;

                if (!gestiopacients.findMalaltia(mp.getSnomedCode())) {
                    gestiopacients.CreateMalaltia(mp);
                }

                Pacient pBd = gestiopacients.findBydniNie(mp.getDniPacient());
                if (pBd != null && !gestiopacients.findMalaltiaPacient(mp.getDniPacient(), mp.getSnomedCode(), "Activo")) {
                    mp.setIdPacient(pBd.getIdPacient());
                    if (gestiopacients.CreateMalaltiaPacient(mp)) {
                        malaltiesInserides++;
                    }
                }
            }

            vista.mostraMissatge("Càrrega completada: " + pacientsInserits + " pacients nous i " + malaltiesInserides + " diagnòstics inserits.");

        } catch (IOException ioe) {
            System.out.println("Error d'entrada/sortida amb l'arxiu XML: " + ioe.getMessage());
        } catch (ParserConfigurationException pce) {
            System.out.println("Error de configuració del parser XML: " + pce.getMessage());
        } catch (SAXException sa) {
            System.out.println("Error de format en l'arxiu XML: " + sa.getMessage());
        } catch (Exception e) {
            System.out.println("Error inesperat: " + e.getMessage());
        }
    }


//=============================================================================================00000
    private void consultarInfoPersonal() throws IOException, SQLException {
        String dni = vista.demana("Introdueix el DNI/NIE del pacient: ");
        if (!esDniNieValid(dni)) {
            vista.mostraError("El format del DNI/NIE no és vàlid.");
            return;
        }

        Pacient p = gestiopacients.findBydniNie(dni.toUpperCase());
        if (p != null) {
            vista.mostraPacient(p);
        } else {
            vista.mostraError("No s'ha trobat cap pacient amb el DNI/NIE: " + dni);
        }
    }

    private void consultarInfoMedica() throws IOException, SQLException {
        String dni = vista.demana("Introdueix el DNI/NIE del pacient: ");
        if (!esDniNieValid(dni)) {
            vista.mostraError("El format del DNI/NIE no és vàlid.");
            return;
        }

        List<MalaltiaPacient> malalties = gestiopacients.findDiseaseBydniNie(dni.toUpperCase());
        if (malalties.isEmpty()) {
            vista.mostraMissatge("No s'han trobat malalties registrades per al DNI/NIE: " + dni);
        } else {
            vista.mostraMalaltiesPacient(malalties);
        }
    }

    private void consultarTots() throws SQLException {
        List<Pacient> pacients = gestiopacients.findAllPacients();
        if (pacients.isEmpty()) {
            vista.mostraMissatge("No hi ha pacients registrats a la base de dades.");
        } else {
            vista.mostraPacients(pacients);
        }
    }

    private void modificarEstat() throws IOException, SQLException {
        String dni = vista.demana("Introdueix el DNI/NIE del pacient: ");
        if (!esDniNieValid(dni)) {
            vista.mostraError("El format del DNI/NIE no és vàlid.");
            return;
        }

        Pacient p = gestiopacients.findBydniNie(dni.toUpperCase());
        if (p == null) {
            vista.mostraError("No s'ha trobat cap pacient amb el DNI/NIE: " + dni);
            return;
        }

        String snomed = vista.demana("Introdueix el codi SNOMED de la malaltia: ");
        String nouEstat = vista.demana("Introdueix el nou estat de la malaltia: ");

        if (snomed == null || snomed.isBlank() || nouEstat == null || nouEstat.isBlank()) {
            vista.mostraError("El codi SNOMED i l'estat no poden estar buits.");
            return;
        }

        boolean actualitzat = gestiopacients.updateStatus(p.getIdPacient(), snomed, nouEstat);
        if (actualitzat) {
            vista.mostraMissatge("Estat actualitzat correctament.");
        } else {
            vista.mostraError("No s'ha pogut actualitzar. Comprova que el pacient tingui aquesta malaltia assignada.");
        }
    }

    private boolean esDniNieValid(String dni) {
        if (dni == null) return false;
        return dni.toUpperCase().matches("^([0-9]{8}[A-Z]|[XYZ][0-9]{7}[A-Z])$");
    }
}
