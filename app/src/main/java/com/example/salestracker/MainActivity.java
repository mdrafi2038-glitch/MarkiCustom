package com.example.salestracker;

import android.app.*;
import android.os.*;
import android.content.*;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.*;
import android.widget.*;
import java.util.*;

public class MainActivity extends Activity {
    LinearLayout content;
    final int NAVY=Color.rgb(11,36,71), TEAL=Color.rgb(0,168,132), BLUE=Color.rgb(22,119,255), BG=Color.rgb(245,248,252);
    ArrayList<Product> products=new ArrayList<>();
    android.content.SharedPreferences prefs;

    static class Product {
        String name; double pct, cartonPrice;
        Product(String n,double p,double c){name=n;pct=p;cartonPrice=c;}
    }

    public void onCreate(Bundle b){
        super.onCreate(b);
        prefs=getSharedPreferences("data",0);
        load();
        home();
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
        } else for(String s:raw.split("\\|")){
            if(s.trim().isEmpty()) continue;
            String[] x=s.split("~",-1);
            try{products.add(new Product(x[0],Double.parseDouble(x[1]),Double.parseDouble(x[2])));}catch(Exception e){}
        }
    }

    void save(){
        StringBuilder s=new StringBuilder();
        for(Product p:products)s.append(p.name.replace("~"," ")).append("~").append(p.pct).append("~").append(p.cartonPrice).append("|");
        prefs.edit().putString("products",s.toString()).apply();
    }

    GradientDrawable bg(int c,float r){GradientDrawable g=new GradientDrawable();g.setColor(c);g.setCornerRadius(r);return g;}
    TextView text(String s,int size,int color){
        TextView v=new TextView(this);v.setText(s);v.setTextSize(size);v.setTextColor(color);v.setPadding(14,10,14,10);return v;
    }
    EditText edit(String hint,String val){
        EditText e=new EditText(this);e.setHint(hint);e.setText(val);e.setTextSize(15);e.setSingleLine(true);e.setPadding(12,4,12,4);e.setBackground(bg(Color.WHITE,14));return e;
    }
    Button button(String s,int c){
        Button b=new Button(this);b.setText(s);b.setTextColor(Color.WHITE);b.setTextSize(14);b.setAllCaps(false);b.setBackground(bg(c,18));return b;
    }
    void base(String title){
        LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setBackgroundColor(BG);
        LinearLayout bar=new LinearLayout(this);bar.setGravity(Gravity.CENTER_VERTICAL);bar.setBackgroundColor(NAVY);
        if(!title.equals("Sales Tracker")){
            Button back=button("‹",NAVY);back.setTextSize(28);back.setOnClickListener(v->home());
            bar.addView(back,new LinearLayout.LayoutParams(55,58));
        }
        TextView t=text(title,20,Color.WHITE);t.setTypeface(null,1);bar.addView(t,new LinearLayout.LayoutParams(0,58,1));root.addView(bar);
        ScrollView sc=new ScrollView(this);content=new LinearLayout(this);content.setOrientation(LinearLayout.VERTICAL);content.setPadding(14,14,14,20);sc.addView(content);root.addView(sc,new LinearLayout.LayoutParams(-1,0,1));
        setContentView(root);
    }
    void add(View v){content.addView(v,new LinearLayout.LayoutParams(-1,-2));}
    void gap(int h){Space s=new Space(this);add(s);s.getLayoutParams().height=h;}
    double num(String s){try{return Double.parseDouble(s.replace(",","").replace("৳","").trim());}catch(Exception e){return 0;}}
    String money(double n){return "৳ "+String.format(Locale.US,"%,.0f",n);}
    String fmt(double n){return Math.abs(n-Math.round(n))<.001?String.valueOf(Math.round(n)):String.format(Locale.US,"%.2f",n);}
    double pctTotal(){double n=0;for(Product p:products)n+=p.pct;return n;}

    void card(String title,String sub,int c,View.OnClickListener l){
        LinearLayout x=new LinearLayout(this);x.setOrientation(LinearLayout.VERTICAL);x.setPadding(18,14,18,14);x.setBackground(bg(Color.WHITE,20));x.setOnClickListener(l);
        TextView a=text(title,18,c);a.setTypeface(null,1);x.addView(a);
        x.addView(text(sub,14,Color.DKGRAY));add(x);
    }

    void home(){
        base("Sales Tracker");
        TextView w=text("Welcome Back!\nPlan smart. Sell better.",19,NAVY);w.setTypeface(null,1);w.setBackground(bg(Color.WHITE,20));add(w);gap(12);
        card("🎯  Sales Planning Maker","Target → product-wise % + amount + carton.",TEAL,v->planning());gap(10);
        card("◔  Product Wise Sales","Sales → estimated result → edit → final result.",BLUE,v->wise());gap(10);
        card("⚙  Product Setup","Add / edit / delete product name and sales %.",Color.rgb(110,70,190),v->setup());
        gap(15);TextView z=text("Products: "+products.size()+"    Total %: "+fmt(pctTotal())+"%",14,Color.DKGRAY);z.setGravity(Gravity.CENTER);add(z);
    }

    void planning(){
        base("Sales Planning Maker");
        add(text("Total Target",16,NAVY));
        EditText target=edit("Enter target","500000");add(target);
        Button calc=button("Calculate Planning",TEAL);add(calc);gap(8);
        LinearLayout table=new LinearLayout(this);table.setOrientation(LinearLayout.VERTICAL);table.setPadding(8,8,8,8);table.setBackground(bg(Color.WHITE,18));add(table);
        Runnable render=()->{
            table.removeAllViews();table.addView(text("Product        %       Amount (৳)      Carton",12,NAVY));
            double total=num(target.getText().toString()),as=0;long cs=0;
            for(Product p:products){
                double a=total*p.pct/100;long c=p.cartonPrice>0?Math.round(a/p.cartonPrice):0;
                LinearLayout r=new LinearLayout(this);r.setPadding(2,3,2,3);
                TextView n=text(p.name,12,Color.DKGRAY);r.addView(n,new LinearLayout.LayoutParams(0,50,1.35f));
                EditText ep=edit("%",fmt(p.pct));r.addView(ep,new LinearLayout.LayoutParams(58,50));
                EditText ea=edit("৳",String.format(Locale.US,"%.0f",a));r.addView(ea,new LinearLayout.LayoutParams(88,50));
                EditText ec=edit("C",String.valueOf(c));r.addView(ec,new LinearLayout.LayoutParams(62,50));
                table.addView(r);as+=a;cs+=c;
            }
            TextView t=text("TOTAL   "+fmt(pctTotal())+"%   "+money(as)+"   "+cs+" cartons",14,TEAL);t.setTypeface(null,1);table.addView(t);
        };
        calc.setOnClickListener(v->render.run());render.run();
    }

    void wise(){
        base("Product Wise Sales");
        add(text("Step 1 — Enter Total Sales",18,NAVY));
        EditText sales=edit("Total sales (৳)","15200");add(sales);
        Button get=button("Get Estimated Result",BLUE);add(get);gap(8);
        LinearLayout table=new LinearLayout(this);table.setOrientation(LinearLayout.VERTICAL);table.setPadding(8,8,8,8);table.setBackground(bg(Color.WHITE,18));add(table);

        Runnable render=()->{
            table.removeAllViews();
            double total=num(sales.getText().toString());
            table.addView(text("Estimated Result — edit values before Final Result",14,NAVY));
            table.addView(text("Product        %       Amount       Carton",12,Color.DKGRAY));
            for(Product p:products){
                double a=total*p.pct/100;long c=p.cartonPrice>0?Math.round(a/p.cartonPrice):0;
                LinearLayout r=new LinearLayout(this);r.setPadding(2,3,2,3);
                TextView n=text(p.name,12,Color.DKGRAY);r.addView(n,new LinearLayout.LayoutParams(0,50,1.35f));
                r.addView(edit("%",fmt(p.pct)),new LinearLayout.LayoutParams(58,50));
                r.addView(edit("৳",String.format(Locale.US,"%.0f",a)),new LinearLayout.LayoutParams(88,50));
                r.addView(edit("C",String.valueOf(c)),new LinearLayout.LayoutParams(62,50));
                table.addView(r);
            }
            Button fin=button("✓  Final Result",TEAL);table.addView(fin);fin.setOnClickListener(v->finalResult(total,table));
        };
        get.setOnClickListener(v->render.run());render.run();
    }

    void finalResult(double total,LinearLayout table){
        base("Final Result");
        double ps=0,as=0;long cs=0;ArrayList<String> rows=new ArrayList<>();
        for(int i=2;i<table.getChildCount();i++){
            View v=table.getChildAt(i);if(!(v instanceof LinearLayout))continue;
            LinearLayout r=(LinearLayout)v;if(r.getChildCount()<4)continue;
            try{
                String n=((TextView)r.getChildAt(0)).getText().toString();
                double p=num(((EditText)r.getChildAt(1)).getText().toString());
                double a=num(((EditText)r.getChildAt(2)).getText().toString());
                long c=Math.round(num(((EditText)r.getChildAt(3)).getText().toString()));
                ps+=p;as+=a;cs+=c;rows.add(n+"   | "+fmt(p)+"% | "+money(a)+" | "+c);
            }catch(Exception e){}
        }
        TextView h=text("✓ Final Product-Wise Sales",20,TEAL);h.setTypeface(null,1);add(h);
        TextView s=text("Total Sales Input: "+money(total)+"\nFinal Amount: "+money(as)+"\nTotal Carton: "+cs+"\nTotal %: "+fmt(ps)+"%",16,NAVY);s.setBackground(bg(Color.WHITE,18));add(s);gap(10);
        LinearLayout t=new LinearLayout(this);t.setOrientation(LinearLayout.VERTICAL);t.setPadding(8,8,8,8);t.setBackground(bg(Color.WHITE,18));add(t);
        t.addView(text("Product | % | Amount | Carton",12,NAVY));
        for(String x:rows)t.addView(text(x,12,Color.DKGRAY));
        Button b=button("Back to Home",TEAL);add(b);b.setOnClickListener(v->home());
    }

    void setup(){
        base("Product Setup");
        add(text("Add products and sales %. The products you add here are used by the other features.",14,Color.DKGRAY));
        Button ap=button("+ Add Product",TEAL);add(ap);gap(8);
        LinearLayout list=new LinearLayout(this);list.setOrientation(LinearLayout.VERTICAL);add(list);
        Runnable render=()->{
            list.removeAllViews();
            for(int i=0;i<products.size();i++){
                final int ix=i;Product p=products.get(i);
                LinearLayout r=new LinearLayout(this);r.setGravity(Gravity.CENTER_VERTICAL);r.setPadding(6,5,3,5);r.setBackground(bg(Color.WHITE,14));
                TextView n=text(p.name,13,NAVY);r.addView(n,new LinearLayout.LayoutParams(0,54,1));
                TextView q=text(fmt(p.pct)+"%",14,TEAL);q.setTypeface(null,1);r.addView(q,new LinearLayout.LayoutParams(60,54));
                Button e=button("Edit",BLUE);r.addView(e,new LinearLayout.LayoutParams(75,50));
                Button d=button("Delete",Color.rgb(200,55,55));r.addView(d,new LinearLayout.LayoutParams(85,50));
                e.setOnClickListener(v->dialog(ix,render));d.setOnClickListener(v->{products.remove(ix);save();render.run();});
                list.addView(r);gapList(list,6);
            }
            TextView tot=text("Total Percentage: "+fmt(pctTotal())+"%",15,pctTotal()>100.001?Color.RED:TEAL);tot.setTypeface(null,1);list.addView(tot);
        };
        ap.setOnClickListener(v->dialog(-1,render));render.run();
    }
    void gapList(LinearLayout l,int h){Space s=new Space(this);l.addView(s,new LinearLayout.LayoutParams(1,h));}

    void dialog(int ix,Runnable refresh){
        Product p=ix>=0?products.get(ix):new Product("",0,0);
        LinearLayout f=new LinearLayout(this);f.setOrientation(LinearLayout.VERTICAL);f.setPadding(20,8,20,4);
        EditText name=edit("Product Name",p.name);f.addView(name);gapList(f,8);
        EditText pct=edit("Sales Percentage (%)",fmt(p.pct));f.addView(pct);gapList(f,8);
        EditText cp=edit("Carton Price (for carton calculation)",fmt(p.cartonPrice));f.addView(cp);
        new AlertDialog.Builder(this).setTitle(ix<0?"Add Product":"Edit Product").setView(f).setNegativeButton("Cancel",null).setPositiveButton("Save",(d,w)->{
            String n=name.getText().toString().trim();if(n.isEmpty())return;
            double pc=num(pct.getText().toString()),price=num(cp.getText().toString());
            if(ix<0)products.add(new Product(n,pc,price));else{products.get(ix).name=n;products.get(ix).pct=pc;products.get(ix).cartonPrice=price;}
            save();refresh.run();
        }).show();
    }
}
