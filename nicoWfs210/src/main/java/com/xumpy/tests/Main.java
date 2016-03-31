/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xumpy.tests;

import be.velleman.nicowfs210.Channel;
import be.velleman.nicowfs210.OsciCalculator;
import be.velleman.nicowfs210.RealWFS210;
import be.velleman.nicowfs210.TCPConnector;
import be.velleman.nicowfs210.VoltageDiv;
import org.apache.log4j.Logger;

/**
 *
 * @author nico
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    private static final Logger log = Logger.getLogger(Main.class);
    
    public static void main(String[] args) throws InterruptedException {
        TCPConnector tcpConnector = new TCPConnector("169.254.1.1", 2000);
        tcpConnector.open();
        Thread.sleep(1000);
        tcpConnector.startReceivingPackets();
        
        RealWFS210 realWfs210 = new RealWFS210(tcpConnector);
        realWfs210.requestSettings();
        
        OsciCalculator osci = new OsciCalculator(realWfs210);
        realWfs210.requestCalibrate();
        Thread.sleep(5000);
        
        Channel channel1 = realWfs210.getChannel1();
        channel1.setIsX10(Boolean.TRUE);
        channel1.setVerticalDiv(VoltageDiv.fromOrdinal(1));
        channel1.setInputCoupling(Channel.InputCoupling.DC);
        realWfs210.sendSettings();
        
        Thread.sleep(5000);
        
        log.info("Voltage measured: " + osci.calculateVdc(channel1));
        
        channel1.debug();
        
        tcpConnector.close();
    }
    
}
