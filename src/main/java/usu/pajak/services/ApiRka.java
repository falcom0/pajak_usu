package usu.pajak.services;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mongodb.*;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import usu.pajak.model.Pegawai;
import usu.pajak.model.TarifPajak;
import usu.pajak.model.UserPajak;
import usu.pajak.util.UserSimSdm;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

public class ApiRka {
//    private MongoClient client = new MongoClient(new MongoClientURI("mongodb://fariz:Laru36Dema@clusterasetmongo-shard-00-00-t3kc1.mongodb.net:27017,clusterasetmongo-shard-00-01-t3kc1.mongodb.net:27017,clusterasetmongo-shard-00-02-t3kc1.mongodb.net:27017/test?ssl=true&replicaSet=ClusterAsetMongo-shard-0&authSource=admin&retryWrites=true")); //connect to mongodb
    private static MongoClient client = new MongoClient(new MongoClientURI("mongodb://localhost:27017/pajak_2019_rev")); //connect to mongodb
    private Datastore datastore = new Morphia().mapPackage("usu.pajak.model.UserPajak").createDatastore(client, "pajak_2019_rev");
    private static final Double persenPotJabatan = Double.valueOf("0.05");
    private static final Double persenPotPensiun = Double.valueOf("0.0475");

//    private static DecimalFormat dec = new DecimalFormat("#.000");

    public static Integer hitungPtkp(String userId){
        Integer initialPtkp = 54000000;
        Integer ptkp = 0;
        try {
//            Thread.sleep(100);
            Response response = new Gson().fromJson(callApiUsu("https://api.usu.ac.id/0.1/users/" + userId + "/ptkp", "GET"), Response.class);
            UserSimSdm us = response.getResponse().get(0);
            int count = 0;
            if(us.getGender().equalsIgnoreCase("Pria") && us.getHas_couple())  count++;
            else if(us.getGender().equalsIgnoreCase("Wanita") && us.getHas_couple()) return initialPtkp;
            if(us.getNum_of_children()>0 && us.getNum_of_children()<=3) count += us.getNum_of_children();
            Integer addPtkp = 4500000;
            addPtkp = count * addPtkp;
            ptkp = initialPtkp+addPtkp;
            return ptkp;
        }catch (IOException io){
            return 0;
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    class Response{
        private List<UserSimSdm> response;

        public List<UserSimSdm> getResponse() {
            return response;
        }
    }

    public void setPtkpSimsdm(){
        List<Pegawai> listPegawai = datastore.find(Pegawai.class).asList();
//        Integer ptkp = hitungPtkp();
    }

    public static Token getSSO(String identity, String password) throws IOException {
        String url = "https://akun.usu.ac.id/auth/login/apps?random_char=TVWBJBSuwyewbwgcuw23657438zs";
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        //add reuqest header
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        String urlParameters = "identity="+identity+"&password="+password;

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr;
        wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();
        InputStream response = con.getInputStream();

        Scanner scanner = new Scanner(response);
        String responseBody = scanner.useDelimiter("\\A").next();
        Token token = new Gson().fromJson(responseBody,Token.class);
//        System.out.println(token.getToken());
        return token;
    }

    static String readFile(String path, Charset encoding)
            throws IOException
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    public void asalData(String month, String year){
        Salary salary = null;
        try {
            salary = new Gson().fromJson(callApiUsu("https://api.usu.ac.id/0.2/salary_receipts?status=1&month="+month, "GET"), Salary.class);
            System.out.println("Bulan :"+month);
            List<SalaryDetail> totalData = salary.getResponse().getSalary_receivers();
//            List<SalaryDetail> gaji = totalData.stream()
//                    .filter(c -> c.getPayment().getAsJsonObject().has("basic_salary"))
////                    .filter(c -> c.getId().intValue()==36158)//11872  36158
//                    .collect(Collectors.toList());
//            List<SalaryDetail> honor = totalData.stream()
//                    .filter(c -> !c.getPayment().getAsJsonObject().has("basic_salary"))
////                    .filter(c -> (c.getPayment().getAsJsonObject().get("type").getAsJsonObject().get("id").getAsInt()==23))
//                    .collect(Collectors.toList());
            List<SalaryDetail> pns = totalData.stream()
                    .filter(c -> c.getUser().getId()!=null)
                    .filter(c -> c.getPayment().getAsJsonObject().has("basic_salary"))
                    .filter(c ->
                            (c.getUser().getGroup().getId() == 5) || (c.getUser().getGroup().getId() == 6)
                    )
                    .collect(Collectors.toList());
//            List<SalaryDetail> non_pns = totalData.stream()
//                    .filter(c -> c.getId().intValue()!=11872)
//                    .filter(c -> c.getPayment().getAsJsonObject().has("basic_salary"))
//                    .filter(c -> !((c.getUser().getGroup().getId() == 0) || (c.getUser().getGroup().getId() == 1)))
//                    .collect(Collectors.toList());

            System.out.println("Total Data :"+totalData.size());
//            System.out.println("Gaji :"+gaji.size());
//            System.out.println("Honor :"+honor.size());
            System.out.println("Total Data :"+pns.size());
//            System.out.println("Total Data :"+non_pns.size());

//            gaji.get(0).getUser().setId(new BigInteger("6283"));
//            gaji.get(0).getUser().setFull_name("Vita Cita Emia Tarigan");
//            gaji.get(0).getUser().setNip_nik(new BigInteger("198404182018112001"));
//            gaji.get(0).getUser().setFront_degree("Dr");
//            gaji.get(0).getUser().setBehind_degree("S.H.,L.LM");
//            gaji.get(0).getUser().setNpwp("353365950121000");
//            hitungPPH21(month,year,gaji);
//            hitungPPH21(month,year,honor);
//            hitungPPH21(month,year,totalData);
        } catch (IOException e) {
            e.printStackTrace();
        }
//            String sisaData = readFile("D:\\GajiAPBN\\sisaDataygBelumTerinput.txt", StandardCharsets.UTF_8);
//            Salary salary = new Gson().fromJson(sisaData,Salary.class);
    }


    private void hitungPPH21(String month,String tahun, List<SalaryDetail> listSalaryDetail ){
        int countGaji = 1;
        for(SalaryDetail sd : listSalaryDetail){
            Query<UserPajak> query = null;
            UserPajak p = null;
//            System.out.println("salary_id"+sd.getId());
            if(sd.getUser().getId() == null){
                System.out.println("Null id on salary_id:"+sd.getId());
                System.out.println("Full name :"+sd.getUser().getFull_name());
//                String fullNameWithDegree = sd.getUser().getFull_name();
//                String result = "%fariz%";
//                if(fullNameWithDegree.contains(".") && fullNameWithDegree.contains(",")){
//                    String[] splitDot = fullNameWithDegree.split("\\.");
//                    for(int i=0;i< splitDot.length;i++){
//                        if(splitDot[i].length() > 3){
//                            String[] splitComma = splitDot[i].split("\\,");
//                            for(int j=0;j<splitComma.length;j++){
//                                if(splitComma[j].length()>3) {
//                                    result = splitComma[j];
//                                }
//                            }
//                        }
//                    }
//                }else if(fullNameWithDegree.contains(".")){
//                    String[] splitDot = fullNameWithDegree.split("\\.");
//                    for(int i=0;i< splitDot.length;i++){
//                        if(splitDot[i].length() > 3){
//                            result = splitDot[i];
//                        }
//                    }
//                }else if(fullNameWithDegree.contains(",")){
//                    String[] splitDot = fullNameWithDegree.split("\\,");
//                    for(int i=0;i< splitDot.length;i++){
//                        if(splitDot[i].length() > 3){
//                            result = splitDot[i];
//                        }
//                    }
//                }else{
//                    result = fullNameWithDegree;
//                }
//
//                String response = null;
//                try {
//                    response = callApiUsu("https://api.usu.ac.id/1.0/users/","GET");
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                JsonArray jArr = new Gson().fromJson(response, JsonArray.class);
//                jArr.forEach(row -> {
//                    row.getAsJsonObject().get("full_name").getAsString().
//                });
//                if(jObj.get("count").getAsInt() == 1) {
//                    JsonObject jres = jObj.getAsJsonArray("data").get(0).getAsJsonObject();
//                    sd.getUser().setFull_name(jres.get("full_name").getAsString());
//                    sd.getUser().setId(BigInteger.valueOf(Integer.parseInt(jres.get("id").getAsString())));
//                    sd.getUser().setNip_nik(BigInteger.valueOf(Long.parseLong(jres.get("nip").getAsString())));
//                    sd.getUser().setFront_degree(jres.get("front_degree").getAsString());
//                    sd.getUser().setBehind_degree(jres.get("behind_degree").getAsString());
//                    sd.getUser().setNpwp(jres.get("npwp").getAsString());
//                    insertUpdateData(sd.getUser().getId().toString(), sd, month, tahun, countGaji);
//                }else{
//                    System.out.println("Ada lebih dari 2  atau tidak ada di simsdm salary id");
//                }
            }else {
                insertUpdateData(sd.getUser().getId().toString(),sd,month,tahun,countGaji);
                countGaji++;
            }
        }
    }

    private int counts = 1;

    private void insertUpdateData(String id,SalaryDetail sd, String month, String tahun, int countGaji){
        Query<UserPajak> query = datastore.createQuery(UserPajak.class).filter("id_user", id);
        UserPajak p = query.get();

        BasicDBList listPph21;
        if (p == null) { // insert data
            UserPajak user = new UserPajak();
            user.setId_user(sd.getUser().getId().toString());
            user.setNpwp("");
            user.setNpwp_simsdm(sd.getUser().getNpwp());
            user.setFront_degree(sd.getUser().getFront_degree());
            user.setFull_name(sd.getUser().getFull_name());
            user.setBehind_degree(sd.getUser().getBehind_degree());
            user.setNip_simsdm(sd.getUser().getNip_nik().toString());

            BasicDBList listPendapatan = new BasicDBList();
            BasicDBObject pendapatan = new BasicDBObject();
            Integer idType = setBasicInput(pendapatan, sd, "1", tahun);

            Iterator<Map.Entry<String, JsonElement>> iterator = sd.getPayment().getAsJsonObject().entrySet().iterator();

            BigDecimal totalPendapatanSementara = new BigDecimal("0.00");
            while (iterator.hasNext()) {
                Map.Entry<String, JsonElement> map = iterator.next();
                pendapatan.put(map.getKey(), map.getValue().getAsString());
                totalPendapatanSementara = totalPendapatanSementara.add(map.getValue().getAsBigDecimal());
            }
            pendapatan.put("bruto_pendapatan",totalPendapatanSementara.toString());

            BigDecimal potonganJabatan = totalPendapatanSementara.multiply(BigDecimal.valueOf(persenPotJabatan));
            if (potonganJabatan.compareTo(new BigDecimal("500000.00")) <= 0) {
                pendapatan.put("pot_jabatan", potonganJabatan.toString());
            } else {
                pendapatan.put("pot_jabatan", "500000.00");
            }

            if (sd.getPayment().getAsJsonObject().has("basic_salary") ) {
                BigDecimal potonganPensiun = totalPendapatanSementara.multiply(BigDecimal.valueOf(persenPotPensiun));
                pendapatan.put("pot_pensiun", potonganPensiun.toString());
            } else {
                pendapatan.put("pot_pensiun", "0.00");
            }

            BigDecimal totalPotongan = new BigDecimal("0.00");
            totalPotongan = new BigDecimal(pendapatan.get("pot_jabatan").toString())
                    .add(new BigDecimal(pendapatan.get("pot_pensiun").toString()));

            BigDecimal nettoPendapatan = totalPendapatanSementara.subtract(totalPotongan);
            pendapatan.put("netto_pendapatan", nettoPendapatan.toString());

            Integer ptkp_setahun = hitungPtkp(sd.getUser().getId().toString());
            BigDecimal ptkpSetahun = new BigDecimal(ptkp_setahun.toString());
            BigDecimal nettoPendapatanSetahun = nettoPendapatan.multiply(BigDecimal.valueOf(12));
            BigDecimal pkpSetahun,sisaPtkpSetahun;
            pendapatan.put("ptkp_setahun",ptkpSetahun.toString());
            if(nettoPendapatanSetahun.compareTo(ptkpSetahun) >= 0){
                pkpSetahun = nettoPendapatanSetahun.subtract(ptkpSetahun);
                sisaPtkpSetahun = new BigDecimal("0.00");
                pendapatan.put("pkp_setahun",pkpSetahun.toString());
                pendapatan.put("sisa_ptkp_setahun",sisaPtkpSetahun.toString());
            }else{
                pkpSetahun = new BigDecimal("0.00");
                sisaPtkpSetahun = ptkpSetahun.subtract(nettoPendapatanSetahun);
                pendapatan.put("pkp_setahun",pkpSetahun.toString());
                pendapatan.put("sisa_ptkp_setahun",sisaPtkpSetahun.toString());
            }

            TarifPajak t = new TarifPajak();
            /*if(user.getNpwp_simsdm()==null||user.getNpwp_simsdm().equalsIgnoreCase("")){
                t.hitungPajak(pkpSetahun,0,TarifPajak.LAYER_SETAHUN,TarifPajak.TARIF_NON_NPWP);
            }else{
                t.hitungPajak(pkpSetahun,0,TarifPajak.LAYER_SETAHUN,TarifPajak.TARIF_NPWP);
            }*/
            listPph21 = t.getListPph21();
            pendapatan.put("pph21",t.getListPph21());
            pendapatan.put("pph21_layer",t.getIndex().toString());
            pendapatan.put("pph21_reminder",t.getReminderPajak().toString());
            pendapatan.put("update_time",new Timestamp(new Date().getTime()).toString());


            BigDecimal ptkp_sebulan = ptkpSetahun.divide(new BigDecimal("12.00"),2,BigDecimal.ROUND_HALF_UP);
            pendapatan.put("ptkp_sebulan", ptkp_sebulan.toString());
            BigDecimal pkp_sebulan = new BigDecimal("0.00"), sisa_ptkp_sebulan = new BigDecimal("0.00");
            if (nettoPendapatan.compareTo(ptkp_sebulan) >= 0) {
                pkp_sebulan = nettoPendapatan.subtract(ptkp_sebulan);
                pendapatan.put("pkp_sebulan", pkp_sebulan.toString());
                pendapatan.put("sisa_ptkp_sebulan", sisa_ptkp_sebulan.toString());
            } else {
                sisa_ptkp_sebulan = ptkp_sebulan.subtract(nettoPendapatan);
                pendapatan.put("pkp_sebulan", pkp_sebulan.toString());
                pendapatan.put("sisa_ptkp_sebulan", sisa_ptkp_sebulan.toString());
            }

            BigDecimal total_pph21_sementara = new BigDecimal(0.00);
            for(int i=0;i<listPph21.size();i++) {
                BasicDBObject obj = (BasicDBObject) listPph21.get(i);
                total_pph21_sementara = total_pph21_sementara.add(new BigDecimal(obj.getString("hasil_sebulan")));
            }

            pendapatan.put("netto_TakeHomePay", totalPendapatanSementara.subtract(total_pph21_sementara).toString());
            pendapatan.put("update_time", new Timestamp(new Date().getTime()).toString());

            listPendapatan.add(pendapatan);
            user.setPendapatan_tdk_tetap(listPendapatan);

            user.setNetto_pendapatan_setahun(nettoPendapatan.toString());
            user.setPtkp_setahun(ptkpSetahun.toString());
            user.setSisa_ptkp(ptkpSetahun.subtract(nettoPendapatan).toString());
            user.setTotal_pkp(pkp_sebulan.toString());
            user.setTotal_pph21_usu_dibayar("0.0");
            user.setTotal_pph21_usu(total_pph21_sementara.toString());
            user.setTotal_pph21_pribadi("0");
            user.setTimestamp(new Timestamp(new Date().getTime()).toString());
            datastore.save(user);

            System.out.println("Save : "+counts+" user_id:"+user.getId_user()+" salary_id:"+sd.getId());
            counts++;

        } else { //update data
                    System.out.println("User Id :"+p.getId_user());
                    System.out.println("Salary Id :"+sd.getId());
            UpdateOperations<UserPajak> ops = datastore.createUpdateOperations(UserPajak.class);
            BasicDBList arrayListPendapatan = p.getPendapatan_tdk_tetap();
            BasicDBList newArrayListPendapatan = new BasicDBList();
            BasicDBObject pendapatanSebelumnya = (BasicDBObject) arrayListPendapatan.get(arrayListPendapatan.size() - 1);
            BasicDBObject pendapatan = new BasicDBObject();
            Integer idType = setBasicInput(pendapatan, sd, month, tahun);

            Iterator<Map.Entry<String, JsonElement>> iterator = sd.getPayment().getAsJsonObject().entrySet().iterator();

            BigDecimal totalPendapatanSementara = new BigDecimal(0.00);
            while (iterator.hasNext()) {
                Map.Entry<String, JsonElement> map = iterator.next();
                pendapatan.put(map.getKey(), map.getValue().getAsString());
                totalPendapatanSementara = totalPendapatanSementara.add(map.getValue().getAsBigDecimal());
            }

            pendapatan.put("bruto_pendapatan",totalPendapatanSementara.toString());

            BigDecimal totalPotongan = new BigDecimal(0.00);
            BigDecimal limitPotonganJabatan = new BigDecimal(0.00);
            for(int i=0;i < arrayListPendapatan.size();i++){
                BasicDBObject obj = (BasicDBObject) arrayListPendapatan.get(i);
                if(obj.get("bulan").toString().equalsIgnoreCase(month))
                    limitPotonganJabatan = limitPotonganJabatan.add(new BigDecimal(obj.getString("pot_jabatan")));
            }

            if (limitPotonganJabatan.compareTo(BigDecimal.valueOf(500000.00)) < 0) {
                BigDecimal potonganJabatan = totalPendapatanSementara.multiply(BigDecimal.valueOf(persenPotJabatan));
                if (limitPotonganJabatan.add(potonganJabatan).compareTo(BigDecimal.valueOf(500000.00)) <= 0) {
                    pendapatan.put("pot_jabatan", potonganJabatan.toString());
                } else {
                    potonganJabatan = BigDecimal.valueOf(500000.00).subtract(limitPotonganJabatan);
                    pendapatan.put("pot_jabatan", potonganJabatan.toString());
                }
            } else
                pendapatan.put("pot_jabatan", "0.00");

            if (sd.getPayment().getAsJsonObject().has("basic_salary")) {
                BigDecimal potonganPensiun = totalPendapatanSementara.multiply(BigDecimal.valueOf(persenPotPensiun));
                pendapatan.put("pot_pensiun", potonganPensiun.toString());
            } else {
                pendapatan.put("pot_pensiun", "0.00");
            }

            totalPotongan = new BigDecimal(pendapatan.get("pot_jabatan").toString())
                    .add(new BigDecimal(pendapatan.get("pot_pensiun").toString()));

            BigDecimal nettoPendapatan = totalPendapatanSementara.subtract(totalPotongan);
            pendapatan.put("netto_pendapatan", nettoPendapatan.toString());

            pendapatan.put("ptkp_setahun", pendapatanSebelumnya.getString("ptkp_setahun"));
//                        pendapatan.put("ptkp_sebulan", (String) pendapatanSebelumnya.get("ptkp_sebulan"));

            Integer bulanSebelumnya = Integer.parseInt(pendapatanSebelumnya.get("bulan").toString());
            Integer bulanSaatIni = Integer.parseInt(month);
            BigDecimal sisa_ptkp_sebulan_sebelumnya = new BigDecimal("0.00");
            if(bulanSebelumnya != bulanSaatIni){
                sisa_ptkp_sebulan_sebelumnya = new BigDecimal(pendapatanSebelumnya.get("sisa_ptkp_sebulan").toString());
                sisa_ptkp_sebulan_sebelumnya = sisa_ptkp_sebulan_sebelumnya.add(
                        new BigDecimal(pendapatanSebelumnya.getString("ptkp_setahun")).divide(
                                new BigDecimal("12.00"),2,BigDecimal.ROUND_HALF_UP));
            }else if(bulanSebelumnya == bulanSaatIni){
                sisa_ptkp_sebulan_sebelumnya = new BigDecimal(pendapatanSebelumnya.get("sisa_ptkp_sebulan").toString());
            }

            BigDecimal pkp_sebulan = new BigDecimal("0.00"), sisa_ptkp_sebulan = new BigDecimal("0.00");
            if (sisa_ptkp_sebulan_sebelumnya.compareTo(BigDecimal.ZERO) > 0) {
                if (sisa_ptkp_sebulan_sebelumnya.compareTo(nettoPendapatan) <= 0) {
                    // netto pendapatan - sisa ptkp sebulan
                    pkp_sebulan = nettoPendapatan.subtract(sisa_ptkp_sebulan_sebelumnya);
                    pendapatan.put("pkp_sebulan", pkp_sebulan.toString());
                    pendapatan.put("sisa_ptkp_sebulan", "0.00");
                } else {
                    // sisa ptkp sebulan - neto pendapatan
                    sisa_ptkp_sebulan = sisa_ptkp_sebulan_sebelumnya.subtract(nettoPendapatan);
                    pendapatan.put("pkp_sebulan", "0.00");
                    pendapatan.put("sisa_ptkp_sebulan", sisa_ptkp_sebulan.toString());
                }
            } else {
                pkp_sebulan = nettoPendapatan;
                pendapatan.put("pkp_sebulan", nettoPendapatan.toString());
                pendapatan.put("sisa_ptkp_sebulan", sisa_ptkp_sebulan.toString());
            }

            BigDecimal pengurang = new BigDecimal(pendapatanSebelumnya.get("pph21_reminder").toString());
            Integer index = Integer.parseInt(pendapatanSebelumnya.get("pph21_layer").toString());
            TarifPajak tp = new TarifPajak();
            BigDecimal pkpSetahun = pkp_sebulan.multiply(new BigDecimal("12.00"));
           /* if(pengurang.compareTo(pkpSetahun) < 0){
                if(p.getNpwp().equalsIgnoreCase("")||p.getNpwp()==null)
                    if(p.getNpwp_simsdm().equalsIgnoreCase("")||p.getNpwp_simsdm()==null)
                        tp.hitungPajak(pengurang, index, TarifPajak.LAYER_SETAHUN, TarifPajak.TARIF_NON_NPWP);
                    else
                        tp.hitungPajak(pengurang, index, TarifPajak.LAYER_SETAHUN, TarifPajak.TARIF_NPWP);
                else
                    tp.hitungPajak(pengurang, index, TarifPajak.LAYER_SETAHUN, TarifPajak.TARIF_NPWP);

                BigDecimal tambah = pkpSetahun.subtract(pengurang);
                index = tp.getIndex();
                if(index > 2)
                    index = 2;
                if(p.getNpwp().equalsIgnoreCase("")||p.getNpwp()==null)
                    if(p.getNpwp_simsdm().equalsIgnoreCase("")||p.getNpwp_simsdm()==null)
                        tp.hitungPajak(tambah, index+1, TarifPajak.LAYER_SETAHUN,TarifPajak.TARIF_NON_NPWP);
                    else
                        tp.hitungPajak(tambah, index+1, TarifPajak.LAYER_SETAHUN,TarifPajak.TARIF_NPWP);
                else
                    tp.hitungPajak(tambah, index+1, TarifPajak.LAYER_SETAHUN,TarifPajak.TARIF_NPWP);
            }else{
                if(p.getNpwp().equalsIgnoreCase("")||p.getNpwp()==null)
                    if(p.getNpwp_simsdm().equalsIgnoreCase("")||p.getNpwp_simsdm()==null)
                        tp.hitungPajak(pkpSetahun, index, TarifPajak.LAYER_SETAHUN,TarifPajak.TARIF_NON_NPWP);
                    else
                        tp.hitungPajak(pkpSetahun, index, TarifPajak.LAYER_SETAHUN,TarifPajak.TARIF_NPWP);
                else
                    tp.hitungPajak(pkpSetahun, index, TarifPajak.LAYER_SETAHUN,TarifPajak.TARIF_NPWP);
            }*/
            listPph21 = tp.getListPph21();
            pendapatan.put("pph21",listPph21);
            pendapatan.put("pph21_layer",tp.getIndex().toString());
            pendapatan.put("pph21_reminder",tp.getReminderPajak().toString());

            BigDecimal total_pph21_sementara = new BigDecimal(0.00);
            for(int i=0;i<listPph21.size();i++) {
                BasicDBObject obj = (BasicDBObject) listPph21.get(i);
                total_pph21_sementara = total_pph21_sementara.add(new BigDecimal(obj.getString("hasil_sebulan")));
            }
            pendapatan.put("netto_TakeHomePay", totalPendapatanSementara.subtract(total_pph21_sementara).toString());
            pendapatan.put("update_time", new Timestamp(new Date().getTime()).toString());

            BigDecimal netto_pendapatan_setahun = new BigDecimal(p.getNetto_pendapatan_setahun()).add(nettoPendapatan);
            ops.set("netto_pendapatan_setahun", netto_pendapatan_setahun.toString());
//                    p.setNetto_pendapatan_setahun(netto_pendapatan_setahun.toString());
            if (new BigDecimal(p.getSisa_ptkp()).compareTo(BigDecimal.ZERO) > 0) {
                if (nettoPendapatan.compareTo(new BigDecimal(p.getSisa_ptkp())) > 0) {
//                            p.setSisa_ptkp("0");
                    ops.set("sisa_ptkp", "0.00");
                } else {
                    BigDecimal sisa_ptkp = new BigDecimal(p.getSisa_ptkp()).subtract(nettoPendapatan);
//                            p.setSisa_ptkp(sisa_ptkp.toString());
                    ops.set("sisa_ptkp", sisa_ptkp.toString());
                }
            }

            BigDecimal total_pkp = new BigDecimal(p.getTotal_pkp()).add(pkp_sebulan);
//                    p.setTotal_pkp(total_pkp.toString());
            ops.set("total_pkp", total_pkp.toString());

            BigDecimal total_pph21_usu = new BigDecimal(p.getTotal_pph21_usu()).add(total_pph21_sementara);
//                    p.setTotal_pph21_usu(Integer.toString(ExcelReader.ceilCustom(String.format("%.2f",total_pph21_usu.get()))));
            ops.set("total_pph21_usu", total_pph21_usu.toString());

//                    p.setTimestamp(new Timestamp(new Date().getTime()).toString());
            ops.set("timestamp", new Timestamp(new Date().getTime()).toString());

            newArrayListPendapatan.add(pendapatan);
            ops.push("pendapatan", newArrayListPendapatan);
            datastore.update(query, ops);

        }
    }


    private Integer setBasicInput(BasicDBObject pendapatan, SalaryDetail sd, String month, String year){
        pendapatan.put("activity_id",sd.getPayment().getAsJsonObject().get("activity").getAsJsonObject().get("id").getAsString());
        pendapatan.put("activity_title",sd.getPayment().getAsJsonObject().get("activity").getAsJsonObject().get("title").getAsString());
        pendapatan.put("request_id",sd.getPayment().getAsJsonObject().get("request").getAsJsonObject().get("id").getAsString());
        pendapatan.put("salary_id",sd.getId().toString());
        pendapatan.put("type_id",sd.getPayment().getAsJsonObject().get("type").getAsJsonObject().get("id").getAsString());
        pendapatan.put("type_title",sd.getPayment().getAsJsonObject().get("type").getAsJsonObject().get("title").getAsString());
        pendapatan.put("unit_id",sd.getUnit().getId().toString());
        pendapatan.put("unit_name",sd.getUnit().getName());

        pendapatan.put("bulan",month);
        pendapatan.put("tahun",year);

        if(sd.getPayment().getAsJsonObject().has("position"))
            pendapatan.put("position",sd.getPayment().getAsJsonObject().get("position").getAsString());

        Integer idType = sd.getPayment().getAsJsonObject().get("type").getAsJsonObject().get("id").getAsInt();
        sd.getPayment().getAsJsonObject().remove("type");
        sd.getPayment().getAsJsonObject().remove("request");
        sd.getPayment().getAsJsonObject().remove("activity");
        sd.getPayment().getAsJsonObject().remove("pph21");
        sd.getPayment().getAsJsonObject().remove("pph21_pbm");
        sd.getPayment().getAsJsonObject().remove("position");

        return idType;
    }

    private BigDecimal selectPtkp(int type){
        BigDecimal result = BigDecimal.valueOf(0);
        switch (type){
            case 0 : result=BigDecimal.valueOf(54000000); break;
            case 1 : result=BigDecimal.valueOf(58500000); break;
            case 2 : result=BigDecimal.valueOf(63000000); break;
            case 3 : result=BigDecimal.valueOf(67500000); break;
        }
        return result;
    }

    public static String callApiUsu(String ep, String method) throws IOException {
//        String endpoint = "https://api.usu.ac.id/0.2/salary_receipts";
        URL obj = new URL(ep);
        HttpsURLConnection conn= (HttpsURLConnection) obj.openConnection();

        conn.setRequestMethod( method );
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Authorization", "Bearer "+getSSO("88062916081001","casper14").getToken());
        conn.setRequestProperty("AppSecret", "simrkausu");
        conn.setUseCaches( false );
        conn.setDoOutput( true );
        conn.setDoInput(true);

//        DataOutputStream wr;
//        wr = new DataOutputStream(conn.getOutputStream());
//        wr.writeBytes(postData);
//        wr.flush();
//        wr.close();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
//            System.out.println(inputLine);
        }
        in.close();
        return response.toString();
    }


//    private String sisaData = ""
}

class Salary{
    private Integer code;
    private String status;
    private SalaryReceiver response;

    public Integer getCode() {
        return code;
    }

    public String getStatus() {
        return status;
    }

    public SalaryReceiver getResponse() {
        return response;
    }
}

class SalaryReceiver{
    private List<SalaryDetail> salary_receivers;

    public List<SalaryDetail> getSalary_receivers() {
        return salary_receivers;
    }
}

class SalaryDetail{
    private Integer id;
    private User user;
    private Unit unit;
    private JsonElement payment;

    public Integer getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Unit getUnit() {
        return unit;
    }

    public JsonElement getPayment() {
        return payment;
    }
}

class User{
    private BigInteger id;
    private String full_name;
    private BigInteger nip_nik;
    private String npwp;
    private String front_degree;
    private String behind_degree;
    private Group group;

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public String getFront_degree() {
        return front_degree;
    }

    public String getBehind_degree() {
        return behind_degree;
    }

    public BigInteger getId() {
        return id;
    }

    public String getFull_name() {
        return full_name;
    }

    public BigInteger getNip_nik() {
        return nip_nik;
    }

    public String getNpwp() {
        return npwp;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public void setFront_degree(String front_degree) {
        this.front_degree = front_degree;
    }

    public void setBehind_degree(String behind_degree) {
        this.behind_degree = behind_degree;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public void setNpwp(String npwp) {
        this.npwp = npwp;
    }

    public void setNip_nik(BigInteger nip_nik) {
        this.nip_nik = nip_nik;
    }
}

class Group{
    private Integer id;
    private String title;

    public void setId(Integer id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public Integer getId() {
        return id;
    }
}

class Unit{
    private Integer id;
    private String type;
    private String name;

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}

class Token{
    private String token;

    public String getToken() {
        return token;
    }

}
