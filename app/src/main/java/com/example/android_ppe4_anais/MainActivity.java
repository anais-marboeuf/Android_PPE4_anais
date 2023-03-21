package com.example.android_ppe4_anais;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.android_ppe4_anais.databinding.ActivityMainBinding;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;



public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private Boolean connex=true;
    private Menu lemenu;
    private String lenom,leprenom;
    private Async mThreadCon = null, vmapref=null;

    private String url;
    private String[] mesparams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

//        //Quand on clique sur le bouton de mail en bas à droite
//        binding.fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        //if(!connex){menuDeconnecte();}else{menuConnecte();}


    }


    public void menuConnecte() {

        lemenu.setGroupVisible(R.id.group_menu_deconnect,true);
        lemenu.setGroupVisible(R.id.group_menu_connect,false);
        return;
    }    //        Affichage de tous les items du menu
    public void menuDeconnecte() {

        lemenu.setGroupVisible(R.id.group_menu_connect,true);
        lemenu.setGroupVisible(R.id.group_menu_deconnect,false);
        return;
    } //        Affichage de l'item de connexion

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        lemenu=menu;
        getMenuInflater().inflate(R.menu.menu_main, lemenu);
        //getMenuInflater().inflate(R.menu.menu_main, menuConnecte(menu));


        //if(!connex){menuDeconnecte();}else{menuConnecte();}

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.menu_connect:
                //Toast.makeText(getApplicationContext(), "clic sur connect", Toast.LENGTH_SHORT).show();
                boolean firstFragActive=(Navigation.findNavController(this,R.id.nav_host_fragment_content_main).getCurrentDestination().getId()==R.id.FirstFragment);
                // Vérification que toutes les permissions sont acceptées pour continuer l'application
                if (permissionOK==false) {
                    checkPermissions();
                }
                if (permissionOK==true) {
                    // Permet de naviguer du premier fragment au second fragment via le "clic" du bouton
                    Navigation.findNavController(this, R.id.nav_host_fragment_content_main).navigate(R.id.action_FirstFragment_to_SecondFragment);
                }

                return permissionOK;
            case R.id.menu_deconnect:
                //Toast.makeText(getApplicationContext(), "clic sur deconnect", Toast.LENGTH_SHORT).show();
                boolean retourAccueil=(Navigation.findNavController(this,R.id.nav_host_fragment_content_main).getCurrentDestination().getId()==R.id.TroisiemeFragment);
                Navigation.findNavController(this, R.id.nav_host_fragment_content_main).navigate(R.id.action_TroisiemeFragment_to_FirstFragment);
                menuConnecte();
                return true;
            case R.id.menu_import:
                Toast.makeText(getApplicationContext(), "clic sur import", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_export:
                Toast.makeText(getApplicationContext(), "clic sur export", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_list:
                Toast.makeText(getApplicationContext(), "clic sur list", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_settings:
                Toast.makeText(getApplicationContext(), "clic sur settings", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Vérifie si l'identifiant et le mot de passe sont validés et pas vide
    public void ctrlConnex(String vid,String vmp) {

    }

    // Demande et vérification des permissions
    /*============================================================================================*/
    private String[] permissions = {
            //Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.READ_CONTACTS};
    //private String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET,Manifest.permission.READ_CONTACTS,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_WIFI_STATE,Manifest.permission.ACCESS_NETWORK_STATE,Manifest.permission.READ_EXTERNAL_STORAGE};
    private static final int MULTIPLE_PERMISSIONS = 10;
    private List<String> listPermissionsNeeded;
    private boolean permissionOverlayAsked=false;
    private boolean permissionOverlayok=false;
    private boolean permissionOK=false;


    @Override
    public void onStart() {
        super.onStart();
        super.onResume();
        if(!permissionOverlayAsked) {
            checkPermissionAlert();
        }
        checkPermissions();
    }


    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result;
            List<String> listPermissionsNeeded = new ArrayList<>();
            for (String p : permissions) {
                result = checkSelfPermission(p);
                if (result != PackageManager.PERMISSION_GRANTED) {
                    listPermissionsNeeded.add(p);
                }
            }
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MULTIPLE_PERMISSIONS);
            } else {
                // Toutes les permissions sont ok
                permissionOK = true;
            }
        }
        return;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissionsList[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissionsList, grantResults);
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0) {
                    String permissionsDenied = "";
                    int posi = 0;
                    for (String per : permissionsList) {
                        if (grantResults[posi] == PackageManager.PERMISSION_DENIED) {
                            permissionsDenied += "\n" + per;
                        }
                        posi++;
                    }
                    // Show permissionsDenied
                    if (permissionsDenied.length() > 0) {
                        Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
                        Toast.makeText(getApplicationContext(), "Nous ne pouvons pas continuer l'application car ces permissions sont nécessaires : \n" + permissionsDenied, Toast.LENGTH_LONG).show();
                    } else {
                        permissionOK = true;
                    }
                }
                return;
            }
        }
    }
    /*============================================================================================*/
    /*============================================================================================*/

    // Alert Message

    public void alertmsg(String title, String msg) {
        if (permissionOverlayok==true) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
            builder.setMessage(msg)
                    .setTitle(title);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                }
            });

            AlertDialog dialog = builder.create();
            dialog.getWindow().setType(WindowManager.LayoutParams.
                    TYPE_APPLICATION_OVERLAY);
            dialog.show();
        }
        else {
            Toast.makeText(getApplicationContext(), "Veuillez activer l'overlay dans les paramètres de votre application.", Toast.LENGTH_LONG).show();
        }

    }

    /*============================================================================================*/
    /*============================================================================================*/

    // Autorisation des alert Message

    public static final int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE= 5469;
    public void checkPermissionAlert() {
        permissionOverlayAsked=true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                // On demande à l'utilisateur l'autorisation de l'overlay
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
            }
            else {
                permissionOverlayok=true;
            }
        }
        else {
            permissionOverlayok=true;
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // on regarde quelle Activity a répondu
        super.onActivityResult(requestCode,resultCode,data);
        switch (requestCode) {
            case ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (Settings.canDrawOverlays(this)) {
                        alertmsg("Permission ALERT","Permission OK");
                        return;
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Pbs demande de permissions"
                                , Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    permissionOverlayok=true;
                }
        }
    }

    /*============================================================================================*/
    /*============================================================================================*/



    public void retourConnexion(StringBuilder sb)
    {
        alertmsg("retour Connexion", sb.toString());

// je reçois le string que je transforme en json



        //je regarde si il y a le "statut":"false"
        //si y'a pas le statut false, je récupère le nom et le prénom de l'infirmière
        //je passe les infos au 3eme fragment et je les affiche dans les textview du troisieme fragment

        //Naviguer de ce fragment jusq'au troisième fragment
       // NavHostFragment.findNavController(SecondFragment.this).navigate(R.id.action_SecondFragment_to_TroisiemeFragment);
    }

    public void testMotDePasse(String login, String mdp) {
        if (!TextUtils.isEmpty(login) && !TextUtils.isEmpty(mdp)) {
            url = "https://www.btssio-carcouet.fr/ppe4/public/connect2/"+login+"/"+mdp+"/infirmiere";
            //Au clic du bouton ok
            mesparams=new String[3];
            mesparams[0]="1";
            mesparams[1]=url;
            mesparams[2]="GET";
            mThreadCon = new Async ((this));
            mThreadCon.execute(mesparams);


        }

    }

//    @Override
//    public boolean onSupportNavigateUp() {
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
//        return NavigationUI.navigateUp(navController, appBarConfiguration)
//                || super.onSupportNavigateUp();
//    }
}