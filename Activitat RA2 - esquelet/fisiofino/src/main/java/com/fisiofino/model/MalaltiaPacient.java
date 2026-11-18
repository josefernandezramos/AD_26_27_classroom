package com.fisiofino.model;

public class MalaltiaPacient {

/****** ATRIBUTS DE CLASSE **********************************/
    private int idDiagnosis;
    private int idPacient;
    private String dniPacient;
    private String snomedCode;
    private String nomMalaltia;
    private String status;
    private String tractament;

    /****** CONSTRUCTORS ************************************/
    public MalaltiaPacient() {
    }

    public MalaltiaPacient(int idPacient, String dniPacient, String snomedCode,
                           String nomMalaltia, String status, String tractament) {
        this.idPacient = idPacient;
        this.dniPacient = dniPacient;
        this.snomedCode = snomedCode;
        this.nomMalaltia = nomMalaltia;
        this.status = status;
        this.tractament = tractament;
    }

    /****** GETTERS I SETTERS *******************************/
    public int getIdDiagnosis() { return idDiagnosis; }
    public void setIdDiagnosis(int idDiagnosis) { this.idDiagnosis = idDiagnosis; }

    public int getIdPacient() { return idPacient; }
    public void setIdPacient(int idPacient) { this.idPacient = idPacient; }

    public String getDniPacient() { return dniPacient; }
    public void setDniPacient(String dniPacient) { this.dniPacient = dniPacient; }

    public String getSnomedCode() { return snomedCode; }
    public void setSnomedCode(String snomedCode) { this.snomedCode = snomedCode; }

    public String getNomMalaltia() { return nomMalaltia; }
    public void setNomMalaltia(String nomMalaltia) { this.nomMalaltia = nomMalaltia; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getTractament() { return tractament; }
    public void setTractament(String tractament) { this.tractament = tractament; }

    /****** TO STRING ***************************************/
    @Override
    public String toString() {
        return "Malaltia [SNOMED=" + snomedCode + ", Nom=" + nomMalaltia +
               ", Estat=" + status + ", Tractament=" + tractament + "]";
    }
}

