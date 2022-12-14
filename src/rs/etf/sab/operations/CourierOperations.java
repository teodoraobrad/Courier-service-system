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
    boolean	deleteCourierâ€‹(java.lang.String courierUserName);	//Brisanje kurira
    java.util.List<java.lang.String>	getAllCouriers();	//Dohvatanje liste svih kurira
    java.math.BigDecimal	getAverageCourierProfitâ€‹(int numberOfDeliveries);	//Dohvatanje prosecne vrednosti profita za kurire sa zadatim brojem isporuka
    java.util.List<java.lang.String>	getCouriersWithStatusâ€‹(int statusOfCourier);	//Dohvatanje liste svih kurira sa zadatim statusom
    boolean	insertCourierâ€‹(java.lang.String courierUserName, java.lang.String driverLicenceNumber);	//Kreiranje kurira
}
