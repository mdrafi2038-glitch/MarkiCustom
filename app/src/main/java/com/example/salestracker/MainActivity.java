package com.example.salestracker;

import android.app.*;
import android.os.*;
import android.content.*;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import java.util.*;

public class MainActivity extends Activity {
    LinearLayout content;
    String currentScreen = "home";
    final int NAVY=Color.rgb(10,35,68), TEAL=Color.rgb(0,165,130), BLUE=Color.rgb(25,112,225), PURPLE=Color.rgb(111,78,190), BG=Color.rgb(246,248,252), TEXT=Color.rgb(35,48,65), MUTED=Color.rgb(105,116,130);
    ArrayList<Product> products=new ArrayList<>();
    SharedPreferences prefs;

    static class Product { String name; double pct, cartonPrice; Product(String n,double p,double c){name=n;pct=p;cartonPrice=c;} }

    int dp(float v){ return (int)(v*getResources().getDisplayMetrics().density+0.5f); }
    int sp(float v){ return (int)v; }
    LinearLayout.LayoutParams lp(int w,int h){ return new LinearLayout.LayoutParams(w,h); }
    LinearLayout.LayoutParams lpw(int h,float weight){ return new LinearLayout.LayoutParams(0,h,weight); }

    @Override public void onCreate(Bundle b){
        super.onCreate(b);
        getWindow().setStatusBarColor(NAVY);
        getWindow().setNavigationBarColor(NAVY);
        prefs=getSharedPreferences("data",0); load(); home();
    }

    @Override public void onBackPressed(){
        if(!currentScreen.equals("home")){ home(); } else { super.onBackPressed(); }
    }

    void load(){
        String raw=prefs.getString("products","");
        if(raw.isEmpty()){
            products.add(new Product("Korean 40gm Chain",31,2250));
            products.add(new Product("4pcs",9,1728));
            products.add(new Product("8pcs CP",8,1692));
            products.add(new Product("14pcs",12,2120));
            products.add(new Product("Low Fat",5,1020));
            products.add(new Product("Ramen 5pcs",14,2400));
            products.add(new Product("Nata",3,550));
            products.add(new Product("Ice Lolly",5,989));
            products.add(new Product("Jhal Boroi",6,1407));
            products.add(new Product("Jelly",7,1350));
        } else {
            for(String s:raw.split("\\|")){
                if(s.trim().isEmpty()) continue;
                String[] x=s.split("~",-1);
                try{products.add(new Product(x[0],Double.parseDouble(x[1]),Double.parseDouble(x[2])));}catch(Exception ignored){}
            }
        }
    }

    void save(){
        StringBuilder s=new StringBuilder();
        for(Product p:products) s.append(p.name.replace("~"," ")).append("~").append(p.pct).append("~").append(p.cartonPrice).append("|");
        prefs.edit().putString("products",s.toString()).apply();
    }

    GradientDrawable bg(int color,float radius){ GradientDrawable g=new GradientDrawable(); g.setColor(color); g.setCornerRadius(dp(radius)); return g; }
    GradientDrawable strokeBg(int color,int stroke,float radius){ GradientDrawable g=bg(Color.WHITE,radius); g.setStroke(dp(1),stroke); return g; }

    TextView text(String s,float size,int color){
        TextView v=new TextView(this); v.setText(s); v.setTextSize(size); v.setTextColor(color); v.setGravity(Gravity.CENTER_VERTICAL); v.setIncludeFontPadding(true); return v;
    }
    TextView label(String s){ TextView v=text(s,14,TEXT); v.setTypeface(Typeface.DEFAULT,Typeface.BOLD); v.setPadding(0,dp(4),0,dp(7)); return v; }
    EditText edit(String hint,String val){
        EditText e=new EditText(this); e.setHint(hint); e.setText(val); e.setTextSize(15); e.setTextColor(TEXT); e.setHintTextColor(Color.rgb(150,158,170)); e.setSingleLine(true); e.setPadding(dp(12),0,dp(12),0); e.setBackground(strokeBg(Color.rgb(224,230,238),Color.rgb(224,230,238),12)); e.setSelectAllOnFocus(false); return e;
    }
    Button button(String s,int color){
        Button b=new Button(this); b.setText(s); b.setTextColor(Color.WHITE); b.setTextSize(14); b.setTypeface(Typeface.DEFAULT,Typeface.BOLD); b.setAllCaps(false); b.setGravity(Gravity.CENTER); b.setMinHeight(0); b.setMinWidth(0); b.setPadding(dp(12),0,dp(12),0); b.setBackground(bg(color,14)); return b;
    }

    void base(String title,String screen){
        currentScreen=screen;
        LinearLayout root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setBackgroundColor(BG);
        LinearLayout bar=new LinearLayout(this); bar.setGravity(Gravity.CENTER_VERTICAL); bar.setPadding(dp(8),0,dp(10),0); bar.setBackgroundColor(NAVY); root.addView(bar,lp(-1,dp(64)));
        if(!screen.equals("home")){
            TextView back=text("‹",34,Color.WHITE); back.setGravity(Gravity.CENTER); back.setOnClickListener(v->home()); bar.addView(back,lp(dp(44),dp(64)));
        }
        TextView t=text(title,20,Color.WHITE); t.setTypeface(Typeface.DEFAULT,Typeface.BOLD); t.setPadding(dp(6),0,0,0); bar.addView(t,lpw(dp(64),1));
        ScrollView sc=new ScrollView(this); sc.setFillViewport(true); sc.setClipToPadding(false);
        content=new LinearLayout(this); content.setOrientation(LinearLayout.VERTICAL); content.setPadding(dp(16),dp(16),dp(16),dp(24)); sc.addView(content); root.addView(sc,lpw(0,1)); setContentView(root);
    }
    void add(View v){ content.addView(v,lp(-1,-2)); }
    void gap(int h){ Space s=new Space(this); content.addView(s,lp(-1,dp(h))); }
    void setMargin(View v,int top,int bottom){ ViewGroup.LayoutParams q=v.getLayoutParams(); if(q instanceof LinearLayout.LayoutParams){ LinearLayout.LayoutParams p=(LinearLayout.LayoutParams)q; p.topMargin=dp(top); p.bottomMargin=dp(bottom); v.setLayoutParams(p); } }
    double num(String s){ try{return Double.parseDouble(s.replace(",","").replace("৳","").trim());}catch(Exception e){return 0;} }
    String money(double n){ return "৳ "+String.format(Locale.US,"%,.0f",n); }
    String fmt(double n){ return Math.abs(n-Math.round(n))<.001?String.valueOf(Math.round(n)):String.format(Locale.US,"%.2f",n); }
    double pctTotal(){ double n=0; for(Product p:products)n+=p.pct; return n; }
    long cartons(double amount,double price){ return price>0?(long)Math.ceil(amount/price):0; }

    void home(){
        base("Sales Tracker","home");
        LinearLayout welcome=new LinearLayout(this); welcome.setOrientation(LinearLayout.VERTICAL); welcome.setPadding(dp(18),dp(16),dp(18),dp(16)); welcome.setBackground(bg(Color.WHITE,18));
        TextView a=text("Welcome Back!",25,NAVY); a.setTypeface(Typeface.DEFAULT,Typeface.BOLD); welcome.addView(a,lp(-1,dp(38)));
        TextView b=text("Plan smart. Sell better.",16,MUTED); welcome.addView(b,lp(-1,dp(30))); add(welcome); gap(14);
        homeCard("🎯","Sales Planning Maker","Set a target and create product-wise planning.",TEAL,v->planning()); gap(12);
        homeCard("◔","Product Wise Sales","Enter sales → estimate → edit → final result.",BLUE,v->wise()); gap(12);
        homeCard("⚙","Product Setup","Manage product name, sales % and carton price.",PURPLE,v->setup()); gap(18);
        LinearLayout info=new LinearLayout(this); info.setGravity(Gravity.CENTER); info.setPadding(dp(8),dp(12),dp(8),dp(12)); info.setBackground(bg(Color.WHITE,16));
        TextView i=text("Products: "+products.size(),14,TEXT); i.setTypeface(Typeface.DEFAULT,Typeface.BOLD); info.addView(i,lpw(dp(30),1));
        TextView j=text("Total %: "+fmt(pctTotal())+"%",14,TEAL); j.setGravity(Gravity.CENTER); j.setTypeface(Typeface.DEFAULT,Typeface.BOLD); info.addView(j,lpw(dp(30),1)); add(info);
    }

    void homeCard(String icon,String title,String sub,int color,View.OnClickListener listener){
        LinearLayout card=new LinearLayout(this); card.setOrientation(LinearLayout.VERTICAL); card.setPadding(dp(18),dp(15),dp(18),dp(15)); card.setBackground(bg(Color.WHITE,18)); card.setOnClickListener(listener);
        LinearLayout head=new LinearLayout(this); head.setGravity(Gravity.CENTER_VERTICAL);
        TextView ic=text(icon,25,color); ic.setGravity(Gravity.CENTER); head.addView(ic,lp(dp(42),dp(42)));
        TextView tt=text(title,20,color); tt.setTypeface(Typeface.DEFAULT,Typeface.BOLD); tt.setPadding(dp(8),0,0,0); head.addView(tt,lpw(dp(42),1)); card.addView(head);
        TextView ss=text(sub,14,MUTED); ss.setPadding(dp(50),dp(5),0,0); card.addView(ss,lp(-1,dp(32))); add(card);
    }

    void planning(){
        base("Sales Planning Maker","planning");
        TextView intro=text("Create your target plan",18,NAVY); intro.setTypeface(Typeface.DEFAULT,Typeface.BOLD); add(intro); gap(6);
        add(label("Total Target")); EditText target=edit("Enter target amount","500000"); add(target); gap(10);
        Button calc=button("Calculate Planning",TEAL); add(calc); gap(14);
        LinearLayout wrap=new LinearLayout(this); wrap.setOrientation(LinearLayout.VERTICAL); wrap.setPadding(dp(10),dp(10),dp(10),dp(10)); wrap.setBackground(bg(Color.WHITE,18)); add(wrap);
        Runnable render=()->{
            wrap.removeAllViews();
            TextView head=text("PRODUCT                 %        AMOUNT        CARTON",12,NAVY); head.setTypeface(Typeface.DEFAULT,Typeface.BOLD); wrap.addView(head,lp(-1,dp(34)));
            double total=num(target.getText().toString()), sum=0; long cs=0;
            for(Product p:products){
                double amount=total*p.pct/100; long c=cartons(amount,p.cartonPrice);
                LinearLayout row=new LinearLayout(this); row.setGravity(Gravity.CENTER_VERTICAL); row.setPadding(0,dp(4),0,dp(4));
                TextView n=text(p.name,13,TEXT); n.setPadding(dp(2),0,dp(6),0); row.addView(n,lpw(dp(48),2.2f));
                EditText ep=compactEdit(fmt(p.pct)); row.addView(ep,lp(dp(58),dp(46)));
                EditText ea=compactEdit(String.format(Locale.US,"%.0f",amount)); row.addView(ea,lp(dp(92),dp(46)));
                EditText ec=compactEdit(String.valueOf(c)); row.addView(ec,lp(dp(60),dp(46)));
                wrap.addView(row,lp(-1,dp(54))); sum+=amount; cs+=c;
            }
            TextView total=text("TOTAL     "+fmt(pctTotal())+"%     "+money(sum)+"     "+cs,14,TEAL); total.setTypeface(Typeface.DEFAULT,Typeface.BOLD); total.setPadding(dp(4),dp(10),0,dp(4)); wrap.addView(total,lp(-1,dp(42)));
        };
        calc.setOnClickListener(v->{hideKeyboard();render.run();}); render.run();
    }

    EditText compactEdit(String value){ EditText e=edit("",value); e.setTextSize(13); e.setGravity(Gravity.CENTER); e.setPadding(dp(3),0,dp(3),0); return e; }

    void wise(){
        base("Product Wise Sales","wise");
        TextView intro=text("Estimate product-wise sales",18,NAVY); intro.setTypeface(Typeface.DEFAULT,Typeface.BOLD); add(intro); gap(6);
        add(label("Total Sales")); EditText sales=edit("Enter total sales","15200"); add(sales); gap(10);
        Button get=button("Get Estimated Result",BLUE); add(get); gap(14);
        LinearLayout wrap=new LinearLayout(this); wrap.setOrientation(LinearLayout.VERTICAL); wrap.setPadding(dp(10),dp(10),dp(10),dp(10)); wrap.setBackground(bg(Color.WHITE,18)); add(wrap);
        Runnable render=()->{
            wrap.removeAllViews(); double total=num(sales.getText().toString());
            TextView note=text("Estimated values — you can edit all 3 columns",14,NAVY); note.setTypeface(Typeface.DEFAULT,Typeface.BOLD); wrap.addView(note,lp(-1,dp(36)));
            TextView head=text("PRODUCT                 %        AMOUNT        CARTON",12,NAVY); head.setTypeface(Typeface.DEFAULT,Typeface.BOLD); wrap.addView(head,lp(-1,dp(34)));
            for(Product p:products){
                double amount=total*p.pct/100; long c=cartons(amount,p.cartonPrice);
                LinearLayout row=new LinearLayout(this); row.setGravity(Gravity.CENTER_VERTICAL); row.setPadding(0,dp(4),0,dp(4));
                TextView n=text(p.name,13,TEXT); n.setPadding(dp(2),0,dp(6),0); row.addView(n,lpw(dp(48),2.2f));
                row.addView(compactEdit(fmt(p.pct)),lp(dp(58),dp(46)));
                row.addView(compactEdit(String.format(Locale.US,"%.0f",amount)),lp(dp(92),dp(46)));
                row.addView(compactEdit(String.valueOf(c)),lp(dp(60),dp(46)));
                wrap.addView(row,lp(-1,dp(54)));
            }
            Button fin=button("✓  Final Result",TEAL); wrap.addView(fin,lp(-1,dp(50))); fin.setOnClickListener(v->{hideKeyboard();finalResult(total,wrap);});
        };
        get.setOnClickListener(v->{hideKeyboard();render.run();}); render.run();
    }

    void finalResult(double total,LinearLayout wrap){
        base("Final Result","final");
        double ps=0,as=0; long cs=0; ArrayList<String[]> rows=new ArrayList<>();
        for(int i=2;i<wrap.getChildCount();i++){
            View vv=wrap.getChildAt(i); if(!(vv instanceof LinearLayout))continue; LinearLayout r=(LinearLayout)vv; if(r.getChildCount()<4)continue;
            try{
                String n=((TextView)r.getChildAt(0)).getText().toString(); double p=num(((EditText)r.getChildAt(1)).getText().toString()); double a=num(((EditText)r.getChildAt(2)).getText().toString()); long c=Math.round(num(((EditText)r.getChildAt(3)).getText().toString()));
                ps+=p;as+=a;cs+=c;rows.add(new String[]{n,fmt(p)+"%",money(a),String.valueOf(c)});
            }catch(Exception ignored){}
        }
        TextView title=text("✓ Final Product-Wise Sales",21,TEAL); title.setTypeface(Typeface.DEFAULT,Typeface.BOLD); add(title); gap(8);
        LinearLayout summary=new LinearLayout(this); summary.setOrientation(LinearLayout.VERTICAL); summary.setPadding(dp(16),dp(14),dp(16),dp(14)); summary.setBackground(bg(Color.WHITE,18));
        addTo(summary,"Input Sales",money(total)); addTo(summary,"Final Amount",money(as)); addTo(summary,"Total Carton",String.valueOf(cs)); addTo(summary,"Total Percentage",fmt(ps)+"%"); add(summary); gap(12);
        LinearLayout table=new LinearLayout(this); table.setOrientation(LinearLayout.VERTICAL); table.setPadding(dp(10),dp(8),dp(10),dp(8)); table.setBackground(bg(Color.WHITE,18));
        LinearLayout h=new LinearLayout(this); addCell(h,"PRODUCT",2.2f,NAVY,true); addCell(h,"%",0.7f,NAVY,true); addCell(h,"AMOUNT",1.4f,NAVY,true); addCell(h,"CARTON",0.9f,NAVY,true); table.addView(h,lp(-1,dp(38)));
        for(String[] x:rows){ LinearLayout r=new LinearLayout(this); addCell(r,x[0],2.2f,TEXT,false); addCell(r,x[1],0.7f,TEXT,false); addCell(r,x[2],1.4f,TEXT,false); addCell(r,x[3],0.9f,TEXT,false); table.addView(r,lp(-1,dp(44))); }
        add(table); gap(14); Button b=button("Back to Home",TEAL); add(b); b.setOnClickListener(v->home());
    }
    void addTo(LinearLayout p,String a,String b){ LinearLayout r=new LinearLayout(this); TextView x=text(a,14,MUTED); TextView y=text(b,15,NAVY); y.setTypeface(Typeface.DEFAULT,Typeface.BOLD); y.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL); r.addView(x,lpw(dp(30),1)); r.addView(y,lpw(dp(30),1)); p.addView(r,lp(-1,dp(34))); }
    void addCell(LinearLayout r,String s,float weight,int color,boolean bold){ TextView v=text(s,12,color); v.setPadding(dp(3),0,dp(3),0); if(bold)v.setTypeface(Typeface.DEFAULT,Typeface.BOLD); r.addView(v,lpw(dp(44),weight)); }

    void setup(){
        base("Product Setup","setup");
        TextView intro=text("Manage the products used in all calculations.",18,NAVY); intro.setTypeface(Typeface.DEFAULT,Typeface.BOLD); add(intro); gap(4);
        TextView sub=text("Name • Sales % • Carton price",14,MUTED); add(sub); gap(12);
        Button ap=button("＋  Add Product",TEAL); add(ap); gap(14);
        LinearLayout list=new LinearLayout(this); list.setOrientation(LinearLayout.VERTICAL); add(list);
        final Runnable[] holder=new Runnable[1];
        Runnable render=()->{
            list.removeAllViews();
            for(int i=0;i<products.size();i++){
                final int ix=i; Product p=products.get(i);
                LinearLayout card=new LinearLayout(this); card.setGravity(Gravity.CENTER_VERTICAL); card.setPadding(dp(12),dp(6),dp(6),dp(6)); card.setBackground(bg(Color.WHITE,14));
                TextView n=text(p.name,14,NAVY); n.setTypeface(Typeface.DEFAULT,Typeface.BOLD); n.setSingleLine(true); card.addView(n,lpw(dp(54),1));
                TextView q=text(fmt(p.pct)+"%",14,TEAL); q.setGravity(Gravity.CENTER); q.setTypeface(Typeface.DEFAULT,Typeface.BOLD); card.addView(q,lp(dp(62),dp(54)));
                Button e=button("Edit",BLUE); card.addView(e,lp(dp(62),dp(46)));
                Button d=button("Delete",Color.rgb(198,65,65)); card.addView(d,lp(dp(76),dp(46)));
                e.setOnClickListener(v->dialog(ix,holder[0]));
                d.setOnClickListener(v->{products.remove(ix);save();holder[0].run();});
                list.addView(card,lp(-1,dp(66))); gapList(list,7);
            }
            LinearLayout total=new LinearLayout(this); total.setPadding(dp(12),dp(10),dp(12),dp(10)); total.setBackground(bg(Color.WHITE,14));
            TextView t=text("Total Percentage",14,MUTED); total.addView(t,lpw(dp(42),1)); TextView tv=text(fmt(pctTotal())+"%",17,pctTotal()>100.001?Color.rgb(205,55,55):TEAL); tv.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL); tv.setTypeface(Typeface.DEFAULT,Typeface.BOLD); total.addView(tv,lpw(dp(42),1)); list.addView(total,lp(-1,dp(48)));
        };
        holder[0]=render; ap.setOnClickListener(v->dialog(-1,holder[0])); render.run();
    }
    void gapList(LinearLayout l,int h){ Space s=new Space(this); l.addView(s,new LinearLayout.LayoutParams(1,dp(h))); }

    void dialog(int ix,Runnable refresh){
        Product p=ix>=0?products.get(ix):new Product("",0,0);
        LinearLayout f=new LinearLayout(this); f.setOrientation(LinearLayout.VERTICAL); f.setPadding(dp(18),dp(4),dp(18),0);
        EditText name=edit("Product Name",p.name); f.addView(name,lp(-1,dp(50))); gapList(f,9);
        EditText pct=edit("Sales Percentage (%)",fmt(p.pct)); f.addView(pct,lp(-1,dp(50))); gapList(f,9);
        EditText cp=edit("Carton Price",fmt(p.cartonPrice)); f.addView(cp,lp(-1,dp(50)));
        AlertDialog dialog=new AlertDialog.Builder(this).setTitle(ix<0?"Add Product":"Edit Product").setView(f).setNegativeButton("Cancel",null).setPositiveButton("Save",null).create();
        dialog.setOnShowListener(v->{ dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(TEAL); dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(w->{ String n=name.getText().toString().trim(); if(n.isEmpty()){name.setError("Enter product name");return;} double pc=num(pct.getText().toString()),price=num(cp.getText().toString()); if(ix<0)products.add(new Product(n,pc,price)); else {products.get(ix).name=n;products.get(ix).pct=pc;products.get(ix).cartonPrice=price;} save(); dialog.dismiss(); refresh.run(); }); });
        dialog.getWindow(); dialog.show();
    }
    void hideKeyboard(){ View v=getCurrentFocus(); if(v!=null){((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(),0);v.clearFocus();} }
}
