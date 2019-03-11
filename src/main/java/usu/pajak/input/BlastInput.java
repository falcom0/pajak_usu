package usu.pajak.input;

import usu.pajak.services.ApiRka;
import usu.pajak.util.PnsApbn;

import java.math.BigDecimal;
import java.util.List;

public class BlastInput {

    public BlastInput(){

    }

    public static void main(String[] args){
//        new PnsApbn();
//        new ApiRka().asalData("1","2019");
        new ApiRka().asalData("2","2019");
//        new ApiRka().asalData("3","2019");
       /* String fullNameWithDegree = "Dr.Vita Cita Ernia Tarigan, SH.LLM";
        String b = "Vita Cita Emia Tarigan";
        String result = "-";
//        System.out.println(a.matches(b));
        if(fullNameWithDegree.contains(".") && fullNameWithDegree.contains(",")){
            String[] splitDot = fullNameWithDegree.split("\\.");
            for(int i=0;i< splitDot.length;i++){
                if(splitDot[i].length() > 3){
                    String[] splitComma = splitDot[i].split("\\,");
                    for(int j=0;j<splitComma.length;j++){
                        if(splitComma[j].length()>3) {
                            result = splitComma[j];
//                            return;
                        }
                    }
                }
            }
        }else if(fullNameWithDegree.contains(".")){
            String[] splitDot = fullNameWithDegree.split("\\.");
            for(int i=0;i< splitDot.length;i++){
                if(splitDot[i].length() > 3){
                    result = splitDot[i];
//                    return;
                }
            }
        }else if(fullNameWithDegree.contains(",")){
            String[] splitDot = fullNameWithDegree.split("\\,");
            for(int i=0;i< splitDot.length;i++){
                if(splitDot[i].length() > 3){
                    result = splitDot[i];
//                    return;
                }
            }
        }else{
            result = fullNameWithDegree;
        }
        System.out.println(fullNameWithDegree.matches(".*"+b+".*"));
        System.out.println(b.contains(fullNameWithDegree));*/
    }

}
