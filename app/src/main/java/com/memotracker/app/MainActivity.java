package com.memotracker.app;

import android.app.*;
import android.os.*;
import android.content.*;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.util.*;

public class MainActivity extends Activity {
    LinearLayout modules;
    android.content.SharedPreferences prefs;

    int dp(int n){ return (int)(n * getResources().getDisplayMetrics().density + .5f); }

    TextView text(String s, float size, boolean bold){
        TextView v=new TextView(this);
        v.setText(s); v.setTextSize(size); v.setTextColor(Color.rgb(30,35,40));
        v.setTypeface(null,bold?1:0); v.setPadding(dp(16),dp(10),dp(16),dp(10));
        return v;
    }

    public void onCreate(Bundle b){
        super.onCreate(b);
        prefs=getSharedPreferences("modules",0);
        buildUi();
    }

    void buildUi(){
        LinearLayout root=new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.rgb(248,249,251));

        LinearLayout header=new LinearLayout(this);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setBackgroundColor(Color.rgb(25,70,100));
        TextView title=text("Marki Custom",21,true);
        title.setTextColor(Color.WHITE);
        header.addView(title,new LinearLayout.LayoutParams(0,dp(64),1));
        Button add=new Button(this);
        add.setText("+ Add APK");
        add.setAllCaps(false);
        header.addView(add,new LinearLayout.LayoutParams(dp(120),dp(52)));
        root.addView(header);
        add.setOnClickListener(v->pickApk());

        TextView info=text("Add your own compatible modules here. Imported APKs are stored inside Marki Custom; arbitrary APK interfaces cannot be injected directly into another app.",14,false);
        root.addView(info);

        ScrollView scroll=new ScrollView(this);
        modules=new LinearLayout(this);
        modules.setOrientation(LinearLayout.VERTICAL);
        modules.setPadding(dp(12),dp(4),dp(12),dp(24));
        scroll.addView(modules);
        root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));

        setContentView(root);
        refresh();
    }

    void pickApk(){
        Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT);
        i.setType("application/vnd.android.package-archive");
        i.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(i,77);
    }

    protected void onActivityResult(int request,int result,Intent data){
        super.onActivityResult(request,result,data);
        if(request!=77 || result!=RESULT_OK || data==null || data.getData()==null) return;
        Uri uri=data.getData();
        try{
            String fileName="module_"+System.currentTimeMillis()+".apk";
            File dir=new File(getFilesDir(),"modules");
            if(!dir.exists()) dir.mkdirs();
            File out=new File(dir,fileName);
            InputStream in=getContentResolver().openInputStream(uri);
            FileOutputStream os=new FileOutputStream(out);
            byte[] buf=new byte[8192]; int n;
            while((n=in.read(buf))!=-1) os.write(buf,0,n);
            in.close(); os.close();

            PackageManager pm=getPackageManager();
            PackageInfo pi=pm.getPackageArchiveInfo(out.getAbsolutePath(),PackageManager.GET_META_DATA);
            if(pi==null){ out.delete(); toast("This file is not a readable Android APK."); return; }

            String label=pi.applicationInfo.loadLabel(pm).toString();
            String pkg=pi.packageName;
            long size=out.length();

            String key="module."+System.currentTimeMillis();
            prefs.edit().putString(key,label+"\n"+pkg+"\n"+pi.versionName+"\n"+size+"\n"+out.getAbsolutePath()).apply();
            refresh();
            toast("Module added: "+label);
        }catch(Exception e){ toast("Could not import APK: "+e.getMessage()); }
    }

    void refresh(){
        if(modules==null)return;
        modules.removeAllViews();
        Map<String,?> all=prefs.getAll();
        boolean any=false;
        for(String key:all.keySet()){
            if(!key.startsWith("module.")) continue;
            any=true;
            String[] a=String.valueOf(all.get(key)).split("\\n",-1);
            String label=a.length>0?a[0]:"Module";
            String pkg=a.length>1?a[1]:"";
            String ver=a.length>2?a[2]:"";
            String size=a.length>3?a[3]:"0";
            LinearLayout card=new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setBackgroundColor(Color.WHITE);
            card.setPadding(dp(4),dp(6),dp(4),dp(6));
            TextView name=text(label,18,true);
            TextView details=text(pkg+"\nVersion "+ver+" • "+formatSize(size),13,false);
            card.addView(name); card.addView(details);
            Button view=new Button(this);
            view.setText("Module details");
            view.setAllCaps(false);
            card.addView(view,new LinearLayout.LayoutParams(-1,dp(46)));
            view.setOnClickListener(v->details(label,pkg,ver));
            card.setOnLongClickListener(v->{removeModule(key);return true;});
            modules.addView(card,new LinearLayout.LayoutParams(-1,dp(150)));
            Space sp=new Space(this); modules.addView(sp,new LinearLayout.LayoutParams(1,dp(10)));
        }
        if(!any){
            TextView empty=text("No modules yet. Tap “+ Add APK” to import one.",16,false);
            empty.setGravity(Gravity.CENTER);
            modules.addView(empty,new LinearLayout.LayoutParams(-1,dp(160)));
        }
    }

    String formatSize(String s){
        try{
            long n=Long.parseLong(s);
            if(n<1024)return n+" B";
            if(n<1024*1024)return (n/1024)+" KB";
            return (n/(1024*1024))+" MB";
        }catch(Exception e){return "?";}
    }

    void details(String label,String pkg,String ver){
        new AlertDialog.Builder(this).setTitle(label)
            .setMessage("Package: "+pkg+"\nVersion: "+ver+"\n\nThis APK is stored as a Marki Custom module. To show its real interface inside Marki Custom, the source app must be converted to the Marki module contract.")
            .setPositiveButton("OK",null).show();
    }

    void removeModule(String key){
        new AlertDialog.Builder(this).setTitle("Remove module?")
            .setMessage("This removes the imported module from Marki Custom.")
            .setNegativeButton("Cancel",null)
            .setPositiveButton("Remove",(d,w)->{
                Object v=prefs.getAll().get(key);
                if(v!=null){
                    String[] a=String.valueOf(v).split("\\n",-1);
                    if(a.length>4)new File(a[4]).delete();
                }
                prefs.edit().remove(key).apply();
                refresh();
            }).show();
    }

    void toast(String s){ Toast.makeText(this,s,Toast.LENGTH_LONG).show(); }
}