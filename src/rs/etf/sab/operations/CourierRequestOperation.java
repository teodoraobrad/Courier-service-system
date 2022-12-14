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
public interface CourierRequestOperation {
    boolean	changeDriverLicenceNumberInCourierRequestâ€‹(java.lang.String userName, java.lang.String licencePlateNumber);	//Promena broj vozacke dozvole
    boolean	deleteCourierRequestâ€‹(java.lang.String userName);	//Brisanje zahteva za postanak kurira
    java.util.List<java.lang.String>	getAllCourierRequests();	//Dohvatanje liste svih zahteva za kurira
    boolean	grantRequestâ€‹(java.lang.String username);	//Odobravanje korisniku da postane kurir
    boolean	insertCourierRequestâ€‹(java.lang.String userName, java.lang.String driverLicenceNumber);	//Kreiranje zahteva za postajanje kurira

}
