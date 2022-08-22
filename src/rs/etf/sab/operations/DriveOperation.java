/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.operations;

import java.util.List;

/**
 *
 * @author Teodora
 */
public interface DriveOperation {
    List<java.lang.Integer>	getPackagesInVehicle​(java.lang.String courierUsername);	//Dohvatanje liste paketa koji su trenutno u vozilu koje kurir koristi
    int	nextStop​(java.lang.String courierUsername);	//Dolazak vozila na sledecu lokaciju u okviru planirane rute i isporucivanje ili preuzimanje paketa sa te adrese.
    boolean	planingDrive​(java.lang.String courierUsername);	//Pravljenje plana za rutu po kojoj ce se vrsiti isporuka paketa.
}
