/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.velleman.nicowfs210;

/**
 *
 * @author nico
 */
public class StaticBytes {
    public static String toHex(byte[] bytes){
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        
        return sb.toString();
    }
}
