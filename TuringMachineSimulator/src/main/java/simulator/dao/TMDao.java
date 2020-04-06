
package simulator.dao;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import simulator.domain.TuringMachine;

public class TMDao {
    
    private File projectFolder;
    
    //creates a project folder
    public void createProjectFolder(){
        projectFolder.mkdir();
    }
    
    //sets the path of the project folder
    public void setProjectFolder(String path){
        projectFolder = new File(path);
    }
    
    //add a file to the project folder specified in projectFile
    public boolean createProjectFile(TuringMachine tm){
        String name = tm.getName();
        File f = new File(projectFolder.getAbsolutePath() + "/" + name + ".txt");
        try {
            boolean b = f.createNewFile();
            if(b){
                FileWriter writer = new FileWriter(f);
                writer.write(name + "\n");
                writer.write(tm.getDesc() + "\n");
                writer.write("-\n");
                writer.write(tm.toStringTable());
                writer.close();
                return true;
            } else {
                return false;
            }
        } catch (IOException ex) {
            Logger.getLogger(TMDao.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
}