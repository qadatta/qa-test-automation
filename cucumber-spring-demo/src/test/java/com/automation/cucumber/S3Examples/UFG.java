package com.automation.cucumber.S3Examples;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
public class UFG {

    List<String> fileList;
    public String ZIP_FILE_URL;
    public String INPUT_ZIP_FILE;
    public String OUTPUT_FOLDER;

    public static void copyInputStream(InputStream in, OutputStream out)
            throws IOException {
        byte[] buffer = new byte[1024];
        int len;
        while ((len = in.read(buffer)) >= 0) {
            out.write(buffer, 0, len);
        }
        in.close();
        out.close();
    }

    public void downloadFile() {
        try {
            long startTime = System.currentTimeMillis();

            URL url = new URL(this.ZIP_FILE_URL);

            url.openConnection();
            InputStream reader = url.openStream();

            FileOutputStream writer = new FileOutputStream(this.INPUT_ZIP_FILE);
            byte[] buffer = new byte[102400];
            int totalBytesRead = 0;
            int bytesRead = 0;

            System.out.println("Reading ZIP file 20KB blocks at a time.\n");

            while ((bytesRead = reader.read(buffer)) > 0) {
                writer.write(buffer, 0, bytesRead);
                buffer = new byte[102400];
                totalBytesRead += bytesRead;
            }

            long endTime = System.currentTimeMillis();

            System.out.println("Done. " + new Integer(totalBytesRead).toString() + " bytes read (" + new Long(endTime - startTime).toString() + " millseconds).\n");
            writer.close();
            reader.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void unZipIt() {
        try {

            ZipFile zipFile = new ZipFile(INPUT_ZIP_FILE);

            Enumeration zipEntries = zipFile.entries();

            File OUTFILEFOLD = new File(OUTPUT_FOLDER);
            if (!OUTFILEFOLD.exists()) {
                OUTFILEFOLD.mkdir();
            }
            String OUTDIR = OUTPUT_FOLDER + File.separator;
            while (zipEntries.hasMoreElements()) {
                ZipEntry zipEntry = (ZipEntry) zipEntries.nextElement();

                if (zipEntry.isDirectory()) {
                    System.out.println("      Extracting directory: " + OUTDIR + zipEntry.getName());

                    new File(OUTDIR + zipEntry.getName()).mkdir();
                    continue;
                }

                System.out.println("       Extracting file: " + OUTDIR + zipEntry.getName());

                copyInputStream(zipFile.getInputStream(zipEntry), new BufferedOutputStream(new FileOutputStream(OUTDIR + zipEntry.getName())));
            }

            zipFile.close();
        } catch (IOException ioe) {
            System.err.println("Unhandled exception:");
            ioe.printStackTrace();
            return;
        }
    }

    public static void main(String[] args) {
        UFG csg = new UFG();
        System.out.println("Please provide the S3 file");
      csg.ZIP_FILE_URL = "https://fnma-ppafo0-prodpubex-us-east-1-files.s3.amazonaws.com/cas/response/96981159-afe1-4b31-b7c2-d669864578f5.zip?X-Amz-Security-Token=IQoJb3JpZ2luX2VjEA0aCXVzLWVhc3QtMSJHMEUCIQCK2FMz7mkooeCLnvw%2BSlCVhXdXc49tdf4IT7UdDEXTNQIgS8CxgrvJTRT7YdKKcC4MuA3WfxA6pvy%2BhLg1TMLt7o4qtAMIJhACGgwwOTAwNDM0NzE5MjciDCIA8TqjH9U3IOju%2BCqRAwHWkvQKo%2FcV%2BULCYAEVriiB8K4nBuBiNKjLDDFut6VTa%2BcZXLU55EP7RlVEiKbgyPUT7n8%2FNp1UceteDAieb6URY2LOKxBtJmbI54ZTvPyS9hr7OIL7KU1%2B3BHmuRJkOO9W0ev7XeeSRgC23gUpeyLIRyFWFBw9zxgu%2BsKdbsr%2FnCjjVjjWgIRBpmdzRNuwLoT46ceTwhj8AKisG5O%2FbnOHfnouNq8Waf1B1Fkc7hnS9WVb2e0xY8YFefzdoXnvFZZHaSX8lCJtehIuqCbuYVorPddp%2B1syMblintDay3WZHcU99GCwnu497V91ShjkG2KPYvueyYM8MBG76kuLwi44yDJn9md05ZKqF3v4tVUQM7t4WdWzcw%2BTHatQs7hOEqMMqV0Biyi4PviagY%2FVtI42W%2B%2F9TkP4xWAdF8c0SJvz6aIXxeluzt62L6L6sbBGN5Enzy9TRwGt0%2FKGYhhDRfSNd5CyysFOOrDDBGYhQPnjgfq3ntr306bOel5wDPFuZoQyqvwZleHTXAjSh6OU5uUaMNmexfQFOusBQt%2BveddTwEvzoIVMscfDVNyasE8UsUEPCdWbXr6r44Gv7zWlxrXutsKz7KqPOvWuiRsyBNGvboEc%2F2VQL9X%2BOhHnhTSLSlJ6eHcy%2BuScl27iCXXZlGfH5Hmtp0PlY1%2BusMJwO%2FSZN1SsLQd7KHFx8uQqJsLNVxqHhggx449xVh2uupGXJOI%2FJyUUmoDzeZNs7ZTLQxCZgJh2jzVHMju411fwytmatEaTXqlq2wDXLYPb5EDxpmk26MSTPN2RBA%2FsAAtW4UTEtsGY2VLt6HyDYi3bo1CtcNS%2FfSnBfq8Uk7TR7b38mSMiBOC0ow%3D%3D&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20200411T050651Z&X-Amz-SignedHeaders=host&X-Amz-Expires=604799&X-Amz-Credential=ASIARJ5YCLA32WIJXUEI%2F20200411%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Signature=3a1467b47f231225db4b4c129e38d04b4a4b45267ee16f3ce415a225078c7481";
      csg.downloadFile();
//        Scanner sc = new Scanner(System.in);
//        csg.ZIP_FILE_URL = sc.nextLine();
//        System.out.println("Please provide the destination with filename");
//        csg.INPUT_ZIP_FILE = sc.nextLine();
//        csg.downloadFile();
//        System.out.println("Provide OUTPUT folder it should unzip to");
//        csg.OUTPUT_FOLDER = sc.nextLine();
//        csg.unZipIt();
//        sc.close();
    	
    	
    }
}