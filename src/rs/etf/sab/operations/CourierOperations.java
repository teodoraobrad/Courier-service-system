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
public interface CourierOperations {
    boolean	deleteCourier​(java.lang.String courierUserName);	//Brisanje kurira
    java.util.List<java.lang.String>	getAllCouriers();	//Dohvatanje liste svih kurira
    java.math.BigDecimal	getAverageCourierProfit​(int numberOfDeliveries);	//Dohvatanje prosecne vrednosti profita za kurire sa zadatim brojem isporuka
    java.util.List<java.lang.String>	getCouriersWithStatus​(int statusOfCourier);	//Dohvatanje liste svih kurira sa zadatim statusom
    boolean	insertCourier​(java.lang.String courierUserName, java.lang.String driverLicenceNumber);	//Kreiranje kurira
}
