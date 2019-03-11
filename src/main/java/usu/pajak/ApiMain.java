package usu.pajak;

import com.google.gson.Gson;
import com.mongodb.BasicDBList;
import spark.*;
import usu.pajak.model.UserPajak;
import usu.pajak.services.ApiRka;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.nio.file.*;
import static spark.Spark.*;
import java.io.File;
import java.io.IOException;
//import static spark.debug.DebugScreen.*;
import java.io.InputStream;
import java.util.Collection;

public class ApiMain {
    public static void main(String[] args){
        String test = "[{\"_id\":{\"$oid\":\"5c7615849af78c5c2c95aee7\"},\"className\":\"usu.pajak.model.UserPajak\",\"id_user\":\"2519\",\"npwp\":\"884128646121000\",\"front_degree\":\"\",\"full_name\":\"Siti Jubaidah\",\"behind_degree\":\", A.Md\",\"nip_simsdm\":\"\",\"nip_gpp\":\"196703242007012023\",\"pendapatan\":[{\"activity_id\":\"apbn\",\"request_id\":\"apbn\",\"salary_id\":\"apbn\",\"type_id\":\"apbn\",\"type_title\":\"Gaji APBN\",\"unit_id\":\"41\",\"unit_name\":\"PSI\",\"bulan\":\"1\",\"tahun\":\"2019\",\"gjpokok\":\"3349800\",\"tjistri\":\"334980\",\"tjanak\":\"133992\",\"tjupns\":\"185000\",\"tjstruk\":\"0\",\"tjfungs\":\"0\",\"tjdaerah\":\"0\",\"tjpencil\":\"0\",\"tjlain\":\"0\",\"tjkompen\":\"0\",\"pembul\":\"25\",\"tjberas\":\"289680\",\"tjpph\":\"0.0\",\"bkn-potpfk2\":\"0\",\"bkn-potpfk10\":\"381877\",\"bkn-potpph\":\"0\",\"bkn-potswrum\":\"0\",\"bkn-potkelbtj\":\"0\",\"bkn-potlain\":\"0\",\"bkn-pottabrum\":\"7000\",\"pot_jabatan\":\"214674.000\",\"pot_pensiun\":\"181392.000\",\"netto_TakeHomePay\":\"3904600\",\"netto_pendapatan\":\"3897411\",\"ptkp_sebulan\":\"4500000\",\"pkp_sebulan\":\"0\",\"sisa_ptkp_sebulan\":\"602589\",\"pph21\":[{\"tarif\":\"0.05\",\"pkp\":\"0.00\",\"hasil\":\"0.00\"}],\"pph21_layer\":\"0\",\"pph21_reminder\":\"4166666.67\",\"update_time\":\"2019-02-27 11:43:48.586\"},{\"activity_id\":\"84674\",\"activity_title\":\"Remunerasi Gol. III/a\",\"request_id\":\"1028\",\"salary_id\":\"17710\",\"type_id\":\"18\",\"type_title\":\"Tendik PNS (Non Struktural)\",\"unit_id\":\"41\",\"unit_name\":\"Pusat Sistem Informasi\",\"bulan\":\"1\",\"tahun\":\"2019\",\"p1\":\"220000\",\"p2\":\"390000\",\"p3\":\"1250000\",\"pot_jabatan\":\"93000.000\",\"pot_pensiun\":\"0.00\",\"netto_pendapatan\":\"1767000\",\"ptkp_sebulan\":\"4500000\",\"pkp_sebulan\":\"1164411\",\"sisa_ptkp_sebulan\":\"0\",\"pph21\":[{\"tarif\":\"0.05\",\"pkp\":\"1164411.000\",\"hasil\":\"58220.550\"}],\"pph21_layer\":\"0\",\"pph21_reminder\":\"3002255.670\",\"netto_TakeHomePay\":\"1801779\",\"update_time\":\"2019-02-27 14:14:28.9\"},{\"activity_id\":\"apbn\",\"request_id\":\"apbn\",\"salary_id\":\"apbn\",\"type_id\":\"apbn\",\"type_title\":\"Gaji APBN\",\"unit_id\":\"41\",\"unit_name\":\"PSI\",\"bulan\":\"2\",\"tahun\":\"2019\",\"gjpokok\":\"3349800\",\"tjistri\":\"334980\",\"tjanak\":\"133992\",\"tjupns\":\"185000\",\"tjstruk\":\"0\",\"tjfungs\":\"0\",\"tjdaerah\":\"0\",\"tjpencil\":\"0\",\"tjlain\":\"0\",\"tjkompen\":\"0\",\"pembul\":\"25\",\"tjberas\":\"289680\",\"tjpph\":\"0.0\",\"bkn-potpfk2\":\"0\",\"bkn-potpfk10\":\"381877\",\"bkn-potpph\":\"0\",\"bkn-potswrum\":\"0\",\"bkn-potkelbtj\":\"0\",\"bkn-potlain\":\"0\",\"bkn-pottabrum\":\"7000\",\"pot_jabatan\":\"214674.000\",\"pot_pensiun\":\"181392.000\",\"netto_TakeHomePay\":\"3904600\",\"netto_pendapatan\":\"3897411\",\"ptkp_sebulan\":\"4500000\",\"pkp_sebulan\":\"0\",\"sisa_ptkp_sebulan\":\"602589\",\"pph21\":[{\"tarif\":\"0.05\",\"pkp\":\"0.00\",\"hasil\":\"0.00\"}],\"pph21_layer\":\"0\",\"pph21_reminder\":\"4166666.670\",\"update_time\":\"2019-02-27 15:01:15.172\"},{\"activity_id\":\"84674\",\"activity_title\":\"Remunerasi Gol. III/a\",\"request_id\":\"1704\",\"salary_id\":\"47650\",\"type_id\":\"18\",\"type_title\":\"Tendik PNS (Non Struktural)\",\"unit_id\":\"41\",\"unit_name\":\"Pusat Sistem Informasi\",\"bulan\":\"2\",\"tahun\":\"2019\",\"p1\":\"220000\",\"p2\":\"390000\",\"p3\":\"1250000\",\"pot_jabatan\":\"0.00\",\"pot_pensiun\":\"0.00\",\"netto_pendapatan\":\"1860000\",\"ptkp_sebulan\":\"4500000\",\"pkp_sebulan\":\"1257411\",\"sisa_ptkp_sebulan\":\"0\",\"pph21\":[{\"tarif\":\"0.05\",\"pkp\":\"1257411.000\",\"hasil\":\"62870.550\"}],\"pph21_layer\":\"0\",\"pph21_reminder\":\"2909255.670\",\"netto_TakeHomePay\":\"1797129\",\"update_time\":\"2019-02-27 16:28:44.816\"}],\"netto_pendapatan_setahun\":\"11421822\",\"ptkp_setahun\":\"54000000\",\"sisa_ptkp\":\"42578178\",\"total_pkp\":\"2421822\",\"total_pph21_usu\":\"121091.100\",\"total_pph21_pribadi\":\"0\",\"timestamp\":\"2019-02-27 16:28:44.816\"}\n" +
                "{\"_id\":{\"$oid\":\"5c7615889af78c5c2c95aef7\"},\"className\":\"usu.pajak.model.UserPajak\",\"id_user\":\"2528\",\"npwp\":\"359091576121000\",\"front_degree\":\"\",\"full_name\":\"M. Azhar\",\"behind_degree\":\"\",\"nip_simsdm\":\"\",\"nip_gpp\":\"196803312007011001\",\"pendapatan\":[{\"activity_id\":\"apbn\",\"request_id\":\"apbn\",\"salary_id\":\"apbn\",\"type_id\":\"apbn\",\"type_title\":\"Gaji APBN\",\"unit_id\":\"41\",\"unit_name\":\"PSI\",\"bulan\":\"1\",\"tahun\":\"2019\",\"gjpokok\":\"3083400\",\"tjistri\":\"308340\",\"tjanak\":\"123336\",\"tjupns\":\"180000\",\"tjstruk\":\"0\",\"tjfungs\":\"0\",\"tjdaerah\":\"0\",\"tjpencil\":\"0\",\"tjlain\":\"0\",\"tjkompen\":\"0\",\"pembul\":\"51\",\"tjberas\":\"289680\",\"tjpph\":\"0.0\",\"bkn-potpfk2\":\"0\",\"bkn-potpfk10\":\"351507\",\"bkn-potpph\":\"0\",\"bkn-potswrum\":\"0\",\"bkn-potkelbtj\":\"0\",\"bkn-potlain\":\"0\",\"bkn-pottabrum\":\"5000\",\"pot_jabatan\":\"199241.000\",\"pot_pensiun\":\"166967.000\",\"netto_TakeHomePay\":\"3628300\",\"netto_pendapatan\":\"3618599\",\"ptkp_sebulan\":\"6000000\",\"pkp_sebulan\":\"0\",\"sisa_ptkp_sebulan\":\"2381401\",\"pph21\":[{\"tarif\":\"0.05\",\"pkp\":\"0.00\",\"hasil\":\"0.00\"}],\"pph21_layer\":\"0\",\"pph21_reminder\":\"4166666.67\",\"update_time\":\"2019-02-27 11:43:52.793\"},{\"activity_id\":\"84681\",\"activity_title\":\"Remunerasi Gol. II/c\",\"request_id\":\"1028\",\"salary_id\":\"17712\",\"type_id\":\"18\",\"type_title\":\"Tendik PNS (Non Struktural)\",\"unit_id\":\"41\",\"unit_name\":\"Pusat Sistem Informasi\",\"bulan\":\"1\",\"tahun\":\"2019\",\"p1\":\"170000\",\"p2\":\"220000\",\"p3\":\"1150000\",\"pot_jabatan\":\"77000.000\",\"pot_pensiun\":\"0.00\",\"netto_pendapatan\":\"1463000\",\"ptkp_sebulan\":\"6000000\",\"pkp_sebulan\":\"0\",\"sisa_ptkp_sebulan\":\"918401\",\"pph21\":[{\"tarif\":\"0.05\",\"pkp\":\"0.00\",\"hasil\":\"0.00\"}],\"pph21_layer\":\"0\",\"pph21_reminder\":\"4166666.670\",\"netto_TakeHomePay\":\"1540000\",\"update_time\":\"2019-02-27 14:14:29.947\"},{\"activity_id\":\"apbn\",\"request_id\":\"apbn\",\"salary_id\":\"apbn\",\"type_id\":\"apbn\",\"type_title\":\"Gaji APBN\",\"unit_id\":\"41\",\"unit_name\":\"PSI\",\"bulan\":\"2\",\"tahun\":\"2019\",\"gjpokok\":\"3083400\",\"tjistri\":\"308340\",\"tjanak\":\"123336\",\"tjupns\":\"180000\",\"tjstruk\":\"0\",\"tjfungs\":\"0\",\"tjdaerah\":\"0\",\"tjpencil\":\"0\",\"tjlain\":\"0\",\"tjkompen\":\"0\",\"pembul\":\"51\",\"tjberas\":\"289680\",\"tjpph\":\"0.0\",\"bkn-potpfk2\":\"0\",\"bkn-potpfk10\":\"351507\",\"bkn-potpph\":\"0\",\"bkn-potswrum\":\"0\",\"bkn-potkelbtj\":\"0\",\"bkn-potlain\":\"0\",\"bkn-pottabrum\":\"5000\",\"pot_jabatan\":\"199241.000\",\"pot_pensiun\":\"166967.000\",\"netto_TakeHomePay\":\"3628300\",\"netto_pendapatan\":\"3618599\",\"ptkp_sebulan\":\"6000000\",\"pkp_sebulan\":\"0\",\"sisa_ptkp_sebulan\":\"2381401\",\"pph21\":[{\"tarif\":\"0.05\",\"pkp\":\"0.00\",\"hasil\":\"0.00\"}],\"pph21_layer\":\"0\",\"pph21_reminder\":\"4166666.670\",\"update_time\":\"2019-02-27 15:01:23.711\"},{\"activity_id\":\"84681\",\"activity_title\":\"Remunerasi Gol. II/c\",\"request_id\":\"1704\",\"salary_id\":\"47652\",\"type_id\":\"18\",\"type_title\":\"Tendik PNS (Non Struktural)\",\"unit_id\":\"41\",\"unit_name\":\"Pusat Sistem Informasi\",\"bulan\":\"2\",\"tahun\":\"2019\",\"p1\":\"170000\",\"p2\":\"220000\",\"p3\":\"1150000\",\"pot_jabatan\":\"24518.000\",\"pot_pensiun\":\"0.00\",\"netto_pendapatan\":\"1515482\",\"ptkp_sebulan\":\"6000000\",\"pkp_sebulan\":\"0\",\"sisa_ptkp_sebulan\":\"865919\",\"pph21\":[{\"tarif\":\"0.05\",\"pkp\":\"0.00\",\"hasil\":\"0.00\"}],\"pph21_layer\":\"0\",\"pph21_reminder\":\"4166666.670\",\"netto_TakeHomePay\":\"1540000\",\"update_time\":\"2019-02-27 16:28:46.175\"},{\"activity_id\":\"98184\",\"activity_title\":\"Pelaksana Pelatihan Microsoft Office Tenaga Kependidikan Fakultas Teknik USU\",\"request_id\":\"2254\",\"salary_id\":\"71264\",\"type_id\":\"29\",\"type_title\":\"Adhoc\",\"unit_id\":\"37\",\"unit_name\":\"Biro Penelitian, Pengabdian Kepada Masyarakat dan Kerjasama\",\"bulan\":\"2\",\"tahun\":\"2019\",\"position\":\"Sekretariat\",\"adhoc\":\"500000\",\"pot_jabatan\":\"0.00\",\"pot_pensiun\":\"0.00\",\"netto_pendapatan\":\"500000\",\"ptkp_sebulan\":\"6000000\",\"pkp_sebulan\":\"0\",\"sisa_ptkp_sebulan\":\"365919\",\"pph21\":[{\"tarif\":\"0.05\",\"pkp\":\"0.00\",\"hasil\":\"0.00\"}],\"pph21_layer\":\"0\",\"pph21_reminder\":\"4166666.670\",\"netto_TakeHomePay\":\"500000\",\"update_time\":\"2019-02-27 17:29:36.205\"}],\"netto_pendapatan_setahun\":\"10715680\",\"ptkp_setahun\":\"72000000\",\"sisa_ptkp\":\"61284320\",\"total_pkp\":\"0\",\"total_pph21_usu\":\".000\",\"total_pph21_pribadi\":\"0\",\"timestamp\":\"2019-02-27 17:29:36.206\"}]";
        String hasil = test.replaceAll("\n",",");
        BasicDBList testtt = new Gson().fromJson(hasil, BasicDBList.class);
        testtt.toString();
//        new ApiRka().hitungPPH21("2","2019");
//        Gson gson =
//        Gson gson = new Gson();
//        File uploadDir = new File("upload");
//        uploadDir.mkdir(); // create the upload directory if it doesn't exist

//        staticFiles.externalLocation("upload");

//        port(8080);
//        post("/uploadFile", "multipart/form-data", (req, res) -> {
//            long maxFileSize = 100000000;       // the maximum size allowed for uploaded files
//            long maxRequestSize = 100000000;    // the maximum size allowed for multipart/form-data requests
//            int fileSizeThreshold = 1024;       // the size threshold after which files will be written to disk
//
//            res.type("text/html");
//            Path tempFile = Files.createTempFile(uploadDir.toPath(), "", ".xls");
//            req.raw().setAttribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp",maxFileSize,maxRequestSize,fileSizeThreshold));
//            Collection<Part> parts = req.raw().getParts();
//            for (Part part : parts) {
//                try (InputStream input = part.getInputStream()) { // getPart needs to use same "name" as input field in form
//                    Files.copy(input, tempFile, StandardCopyOption.REPLACE_EXISTING);
//                }
//            }
//            return "<h1>You uploaded this image:<h1><img src='" + tempFile.getFileName() + "'>";
//
//        });
//        post("/api/adam_malik", (req, res) -> {
//            res.type("application/json");
//            String auth = req.headers("Authorization");
//            if(auth.equalsIgnoreCase("Basic YWRhbS1tYWxpazpQc2lBZGFtTWFsaWswMQ==")) {
////                Item item = gson.fromJson(req.body(), Item.class);
////                ItemService itemService = new ItemService();
//                return itemService.insertToLog(item);
//            }else{
//                return "{\"status\" : 3, \"message\" : \"Authorization not valid\"}";
//            }
//        });
    }

}
