/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.operations;

/**
 *
 * @author Teodora
 */
public interface VehicleOperations {
    boolean	changeCapacity​(java.lang.String licensePlateNumber, java.math.BigDecimal capacity);	//Promena kapaciteta Promena kapaciteta je moguca kada se vozilo nalazi u magacinu.
    boolean	changeConsumption​(java.lang.String licensePlateNumber, java.math.BigDecimal fuelConsumption);	//Promena potrosnje Promena potrosnje je moguca kada se vozilo nalazi u magacinu.
    boolean	changeFuelType​(java.lang.String licensePlateNumber, int fuelType);	//Promena tipa goriva Promena tipa goriva je moguca kada se vozilo nalazi u magacinu.
    int	deleteVehicles​(java.lang.String... licencePlateNumbers);	//Brisanje vozila sa zadatim registarskim brojevima
    java.util.List<java.lang.String>	getAllVehichles();	//Dohvatanje liste svih vozila
    boolean	insertVehicle​(java.lang.String licencePlateNumber, int fuelType, java.math.BigDecimal fuelConsumtion, java.math.BigDecimal capacity);	//Dodavanje vozila u sistem
    boolean	parkVehicle​(java.lang.String licencePlateNumbers, int idStockroom);	//Smestanje vozila u magacin Smestanje vozila u magacin je moguce ukoliko voznja koja koristi to vozilo nije u toku.
}
