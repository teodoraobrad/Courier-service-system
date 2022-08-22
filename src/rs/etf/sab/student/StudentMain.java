/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import java.math.BigDecimal;
import java.util.List;
import rs.etf.sab.operations.*;
import rs.etf.sab.tests.TestHandler;
import rs.etf.sab.tests.TestRunner;

/**
 *
 * @author Teodora
 */

public class StudentMain {

    public static void main(String[] args) {
        AddressOperations addressOperations = new ot160128_AddressOperations(); // Change this to your implementation.
        CityOperations cityOperations = new ot160128_CityOperations(); // Do it for all classes.
        CourierOperations courierOperations = new ot160128_CourierOperations(); // e.g. = new MyDistrictOperations();
        CourierRequestOperation courierRequestOperation = new ot160128_CourierRequestOperation();
        DriveOperation driveOperation = new ot160128_DriveOperation();
        GeneralOperations generalOperations = new ot160128_GeneralOperations();
        PackageOperations packageOperations = new ot160128_PackageOperations();
        StockroomOperations stockroomOperations = new ot160128_StockroomOperations();
        UserOperations userOperations = new ot160128_UserOperations();
        VehicleOperations vehicleOperations = new ot160128_VehicleOperations();

     //    courierOperations.insertCourier("a", "ab");
     //   cityOperations.deleteCity("ns");
    /*   generalOperations.eraseAll();
        vehicleOperations.insertVehicle("132", 0, BigDecimal.valueOf(12.0), BigDecimal.valueOf(10));
        vehicleOperations.getAllVehichles();
        vehicleOperations.insertVehicle("13s2", 0, BigDecimal.valueOf(12.0), BigDecimal.valueOf(10));
        vehicleOperations.getAllVehichles();
        vehicleOperations.insertVehicle("132", 0, BigDecimal.valueOf(12.0), BigDecimal.valueOf(10));
        vehicleOperations.getAllVehichles();
        vehicleOperations.deleteVehicles("123","132");
        vehicleOperations.getAllVehichles();
      */ 
       TestHandler.createInstance(
                addressOperations,
                cityOperations,
                courierOperations,
                courierRequestOperation,
                driveOperation,
                generalOperations,
                packageOperations,
                stockroomOperations,
                userOperations,
                vehicleOperations);

        TestRunner.runTests();
    /* /*/
       
       
            
   /*   generalOperations.eraseAll();
        int init;
        int id1 = cityOperations.insertCity("Beograd", "11000");
       
         init = addressOperations.insertAddress("Palilula",5, id1, 100, 200);
        int id = cityOperations.insertCity("Nis", "18000");
        id = cityOperations.insertCity("Leskovac", "16000");
        init = addressOperations.insertAddress("Mladenocax",5, id, 3, 200);
        int a=stockroomOperations.insertStockroom(init);
        vehicleOperations.insertVehicle("123", 0, BigDecimal.ONE, BigDecimal.ONE);
        
        List<Integer> allAddresses = addressOperations.getAllAddresses();
        for (Integer allAdd: allAddresses) {
            System.out.println(" "+allAdd);
            
        }addressOperations.deleteAdress(id);
        cityOperations.deleteCity(id1);
        
       
        
        c
         */
    }
}
