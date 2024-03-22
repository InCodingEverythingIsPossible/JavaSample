package JsonManipulation;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

public class JsonManipulation {

    JSONObject event72;
    public  SurpriseLine(String json){

        event72 = new JSONObject(json);

    }

    public void CreateSurpriseLine(){


        JSONObject jsonBefore = event72;

        JSONObject jsonDetail = jsonBefore.getJSONObject("json_detail");

        JSONArray IPGcomments = jsonDetail.getJSONArray("IPGComments");

        JSONArray receiptLines = jsonDetail.getJSONArray("line_list");

        int NumberOfLines = 0;

        /////////////////////////////////Find a Number Lines/////////////////////////////////

        for(int x=0;x<IPGcomments.length();x++){
            JSONObject IPG = (JSONObject)IPGcomments.get(x);
            try {
                int LNU_Comment = Integer.parseInt(IPG.getString("COMM_LNU"));
                if (NumberOfLines < LNU_Comment) {
                    NumberOfLines = LNU_Comment;
                }
            }catch(Exception e){
                continue;
            }
        }

        for(int z=0;z < receiptLines.length();z++){
            JSONObject CountReceiptLine = (JSONObject) receiptLines.get(z);
            for(int p=0;p<CountReceiptLine.getJSONArray("comment_list").length();p++){
                JSONObject LNUcomment = (JSONObject) CountReceiptLine.getJSONArray("comment_list").get(p);
                if(LNUcomment.getString("group").equals("LNU")){
                    int LNUvalue = Integer.parseInt(LNUcomment.getString("comment"));
                    if(NumberOfLines<LNUvalue){
                        NumberOfLines=LNUvalue;
                    }

                }
            }
        }
        System.out.println("Number of Lines: "+NumberOfLines);

        for(int i = 0; i< receiptLines.length(); i++){

            JSONObject receiptLine = (JSONObject) receiptLines.get(i);
            if(receiptLine.isNull("manufacture_date")){
                continue;
            }
            else {
                String ReceiptLineManufactureDate = receiptLine.getString("manufacture_date");

                JSONArray detailList = receiptLine.getJSONArray("detail_list");

                if(detailList.length()==0){
                    continue;
                }
                else{
                    for(int j=0; j<detailList.length();j++){

                        JSONObject detailLine = (JSONObject) detailList.get(j);
                        String DetailLineManufactureDate = detailLine.getString("manufacture_date");


                        if(ReceiptLineManufactureDate.equals(DetailLineManufactureDate)){
                            continue;
                        }
                        /////////////////////////////////Find a Suprise Line/////////////////////////////////
                        else{
                            System.out.println("ReceiptLine"+receiptLine.getInt("line_number"));
                            System.out.println("ReceiptDetailLine"+j);
                            /////////////////////////////////Change Quantity on Mother Line/////////////////////////////////

                            int baseLVQuantity = detailLine.getInt("base_lv_quantity");

                            int baseLVQuantityConfirmed = receiptLine.getInt("base_lv_quantity_validated");

                            receiptLine.put("base_lv_quantity_validated",baseLVQuantityConfirmed-baseLVQuantity);

                            /////////////////////////////////Decide to insert on Existing Line or Create a new/////////////////////////////////

                            String itemcode = receiptLine.getString("item_code");
                            String lvCode = receiptLine.getString("item_lv_code");
                            for(int a=0; a<receiptLines.length();a++){

                                JSONObject receiptLinetoCheck = (JSONObject) receiptLines.get(a);

                                if(receiptLinetoCheck.has("manufacture_date") && !receiptLinetoCheck.isNull("manufacture_date")){


                                    /*System.out.println("Linia recepcyjna ->"+a);
                                    System.out.println("ManufacturedateDetailList ->" +DetailLineManufactureDate);
                                    System.out.println(receiptLinetoCheck.getString("manufacture_date"));
                                    System.out.println("ItemCodeDetailList ->" +itemcode );
                                    System.out.println(receiptLinetoCheck.getString("item_code"));
                                    System.out.println("LvCodeDetailList ->" +lvCode);
                                    System.out.println(receiptLinetoCheck.getString("item_lv_code"));*/

                                    if(receiptLinetoCheck.getString("manufacture_date").equals(DetailLineManufactureDate)
                                            && receiptLinetoCheck.getString("item_code").equals(itemcode)
                                            && receiptLinetoCheck.getString("item_lv_code").equals(lvCode)){

                                        /////////////////////////////////Insert on Existing Line/////////////////////////////////
                                        int QuantityToUpdate = receiptLinetoCheck.getInt("base_lv_quantity_validated");
                                        receiptLinetoCheck.put("base_lv_quantity_validated",QuantityToUpdate+baseLVQuantity);

                                        JSONArray addNewDetailListInExistingLine = receiptLinetoCheck.getJSONArray("detail_list");
                                        addNewDetailListInExistingLine.put(detailLine);
                                        System.out.println(detailList);
                                        break;
                                    }
                                    else if(receiptLines.length()-1==a){

                                        String comment = receiptLine.getJSONArray("comment_list").toString();

                                        JSONArray comments = new JSONArray(comment);
                                        System.out.println(comments);

                                        NumberOfLines=NumberOfLines+1;

                                        /////////////////////////////////Create a Suprise Line/////////////////////////////////
                                        System.out.println("Create a new Line");
                                        JSONObject newLine = new JSONObject();
                                        newLine.put("base_lv_quantity_validated",baseLVQuantity);
                                        newLine.put("base_lv_quantity",0);
                                        newLine.put("manufacture_date",DetailLineManufactureDate);
                                        newLine.put("item_code",itemcode);
                                        newLine.put("item_lv_code",lvCode);
                                        newLine.put("line_number",NumberOfLines*100);
                                        newLine.put("missing_code"," ");
                                        newLine.put("flag_missing","False");
                                        newLine.put("comment_list",comments);
                                        newLine.put("surprise_line","True");


                                        /////////////////////////////////Change LNU comment/////////////////////////////////
                                        for(int p=0;p<newLine.getJSONArray("comment_list").length();p++){
                                            JSONObject LNUcomment = (JSONObject) newLine.getJSONArray("comment_list").get(p);
                                            if(LNUcomment.getString("group").equals("LNU")){
                                                LNUcomment.put("comment",NumberOfLines);
                                            }
                                        }
                                        System.out.println(newLine);


                                        JSONArray newDetailList = new JSONArray();
                                        newDetailList.put(detailLine);

                                        newLine.put("detail_list",newDetailList);

                                        detailList.remove(j);
                                        j=-1;

                                        System.out.println(newLine);

                                        System.out.println("-------------------------");

                                        receiptLines.put(newLine);
                                        break;
                                    }
                                    else{
                                        continue;
                                    }
                                }

                                else{
                                    if(receiptLines.length()-1==a){



                                        String comment = receiptLine.getJSONArray("comment_list").toString();

                                        JSONArray comments = new JSONArray(comment);
                                        System.out.println(comments);

                                        NumberOfLines=NumberOfLines+1;

                                        /////////////////////////////////Create a Suprise Line/////////////////////////////////
                                        System.out.println("Create a new Line");
                                        JSONObject newLine = new JSONObject();
                                        newLine.put("base_lv_quantity_validated",baseLVQuantity);
                                        newLine.put("base_lv_quantity",0);
                                        newLine.put("manufacture_date",DetailLineManufactureDate);
                                        newLine.put("item_code",itemcode);
                                        newLine.put("item_lv_code",lvCode);
                                        newLine.put("line_number",NumberOfLines*100);
                                        newLine.put("missing_code"," ");
                                        newLine.put("flag_missing","False");
                                        newLine.put("comment_list",comments);
                                        newLine.put("surprise_line","True");


                                        /////////////////////////////////Change LNU comment/////////////////////////////////
                                        for(int p=0;p<newLine.getJSONArray("comment_list").length();p++){
                                            JSONObject LNUcomment = (JSONObject) newLine.getJSONArray("comment_list").get(p);
                                            if(LNUcomment.getString("group").equals("LNU")){
                                                LNUcomment.put("comment",NumberOfLines);
                                            }
                                        }
                                        System.out.println(newLine);

                                        JSONArray newDetailList = new JSONArray();
                                        newDetailList.put(detailLine);

                                        newLine.put("detail_list",newDetailList);

                                        detailList.remove(j);
                                        j=-1;

                                        System.out.println(newLine);

                                        System.out.println("-------------------------");

                                        receiptLines.put(newLine);
                                        break;
                                    }

                                }

                            }

                        }

                    }
                }
            }


        }

        //System.out.println(jsonBefore);

    }
    public String getJson(){
        return event72.toString();
    }


}


    Process finished with exit code 0

