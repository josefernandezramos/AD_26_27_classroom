package com.fisiofino.model;

/*
 * Model (la "M" de MVC): representa un pacient de la clínica.
 * Cada objecte equival a una fila de la taula patient de la base de dades.
 *
 * És una classe de dades simple: té atributs, constructor, getters i toString, però cap
 * lògica ni accés a la base de dades (d'això s'ocupa GestioPacients).
 */


import java.time.LocalDate;


public class Pacient {

/****** ATRIBUTS DE CLASSE ******************************/
    private int idPacient;
    private String nom;
    private String cognoms;
    private String dniNie;
    private LocalDate dataNaixement;
    private String genere;
    private String adreca;
    private String ciutat;
    private String codiPostal;
    private String pais;
    private String telefon;
    private String mail;
/****** CONSTRUCTORS ************************************/
    public Pacient() {
    }

    public Pacient(int idPacient, String nom, String cognoms, String dniNie, LocalDate dataNaixement,
                   String genere, String adreca, String ciutat, String codiPostal, String pais,
                   String telefon, String mail) {
        this.idPacient = idPacient;
        this.nom = nom;
        this.cognoms = cognoms;
        this.dniNie = dniNie;
        this.dataNaixement = dataNaixement;
        this.genere = genere;
        this.adreca = adreca;
        this.ciutat = ciutat;
        this.codiPostal = codiPostal;
        this.pais = pais;
        this.telefon = telefon;
        this.mail = mail;
    }
/****** GETTERS I SETTERS *******************************/
    public int getIdPacient() { return idPacient; }
    public void setIdPacient(int idPacient) { this.idPacient = idPacient; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getCognoms() { return cognoms; }
    public void setCognoms(String cognoms) { this.cognoms = cognoms; }

    public String getDniNie() { return dniNie; }
    public void setDniNie(String dniNie) { this.dniNie = dniNie; }

    public LocalDate getDataNaixement() { return dataNaixement; }
    public void setDataNaixement(LocalDate dataNaixement) { this.dataNaixement = dataNaixement; }

    public String getGenere() { return genere; }
    public void setGenere(String genere) { this.genere = genere; }

    public String getAdreca() { return adreca; }
    public void setAdreca(String adreca) { this.adreca = adreca; }

    public String getCiutat() { return ciutat; }
    public void setCiutat(String ciutat) { this.ciutat = ciutat; }

    public String getCodiPostal() { return codiPostal; }
    public void setCodiPostal(String codiPostal) { this.codiPostal = codiPostal; }

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }

    public String getTelefon() { return telefon; }
    public void setTelefon(String telefon) { this.telefon = telefon; }

    public String getMail() { return mail; }
    public void setMail(String mail) { this.mail = mail; }
/****** TO STRING ***************************************/
    @Override
    public String toString() {
        return "Pacient [ID=" + idPacient + ", DNI/NIE=" + dniNie + ", Nom=" + nom + " " + cognoms +
               ", Naixement=" + dataNaixement + ", Gènere=" + genere + ", Adreça=" + adreca +
               ", Ciutat=" + ciutat + " (" + codiPostal + "), País=" + pais +
               ", Tel=" + telefon + ", Email=" + mail + "]";
    }
}
