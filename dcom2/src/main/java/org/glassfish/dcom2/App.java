package org.glassfish.dcom2;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import org.jinterop.dcom.common.JIDefaultAuthInfoImpl;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) {
        System.out.println("Hello World!");

        App app = new App();
        app.foo();
    }

    public void foo() {
        try {
            NtlmPasswordAuthentication authOracle = createSmbAuth("wnevins-lnr", "wnevins", password);
            NtlmPasswordAuthentication authBnevins = createSmbAuth("sony", "bnevins", password);
            System.out.println("DUMP: " + authOracle);
            System.out.println("DUMP: " + authBnevins);

            //SmbFile remoteRoot = new SmbFile("smb://" + name + "/" + path.replace('\\', '/').replace(':', '$')+"/",createSmbAuth());
            //SmbFile remoteRoot = new SmbFile("smb://wnevins-lnr/C$/temp/test", auth);
            //System.out.println("FILE: " + remoteRoot);



            SmbFile q = new SmbFile("smb://sony/C$/", authBnevins);
            System.out.println(Arrays.toString(q.list()));
            SmbFile oracle = new SmbFile("smb://wnevins-lnr/C$/", authOracle);
            System.out.println("Heeeeeeere's Oracle!!");
            System.out.println(Arrays.toString(oracle.list()));

            WindowsRemoteFileSystem wrfs =
                    new WindowsRemoteFileSystem("wnevins-lnr", authOracle);

            WinFile wf = new WinFile(wrfs, "C:/temp", true);
            WinFile wf2 = new WinFile(wrfs, "C:/temp/notexists", true);
            WinFile foo = new WinFile(wrfs, "C:/temp/foo.txt", true);
            foo.copyTo(wf2);



            System.out.println("WF: " + Arrays.toString(wf.list()));
            System.out.println("wf exists, wf2 exists: " + wf.exists() + wf2.exists());
            //wf2.createNewFile();
            System.out.println("wf exists, wf2 exists: " + wf.exists() + wf2.exists());
        }
        catch (UnknownHostException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }        catch (SmbException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (MalformedURLException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }



    }

    private NtlmPasswordAuthentication createSmbAuth(String host, String user, String pw) {
        JIDefaultAuthInfoImpl auth = new JIDefaultAuthInfoImpl(host, user, pw);
        return new NtlmPasswordAuthentication(auth.getDomain(), auth.getUserName(), auth.getPassword());
    }
    /**
     * taken from Hudson
     * @return
     *
     * private JIDefaultAuthInfoImpl createAuth() {
    String[] tokens = userName.split("\\\\");
    if(tokens.length==2)
    return new JIDefaultAuthInfoImpl(tokens[0], tokens[1], Secret.toString(password));
    return new JIDefaultAuthInfoImpl("", userName, Secret.toString(password));
    }*/

    // I don't want my password going into subversion!  Put it into a
    // properties file instead.
    final static String password;
    static {
        String pw = null;

        try {
            Properties p = new Properties();
            URL url = App.class.getResource("/password.properties");
            System.out.println(url);
            p.load(App.class.getResourceAsStream("/password.properties"));
            pw = p.getProperty("password");
        }
        catch (Exception ex) {
            System.out.println(ex);
        }
        password = pw;
    }

}
