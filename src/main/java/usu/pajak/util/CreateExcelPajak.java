package usu.pajak.util;

import com.google.gson.Gson;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import usu.pajak.model.UserPajak;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.text.DateFormatSymbols;
import java.util.List;

public class CreateExcelPajak {
    private String[] header = new String[]{"No.","NIP","Nama","NPWP","Kegiatan","Jenis","Keterangan","Nilai"};
//    private MongoClient client = new MongoClient(new MongoClientURI("mongodb://fariz:Laru36Dema@clusterasetmongo-shard-00-00-t3kc1.mongodb.net:27017,clusterasetmongo-shard-00-01-t3kc1.mongodb.net:27017,clusterasetmongo-shard-00-02-t3kc1.mongodb.net:27017/test?ssl=true&replicaSet=ClusterAsetMongo-shard-0&authSource=admin&retryWrites=true")); //connect to mongodb
private static MongoClient client = new MongoClient(new MongoClientURI("mongodb://localhost:27017/pajak_2019_rev")); //connect to mongodb
    private Datastore datastore = new Morphia().mapPackage("usu.pajak.model.UserPajak").createDatastore(client, "pajak_2019_rev");
    private static CellStyle style,currency,styleWarning;
    private String[] headerMonth = new String[]{"No.","NIP","Nama","NPWP","Kegiatan","Jenis","Bruto Pendapatan","Pengurang Jabatan","Pengurang Pensiun",
            "Netto Pendapatan","Netto Setahun","PTKP Sebulan","PTKP Setahun","PKP","PKP Setahun","Total PPH21"};

//    private String[] headerPerson = new String[]{"No.","NIP","Nama","NPWP","Keterangan"};

    public CreateExcelPajak() throws IOException{
//        createExcelBasedOnUnit();
//        createExcelBasedOnMonth();
//        createExcelBasedOnPerson();
    }

    private void createExcelBasedOnPerson() throws IOException{
        Workbook workbook = WorkbookFactory.create(new File("D:/PAJAK_2019.xls"));
        currency = workbook.createCellStyle();
        currency.setDataFormat(workbook.createDataFormat().getFormat("#,##0.00"));

        List<UserPajak> listResult = datastore.createQuery(UserPajak.class).asList();
        System.out.println("Size : "+listResult.size());
        Integer xTimes = listResult.size()/800;
        Integer reminder = listResult.size()%800;
        for(int a=0;a<xTimes+1;a++) {
            Sheet sheet = workbook.createSheet("Pajak Pegawai "+(a+1));
            Row rowHeader = sheet.createRow(0);
            for (int j = 0; j < header.length; j++) {
                rowHeader.createCell(j).setCellValue(header[j]);
            }

            int z = 1;
            int batasAwal = a * 800;
            int batasAkhir = (a+1)*800;
            if(a==4)
                batasAkhir = batasAwal+reminder;
            for (int i = batasAwal; i < batasAkhir; i++) {
                UserPajak up = listResult.get(i);
                Row rows = sheet.createRow(z);
                Cell cell = rows.createCell(0);
                cell.setCellValue(i + 1);
                if (up.getNip_gpp() == null || up.getNip_gpp().equalsIgnoreCase(""))
                    rows.createCell(1).setCellValue(up.getNip_simsdm());
                else
                    rows.createCell(1).setCellValue(up.getNip_gpp());

                rows.createCell(2).setCellValue(up.getFront_degree() + " " + up.getFull_name() + " " + up.getBehind_degree());
                rows.createCell(3).setCellValue(up.getNpwp());

                BasicDBList pendapatan = up.getPendapatan_tdk_tetap();
                int count = 0;
                for (int j = 0; j < pendapatan.size(); j++) {
                    BasicDBObject obj = (BasicDBObject) pendapatan.get(j);
                    if (count == 0) {
                        rows.createCell(4).setCellValue(obj.getString("activity_title"));
                        rows.createCell(5).setCellValue(obj.getString("type_title"));
                        rows.createCell(6).setCellValue("bulan");
                        rows.createCell(7).setCellValue(obj.getString("bulan"));
                        z++;
                        rows = sheet.createRow(z);
                        rows.createCell(6).setCellValue("pendapatan bruto");
                        Cell cellB = rows.createCell(7);
                        cellB.setCellValue(new BigDecimal(obj.getString("bruto_pendapatan")).doubleValue());
                        cellB.setCellType(CellType.NUMERIC);
                        cellB.setCellStyle(currency);
                        z++;
                        rows = sheet.createRow(z);
                        rows.createCell(6).setCellValue("pengurang biaya jabatan");
                        cellB = rows.createCell(7);
                        cellB.setCellValue(new BigDecimal(obj.getString("pot_jabatan")).doubleValue());
                        cellB.setCellType(CellType.NUMERIC);
                        cellB.setCellStyle(currency);
                        z++;
                        rows = sheet.createRow(z);
                        rows.createCell(6).setCellValue("pengurang iuran pensiun");
                        cellB = rows.createCell(7);
                        cellB.setCellValue(new BigDecimal(obj.getString("pot_pensiun")).doubleValue());
                        cellB.setCellType(CellType.NUMERIC);
                        cellB.setCellStyle(currency);
                        z++;
                        rows = sheet.createRow(z);
                        rows.createCell(6).setCellValue("pendapatn netto");
                        cellB = rows.createCell(7);
                        cellB.setCellValue(new BigDecimal(obj.getString("netto_pendapatan")).doubleValue());
                        cellB.setCellType(CellType.NUMERIC);
                        cellB.setCellStyle(currency);
                        z++;
                        rows = sheet.createRow(z);
                        rows.createCell(6).setCellValue("ptkp sebulan");
                        cellB = rows.createCell(7);
                        if (obj.getString("ptkp_sebulan") == null)
                            cellB.setCellValue("");
                        else {
                            cellB.setCellValue(new BigDecimal(obj.getString("ptkp_sebulan")).doubleValue());
                            cellB.setCellType(CellType.NUMERIC);
                            cellB.setCellStyle(currency);
                        }
                        z++;
                        rows = sheet.createRow(z);
                        rows.createCell(6).setCellValue("pkp sebulan");
                        cellB = rows.createCell(7);
                        cellB.setCellValue(new BigDecimal(obj.getString("pkp_sebulan")).doubleValue());
                        cellB.setCellType(CellType.NUMERIC);
                        cellB.setCellStyle(currency);
                        z++;
                        rows = sheet.createRow(z);
                        rows.createCell(6).setCellValue("sisa ptkp sebulan");
                        cellB = rows.createCell(7);
                        cellB.setCellValue(new BigDecimal(obj.getString("sisa_ptkp_sebulan")).doubleValue());
                        cellB.setCellType(CellType.NUMERIC);
                        cellB.setCellStyle(currency);
                        BasicDBList listPph = (BasicDBList) obj.get("pph21");
                        for (Object p : listPph) {
                            BasicDBObject t = (BasicDBObject) p;
                            z++;
                            rows = sheet.createRow(z);
                            rows.createCell(6).setCellValue("tarif");
                            cellB = rows.createCell(7);
                            cellB.setCellValue(new BigDecimal(t.getString("tarif")).doubleValue());
                            cellB.setCellType(CellType.NUMERIC);
                            cellB.setCellStyle(currency);
                            z++;
                            rows = sheet.createRow(z);
                            rows.createCell(6).setCellValue("pkp");
                            cellB = rows.createCell(7);
                            cellB.setCellValue(new BigDecimal(t.getString("pkp_sebulan")).doubleValue());
                            cellB.setCellType(CellType.NUMERIC);
                            cellB.setCellStyle(currency);
                            z++;
                            rows = sheet.createRow(z);
                            rows.createCell(6).setCellValue("pph21");
                            cellB = rows.createCell(7);
                            cellB.setCellValue(new BigDecimal(t.getString("hasil_sebulan")).doubleValue());
                            cellB.setCellType(CellType.NUMERIC);
                            cellB.setCellStyle(currency);
                        }
                        z++;
                        rows = sheet.createRow(z);
                        rows.createCell(6).setCellValue("pendapatan setelah dipotong pajak");
                        cellB = rows.createCell(7);
                        cellB.setCellValue(new BigDecimal(obj.getString("netto_TakeHomePay")).doubleValue());
                        cellB.setCellType(CellType.NUMERIC);
                        cellB.setCellStyle(currency);
                    } else {
                        Row add = sheet.createRow(z);
                        add.createCell(4).setCellValue(obj.getString("activity_title"));
                        add.createCell(5).setCellValue(obj.getString("type_title"));
                        add.createCell(6).setCellValue("bulan");
                        add.createCell(7).setCellValue(obj.getString("bulan"));
                        z++;
                        add = sheet.createRow(z);
                        add.createCell(6).setCellValue("pendapatan bruto");
                        Cell cellB = add.createCell(7);
                        cellB.setCellValue(new BigDecimal(obj.getString("bruto_pendapatan")).doubleValue());
                        cellB.setCellType(CellType.NUMERIC);
                        cellB.setCellStyle(currency);
                        z++;
                        add = sheet.createRow(z);
                        add.createCell(6).setCellValue("pengurang biaya jabatan");
                        cellB = add.createCell(7);
                        cellB.setCellValue(new BigDecimal(obj.getString("pot_jabatan")).doubleValue());
                        cellB.setCellType(CellType.NUMERIC);
                        cellB.setCellStyle(currency);
                        z++;
                        add = sheet.createRow(z);
                        add.createCell(6).setCellValue("pengurang iuran pensiun");
                        cellB = add.createCell(7);
                        cellB.setCellValue(new BigDecimal(obj.getString("pot_pensiun")).doubleValue());
                        cellB.setCellType(CellType.NUMERIC);
                        cellB.setCellStyle(currency);
                        z++;
                        add = sheet.createRow(z);
                        add.createCell(6).setCellValue("pendapatn netto");
                        cellB = add.createCell(7);
                        cellB.setCellValue(new BigDecimal(obj.getString("netto_pendapatan")).doubleValue());
                        cellB.setCellType(CellType.NUMERIC);
                        cellB.setCellStyle(currency);
                        z++;
                        add = sheet.createRow(z);
                        add.createCell(6).setCellValue("ptkp sebulan");
                        cellB = add.createCell(7);
                        if (obj.getString("ptkp_sebulan") == null)
                            cellB.setCellValue("");
                        else {
                            cellB.setCellValue(new BigDecimal(obj.getString("ptkp_sebulan")).doubleValue());
                            cellB.setCellType(CellType.NUMERIC);
                            cellB.setCellStyle(currency);
                        }
                        z++;
                        add = sheet.createRow(z);
                        add.createCell(6).setCellValue("pkp sebulan");
                        cellB = add.createCell(7);
                        cellB.setCellValue(new BigDecimal(obj.getString("pkp_sebulan")).doubleValue());
                        cellB.setCellType(CellType.NUMERIC);
                        cellB.setCellStyle(currency);
                        z++;
                        add = sheet.createRow(z);
                        add.createCell(6).setCellValue("sisa ptkp sebulan");
                        cellB = add.createCell(7);
                        cellB.setCellValue(new BigDecimal(obj.getString("sisa_ptkp_sebulan")).doubleValue());
                        cellB.setCellType(CellType.NUMERIC);
                        cellB.setCellStyle(currency);
                        BasicDBList listPph = (BasicDBList) obj.get("pph21");
                        for (Object p : listPph) {
                            BasicDBObject t = (BasicDBObject) p;
                            z++;
                            add = sheet.createRow(z);
                            add.createCell(6).setCellValue("tarif");
                            cellB = add.createCell(7);
                            cellB.setCellValue(new BigDecimal(t.getString("tarif")).doubleValue());
                            cellB.setCellType(CellType.NUMERIC);
                            cellB.setCellStyle(currency);
                            z++;
                            add = sheet.createRow(z);
                            add.createCell(6).setCellValue("pkp");
                            cellB = add.createCell(7);
                            cellB.setCellValue(new BigDecimal(t.getString("pkp_sebulan")).doubleValue());
                            cellB.setCellType(CellType.NUMERIC);
                            cellB.setCellStyle(currency);
                            z++;
                            add = sheet.createRow(z);
                            add.createCell(6).setCellValue("pph21");
                            cellB = add.createCell(7);
                            cellB.setCellValue(new BigDecimal(t.getString("hasil_sebulan")).doubleValue());
                            cellB.setCellType(CellType.NUMERIC);
                            cellB.setCellStyle(currency);
                        }
                        z++;
                        add = sheet.createRow(z);
                        add.createCell(6).setCellValue("pendapatan setelah dipotong pajak");
                        cellB = add.createCell(7);
                        cellB.setCellValue(new BigDecimal(obj.getString("netto_TakeHomePay")).doubleValue());
                        cellB.setCellType(CellType.NUMERIC);
                        cellB.setCellStyle(currency);
                    }
                    count++;
                    z++;
                }
                z++;
            }
        }
        try (OutputStream fileOut = new FileOutputStream("D:\\PAJAK_2019_BASEDON_PERSON.xls")) {
            workbook.write(fileOut);
        }
        workbook.close();
    }

    private void createExcelBasedOnMonth() throws  IOException{
        Workbook workbook = WorkbookFactory.create(new File("D:/PAJAK_2019.xls"));
        currency = workbook.createCellStyle();
        currency.setDataFormat(workbook.createDataFormat().getFormat("#,##0.00"));

        String[] months = new DateFormatSymbols().getMonths();
        for(int i=0;i<2;i++){
            Sheet sheet = workbook.createSheet(months[i]);
            Row rowHeader = sheet.createRow(0);
            for(int j=0;j<headerMonth.length;j++){
                rowHeader.createCell(j).setCellValue(headerMonth[j]);
            }

            List<UserPajak> listResult = datastore.createQuery(UserPajak.class).disableValidation()
                    .filter("pendapatan.bulan", Integer.toString(i+1)).asList();

            int z =1;
            for(int k=0;k<listResult.size();k++){
                UserPajak up = listResult.get(k);
                Row rows = sheet.createRow(z);
                rows.createCell(0).setCellValue(i+1);
                if(up.getNip_gpp() == null || up.getNip_gpp().equalsIgnoreCase(""))
                    rows.createCell(1).setCellValue(up.getNip_simsdm());
                else
                    rows.createCell(1).setCellValue(up.getNip_gpp());
                rows.createCell(2).setCellValue(up.getFront_degree()+" "+up.getFull_name()+" "+up.getBehind_degree());
                rows.createCell(3).setCellValue(up.getNpwp());

                BasicDBList pendapatan = up.getPendapatan_tdk_tetap();

                int count=0;
                for(int l=0;l<pendapatan.size();l++){
                    BasicDBObject obj = (BasicDBObject) pendapatan.get(l);
                    if(obj.getString("bulan").equalsIgnoreCase(Integer.toString(i+1)) && !obj.getString("salary_id").equalsIgnoreCase("apbn")){
                        if(count==0){
                            rows.createCell(4).setCellValue(obj.getString("activity_title"));
                            rows.createCell(5).setCellValue(obj.getString("type_title"));
                            Cell cellB = rows.createCell(6);
                            cellB.setCellValue(new BigDecimal(obj.getString("bruto_pendapatan")).doubleValue());
                            cellB.setCellType(CellType.NUMERIC);
                            cellB.setCellStyle(currency);
                            cellB = rows.createCell(7);
                            cellB.setCellValue(new BigDecimal(obj.getString("pot_jabatan")).doubleValue());
                            cellB.setCellType(CellType.NUMERIC);
                            cellB.setCellStyle(currency);
                            cellB = rows.createCell(8);
                            cellB.setCellValue(new BigDecimal(obj.getString("pot_pensiun")).doubleValue());
                            cellB.setCellType(CellType.NUMERIC);
                            cellB.setCellStyle(currency);
                            cellB = rows.createCell(9);
                            cellB.setCellValue(new BigDecimal(obj.getString("netto_pendapatan")).doubleValue());
                            cellB.setCellType(CellType.NUMERIC);
                            cellB.setCellStyle(currency);
                            cellB = rows.createCell(10);
                            cellB.setCellValue(new BigDecimal(obj.getString("netto_pendapatan")).multiply(BigDecimal.valueOf(12.00)).doubleValue());
                            cellB.setCellType(CellType.NUMERIC);
                            cellB.setCellStyle(currency);
                            cellB = rows.createCell(11);
                            cellB.setCellValue(new BigDecimal(up.getPtkp_setahun()).divide(new BigDecimal("12.00"),2,BigDecimal.ROUND_HALF_UP).doubleValue());
                            cellB.setCellType(CellType.NUMERIC);
                            cellB.setCellStyle(currency);
                            cellB = rows.createCell(12);
                            cellB.setCellValue(new BigDecimal(up.getPtkp_setahun()).doubleValue());
                            cellB.setCellType(CellType.NUMERIC);
                            cellB.setCellStyle(currency);
                            cellB = rows.createCell(13);
                            cellB.setCellValue(new BigDecimal(obj.getString("pkp_sebulan")).doubleValue());
                            cellB.setCellType(CellType.NUMERIC);
                            cellB.setCellStyle(currency);
                            cellB = rows.createCell(14);
                            cellB.setCellValue(new BigDecimal(obj.getString("pkp_sebulan")).multiply(BigDecimal.valueOf(12.00)).doubleValue());
                            cellB.setCellType(CellType.NUMERIC);
                            cellB.setCellStyle(currency);
                            BasicDBList listPph = (BasicDBList) obj.get("pph21");
                            BigDecimal totalPph21 = BigDecimal.valueOf(0.00);
                            for (Object p : listPph) {
                                BasicDBObject t = (BasicDBObject) p;
                                totalPph21 = totalPph21.add(new BigDecimal(t.getString("hasil_sebulan")));
                            }
                            cellB = rows.createCell(15);
                            cellB.setCellValue(totalPph21.doubleValue());
                            cellB.setCellType(CellType.NUMERIC);
                            cellB.setCellStyle(currency);
                        }else{
                            Row add = sheet.createRow(z);
                            add.createCell(4).setCellValue(obj.getString("activity_title"));
                            add.createCell(5).setCellValue(obj.getString("type_title"));
                            Cell cellB = add.createCell(6);
                            cellB.setCellValue(new BigDecimal(obj.getString("bruto_pendapatan")).doubleValue());
                            cellB.setCellType(CellType.NUMERIC);
                            cellB.setCellStyle(currency);
                            cellB = add.createCell(7);
                            cellB.setCellValue(new BigDecimal(obj.getString("pot_jabatan")).doubleValue());
                            cellB.setCellType(CellType.NUMERIC);
                            cellB.setCellStyle(currency);
                            cellB = add.createCell(8);
                            cellB.setCellValue(new BigDecimal(obj.getString("pot_pensiun")).doubleValue());
                            cellB.setCellType(CellType.NUMERIC);
                            cellB.setCellStyle(currency);
                            cellB = add.createCell(9);
                            cellB.setCellValue(new BigDecimal(obj.getString("netto_pendapatan")).doubleValue());
                            cellB.setCellType(CellType.NUMERIC);
                            cellB.setCellStyle(currency);
                            cellB = add.createCell(10);
                            cellB.setCellValue(new BigDecimal(obj.getString("netto_pendapatan")).multiply(BigDecimal.valueOf(12.00)).doubleValue());
                            cellB.setCellType(CellType.NUMERIC);
                            cellB.setCellStyle(currency);
                            cellB = add.createCell(11);
                            cellB.setCellValue(new BigDecimal(up.getPtkp_setahun()).divide(new BigDecimal("12.00"),2,BigDecimal.ROUND_HALF_UP).doubleValue());
                            cellB.setCellType(CellType.NUMERIC);
                            cellB.setCellStyle(currency);
                            cellB = add.createCell(12);
                            cellB.setCellValue(new BigDecimal(up.getPtkp_setahun()).doubleValue());
                            cellB.setCellType(CellType.NUMERIC);
                            cellB.setCellStyle(currency);
                            cellB = add.createCell(13);
                            cellB.setCellValue(new BigDecimal(obj.getString("pkp_sebulan")).doubleValue());
                            cellB.setCellType(CellType.NUMERIC);
                            cellB.setCellStyle(currency);
                            cellB = add.createCell(14);
                            cellB.setCellValue(new BigDecimal(obj.getString("pkp_sebulan")).multiply(BigDecimal.valueOf(12.00)).doubleValue());
                            cellB.setCellType(CellType.NUMERIC);
                            cellB.setCellStyle(currency);
                            BasicDBList listPph = (BasicDBList) obj.get("pph21");
                            BigDecimal totalPph21 = BigDecimal.valueOf(0.00);
                            for (Object p : listPph) {
                                BasicDBObject t = (BasicDBObject) p;
                                totalPph21 = totalPph21.add(new BigDecimal(t.getString("hasil_sebulan")));
                            }
                            cellB = add.createCell(15);
                            cellB.setCellValue(totalPph21.doubleValue());
                            cellB.setCellType(CellType.NUMERIC);
                            cellB.setCellStyle(currency);
                        }
                        count++;
                        z++;
                    }
                }
                z++;
            }
        }
        try (OutputStream fileOut = new FileOutputStream("D:\\PAJAK_2019_BASEDON_MONTH.xls")) {
            workbook.write(fileOut);
        }
        workbook.close();
    }

    private void createExcelBasedOnUnit() throws IOException{
        String response = callApi("https://api.usu.ac.id/0.1/units","GET");
        DataUnit du = new Gson().fromJson(response, DataUnit.class);
        Workbook workbook = WorkbookFactory.create(new File("D:/PAJAK_2019.xls"));
        style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.DARK_RED.getIndex());
        style.setFillBackgroundColor(HSSFColor.HSSFColorPredefined.RED.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        currency = workbook.createCellStyle();
        currency.setDataFormat(workbook.createDataFormat().getFormat("#,##0.00"));

//        for(int x=0;x<4;x++){
//            Parent par = du.getData().get(x);
//            Children children = new Children();
//            children.setId(par.getId());
//            children.setName(par.getName());
        for(int x=0;x<du.getData().get(0).getChildren().size();x++){
            Children children = du.getData().get(0).getChildren().get(x);
            Sheet sheet = workbook.createSheet(children.getName().replaceAll("/",""));
            Row row = sheet.createRow(0);
            for(int i=0;i<header.length;i++){
                row.createCell(i).setCellValue(header[i]);
            }

            List<UserPajak> listResult = datastore.createQuery(UserPajak.class).disableValidation()
                    .filter("pendapatan.unit_id", children.getId()).asList();
//            int count
            int z =1;
            for(int i=0;i<listResult.size();i++){
                UserPajak up = listResult.get(i);
                Row rows = sheet.createRow(z);
                Cell cell = rows.createCell(0);
                cell.setCellValue(i+1);
                if(up.getNip_gpp() == null || up.getNip_gpp().equalsIgnoreCase(""))
                    rows.createCell(1).setCellValue(up.getNip_simsdm());
                else
                    rows.createCell(1).setCellValue(up.getNip_gpp());

                rows.createCell(2).setCellValue(up.getFront_degree()+" "+up.getFull_name()+" "+up.getBehind_degree());
                rows.createCell(3).setCellValue(up.getNpwp());
//                rows.setRowStyle(style);
                BasicDBList pendapatan = up.getPendapatan_tdk_tetap();
                BasicDBList refinePendapatan = pendapatan;

                int count=0;
                for (int j = 0; j < pendapatan.size(); j++) {
                    BasicDBObject obj = (BasicDBObject) pendapatan.get(j);
                    if (!(obj.getString("unit_id").equalsIgnoreCase(children.getId()) /*|| obj.getString("type_id").equalsIgnoreCase("apbn")*/)) {
//                        refinePendapatan.remove(j);
                    } else {

                        if (count == 0) {
                            rows.createCell(4).setCellValue(obj.getString("activity_title"));
                            rows.createCell(5).setCellValue(obj.getString("type_title"));
                            rows.createCell(6).setCellValue("bulan");
                            rows.createCell(7).setCellValue(obj.getString("bulan"));
                            z++;
                            rows = sheet.createRow(z);
                            rows.createCell(6).setCellValue("pendapatan bruto");
                            Cell cellB = rows.createCell(7);
                            cellB.setCellValue(new BigDecimal(obj.getString("bruto_pendapatan")).doubleValue());
                            cellB.setCellType(CellType.NUMERIC);
                            cellB.setCellStyle(currency);
                            z++;
                            rows = sheet.createRow(z);
                            rows.createCell(6).setCellValue("pengurang biaya jabatan");
                            cellB = rows.createCell(7);
                            cellB.setCellValue(new BigDecimal(obj.getString("pot_jabatan")).doubleValue());
                            cellB.setCellType(CellType.NUMERIC);
                            cellB.setCellStyle(currency);
                            z++;
                            rows = sheet.createRow(z);
                            rows.createCell(6).setCellValue("pengurang iuran pensiun");
                            cellB = rows.createCell(7);
                            cellB.setCellValue(new BigDecimal(obj.getString("pot_pensiun")).doubleValue());
                            cellB.setCellType(CellType.NUMERIC);
                            cellB.setCellStyle(currency);
                            z++;
                            rows = sheet.createRow(z);
                            rows.createCell(6).setCellValue("pendapatn netto");
                            cellB = rows.createCell(7);
                            cellB.setCellValue(new BigDecimal(obj.getString("netto_pendapatan")).doubleValue());
                            cellB.setCellType(CellType.NUMERIC);
                            cellB.setCellStyle(currency);
                            z++;
                            rows = sheet.createRow(z);
                            rows.createCell(6).setCellValue("ptkp sebulan");
                            cellB = rows.createCell(7);
                            if(obj.getString("ptkp_sebulan") == null)
                                cellB.setCellValue("");
                            else {
                                cellB.setCellValue(new BigDecimal(obj.getString("ptkp_sebulan")).doubleValue());
                                cellB.setCellType(CellType.NUMERIC);
                                cellB.setCellStyle(currency);
                            }
                            z++;
                            rows = sheet.createRow(z);
                            rows.createCell(6).setCellValue("pkp sebulan");
                            cellB = rows.createCell(7);
                            cellB.setCellValue(new BigDecimal(obj.getString("pkp_sebulan")).doubleValue());
                            cellB.setCellType(CellType.NUMERIC);
                            cellB.setCellStyle(currency);
                            z++;
                            rows = sheet.createRow(z);
                            rows.createCell(6).setCellValue("sisa ptkp sebulan");
                            cellB = rows.createCell(7);
                            cellB.setCellValue(new BigDecimal(obj.getString("sisa_ptkp_sebulan")).doubleValue());
                            cellB.setCellType(CellType.NUMERIC);
                            cellB.setCellStyle(currency);
                            BasicDBList listPph = (BasicDBList) obj.get("pph21");
                            for (Object p : listPph) {
                                BasicDBObject t = (BasicDBObject) p;
                                z++;
                                rows = sheet.createRow(z);
                                rows.createCell(6).setCellValue("tarif");
                                cellB = rows.createCell(7);
                                cellB.setCellValue(new BigDecimal(t.getString("tarif")).doubleValue());
                                cellB.setCellType(CellType.NUMERIC);
                                cellB.setCellStyle(currency);
                                z++;
                                rows = sheet.createRow(z);
                                rows.createCell(6).setCellValue("pkp");
                                cellB = rows.createCell(7);
                                cellB.setCellValue(new BigDecimal(t.getString("pkp_sebulan")).doubleValue());
                                cellB.setCellType(CellType.NUMERIC);
                                cellB.setCellStyle(currency);
                                z++;
                                rows = sheet.createRow(z);
                                rows.createCell(6).setCellValue("pph21");
                                cellB = rows.createCell(7);
                                cellB.setCellValue(new BigDecimal(t.getString("hasil_sebulan")).doubleValue());
                                cellB.setCellType(CellType.NUMERIC);
                                cellB.setCellStyle(currency);
                            }
                            z++;
                            rows = sheet.createRow(z);
                            rows.createCell(6).setCellValue("pendapatan setelah dipotong pajak");
                            cellB = rows.createCell(7);
                            cellB.setCellValue(new BigDecimal(obj.getString("netto_TakeHomePay")).doubleValue());
                            cellB.setCellType(CellType.NUMERIC);
                            cellB.setCellStyle(currency);
                        } else {
//                            System.out.println("Here "+j);
                            Row add = sheet.createRow(z);
                            add.createCell(4).setCellValue(obj.getString("activity_title"));
                            add.createCell(5).setCellValue(obj.getString("type_title"));
                            add.createCell(6).setCellValue("bulan");
                            add.createCell(7).setCellValue(obj.getString("bulan"));
                            z++;
                            add = sheet.createRow(z);
                            add.createCell(6).setCellValue("pendapatan bruto");
                            Cell cellB = add.createCell(7);
                            cellB.setCellValue(new BigDecimal(obj.getString("bruto_pendapatan")).doubleValue());
                            cellB.setCellType(CellType.NUMERIC);
                            cellB.setCellStyle(currency);
                            z++;
                            add = sheet.createRow(z);
                            add.createCell(6).setCellValue("pengurang biaya jabatan");
                            cellB = add.createCell(7);
                            cellB.setCellValue(new BigDecimal(obj.getString("pot_jabatan")).doubleValue());
                            cellB.setCellType(CellType.NUMERIC);
                            cellB.setCellStyle(currency);
                            z++;
                            add = sheet.createRow(z);
                            add.createCell(6).setCellValue("pengurang iuran pensiun");
                            cellB = add.createCell(7);
                            cellB.setCellValue(new BigDecimal(obj.getString("pot_pensiun")).doubleValue());
                            cellB.setCellType(CellType.NUMERIC);
                            cellB.setCellStyle(currency);
                            z++;
                            add = sheet.createRow(z);
                            add.createCell(6).setCellValue("pendapatn netto");
                            cellB = add.createCell(7);
                            cellB.setCellValue(new BigDecimal(obj.getString("netto_pendapatan")).doubleValue());
                            cellB.setCellType(CellType.NUMERIC);
                            cellB.setCellStyle(currency);
                            z++;
                            add = sheet.createRow(z);
                            add.createCell(6).setCellValue("ptkp sebulan");
                            cellB = add.createCell(7);
                            if(obj.getString("ptkp_sebulan") == null)
                                cellB.setCellValue("");
                            else {
                                cellB.setCellValue(new BigDecimal(obj.getString("ptkp_sebulan")).doubleValue());
                                cellB.setCellType(CellType.NUMERIC);
                                cellB.setCellStyle(currency);
                            }
                            z++;
                            add = sheet.createRow(z);
                            add.createCell(6).setCellValue("pkp sebulan");
                            cellB = add.createCell(7);
                            cellB.setCellValue(new BigDecimal(obj.getString("pkp_sebulan")).doubleValue());
                            cellB.setCellType(CellType.NUMERIC);
                            cellB.setCellStyle(currency);
                            z++;
                            add = sheet.createRow(z);
                            add.createCell(6).setCellValue("sisa ptkp sebulan");
                            cellB = add.createCell(7);
                            cellB.setCellValue(new BigDecimal(obj.getString("sisa_ptkp_sebulan")).doubleValue());
                            cellB.setCellType(CellType.NUMERIC);
                            cellB.setCellStyle(currency);
                            BasicDBList listPph = (BasicDBList) obj.get("pph21");
                            for (Object p : listPph) {
                                BasicDBObject t = (BasicDBObject) p;
                                z++;
                                add = sheet.createRow(z);
                                add.createCell(6).setCellValue("tarif");
                                cellB = add.createCell(7);
                                cellB.setCellValue(new BigDecimal(t.getString("tarif")).doubleValue());
                                cellB.setCellType(CellType.NUMERIC);
                                cellB.setCellStyle(currency);
                                z++;
                                add = sheet.createRow(z);
                                add.createCell(6).setCellValue("pkp");
                                cellB = add.createCell(7);
                                cellB.setCellValue(new BigDecimal(t.getString("pkp_sebulan")).doubleValue());
                                cellB.setCellType(CellType.NUMERIC);
                                cellB.setCellStyle(currency);
                                z++;
                                add = sheet.createRow(z);
                                add.createCell(6).setCellValue("pph21");
                                cellB = add.createCell(7);
                                cellB.setCellValue(new BigDecimal(t.getString("hasil_sebulan")).doubleValue());
                                cellB.setCellType(CellType.NUMERIC);
                                cellB.setCellStyle(currency);
                            }
                            z++;
                            add = sheet.createRow(z);
                            add.createCell(6).setCellValue("pendapatan setelah dipotong pajak");
                            cellB = add.createCell(7);
                            cellB.setCellValue(new BigDecimal(obj.getString("netto_TakeHomePay")).doubleValue());
                            cellB.setCellType(CellType.NUMERIC);
                            cellB.setCellStyle(currency);
                        }
                        count++;
                        z++;
                    }
                }
                z++;
            }
        }
        try (OutputStream fileOut = new FileOutputStream("D:\\PAJAK_2019_0-REV-AGAIN2.xls")) {
            workbook.write(fileOut);
        }
        workbook.close();
    }

    public static void main(String[] args) throws IOException {
        new CreateExcelPajak();
    }

    private String callApi(String ep, String method) throws IOException {
        URL obj = new URL(ep);
        HttpsURLConnection conn= (HttpsURLConnection) obj.openConnection();

        conn.setConnectTimeout(300);
        conn.setRequestMethod( method );
        conn.setUseCaches( true );
        conn.setDoOutput( true );
        conn.setDoInput(true);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }
}

class DataUnit{
    private List<Parent> data;

    public List<Parent> getData() {
        return data;
    }
}

class Parent{
    private String id;
    private String name;

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    private List<Children> children;

    public List<Children> getChildren() {
        return children;
    }
}

class Children{
    private String id;
    private String name;

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
