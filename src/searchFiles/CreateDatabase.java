/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package searchFiles;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import jcifs.smb.*;

/**
 *
 * @author varun
 */
public class CreateDatabase {
    String ipAddress1, ipAddress2;
    boolean onlan = false;
    BufferedWriter bufferedWriter;
    public CreateDatabase(String ipAddress1, String ipAddress2) {
        this.ipAddress1 = ipAddress1;
        this.ipAddress2 = ipAddress2;
    }
    public void scan() {
        //if user entered range in opposite way
        if(ipAddress1.compareTo(ipAddress2) > 0) {
            String temp = ipAddress1;
            ipAddress1 = ipAddress2;
            ipAddress2 = temp;
        }
        while(true) {
            System.out.println("scanning " + ipAddress1);
            ipAddress1 = getNextIpAddress(ipAddress1);
            Socket socket = new Socket();
            try {
                socket.connect(new InetSocketAddress(ipAddress1, 4000), 1000);
                onlan = true;
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            if(onlan) {
                try {
                    bufferedWriter = new BufferedWriter(new FileWriter("data/" + ipAddress1 + ".dat", false));
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }

                //scan the root of host
                try {
                    SmbFile smbFile = new SmbFile("smb://" + ipAddress1);
                    SmbFile[] list = smbFile.listFiles();

                    for (int i = 0; i < list.length; i++) {
                        writeToFile(list[i], "smb://" + ipAddress1 + "/" + list[i].getName(), bufferedWriter);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println(e.getLocalizedMessage() + " Exception occured in GUI.java\n");
                }

                try {
                    bufferedWriter.close();
                } catch (Exception e) {
                    
                }
            }
            if(ipAddress1.compareTo(ipAddress2) > 0)
                break;
        }
    }
    //these are the possible IPs (Ipv4)
    //10.0.0.1 to 10.255.255.254
    //172.16.0.1 to 172.31.255.254
    //192.168.0.1 to 192.168.255.254
    //thanx to lanhunt
    public String getNextIpAddress(String ip) {
        String[] nums = ip.split("\\.");
        int i = (Integer.parseInt(nums[0]) << 24
                | Integer.parseInt(nums[2]) << 8
                | Integer.parseInt(nums[1]) << 16 | Integer
                .parseInt(nums[3])) + 1;
        // If you wish to skip over .255 addresses.
        if ((byte) i == -1) {
            i++;
        }
        return String.format("%d.%d.%d.%d", i >>> 24 & 0xFF,
                i >> 16 & 0xFF, i >> 8 & 0xFF, i >> 0 & 0xFF);
    }
    private void writeToFile(SmbFile node,String address, BufferedWriter bufferedWriter) {
        try {
            if(node.isDirectory()) {
                SmbFile subNote[] = node.listFiles();
                //1 stands for directory
                bufferedWriter.write("1" + address);
                bufferedWriter.newLine();
                for(SmbFile fileName : subNote) {
                    writeToFile(fileName, address+fileName.getName(), bufferedWriter);
                }
            }
            else {
                bufferedWriter.write("2" + address);
                System.out.println("wrote a new file");
                bufferedWriter.newLine();
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
