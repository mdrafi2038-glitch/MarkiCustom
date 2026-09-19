package com.memotracker.app;

import android.app.*;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.content.*;
import android.view.*;
import android.widget.*;
import org.json.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainActivity extends Activity {
    LinearLayout root, list;
    TextView totalView;
    EditText productInput, qtyInput;
    SharedPreferences prefs;
    String dateKey;
    JSONObject data = new JSONObject();

    int dp(float n){ return (int)(n*getResources().getDisplayMetrics().density+0.5f); }
    TextView tv(String s,float sp,boolean bold){
        TextView t=new TextView(this); t.setText(s); t.setTextSize(sp); t.setTextColor(Color.rgb(23,49,45));
        t.setTypeface(null,bold?Typeface.BOLD:Typeface.NORMAL); return t;
    }
    Button btn(String s){
        Button b=new Button(this); b.setText(s); b.setTextSize(14); b.setAllCaps(false);
        b.setTextColor(Color.WHITE); b.setBackgroundColor(Color.rgb(0,105,92)); return b;
    }

    @Override public void onCreate(Bundle b){
        super.onCreate(b);
        prefs=getSharedPreferences("memo",MODE_PRIVATE);
        dateKey=new SimpleDateFormat("yyyy-MM-dd",Locale.US).format(new Date());
        load(); build();
    }
    void load(){
        try{ data=new JSONObject(prefs.getString("data_"+dateKey,"{}")); }
        catch(Exception e){ data=new JSONObject(); }
    }
    void save(){ prefs.edit().putString("data_"+dateKey,data.toString()).apply(); }
    int qty(String n){ return data.optInt(n,0); }
    void setQty(String n,int q){
        try{ if(q<=0)data.remove(n); else data.put(n,q); }catch(Exception ignored){}
        save(); refresh();
    }

    void build(){
        ScrollView scroll=new ScrollView(this);
        root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(14),dp(10),dp(14),dp(18)); scroll.addView(root);

        LinearLayout bar=new LinearLayout(this); bar.setGravity(Gravity.CENTER_VERTICAL);
        TextView title=tv("Memo Tracker",21,true); title.setTextColor(Color.WHITE); title.setPadding(dp(16),0,0,0);
        bar.setBackgroundColor(Color.rgb(0,105,92)); bar.addView(title,new LinearLayout.LayoutParams(0,dp(58),1));
        Button hist=btn("History"); hist.setTextSize(13); bar.addView(hist,new LinearLayout.LayoutParams(dp(90),dp(48)));
        root.addView(bar,new LinearLayout.LayoutParams(-1,dp(58)));
        hist.setOnClickListener(v->showHistory());

        LinearLayout dateRow=new LinearLayout(this); dateRow.setGravity(Gravity.CENTER_VERTICAL);
        TextView date=tv("📅 "+new SimpleDateFormat("dd MMM yyyy (EEE)",Locale.US).format(new Date()),14,true);
        dateRow.addView(date,new LinearLayout.LayoutParams(0,dp(52),1));
        Button clear=btn("Clear Day"); clear.setTextSize(12); dateRow.addView(clear,new LinearLayout.LayoutParams(dp(95),dp(44)));
        root.addView(dateRow);
        clear.setOnClickListener(v->new AlertDialog.Builder(this).setTitle("Clear today?").setMessage("All today's quantities will be deleted.").setNegativeButton("Cancel",null).setPositiveButton("Clear",(d,w)->{data=new JSONObject();save();refresh();}).show());

        LinearLayout card=new LinearLayout(this); card.setOrientation(LinearLayout.VERTICAL); card.setPadding(dp(12),dp(12),dp(12),dp(12)); card.setBackgroundColor(Color.rgb(232,245,243));
        card.addView(tv("Add quantity to a product",15,true));
        LinearLayout inputs=new LinearLayout(this); inputs.setGravity(Gravity.CENTER_VERTICAL);
        productInput=new EditText(this); productInput.setHint("Product name"); productInput.setSingleLine(true);
        qtyInput=new EditText(this); qtyInput.setHint("Qty"); qtyInput.setInputType(2); qtyInput.setSingleLine(true);
        inputs.addView(productInput,new LinearLayout.LayoutParams(0,dp(52),1));
        inputs.addView(qtyInput,new LinearLayout.LayoutParams(dp(80),dp(52))); card.addView(inputs);
        Button add=btn("Save / Add"); card.addView(add,new LinearLayout.LayoutParams(-1,dp(48)));
        root.addView(card,new LinearLayout.LayoutParams(-1,dp(155)));
        add.setOnClickListener(v->addMemo());

        totalView=tv("Total Quantity: 0 pcs",17,true); totalView.setPadding(dp(4),dp(16),dp(4),dp(10)); root.addView(totalView);
        list=new LinearLayout(this); list.setOrientation(LinearLayout.VERTICAL); root.addView(list);
        setContentView(scroll); refresh();
    }

    void addMemo(){
        String n=productInput.getText().toString().trim(), qs=qtyInput.getText().toString().trim();
        if(n.isEmpty()){productInput.setError("Product name");return;}
        if(qs.isEmpty()){qtyInput.setError("Quantity");return;}
        int q; try{q=Integer.parseInt(qs);}catch(Exception e){qtyInput.setError("Number");return;}
        if(q==0)return; setQty(n,qty(n)+q);
        productInput.setText(""); qtyInput.setText("");
        Toast.makeText(this,"Saved: "+n+" +"+q+" pcs",Toast.LENGTH_SHORT).show();
    }

    void refresh(){
        if(list==null)return; list.removeAllViews(); int total=0;
        ArrayList<String> names=new ArrayList<>(); Iterator<String> it=data.keys(); while(it.hasNext())names.add(it.next());
        Collections.sort(names,String.CASE_INSENSITIVE_ORDER);
        if(names.isEmpty()){ TextView e=tv("No products yet. Add your first memo above.",15,false); e.setTextColor(Color.GRAY); e.setPadding(dp(8),dp(22),dp(8),dp(22)); list.addView(e); }
        for(String n:names){
            int q=qty(n); total+=q;
            LinearLayout row=new LinearLayout(this); row.setGravity(Gravity.CENTER_VERTICAL); row.setPadding(dp(10),dp(7),dp(5),dp(7)); row.setBackgroundColor(Color.WHITE);
            TextView name=tv(n,16,true); row.addView(name,new LinearLayout.LayoutParams(0,dp(54),1));
            Button minus=btn("−"); minus.setTextSize(20); row.addView(minus,new LinearLayout.LayoutParams(dp(52),dp(48)));
            TextView val=tv(q+" pcs",15,true); val.setGravity(Gravity.CENTER); row.addView(val,new LinearLayout.LayoutParams(dp(72),dp(48)));
            Button plus=btn("+"); plus.setTextSize(20); row.addView(plus,new LinearLayout.LayoutParams(dp(52),dp(48)));
            minus.setOnClickListener(v->setQty(n,qty(n)-1)); plus.setOnClickListener(v->setQty(n,qty(n)+1));
            row.setOnLongClickListener(v->{showProductDetail(n);return true;});
            list.addView(row,new LinearLayout.LayoutParams(-1,dp(68)));
        }
        totalView.setText("Total Quantity: "+total+" pcs");
    }

    void showProductDetail(String n){
        new AlertDialog.Builder(this).setTitle(n).setMessage("Current quantity: "+qty(n)+" pcs\n\nUse + / − on the list to change one piece at a time.")
            .setPositiveButton("OK",null).setNegativeButton("Delete",(d,w)->{data.remove(n);save();refresh();}).show();
    }

    void showHistory(){
        LinearLayout box=new LinearLayout(this); box.setOrientation(LinearLayout.VERTICAL); box.setPadding(dp(16),dp(8),dp(16),dp(8));
        ArrayList<String> dates=new ArrayList<>(); for(String k:prefs.getAll().keySet())if(k.startsWith("data_"))dates.add(k.substring(5));
        Collections.sort(dates,Collections.reverseOrder());
        if(dates.isEmpty())box.addView(tv("No saved history.",15,false));
        for(String d:dates){
            TextView line=tv(d,15,false); line.setPadding(0,dp(12),0,dp(12)); box.addView(line);
            line.setOnClickListener(v->{
                try{JSONObject o=new JSONObject(prefs.getString("data_"+d,"{}"));StringBuilder s=new StringBuilder();Iterator<String>x=o.keys();while(x.hasNext()){String n=x.next();s.append(n).append(" = ").append(o.optInt(n)).append(" pcs\n");}
                    new AlertDialog.Builder(this).setTitle("Summary: "+d).setMessage(s.length()==0?"No entries":s.toString()).setPositiveButton("OK",null).show();
                }catch(Exception ignored){}
            });
        }
        new AlertDialog.Builder(this).setTitle("History").setView(box).setPositiveButton("Close",null).show();
    }
}
