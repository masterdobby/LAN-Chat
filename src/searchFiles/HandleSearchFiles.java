/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package searchFiles;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/**
 *
 * @author varun
 */
public class HandleSearchFiles extends SwingWorker<Void, Void> {
    
    @Override
    public Void doInBackground() throws Exception {
        new SearchWindow().main(new String[]{});
        return null;
    }
    
    
}
